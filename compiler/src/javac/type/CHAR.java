package javac.type;

public final class CHAR extends Type {

	private static CHAR instance = null;

	public static synchronized CHAR getInstance() {
		if (instance == null) {
			instance = new CHAR();
		}
		instance.size = 1;
		return instance;
	}

	private CHAR() {
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof CHAR;
	}

	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public boolean isRecord() {
		return false;
	}
}
