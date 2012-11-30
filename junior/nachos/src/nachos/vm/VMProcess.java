package nachos.vm;

import java.util.*;

import nachos.machine.Coff;
import nachos.machine.CoffSection;
import nachos.machine.Lib;
import nachos.machine.Machine;
import nachos.machine.Processor;
import nachos.machine.TranslationEntry;
import nachos.threads.Condition;
import nachos.threads.KThread;
import nachos.threads.Lock;
import nachos.threads.Semaphore;
import nachos.userprog.UThread;
import nachos.userprog.UserProcess;

/**
 * A <tt>UserProcess</tt> that supports demand-paging.
 */
public class VMProcess extends UserProcess {
	/**
	 * Allocate a new process.
	 */
	public VMProcess() {
		super();
		
		if (invertPageTable == null) {
			invertPageTable = new HashMap<Integer, PageIdentifier>();
		}
	}
	
	@Override
	public int readVirtualMemory(int vaddr, byte[] data, int offset, int length) {
		Lib.assertTrue(offset >= 0 && length >= 0
				&& offset + length <= data.length);

		byte[] memory = Machine.processor().getMemory();

		if (vaddr < 0 || vaddr >= numPages * pageSize)
			return 0;

		length = Math.min(length, numPages * pageSize - vaddr);
		
		int amount = 0;
		for (int i = 0; i < length; ++ i) {
			int vpage = vaddr / pageSize;
			if (vpage > numPages) break;
			if (!pageTable[vpage].valid) {
				if (!loadPage(vpage))
					return 0;
			}
			int paddr = pageTable[vpage].ppn * pageSize + (vaddr % pageSize);
			pageTable[vpage].used = true;
			data[offset + i] = memory[paddr];
			vaddr ++; amount ++;
		}
		
		//System.out.println(new String(data, offset, length));
		//System.out.println(amount);

		return amount;
	}
	
	@Override
	public int writeVirtualMemory(int vaddr, byte[] data, int offset, int length) {
		Lib.assertTrue(offset >= 0 && length >= 0
				&& offset + length <= data.length);

		byte[] memory = Machine.processor().getMemory();

		// for now, just assume that virtual addresses equal physical addresses
		if (vaddr < 0 || vaddr >= numPages * pageSize)
			return 0;

		length = Math.min(length, numPages * pageSize - vaddr);
		int amount = 0;

		for (int i = 0; i < length; ++ i) {
			int vpage = vaddr / pageSize;
			if (vpage > numPages || pageTable[vpage].readOnly) break;
			if (!pageTable[vpage].valid) loadPage(vpage);
			int paddr = pageTable[vpage].ppn * pageSize + (vaddr % pageSize);
			pageTable[vpage].used = true;
			pageTable[vpage].dirty = true;
			memory[paddr] = data[offset + i];
			vaddr ++; amount ++;
		}

		return amount;
	}

	/**
	 * Save the state of this process in preparation for a context switch.
	 * Called by <tt>UThread.saveState()</tt>.
	 */
	public void saveState() {
		super.saveState();

		for (int i = 0; i < usedTLB; ++ i) {
			TranslationEntry trans = Machine.processor().readTLBEntry(i);
			pageTable[trans.vpn] = trans;
			//Lib.debug(dbgVM, trans.vpn + " " + trans.ppn + " " + trans.valid);
			TLBPos[trans.vpn] = -1;
		}
	}

	/**
	 * Restore the state of this process after a context switch. Called by
	 * <tt>UThread.restoreState()</tt>.
	 */
	public void restoreState() {
		int TLBSize = Machine.processor().getTLBSize();

		Lib.debug(dbgVM, "Process #" + pid + " restoring its state");
		usedTLB = 0;
		for (int i = 0; i < TLBSize; ++ i)
			Machine.processor().writeTLBEntry(i, pageTable[0]);
	}
	/**
	 * Initializes page tables for this process so that the executable can be
	 * demand-paged.
	 * 
	 * @return <tt>true</tt> if successful.
	 */
	protected boolean loadSections() {
		TLBPos = new int[numPages];
		pageTable = new TranslationEntry[numPages];
		for (int i = 0; i < numPages; ++ i) {
			pageTable[i] = new TranslationEntry(i, i, false, false, false, false);
			TLBPos[i] = -1;
		}
		
		for (int i = 0; i < coff.getNumSections(); ++ i) {
			CoffSection section = coff.getSection(i);
			for (int j = 0; j < section.getLength(); ++ j)
				sections.put(section.getFirstVPN() + j, i);
		}
		return true;
	}

	/**
	 * Release any resources allocated by <tt>loadSections()</tt>.
	 */
	protected void unloadSections() {
		for (int page : ownPages) {
			invertPageTable.remove(page);
			allPages.add(page);
		}
		for (int page : swappedPage)
			SwapFile.remove(pid, page);
	}

