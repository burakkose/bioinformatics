package com.bioinformatics.globalignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Stack;

/**
 * 
 * @author BURAK KÖSE
 *
 */

public class GlobalAlignment {

	private String inputString1;
	private String inputString2;

	private final Node[][] solveMatrix;

	PrintWriter writer = null;

	public GlobalAlignment() {

		try {
			writer = new PrintWriter("out.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		readFile("inp.txt");// read input file for input strings

		final int lenString1 = inputString1.length() + 1;
		final int lenString2 = inputString2.length() + 1;

		solveMatrix = new Node[lenString2][lenString1];

		for (int i = 0; i < lenString2; i++) {
			for (int j = 0; j < lenString1; j++) {
				solveMatrix[i][j] = new Node();
			}
		}
		needlemanWunsch(lenString1, lenString2); // Needleman - Wunsch algorithm
		traceback(lenString2 - 1, lenString1 - 1); // find all possibility
	}

	/**
	 * This is Needleman-Wunsch Algorithm
	 * 
	 * @param lenString1
	 *            first string length + 1
	 * @param lenString2
	 *            second string length + 1
	 */
	private void needlemanWunsch(final int lenString1, final int lenString2) {
		// Scores
		final int gap      = -1;   // gap score
		final int match    = 2;   // match score
		final int mismatch = -1; // mismatch score
		final int exMatch  = 5; // extension of a match score

		// initialize matrix
		for (int i = 0; i < lenString2; i++) {
			solveMatrix[i][0].setValue(gap * i);
			solveMatrix[i][0].pushDirection('n');
		}
		for (int i = 0; i < lenString1; i++) {
			solveMatrix[0][i].setValue(gap * i);
			solveMatrix[0][i].pushDirection('w');
		}

		boolean matchControl = false;

		for (int i = 1; i < lenString2; i++) {
			for (int j = 1; j < lenString1; j++) {

				int north = solveMatrix[i - 1][j].getValue() + gap;
				int west = solveMatrix[i][j - 1].getValue() + gap;
				int northWest = solveMatrix[i - 1][j - 1].getValue();

				if (inputString1.charAt(j - 1) == inputString2.charAt(i - 1)) {

					Node controlNode = solveMatrix[i - 1][j - 1];
					matchControl = true;

					if (controlNode.isMatchWay()) {
						northWest += exMatch;
					} else {
						northWest += match;
					}

				} else {
					northWest += mismatch;
					matchControl = false;
				}

				final int max = Math.max(Math.max(northWest, west), north);

				solveMatrix[i][j].setValue(max);

				if (northWest == max) {
					solveMatrix[i][j].pushDirection('c');
					if (matchControl)
						solveMatrix[i][j].setMatchWay(true); // for extension match score
				}
				if (west == max) {
					solveMatrix[i][j].pushDirection('w');
				}
				if (north == max) {
					solveMatrix[i][j].pushDirection('n');
				}
			}
		}
		writeFile(solveMatrix[lenString2 - 1][lenString1 - 1].getValue()); // print total score
	}

	/**
	 * This is finding all possibility algorithm like Backtracking algorithm.
	 * 
	 * @param i
	 *            matrix last X position
	 * @param j
	 *            matrix last Y position
	 */
	private void traceback(int i, int j) {

		final Stack<Point> stack = new Stack<Point>();
		stack.push(new Point(i, j, "", "")); // first push

		do {

			final StringBuilder lastString1 = new StringBuilder(), lastString2 = new StringBuilder();

			final Point p = stack.pop();
			i = p.getX();
			j = p.getY();

			lastString1.append(p.getStoreString1()); // get back last string
			lastString2.append(p.getStoreString2()); // ""

			do {

				final Node n = solveMatrix[i][j];
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
				case 'w':
					// go west
					lastString1.append(inputString1.charAt(--j));
					lastString2.append('-');
					break;
				case 'n':
					// go north
					lastString1.append('-');
					lastString2.append(inputString2.charAt(--i));
					break;
				}

			} while (i > 0 || j > 0);

			writeFile(lastString1.reverse().toString(), lastString2.reverse()
					.toString());

		} while (stack.size() > 0);

		writer.close(); // close out file.
	}

	private void readFile(final String location) {

		Scanner input = null;

		try {
			input = new Scanner(new File(location));
			inputString1 = input.nextLine();
			inputString2 = input.nextLine();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			input.close();
		}

	}

	private void writeFile(String str1, String str2) {

		writer.println(str1);
		writer.println(str2);
		writer.println("");
	}

	private void writeFile(int point) {

		writer.println(point);
		writer.println("");
	}

}
