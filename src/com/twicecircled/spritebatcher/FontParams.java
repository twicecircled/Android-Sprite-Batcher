package com.twicecircled.spritebatcher;

import java.util.ArrayList;

public class FontParams {
	// Hold the parameters of a particular font.
	private int size = 24; // default
	private ArrayList<Integer> charStart = new ArrayList<Integer>();
	private ArrayList<Integer> charEnd = new ArrayList<Integer>();
	private int charUnknown = 32; // defaut = space
	private int padX = 2; // default
	private int padY = 2; // default

	/**
	 * Create a default FontParams object. The resourceId must be that of a
	 * String which points to the font's path. Use FontParams other methods to
	 * change the defaults.
	 * 
	 * @param resourceId
	 */
	public FontParams() {
		charStart.add(32); // default = hyphen
		charEnd.add(126); // default = hyphen
	}

	/**
	 * Change the font's size. Default is 24.
	 * 
	 * @param size
	 * @return this FontParams object
	 */
	public FontParams size(int size) {
		this.size = size;
		return this;
	}

	/**
	 * The ASCII code for the first character that you require for this font.
	 * All characters between charStart and charEnd will be available. The more
	 * characters available, the larger the texture size and therefore the
	 * memory requirements. The defualt settings cover all the standard
	 * characters on a latin alphabet keyboard.
	 * 
	 * @param charStart
	 *            ASCII code for first available char
	 * @return this FontParams object
	 */
	public FontParams charStart(int charStart) {
		this.charStart.add(charStart);
		return this;
	}

	/**
	 * The ASCII code for the last character that you require for this font. All
	 * characters between charStart and charEnd will be available. The more
	 * characters available, the larger the texture size and therefore the
	 * memory requirements. The defualt settings cover all the standard
	 * characters on a latin alphabet keyboard.
	 * 
	 * @param charEnd
	 *            ASCII code for last available char
	 * @return this FontParams object
	 */
	public FontParams charEnd(int charEnd) {
		this.charEnd.add(charEnd);
		return this;
	}

	/**
	 * This character will be substituted if an unsupported char is drawn.
	 * Default is a blank 'space'.
	 * 
	 * @param charUnknown
	 *            ASCII code for unknown char. Default is 32 (space)
	 * @return this FontParams object
	 */
	public FontParams charUnknown(int charUnknown) {
		this.charUnknown = charUnknown;
		return this;
	}

	/**
	 * Used to add padding between characters in the generated sprite atlas.
	 * Increase padX and padY if parts of characters are being chopped off or
	 * clipping into adjacent characters.
	 * 
	 * @param padX
	 *            amount of x padding in pixels. Default is 2.
	 * @return this FontParams object
	 */
	public FontParams padX(int padX) {
		this.padX = padX;
		return this;
	}

	/**
	 * Used to add padding between characters in the generated sprite atlas.
	 * Increase padX and padY if parts of characters are being chopped off or
	 * clipping into adjacent characters.
	 * 
	 * @param padX
	 *            amount of y padding in pixels. Default is 2.
	 * @return this FontParams object
	 */
	public FontParams padY(int padY) {
		this.padY = padY;
		return this;
	}

	protected int getSize() {
		return size;
	}

	protected ArrayList<Integer> getCharStart() {
		return charStart;
	}

	protected ArrayList<Integer> getCharEnd() {
		return charEnd;
	}

	protected int getCharUnknown() {
		return charUnknown;
	}

	protected int getPadX() {
		return padX;
	}

	protected int getPadY() {
		return padY;
	}
}