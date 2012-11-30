package nachos.vm;

import java.util.*;

import nachos.machine.CoffSection;
import nachos.machine.Lib;
import nachos.machine.Machine;
import nachos.machine.Processor;
import nachos.machine.TranslationEntry;
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

	/**
	 * Save the state of this process in preparation for a context switch.
	 * Called by <tt>UThread.saveState()</tt>.
	 */
	public void saveState() {
		super.saveState();

		int TLBSize = Machine.processor().getTLBSize();
		for (int i = 0; i < usedTLB; ++ i) {
			storedEntries[i] = Machine.processor().readTLBEntry(i);
			pageTable[storedEntries[i].vpn] = storedEntries[i];
		}
	}

	/**
	 * Restore the state of this process after a context switch. Called by
	 * <tt>UThread.restoreState()</tt>.
	 */
	public void restoreState() {
		int TLBSize = Machine.processor().getTLBSize();
		for (int i = 0; i < TLBSize; ++ i) {
			if (i < usedTLB)
				Machine.processor().writeTLBEntry(i, storedEntries[i]);
			else
				Machine.processor().writeTLBEntry(i, pageTable[0]);
		}
	}

	/**
	 * Initializes page tables for this process so that the executable can be
	 * demand-paged.
	 * 
	 * @return <tt>true</tt> if successful.
	 */
	protected boolean loadSections() {
		if (!super.loadSections())
			return false;
		int index = 0;
		for (int page : ownPages) {
			invertPageTable.put(index, new PageIdentifier(pid, page));
			++ index;
		}
		return true;
	}

	/**
	 * Release any resources allocated by <tt>loadSections()</tt>.
	 */
	protected void unloadSections() {
		for (int page : ownPages)
			invertPageTable.remove(page);
		super.unloadSections();
	}

	/**
	 * Handle a user exception. Called by <tt>UserKernel.exceptionHandler()</tt>
	 * . The <i>cause</i> argument identifies which exception occurred; see the
	 * <tt>Processor.exceptionZZZ</tt> constants.
	 * 
	 * @param cause
	 *            the user exception that occurred.
	 */
	public void handleException(int cause) {
		Processor processor = Machine.processor();
		
		switch (cause) {
			
		case Processor.exceptionTLBMiss:
			handleTLBMiss();
			break;
		
		/*case Processor.exceptionPageFault:
			handlePageFault();
			break;*/
			
		default:
			super.handleException(cause);
			break;
		}
	}
	
	private void handlePageFault() {
	}

	private void handleTLBMiss() {
		Processor processor = Machine.processor();
		int bvaddr = processor.readRegister(processor.regBadVAddr);
		int bvpage = bvaddr / pageSize;
		if (bvpage >= numPages || bvpage < 0)
			super.handleException(processor.exceptionTLBMiss);

		int p = Lib.random(processor.getTLBSize());
		if (usedTLB < processor.getTLBSize())
			p = usedTLB ++;
		else {
			TranslationEntry trans = processor.readTLBEntry(p);
			if (ownPages.contains(trans.ppn))
				pageTable[trans.vpn] = trans;
		}
		processor.writeTLBEntry(p, pageTable[bvpage]); 
	}

	private static final int pageSize = Processor.pageSize;
	private static final char dbgProcess = 'a';
	private static final char dbgVM = 'v';
	
	private static HashMap<Integer, PageIdentifier> invertPageTable;
	
	private int usedTLB = 0;
	private TranslationEntry[] storedEntries = new TranslationEntry[Machine.processor().getTLBSize()];
	
	public static class PageIdentifier implements Comparable {
		public int pid, page;
		
		PageIdentifier(int pid, int page) {
			this.pid = pid;
			this.page = page;
		}

		@Override
		public int compareTo(Object arg0) {
			PageIdentifier p = (PageIdentifier) arg0;
			if (pid != p.pid) return pid < p.pid ? -1 : 1;
			if (page != p.page) return page < p.page ? -1 : 1; 
			return 0;
		}
	}
}
