package nachos.vm;


import java.util.*;
import java.util.Map.Entry;

import nachos.machine.CoffSection;
import nachos.machine.Lib;
import nachos.machine.Machine;
import nachos.machine.Processor;
import nachos.machine.TranslationEntry;
import nachos.threads.Lock;
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
		phyLock.acquire();
		for (int i = 0; i < length; ++ i) {
			int vpage = vaddr / pageSize;
			if (vpage > numPages) break;
			if (!pageTable[vpage].valid)
				if (!loadPage(vpage)) {
					phyLock.acquire();
					break;
				}
			int paddr = pageTable[vpage].ppn * pageSize + (vaddr % pageSize);
			data[offset + i] = memory[paddr];
			pageTable[vpage].used = true;
			vaddr ++; amount ++;
		}
		phyLock.release();
		return amount;
	}
	
	@Override
	public int writeVirtualMemory(int vaddr, byte[] data, int offset, int length) {
		Lib.assertTrue(offset >= 0 && length >= 0
				&& offset + length <= data.length);

		byte[] memory = Machine.processor().getMemory();

		if (vaddr < 0 || vaddr >= numPages * pageSize)
			return 0;

		length = Math.min(length, numPages * pageSize - vaddr);
		int amount = 0;

		phyLock.acquire();
		for (int i = 0; i < length; ++ i) {
			int vpage = vaddr / pageSize;
			if (vpage > numPages || pageTable[vpage].readOnly) break;
			if (!pageTable[vpage].valid)
				if (!loadPage(vpage)) {
					phyLock.acquire();
					break;
				}
			int paddr = pageTable[vpage].ppn * pageSize + (vaddr % pageSize);
			memory[paddr] = data[offset + i];
			pageTable[vpage].used = true;
			pageTable[vpage].dirty = true;
			vaddr ++; amount ++;
		}
		phyLock.release();

		return amount;
	}

	/**
	 * Save the state of this process in preparation for a context switch.
	 * Called by <tt>UThread.saveState()</tt>.
	 */
	public void saveState() {
		for (int i = 0; i < TLBSize; ++ i) {
			TranslationEntry trans = Machine.processor().readTLBEntry(i);
			if (trans.valid) {
				pageTable[trans.vpn].used |= trans.used;
				pageTable[trans.vpn].dirty |= trans.dirty;
				onTLB[trans.vpn] = -1;
			}
			TLBBackup[i] = trans;
		}
	}

	/**
	 * Restore the state of this process after a context switch. Called by
	 * <tt>UThread.restoreState()</tt>.
	 */
	public void restoreState() {
		for (int i = 0; i < TLBSize; ++ i) {
			if (TLBBackup[i] == null) TLBBackup[i] = nullEntry;
			if (TLBBackup[i].valid && !pageTable[TLBBackup[i].vpn].valid)
				TLBBackup[i] = nullEntry;
			if (TLBBackup[i].valid)
				onTLB[TLBBackup[i].vpn] = i;
			Machine.processor().writeTLBEntry(i, TLBBackup[i]);
		}
	}
	/**
	 * Initializes page tables for this process so that the executable can be
	 * demand-paged.
	 * 
	 * @return <tt>true</tt> if successful.
	 */
	protected boolean loadSections() {
		onTLB = new int[numPages];
		pageTable = new TranslationEntry[numPages];
		for (int i = 0; i < numPages; ++ i) {
			pageTable[i] = new TranslationEntry(i, i, false, false, false, false);
			onTLB[i] = -1;
		}
		
		for (int i = 0; i < coff.getNumSections(); ++ i) {
			CoffSection section = coff.getSection(i);
			for (int j = 0; j < section.getLength(); ++ j) {
				sections.put(section.getFirstVPN() + j, section);
				if (section.isReadOnly())
					pageTable[j].readOnly = true;
			}
		}
		return true;
	}

	/**
	 * Release any resources allocated by <tt>loadSections()</tt>.
	 */
	protected void unloadSections() {
		phyLock.acquire();
		for (int page : ownPages) {
			phyTable.remove(page);
			allPages.add(page);
		}
		phyLock.release();
		for (int page : swappedPages)
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
	
	private boolean loadPage(int page) {
		Lib.debug(dbgVM, "Process #" + pid + " loading page " + page);
		if (page < 0 || page >= numPages) return false;
		
		Lib.assertTrue(phyLock.isHeldByCurrentThread());

		byte[] memory = Machine.processor().getMemory();
		int ppn = 0;
		if (!allPages.isEmpty()) {
			ppn = allPages.pollFirst();
		} else {
			VMProcess proc = null;
			PageIdentifier pageID = null;
			for (int i = 0; i < 10; ++ i) {
				ppn = Lib.random(Machine.processor().getNumPhysPages());
				pageID = phyTable.get(ppn);
				proc = (VMProcess) processTable.get(pageID.pid);
				if (proc.onTLB[pageID.page] == -1) break;
			}
			
			proc.pageTable[pageID.page].valid = false;
			if (proc.onTLB[pageID.page] != -1) {
				TranslationEntry trans = Machine.processor().readTLBEntry(proc.onTLB[pageID.page]);
				Machine.processor().writeTLBEntry(proc.onTLB[pageID.page], nullEntry);
				proc.onTLB[pageID.page] = -1;
				if (trans.vpn == pageID.page && trans.ppn == ppn && trans.valid) {
					proc.pageTable[pageID.page].used |= trans.used;
					proc.pageTable[pageID.page].dirty |= trans.dirty;
				}
			}
			if (proc.pageTable[pageID.page].dirty) {
				SwapFile.write(proc.pid, pageID.page, memory, ppn * pageSize);
				proc.swappedPages.add(pageID.page);
			}

			proc.ownPages.remove(ppn);
		}

		ownPages.add(ppn);
		phyTable.put(ppn, new PageIdentifier(pid, page));
		
		if (swappedPages.contains(page))
			SwapFile.read(pid, page, memory, ppn * pageSize);
		else if (sections.containsKey(page)) {
			CoffSection section = sections.get(page);
			section.loadPage(page - section.getFirstVPN(), ppn);
		} else {
			Arrays.fill(memory, ppn * pageSize, (ppn + 1) * pageSize, (byte) 0);
		}
		pageTable[page].valid = true; pageTable[page].ppn = ppn;
		pageTable[page].used = pageTable[page].dirty = false;
		return true;
	}
	
	private void handleTLBMiss() {
		Processor processor = Machine.processor();
		int bvaddr = processor.readRegister(processor.regBadVAddr);
		int bvpage = bvaddr / pageSize;
		if (bvpage >= numPages || bvpage < 0) 
			super.handleException(processor.exceptionTLBMiss);
		
		//TLBLock.acquire();
		int p = -1;
		for (int i = 0; i < TLBSize; ++ i)
			if (Machine.processor().readTLBEntry(i).valid == false) {
				p = i;
				break;
			}
		if (p == -1) {
			p = Lib.random(TLBSize);
			TranslationEntry trans = Machine.processor().readTLBEntry(p);
			Machine.processor().writeTLBEntry(p, nullEntry);
			if (trans.valid) {
				pageTable[trans.vpn].used |= trans.used;
				pageTable[trans.vpn].dirty |= trans.dirty;
				onTLB[trans.vpn] = -1;
			}
		}

		phyLock.acquire();
		if (pageTable[bvpage].valid == false)
			loadPage(bvpage);

		Machine.processor().writeTLBEntry(p, pageTable[bvpage]);
		onTLB[bvpage] = p;
		phyLock.release();
	}

	private static final int TLBSize = Machine.processor().getTLBSize();
	private static final int pageSize = Processor.pageSize;
	private static final char dbgProcess = 'a';
	private static final char dbgVM = 'v';
	private static HashMap<Integer, PageIdentifier> phyTable = new HashMap<Integer, PageIdentifier>();
	private static Lock phyLock = new Lock();
	//private static Lock TLBLock = new Lock();
	
	private TranslationEntry[] TLBBackup = new TranslationEntry[TLBSize];
	
	private int[] onTLB;
	private HashSet<Integer> swappedPages = new HashSet<Integer>();
	private HashMap<Integer, CoffSection> sections = new HashMap<Integer, CoffSection>();
	
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
	
	private final TranslationEntry nullEntry = new TranslationEntry(-1, -1, false, false, false, false);
}
