package nachos.userprog;

import nachos.machine.*;
import nachos.threads.*;

import java.io.EOFException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Encapsulates the state of a user process that is not contained in its user
 * thread (or threads). This includes its address translation state, a file
 * table, and information about the program being executed.
 * 
 * <p>
 * This class is extended by other classes to support additional functionality
 * (such as additional syscalls).
 * 
 * @see nachos.vm.VMProcess
 * @see nachos.network.NetProcess
 */
public class UserProcess {
	/**
	 * Allocate a new process.
	 */
	public UserProcess() {
		int numPhysPages = Machine.processor().getNumPhysPages();
		pageTable = new TranslationEntry[numPhysPages];
		for (int i = 0; i < numPhysPages; i++)
			pageTable[i] = new TranslationEntry(i, i, false, false, false, false);
		
		fileDescriptors = new TreeMap<Integer, OpenFile>();
		fileDescriptors.put(0, UserKernel.console.openForReading());
		fileDescriptors.put(1, UserKernel.console.openForWriting());
		nextFd = 2;
		
		if (allPages == null) {
			remainedPages = Machine.processor().getNumPhysPages();
			allPages = new TreeSet<Segment> ();
			allPages.add(new Segment(-1, 1));
			allPages.add(new Segment(remainedPages, 1));
		}
		
		pid = nextPID ++;
		processTable.put(pid, this);
		
		childs = new TreeSet<Integer>();
	}

	/**
	 * Allocate and return a new process of the correct class. The class name is
	 * specified by the <tt>nachos.conf</tt> key
	 * <tt>Kernel.processClassName</tt>.
	 * 
	 * @return a new process of the correct class.
	 */
	public static UserProcess newUserProcess() {
		return (UserProcess) Lib.constructObject(Machine.getProcessClassName());
	}

	/**
	 * Execute the specified program with the specified arguments. Attempts to
	 * load the program, and then forks a thread to run it.
	 * 
	 * @param name
	 *            the name of the file containing the executable.
	 * @param args
	 *            the arguments to pass to the executable.
	 * @return <tt>true</tt> if the program was successfully executed.
	 */
	public boolean execute(String name, String[] args) {
		if (!load(name, args))
			return false;

		mainThread = new UThread(this);
		mainThread.setName(name).fork();
		
		++ runningProcesses;
		
		return true;
	}

	/**
	 * Save the state of this process in preparation for a context switch.
	 * Called by <tt>UThread.saveState()</tt>.
	 */
	public void saveState() {
	}

	/**
	 * Restore the state of this process after a context switch. Called by
	 * <tt>UThread.restoreState()</tt>.
	 */
	public void restoreState() {
		Machine.processor().setPageTable(pageTable);
	}

	/**
	 * Read a null-terminated string from this process's virtual memory. Read at
	 * most <tt>maxLength + 1</tt> bytes from the specified address, search for
	 * the null terminator, and convert it to a <tt>java.lang.String</tt>,
	 * without including the null terminator. If no null terminator is found,
	 * returns <tt>null</tt>.
	 * 
	 * @param vaddr
	 *            the starting virtual address of the null-terminated string.
	 * @param maxLength
	 *            the maximum number of characters in the string, not including
	 *            the null terminator.
	 * @return the string read, or <tt>null</tt> if no null terminator was
	 *         found.
	 */
	public String readVirtualMemoryString(int vaddr, int maxLength) {
		Lib.assertTrue(maxLength >= 0);

		byte[] bytes = new byte[maxLength + 1];

		int bytesRead = readVirtualMemory(vaddr, bytes);

		for (int length = 0; length < bytesRead; length++) {
			if (bytes[length] == 0)
				return new String(bytes, 0, length);
		}

		return null;
	}

	/**
	 * Transfer data from this process's virtual memory to all of the specified
	 * array. Same as <tt>readVirtualMemory(vaddr, data, 0, data.length)</tt>.
	 * 
	 * @param vaddr
	 *            the first byte of virtual memory to read.
	 * @param data
	 *            the array where the data will be stored.
	 * @return the number of bytes successfully transferred.
	 */
	public int readVirtualMemory(int vaddr, byte[] data) {
		return readVirtualMemory(vaddr, data, 0, data.length);
	}

