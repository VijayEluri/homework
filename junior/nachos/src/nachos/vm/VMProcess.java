package nachos.vm;

import nachos.machine.Machine;
import nachos.machine.Processor;
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

	/**
	 * Save the state of this process in preparation for a context switch.
	 * Called by <tt>UThread.saveState()</tt>.
	 */
	public void saveState() {
		super.saveState();
	}

	/**
	 * Restore the state of this process after a context switch. Called by
	 * <tt>UThread.restoreState()</tt>.
	 */
	public void restoreState() {
		super.restoreState();
	}

	/**
	 * Initializes page tables for this process so that the executable can be
	 * demand-paged.
	 * 
	 * @return <tt>true</tt> if successful.
	 */
	protected boolean loadSections() {
		return super.loadSections();
	}

	/**
	 * Release any resources allocated by <tt>loadSections()</tt>.
	 */
	protected void unloadSections() {
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
		default:
			super.handleException(cause);
			break;
		}
	}
<<<<<<< HEAD
	
<<<<<<< HEAD
=======
	private void handlePageFault() {
	}

>>>>>>> parent of 8cb251d... Fucking ass
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
=======
>>>>>>> parent of 050372b... nachos phase 3 task 1

	private static final int pageSize = Processor.pageSize;
	private static final char dbgProcess = 'a';
	private static final char dbgVM = 'v';
}
