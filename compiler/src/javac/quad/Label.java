package javac.quad;

public class Label {
	String name;
	public boolean avaiable = false;
	public Label() {
		name = "_L" + count++;
	}
	
	public Label(String s) {
		name = s;
	}
	
	public String toString() {
		return name;
	}
	
	public boolean equals(Label ano) {
		return name == ano.name;
	}
	
	static int count = 0;
}