	/**
	 * Transfer data from this process's virtual memory to the specified array.
	 * This method handles address translation details. This method must
	 * <i>not</i> destroy the current process if an error occurs, but instead
	 * should return the number of bytes successfully copied (or zero if no data
	 * could be copied).
	 * 
	 * @param vaddr
	 *            the first byte of virtual memory to read.
	 * @param data
	 *            the array where the data will be stored.
	 * @param offset
	 *            the first byte to write in the array.
	 * @param length
	 *            the number of bytes to transfer from virtual memory to the
	 *            array.
	 * @return the number of bytes successfully transferred.
	 */
	public int readVirtualMemory(int vaddr, byte[] data, int offset, int length) {
		Lib.assertTrue(offset >= 0 && length >= 0
				&& offset + length <= data.length);

		byte[] memory = Machine.processor().getMemory();

		// for now, just assume that virtual addresses equal physical addresses
		// holy shit
		if (vaddr < 0 || vaddr >= memory.length)
			return 0;

		length = Math.min(length, memory.length - vaddr);
		
		int amount = 0;
		//System.arraycopy(memory, vaddr, data, offset, amount);
		for (int i = 0; i < length; ++ i) {
			int vpage = vaddr / pageSize;
			if (!pageTable[vpage].valid) break;
			int paddr = pageTable[vpage].ppn * pageSize + (vaddr % pageSize);
			pageTable[vpage].used = true;
			data[offset + i] = memory[paddr];
			vaddr ++; amount ++;
		}

		return amount;
	}

	/**
	 * Transfer all data from the specified array to this process's virtual
	 * memory. Same as <tt>writeVirtualMemory(vaddr, data, 0, data.length)</tt>.
	 * 
	 * @param vaddr
	 *            the first byte of virtual memory to write.
	 * @param data
	 *            the array containing the data to transfer.
	 * @return the number of bytes successfully transferred.
	 */
	public int writeVirtualMemory(int vaddr, byte[] data) {
		return writeVirtualMemory(vaddr, data, 0, data.length);
	}

	/**
	 * Transfer data from the specified array to this process's virtual memory.
	 * This method handles address translation details. This method must
	 * <i>not</i> destroy the current process if an error occurs, but instead
	 * should return the number of bytes successfully copied (or zero if no data
	 * could be copied).
	 * 
	 * @param vaddr
	 *            the first byte of virtual memory to write.
	 * @param data
	 *            the array containing the data to transfer.
	 * @param offset
	 *            the first byte to transfer from the array.
	 * @param length
	 *            the number of bytes to transfer from the array to virtual
	 *            memory.
	 * @return the number of bytes successfully transferred.
	 */
	public int writeVirtualMemory(int vaddr, byte[] data, int offset, int length) {
		Lib.assertTrue(offset >= 0 && length >= 0
				&& offset + length <= data.length);

		byte[] memory = Machine.processor().getMemory();

		// for now, just assume that virtual addresses equal physical addresses
		if (vaddr < 0 || vaddr >= memory.length)
			return 0;

		length = Math.min(length, memory.length - vaddr);
		//System.arraycopy(data, offset, memory, vaddr, amount);
		int amount = 0;

		for (int i = 0; i < length; ++ i) {
			int vpage = vaddr / pageSize;
			if (!pageTable[vpage].valid) break;
			int paddr = pageTable[vpage].ppn * pageSize + (vaddr % pageSize);
			pageTable[vpage].used = true;
			pageTable[vpage].dirty = true;
			memory[paddr] = data[offset + i];
			vaddr ++; amount ++;
		}

		return amount;
	}

