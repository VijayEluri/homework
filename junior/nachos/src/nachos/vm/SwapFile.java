/**
 * 
 */
package nachos.vm;

import java.util.*;
import nachos.machine.*;
import nachos.vm.VMProcess.PageIdentifier;

/**
 * @author mrain
 *
 */
public class SwapFile {
	
	public SwapFile() {
	}
	
	public static boolean write(int pid, int page, byte[] data, int offset) {
		if (swap == null) return false;
		PageIdentifier pageID = new PageIdentifier(pid, page);
		int addr = counts;
		if (collector.isEmpty()) ++ counts;
		else addr = collector.pollFirst();
		if (swap.write(addr * pageSize, data, offset, pageSize) != pageSize) {
			collector.add(addr);
			return false;
		}
		swapAddr.put(pageID, addr);
		return true;
	}
	
	public static boolean read(int pid, int page, byte[] data, int offset) {
		if (swap == null) return false;
		PageIdentifier pageID = new PageIdentifier(pid, page);
		if (!swapAddr.containsKey(pageID)) return false;
		int addr = swapAddr.get(pageID);
		if (swap.read(addr * pageSize, data, offset, pageSize) != pageSize) return false;
		swapAddr.remove(pageID);
		collector.add(addr);
		return true;
	}
	
	private static final int pageSize = Machine.processor().pageSize;
	private static TreeMap<PageIdentifier, Integer> swapAddr = new TreeMap<PageIdentifier, Integer>();
	private static TreeSet<Integer> collector = new TreeSet<Integer>();
	private static int counts = 0;
	private static OpenFile swap = VMKernel.fileSystem.open("SWAP", true);
}
