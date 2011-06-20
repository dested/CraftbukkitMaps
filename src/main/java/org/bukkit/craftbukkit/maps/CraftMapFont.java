package org.bukkit.craftbukkit.maps;

import org.bukkit.maps.MapFont;

public class CraftMapFont implements MapFont {
	private String name;
	private final int characterSize;
	private final int characterHeight;
	private final int characterWidth;
	private final int spaceWidth;
	private byte[][] chars;
	private String allowedChars;

	public CraftMapFont(String name, int characterSize, int characterWidth,
			int characterHeight, int spaceWidth, String allowedChars) {
		this.name = name;
		this.characterSize = characterSize;
		this.characterHeight = characterHeight;
		this.characterWidth = characterWidth;
		this.spaceWidth = spaceWidth;
		this.allowedChars = allowedChars;
	}

	public String getName() {
		return name;
	}

	public int getCharacterSize() {
		return characterSize;
	}

	public int getCharacterHeight() {
		return characterHeight;
	}

	public int getCharacterWidth() {
		return characterWidth;
	}

	public int getSpaceWidth() {
		return spaceWidth;
	}

	public boolean setChars(byte[][] chars) {
		this.chars = chars;
		return true;
	}

	public byte[][] getChars() {
		return chars;
	}

	public boolean characterAllowed(Character ch) {
		return allowedChars.indexOf(ch) != -1;
	}

	public int getCharIndex(Character ch) {
		return allowedChars.indexOf(ch);
	}

	public void setBytes(byte[][] bytes) {
		chars = bytes;
	}

	public byte[][] getBytes() {
		return chars;
	}

	public void setName(String name) {
		this.name = name;

	}
}