	/**
	 * Load the executable with the specified name into this process, and
	 * prepare to pass it the specified arguments. Opens the executable, reads
	 * its header information, and copies sections and arguments into this
	 * process's virtual memory.
	 * 
	 * @param name
	 *            the name of the file containing the executable.
	 * @param args
	 *            the arguments to pass to the executable.
	 * @return <tt>true</tt> if the executable was successfully loaded.
	 */
	private boolean load(String name, String[] args) {
		Lib.debug(dbgProcess, "UserProcess.load(\"" + name + "\")");

		OpenFile executable = ThreadedKernel.fileSystem.open(name, false);
		if (executable == null) {
			Lib.debug(dbgProcess, "\topen failed");
			return false;
		}

		try {
			coff = new Coff(executable);
		} catch (EOFException e) {
			executable.close();
			Lib.debug(dbgProcess, "\tcoff load failed");
			executable.close();
			return false;
		}

		// make sure the sections are contiguous and start at page 0
		numPages = 0;
		for (int s = 0; s < coff.getNumSections(); s++) {
			CoffSection section = coff.getSection(s);
			if (section.getFirstVPN() != numPages) {
				coff.close();
				Lib.debug(dbgProcess, "\tfragmented executable");
				executable.close();
				return false;
			}
			numPages += section.getLength();
		}

		// make sure the argv array will fit in one page
		byte[][] argv = new byte[args.length][];
		int argsSize = 0;
		for (int i = 0; i < args.length; i++) {
			argv[i] = args[i].getBytes();
			// 4 bytes for argv[] pointer; then string plus one for null byte
			argsSize += 4 + argv[i].length + 1;
		}
		if (argsSize > pageSize) {
			coff.close();
			Lib.debug(dbgProcess, "\targuments too long");
			executable.close();
			return false;
		}

		// program counter initially points at the program entry point
		initialPC = coff.getEntryPoint();

		// next comes the stack; stack pointer initially points to top of it
		numPages += stackPages;
		initialSP = numPages * pageSize;

		// and finally reserve 1 page for arguments
		numPages++;

		// allocation physical pages
		if (!allcation_physical_pages()) {
			executable.close();
			return false;
		}
		
		if (!loadSections()) {
			executable.close();
			return false;
		}

		// store arguments in last page
		int entryOffset = (numPages - 1) * pageSize;
		int stringOffset = entryOffset + args.length * 4;

		this.argc = args.length;
		this.argv = entryOffset;

		for (int i = 0; i < argv.length; i++) {
			byte[] stringOffsetBytes = Lib.bytesFromInt(stringOffset);
			Lib
					.assertTrue(writeVirtualMemory(entryOffset,
							stringOffsetBytes) == 4);
			entryOffset += 4;
			Lib
					.assertTrue(writeVirtualMemory(stringOffset, argv[i]) == argv[i].length);
			stringOffset += argv[i].length;
			Lib
					.assertTrue(writeVirtualMemory(stringOffset,
							new byte[] { 0 }) == 1);
			stringOffset += 1;
		}
		
		executable.close();

		return true;
	}
	
	private boolean allcation_physical_pages() {
		if (remainedPages < numPages)
			return false;
		remainedPages -= numPages;
		
		
		Iterator<Segment> iter1 = allPages.iterator();
		iter1.next();
		Iterator<Segment> iter0 = allPages.iterator();
		int temp = numPages;
		while (iter0.hasNext()) {
			Segment seg0 = iter0.next();
			Segment seg1 = iter1.next();
			int length = seg1.offset - seg0.offset - seg0.length;
			if (length == 0) continue;
			int offset = seg0.offset + seg0.length;
			length = Math.min(length, temp);
			Segment newSeg = new Segment(offset, length);
			ownPages.add(newSeg);
			temp -= length;
			if (temp <= 0) break;
		}
		int index = 0;
		for (Segment seg : ownPages) {
			allPages.add(seg);
			for (int i = 0; i < seg.length; ++ i) {
				pageTable[index].ppn = seg.offset + i;
				pageTable[index].valid = true;
				index ++;
			}
		}
		Machine.processor().setPageTable(pageTable);
		return true;
	}

	/**
	 * Allocates memory for this process, and loads the COFF sections into
	 * memory. If this returns successfully, the process will definitely be run
	 * (this is the last step in process initialization that can fail).
	 * 
	 * @return <tt>true</tt> if the sections were successfully loaded.
	 */
	protected boolean loadSections() {
		if (numPages > Machine.processor().getNumPhysPages()) {
			coff.close();
			Lib.debug(dbgProcess, "\tinsufficient physical memory");
			return false;
		}

		// load sections
		for (int s = 0; s < coff.getNumSections(); s++) {
			CoffSection section = coff.getSection(s);

			Lib.debug(dbgProcess, "\tinitializing " + section.getName()
					+ " section (" + section.getLength() + " pages)");

			for (int i = 0; i < section.getLength(); i++) {
				int vpn = section.getFirstVPN() + i;

				// for now, just assume virtual addresses=physical addresses
				section.loadPage(i, pageTable[vpn].ppn);
				if (section.isReadOnly())
					pageTable[vpn].readOnly = true;
			}
		}

		return true;
	}

	/**
	 * Release any resources allocated by <tt>loadSections()</tt>.
	 */
	protected void unloadSections() {
		// TODO
		for (Segment seg : ownPages)
			allPages.remove(seg);
		remainedPages += numPages;
		childs.clear();
		for (Entry<Integer, OpenFile> entry : fileDescriptors.entrySet())
			entry.getValue().close();
		fileDescriptors.clear();
	}

