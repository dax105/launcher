package com.jboudny.launcher;

public class Version {

	public final int i0, i1, i2;
	
	public Version(int i0, int i1, int i2) {
		this.i0 = i0;
		this.i1 = i1;
		this.i2 = i2;
	}
	
	public boolean isNewerThan(Version toCompare) {
		if (toCompare == null) {
			return true;
		}
		
		if (this.i0 < toCompare.i0) {
			return false;
		} else if (this.i0 > toCompare.i0) {
			return true;
		}
		
		if (this.i1 < toCompare.i1) {
			return false;
		} else if (this.i1 > toCompare.i1) {
			return true;
		}
		
		if (this.i2 <= toCompare.i2) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return i0 + "." + i1 + "." + i2;
	}
	
	public static Version parseVersion(String s) throws Exception {
		
		String[] indexes = s.split("\\.");
	
		if (indexes.length != 3) {
			throw new Exception("Not a valid version!");
		} 
		
		return new Version(Integer.parseInt(indexes[0]), Integer.parseInt(indexes[1]), Integer.parseInt(indexes[2]));
		
	}
	
}
