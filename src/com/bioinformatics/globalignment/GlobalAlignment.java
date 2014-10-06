package com.bioinformatics.globalignment;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

class Point {
	private final int	x, y;
	private final String	storeString1, storeString2;

	public Point(int x, int y, String s1, String s2) {
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

class Node {
	private int				value;
	private Queue<String>	direction	= new LinkedList<String>();

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public char popDirection() {
		return direction.poll().charAt(0);
	}

	public char peekDirection() {
		return direction.peek().charAt(0);
	}

	public int sizeDirection() {
		return direction.size();
	}

	public boolean pushDirection(String c) {
		return direction.add(c);
	}
}

public class GlobalAlignment {
	private final String	inputString1;
	private final String	inputString2;

	private final Node[][]	solveMatrix;

	public GlobalAlignment(String str1, String str2) {
		inputString1 = str1;
		inputString2 = str2;

		int lenString1 = inputString1.length() + 1;
		int lenString2 = inputString2.length() + 1;

		solveMatrix = new Node[lenString2][lenString1];

		for (int i = 0; i < lenString2; i++) {
			for (int j = 0; j < lenString1; j++) {
				solveMatrix[i][j] = new Node();
			}
		}
		needlemanWunsch(lenString1, lenString2); // Needleman - Wunsch algorithm
		backtracking(lenString2 - 1, lenString1 - 1); // find all possibility
	}

	private void needlemanWunsch(int lenString1, int lenString2) {
		int gap = -1;
		int match = 1;
		for (int i = 0; i < lenString2; i++) {
			solveMatrix[i][0].setValue(gap * i);
		}
		for (int i = 0; i < lenString1; i++) {
			solveMatrix[0][i].setValue(gap * i);
		}
		for (int i = 1; i < lenString2; i++) {
			for (int j = 1; j < lenString1; j++) {
				if (inputString1.charAt(j - 1) == inputString2.charAt(i - 1)) {
					match = 1;
				} else {
					match = -1;
				}
				int northWest = solveMatrix[i - 1][j - 1].getValue() + match;
				int north = solveMatrix[i - 1][j].getValue() + gap;
				int west = solveMatrix[i][j - 1].getValue() + gap;

				int max = Math.max(Math.max(northWest, west), north);

				solveMatrix[i][j].setValue(max);

				if (northWest == max) {
					solveMatrix[i][j].pushDirection("c");
				}
				if (west == max) {
					solveMatrix[i][j].pushDirection("l");
				}
				if (north == max) {
					solveMatrix[i][j].pushDirection("u");
				}
			}
		}
	}

	private void backtracking(int i, int j) {
		Stack<Point> stack = new Stack<Point>();
		stack.push(new Point(i, j, "", ""));

		do {
			StringBuilder lastString1 = new StringBuilder(), lastString2 = new StringBuilder();

			Point p = (stack.pop());
			i = p.getX();
			j = p.getY();

			lastString1.append(p.getStoreString1());
			lastString2.append(p.getStoreString2());

			do {
				Node n = solveMatrix[i][j];
				char forSwitchChar;

				if (n.sizeDirection() > 1) {
					stack.push(new Point(i, j, lastString1.toString(),
							lastString2.toString()));
					forSwitchChar = n.popDirection();
				} else {
					forSwitchChar = n.peekDirection();
				}

				switch (forSwitchChar) {
					case 'c':
						// go northwest
						lastString1.append(inputString1.charAt(--j));
						lastString2.append(inputString2.charAt(--i));
						break;
					case 'l':
						// go west
						lastString1.append(inputString1.charAt(--j));
						lastString2.append('-');
						break;
					case 'u':
						// go north
						lastString1.append('-');
						lastString2.append(inputString2.charAt(--i));
						break;
				}
			} while (i > 0 || j > 0);
			System.out.println(lastString1.reverse().toString() + " + "
					+ lastString2.reverse().toString());
		} while (stack.size() > 0);
	}
}