package org.bukkit.craftbukkit.maps;

import org.bukkit.maps.MapCursor;

public class CraftMapCursor implements MapCursor {

	boolean _isDefault;
	int _x;
	int _y;
	byte _direction;
	byte _type;
	boolean _visible;

	 CraftMapCursor(boolean b) {
		_isDefault = b;
	}
	public CraftMapCursor() {
		_isDefault = false;
	}

	public boolean isDefault() {
		return _isDefault;
	}

	public int getX() {
		// TODO Auto-generated method stub
		return _x;
	}

	public int getY() {
		// TODO Auto-generated method stub
		return _y;
	}

	public byte getDirection() {
		// TODO Auto-generated method stub
		return _direction;
	}

	public byte getCursorType() {
		// TODO Auto-generated method stub
		return _type;
	}

	public boolean getVisible() {
		// TODO Auto-generated method stub
		return _visible;
	}

	public void setX(int x) {
		_x = x;

	}

	public void setY(int y) {
		_y = y;
	}

	public void setDirection(byte direction) {
		_direction = direction;
	}

	public void setCurstorType(byte cursorType) {
		_type = cursorType;
	}

	public void setVisible(boolean visible) {
		_visible = visible;
	}

}