	/**
	 * Initialize the processor's registers in preparation for running the
	 * program loaded into this process. Set the PC register to point at the
	 * start function, set the stack pointer register to point at the top of the
	 * stack, set the A0 and A1 registers to argc and argv, respectively, and
	 * initialize all other registers to 0.
	 */
	public void initRegisters() {
		Processor processor = Machine.processor();

		// by default, everything's 0
		for (int i = 0; i < Processor.numUserRegisters; i++)
			processor.writeRegister(i, 0);

		// initialize PC and SP according
		processor.writeRegister(Processor.regPC, initialPC);
		processor.writeRegister(Processor.regSP, initialSP);

		// initialize the first two argument registers to argc and argv
		processor.writeRegister(Processor.regA0, argc);
		processor.writeRegister(Processor.regA1, argv);
	}

	/**
	 * Handle the halt() system call.
	 */
	private int handleHalt() {

		if (pid != 1) return 0;
		
		Machine.halt();

		Lib.assertNotReached("Machine.halt() did not halt machine!");
		return 0;
	}
	
	private int handleExit(int exitCode) {
		// TODO
		this.exitCode = exitCode;
		normalExit = true;
		unloadSections();
		runningProcesses --;
		if (runningProcesses == 0)
			Machine.halt();
		UThread.finish();
		Lib.assertNotReached();
		return 0;
	}
	
	private int handleExec(int a0, int argc, int argv) {
		String name = readVirtualMemoryString(a0, 256);
		String[] args = new String[argc];
		byte[] buffer = new byte[4];
		for (int i = 0; i < argc; ++ i) {
			readVirtualMemory(argv + (4 * i), buffer);
			int addr = Lib.bytesToInt(buffer, 0);
			args[i] = readVirtualMemoryString(addr, 256);
		}
		UserProcess process = UserProcess.newUserProcess();
		if (!process.execute(name, args))
			return -1;
		childs.add(process.pid);
		return process.pid;
	}
	
	private int handleJoin(int pid, int addr) {
		// TODO
		if (childs.contains(pid) == false)
			return -1;
		UserProcess proc = processTable.get(pid);
		proc.mainThread.join();
		processTable.remove(pid);
		byte[] buffer = new byte[4];
		Lib.bytesFromInt(buffer, 0, 4, proc.exitCode);
		writeVirtualMemory(addr, buffer);
		if (proc.normalExit) return 1;
		else return 0;
	}
	
	private int handleCreate(int addr) {
		String name = readVirtualMemoryString(addr, 256);
		/*if (UserKernel.fileSystem.open(name, false) != null)
			return -1;*/
		OpenFile file = UserKernel.fileSystem.open(name, true);
		if (file == null) return -1;
		fileDescriptors.put(nextFd, file);
		nextFd = nextFd + 1;
		return nextFd - 1;
	}
	
	private int handleOpen(int addr) {
		String name = readVirtualMemoryString(addr, 256);
		OpenFile file = UserKernel.fileSystem.open(name, false);
		if (file == null) return -1;
		fileDescriptors.put(nextFd, file);
		nextFd = nextFd + 1;
		return nextFd - 1;
	}
	
	private int handleRead(int fd, int addr, int size) {
		if (!fileDescriptors.containsKey(fd)) return -1;
		OpenFile file = fileDescriptors.get(fd);
		byte[] buffer = new byte[size];
		int ret = file.read(buffer, 0, size);
		if (ret == -1) return -1;
		return writeVirtualMemory(addr, buffer, 0, ret);
	}

	private int handleWrite(int fd, int addr, int size) {
		if (!fileDescriptors.containsKey(fd)) return -1;
		OpenFile file = fileDescriptors.get(fd);
		byte[] buffer = new byte[size];
		size = readVirtualMemory(addr, buffer);
		if (size == -1) return -1;
		return file.write(buffer, 0, size);
	}

	private int handleClose(int fd) {
		if (!fileDescriptors.containsKey(fd)) return -1;
		OpenFile file = fileDescriptors.get(fd);
		file.close();
		fileDescriptors.remove(fd);
		return 0;
	}
	
	private int handleUnlink(int addr) {
		String name = readVirtualMemoryString(addr, 256);
		if (UserKernel.fileSystem.remove(name)) return 0;
		else return -1;
	}

	private static final int syscallHalt = 0, syscallExit = 1, syscallExec = 2,
			syscallJoin = 3, syscallCreate = 4, syscallOpen = 5,
			syscallRead = 6, syscallWrite = 7, syscallClose = 8,
			syscallUnlink = 9;

