/**
 * 
 */
package nachos.vm;

import java.util.*;
import nachos.machine.*;
import nachos.threads.Lock;
import nachos.vm.VMProcess.PageIdentifier;

/**
 * @author mrain
 *
 */
public class SwapFile {
	
	public SwapFile() {
	}
	
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 8cb251d... Fucking ass
	public static boolean contains(int pid, int page) {
		return swapAddr.containsKey(new PageIdentifier(pid, page));
	}
	
	public static void remove(int pid, int page) {
<<<<<<< HEAD
		PageIdentifier pageID = new PageIdentifier(pid, page);
		//if (!swapAddr.containsKey(pageID)) return;
		swapAddr.remove(pageID);
	}
	
=======
>>>>>>> parent of 8cb251d... Fucking ass
=======
		if (swap == null) return;
		PageIdentifier pageID = new PageIdentifier(pid, page);
		if (swapAddr.containsKey(pageID)) {
			swapLock.acquire();
			collector.add(swapAddr.get(pageID));
			swapAddr.remove(pageID);
			swapLock.release();
		}
	}
	
>>>>>>> 8cb251d... Fucking ass
	public static boolean write(int pid, int page, byte[] data, int offset) {
		Lib.debug('v', "Process #" + pid + " is writing on swap page " + page);
		if (swap == null) return false;
		PageIdentifier pageID = new PageIdentifier(pid, page);
		swapLock.acquire();
		int addr = counts;
		if (swapAddr.containsKey(pageID))
			addr = swapAddr.get(pageID);
		else if (collector.isEmpty()) ++ counts;
		else addr = collector.pollFirst();
		if (swap.write(addr * pageSize, data, offset, pageSize) != pageSize) {
			collector.add(addr);
			swapLock.release();
			return false;
		}
		swapAddr.put(pageID, addr);
		swapLock.release();
		return true;
	}
	
	public static boolean read(int pid, int page, byte[] data, int offset) {
		Lib.debug('v', "Process #" + pid + " is reading on swap page " + page);
		if (swap == null) return false;
		PageIdentifier pageID = new PageIdentifier(pid, page);
		if (!swapAddr.containsKey(pageID)) return false;
		int addr = swapAddr.get(pageID);
<<<<<<< HEAD
		if (swap.read(addr * pageSize, data, offset, pageSize) != pageSize) return false;
<<<<<<< HEAD
=======
		swapAddr.remove(pageID);
		collector.add(addr);
>>>>>>> parent of 8cb251d... Fucking ass
=======
		swapLock.acquire();
		if (swap.read(addr * pageSize, data, offset, pageSize) != pageSize) {
			swapLock.release();
			return false;
		}
		swapLock.release();
>>>>>>> 8cb251d... Fucking ass
		return true;
	}
	
	public static void close() {
		swap.close();
	}
	
	private static final int pageSize = Machine.processor().pageSize;
	private static TreeMap<PageIdentifier, Integer> swapAddr = new TreeMap<PageIdentifier, Integer>();
	private static TreeSet<Integer> collector = new TreeSet<Integer>();
	private static int counts = 0;
	private static OpenFile swap = VMKernel.fileSystem.open("SWAP", true);
	private static Lock swapLock = new Lock();
}
