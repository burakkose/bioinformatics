package com.bioinformatics.globalignment;

public class Point {

	private final int x, y;
	private final String storeString1, storeString2;

	public Point(final int x, final int y, final String s1, final String s2) {
		this.x = x;
		this.y = y;
		storeString1 = s1;
		storeString2 = s2;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getStoreString1() {
		return storeString1;
	}

	public String getStoreString2() {
		return storeString2;
	}

}