	/**
	 * Handle a syscall exception. Called by <tt>handleException()</tt>. The
	 * <i>syscall</i> argument identifies which syscall the user executed:
	 * 
	 * <table>
	 * <tr>
	 * <td>syscall#</td>
	 * <td>syscall prototype</td>
	 * </tr>
	 * <tr>
	 * <td>0</td>
	 * <td><tt>void halt();</tt></td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td><tt>void exit(int status);</tt></td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td><tt>int  exec(char *name, int argc, char **argv);
     * 								</tt></td>
	 * </tr>
	 * <tr>
	 * <td>3</td>
	 * <td><tt>int  join(int pid, int *status);</tt></td>
	 * </tr>
	 * <tr>
	 * <td>4</td>
	 * <td><tt>int  creat(char *name);</tt></td>
	 * </tr>
	 * <tr>
	 * <td>5</td>
	 * <td><tt>int  open(char *name);</tt></td>
	 * </tr>
	 * <tr>
	 * <td>6</td>
	 * <td><tt>int  read(int fd, char *buffer, int size);
     *								</tt></td>
	 * </tr>
	 * <tr>
	 * <td>7</td>
	 * <td><tt>int  write(int fd, char *buffer, int size);
     *								</tt></td>
	 * </tr>
	 * <tr>
	 * <td>8</td>
	 * <td><tt>int  close(int fd);</tt></td>
	 * </tr>
	 * <tr>
	 * <td>9</td>
	 * <td><tt>int  unlink(char *name);</tt></td>
	 * </tr>
	 * </table>
	 * 
	 * @param syscall
	 *            the syscall number.
	 * @param a0
	 *            the first syscall argument.
	 * @param a1
	 *            the second syscall argument.
	 * @param a2
	 *            the third syscall argument.
	 * @param a3
	 *            the fourth syscall argument.
	 * @return the value to be returned to the user.
	 */
	public int handleSyscall(int syscall, int a0, int a1, int a2, int a3) {
		switch (syscall) {
		case syscallHalt:
			return handleHalt();
			
		case syscallExit:
			return handleExit(a0);
			
		case syscallExec:
			return handleExec(a0, a1, a2);
		
		case syscallJoin:
			return handleJoin(a0, a1);
		
		case syscallCreate:
			return handleCreate(a0);
		
		case syscallOpen:
			return handleOpen(a0);
		
		case syscallRead:
			return handleRead(a0, a1, a2);
		
		case syscallWrite:
			return handleWrite(a0, a1, a2);
		
		case syscallClose:
			return handleClose(a0);
		
		case syscallUnlink:
			return handleUnlink(a0);

		default:
			Lib.debug(dbgProcess, "Unknown syscall " + syscall);
			unloadSections();
			mainThread.finish();
			runningProcesses --;
			if (runningProcesses == 0) Machine.halt();
			Lib.assertNotReached("Unknown system call!");
		}
		return 0;
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
		case Processor.exceptionSyscall:
			int result = handleSyscall(processor.readRegister(Processor.regV0),
					processor.readRegister(Processor.regA0), processor
							.readRegister(Processor.regA1), processor
							.readRegister(Processor.regA2), processor
							.readRegister(Processor.regA3));
			processor.writeRegister(Processor.regV0, result);
			processor.advancePC();
			break;

		default:
			Lib.debug(dbgProcess, "Unexpected exception: "
					+ Processor.exceptionNames[cause]);
			Lib.assertNotReached("Unexpected exception");
		}
	}

	/** The program being run by this process. */
	protected Coff coff;

	/** This process's page table. */
	protected TranslationEntry[] pageTable;
	/** The number of contiguous pages occupied by the program. */
	protected int numPages;

	/** The number of pages in the program's stack. */
	protected final int stackPages = Config.getInteger("Processor.numStackPages", 8);

	private int initialPC, initialSP;
	private int argc, argv;

	private static final int pageSize = Processor.pageSize;
	private static final char dbgProcess = 'a';
	
	protected TreeMap<Integer, OpenFile> fileDescriptors;
	protected int nextFd;
	
	private LinkedList<Segment> ownPages = new LinkedList<Segment>();
	
	/** refer to the child processes **/
	protected TreeSet<Integer> childs;
	
	protected int pid, exitCode;
	protected boolean normalExit = false;
	
	protected UThread mainThread;
	
	
	private static class Segment implements Comparable {
		int offset;
		int length;
		
		public Segment(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}
		
		@Override
		public int compareTo(Object arg0) {
			Segment obj = (Segment) arg0;
			return this.offset < obj.offset ? -1 : this.offset > obj.offset ? 1 : 0;
		}
	}
	
	private static TreeSet<Segment> allPages = null;
	private static int remainedPages;
	private static int nextPID = 1;
	private static int runningProcesses = 0;
	public static TreeMap<Integer, UserProcess> processTable = new TreeMap<Integer, UserProcess>();
}
