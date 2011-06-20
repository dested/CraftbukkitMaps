package org.bukkit.craftbukkit.maps;

import java.util.ArrayList;

import org.bukkit.maps.MapCursor;
import org.bukkit.maps.MapCursorCollection;

public class CraftMapCursorCollection implements MapCursorCollection {

	private ArrayList<MapCursor> _cursors = new ArrayList<MapCursor>();
	private CraftMapCanvas _craftMap;

	public CraftMapCursorCollection(CraftMapCanvas craftMap) {
		_craftMap = craftMap;
	}

	public MapCursor getCursor(int cursorIndex) {
		if (cursorIndex >= _cursors.size())
			return null;
		return _cursors.get(cursorIndex);
	}

	public int cursorLength() {
		return _cursors.size();
	}

	public void addCursor(MapCursor cursor) {
		_cursors.add(cursor);
	}

	public void removeCursor(int cursorIndex) {
		_cursors.remove(cursorIndex);
	}

	public MapCursor addCursor(int x, int y, byte direction, byte type) {
		MapCursor cu = new CraftMapCursor(false);
		cu.setCurstorType(type);
		cu.setDirection(direction);
		cu.setX(x);
		cu.setY(y);

		_cursors.add(cu);
		return cu;
	}

	public MapCursor addCursor(int x, int y, byte direction, byte type,
			boolean visible) {
		MapCursor cu = new CraftMapCursor(false);
		cu.setCurstorType(type);
		cu.setDirection(direction);
		cu.setX(x);
		cu.setY(y);
		cu.setVisible(visible);
		_cursors.add(cu);
		return cu;
	}

	public void insertCursor(int index, MapCursor cu) {
		_cursors.add(index,cu);
	}

}
