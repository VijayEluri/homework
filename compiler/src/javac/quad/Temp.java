package javac.quad;

public class Temp {

	public Temp() {
		num = count++;
		isTemp = false;
	}
	
	public Temp(boolean t) {
		num = count ++;
		isTemp = t;
	}
	
	public boolean equal(Temp n) {
		return num == n.num;
	}
	
	public boolean equals(Temp n) {
		return num == n.num;
	}
	
	@Override
	public String toString() {
		return "t" + num;
	}
	public int num;
	public static int count = 0;
	public boolean isTemp;
}
