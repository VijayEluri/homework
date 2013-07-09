/**
 * 
 */
package fatworm.storage;

import java.io.IOException;
import fatworm.value.*;
import fatworm.value.FloatNumber;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import fatworm.db.Schema;

/**
 * @author mrain
 * TODO
 */
public class RecordPage {
	public BufferedFile file;
	public int position;
	public int next;
	public int prev;
	byte[] data;
	
	public RecordPage(BufferedFile file, int position, int prev, int next, byte[] data) {
		this.file = file;
		this.position = position;
		this.prev = prev;
		this.next = next;
		this.data = data;
	}
	
	public RecordPage(BufferedFile file, int position, int prev, int next, List<Value> values) {
		this.file = file;
		this.position = position;
		this.prev = prev;
		this.next = next;
		acquire(values);
	}
	
	public void acquire(List<Value> values) {
		int length = 0;
		for (Value v : values)
			length += v.length();
		ByteBuffer bb = ByteBuffer.allocate(length);
		for (Value v : values)
			bb.put(v.toByteArray());
		this.data = bb.array();
	}
	
	public void commit() throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(data.length + 12);
		bb.putInt(prev);
		bb.putInt(data.length);
		bb.putInt(next);
		bb.put(data);
		file.write(bb.array(), position, data.length + 12);
	}
	
	public List<Value> resolve() {
		ByteBuffer bb = ByteBuffer.wrap(data);
		List<Value> ret = new ArrayList<Value>();
		while (bb.remaining() > 0) {
			byte typeSpecifier = bb.get();
			switch (typeSpecifier) {
				case 0: // INT
					ret.add(new Int(bb.getInt()));
					break;
				case 1: // Bool
					ret.add(new Bool(bb.get() == 1));
					break;
				case 2: // Float
					ret.add(new FloatNumber(bb.getFloat()));
					break;
				case 3: { // Varchar 
					int len = bb.getInt();
					//System.out.println(len);
					String t = "";
					for (int i = 0; i < len; ++ i) {
						t += (char) bb.get();
					}
					ret.add(new Varchar(t));
					break;
				}
				
				case 4: { // Time 
					int len = bb.getInt();
					String t = "";
					for (int i = 0; i < len; ++ i) {
						t += (char) bb.get();
					}
					ret.add(new Time(t));
					break;
				}
				
				case 10:
					ret.add(new Null());
					break;
					
				default:
					throw new RuntimeException("Unknown type specifier " + typeSpecifier);
			}
		}
		return ret;
	}
	
	public int getNext() {
		return next;
	}
	
	public int getPrevious() {
		return prev;
	}
	
	public int length() {
		return data.length + 12;
	}
	
	public int getPosition() {
		return position;
	}
}