	/**
	 * Handle a user exception. Called by <tt>UserKernel.exceptionHandler()</tt>
	 * . The <i>cause</i> argument identifies which exception occurred; see the
	 * <tt>Processor.exceptionZZZ</tt> constants.
	 * 
	 * @param cause
	 *            the user exception that occurred.
	 */
	@Override
	public void handleException(int cause) {
		switch (cause) {
			
		case Processor.exceptionTLBMiss:
			handleTLBMiss();
			break;
		
		default:
			super.handleException(cause);
			break;
		}
	}
	
	private boolean unloadPage(int page) {
		Lib.debug(dbgVM, "Process #" + pid + " unload page " + page);
		if (page < 0 || page >= numPages) return false;
		int ppn = pageTable[page].ppn;

		boolean intStatus = Machine.interrupt().disable();
		pageTable[page].valid = false;
		if (TLBPos[page] != -1) {
			pageTable[page] = Machine.processor().readTLBEntry(TLBPos[page]);
			pageTable[page].valid = false;
			Machine.processor().writeTLBEntry(TLBPos[page], pageTable[page]);
		}
		
		if (pageTable[page].dirty == true) {
			byte[] memory = Machine.processor().getMemory();
			if (!SwapFile.write(pid, page, memory, ppn * pageSize))
				return false;
			swappedPage.add(page);
		}
		Machine.interrupt().restore(intStatus);

		invertPageTable.remove(new PageIdentifier(pid, page));
		ownPages.remove(ppn);
		allPages.add(ppn);
		return true;
	}
	
	private boolean loadPage(int page) {
		if (page < 0 || page >= numPages)
			return false;
		Lib.assertTrue(pageTable[page].valid == false);
		pageLock.acquire();
		int ppn = Lib.random(Machine.processor().getNumPhysPages());
		if (!allPages.isEmpty()) {
			ppn = allPages.pollFirst();
		} else {
			PageIdentifier pageID = invertPageTable.get(ppn);
			VMProcess proc = (VMProcess) processTable.get(pageID.pid);
			if (!proc.unloadPage(pageID.page)) {
				pageLock.release();
				return false;
			}
			allPages.remove(ppn);
		}

		Lib.debug(dbgVM, "Process #" + pid + " load virtual page " + page + " to page " + ppn);
		
		if (swappedPage.contains(page)) {
			byte[] memory = Machine.processor().getMemory();
			if (!SwapFile.read(pid, page, memory, ppn * pageSize)) {
				Lib.debug(dbgVM, "Failed to load page " + page + " from swap");
				pageLock.release();
				return false;
			}
		} else {
			if (sections.containsKey(page)) {
				CoffSection section = coff.getSection(sections.get(page));
				section.loadPage(page - section.getFirstVPN(), ppn);
				if (section.isReadOnly())
					pageTable[page].readOnly = true;
			} else {
				byte[] memory = Machine.processor().getMemory();
				Arrays.fill(memory, ppn * pageSize, ppn * pageSize + pageSize, (byte) 0);
			}
		}
		ownPages.add(ppn);
		pageTable[page].ppn = ppn;
		pageTable[page].dirty = pageTable[page].used = false;
		pageTable[page].valid = true;
		invertPageTable.put(ppn, new PageIdentifier(pid, page));
		pageLock.release();
		
		return true;
	}
	
	private void handleTLBMiss() {
		Processor processor = Machine.processor();
		int bvaddr = processor.readRegister(processor.regBadVAddr);
		int bvpage = bvaddr / pageSize;
				
		if (bvpage >= numPages || bvpage < 0)
			super.handleException(processor.exceptionTLBMiss);

		if (!pageTable[bvpage].valid) {
			if (!loadPage(bvpage)) {
				super.handleException(processor.exceptionPageFault);
			}
		}
		
		int p = Lib.random(processor.getTLBSize());
		if (usedTLB < processor.getTLBSize()) {
			p = usedTLB;
			TLBPos[bvpage] = p;
			processor.writeTLBEntry(p, pageTable[bvpage]);
			usedTLB ++;
		} else {
			TranslationEntry trans = processor.readTLBEntry(p);
			pageTable[trans.vpn] = trans;
			TLBPos[trans.vpn] = -1; 
			TLBPos[bvpage] = p;
			processor.writeTLBEntry(p, pageTable[bvpage]);
		}
	}

	private static final int pageSize = Processor.pageSize;
	//private static final char dbgProcess = 'a';
	private static final char dbgVM = 'v';
	
	private static HashMap<Integer, PageIdentifier> invertPageTable;
	private static Lock pageLock = new Lock();
	
	//private Lock translationEntryLock = new Lock();
	private TreeSet<Integer> swappedPage = new TreeSet<Integer>();
	private TreeMap<Integer, Integer> sections = new TreeMap<Integer, Integer>();	
	private int usedTLB = 0;
	private int[] TLBPos;
	
	public static class PageIdentifier implements Comparable<PageIdentifier> {
		public int pid, page;
		
		PageIdentifier(int pid, int page) {
			this.pid = pid;
			this.page = page;
		}

		@Override
		public int compareTo(PageIdentifier p) {
			if (pid != p.pid) return pid < p.pid ? -1 : 1;
			if (page != p.page) return page < p.page ? -1 : 1; 
			return 0;
		}
	}
}
