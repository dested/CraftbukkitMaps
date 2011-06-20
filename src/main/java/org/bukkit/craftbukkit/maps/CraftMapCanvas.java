package org.bukkit.craftbukkit.maps;

import java.awt.Color;

import org.bukkit.entity.Player;
import org.bukkit.maps.MapCursorCollection;
import org.bukkit.maps.MapView;
import org.bukkit.maps.MapCanvas;
import org.bukkit.maps.MapType;
import org.bukkit.maps.MapViewManager;

public class CraftMapCanvas implements MapCanvas {

	private MapView _craftMap;
	private Player _currentPlayer;
	private MapViewManager _manager;

	public MapView getMapView() {
		return _craftMap;
	}

	public void setMapView(MapView c) {
		_craftMap = c;
		_cursorCollection = new CraftMapCursorCollection(this);
	}

	public CraftMapCanvas(CraftMapView craftMap) {
		_craftMap = craftMap;
		_cursorCollection = new CraftMapCursorCollection(this);

		setManager(_craftMap.getMapViewManager());
	}

	public void setManager(MapViewManager manager) {
		_manager = manager;
	}

	public Player getCurrentUser() {
		return _currentPlayer;
	}

	public void setCurrentUser(Player player) {
		_currentPlayer = player;
	}

	public void setPixel(int x, int y, byte color) {
		if (x < 0 || y < 0 || x > 127 || y > 127)
			return;

		int ind = y * 128 + x;
		if (_curRenderType == MapType.Base) {
			if (_manager.getContextual())
				_manager.getUserPiece(_currentPlayer).getBaseBuffer()[ind] = color;
			else
				_manager.getBasePiece().getBaseBuffer()[ind] = color;
		} else {
			if (_manager.getContextual())
				_manager.getUserPiece(_currentPlayer).getOverlayBuffer()[ind] = color;
			else
				_manager.getBasePiece().getOverlayBuffer()[ind] = color;
		}
		if (!_craftMap.isVirtual())// virtual dirty tracking is handled after
									// all drawing is done
			markDirty(x, y, y);
	}

	public byte getPixel(int x, int y) {
		int ind = y * 128 + x;

		byte[] overlay;
		byte[] bottom;
		if (_manager.getContextual()) {

			if ((overlay = _manager.getUserPiece(_currentPlayer)
					.getOverlayBuffer()) == null)
				return _manager.getUserPiece(_currentPlayer).getBaseBuffer()[ind];
			bottom = _manager.getBasePiece().getBaseBuffer();
		} else {

			if ((overlay = _manager.getBasePiece().getOverlayBuffer()) == null)
				return _manager.getBasePiece().getBaseBuffer()[ind];
			bottom = _manager.getBasePiece().getBaseBuffer();
		}
		return overlay[ind] < 3 ? bottom[ind] : overlay[ind];
	}

	public byte getOverlayPixel(int x, int y) {
		int ind = y * 128 + x;
		if (_manager.getContextual())
			return _manager.getUserPiece(_currentPlayer).getOverlayBuffer()[ind];
		else
			return _manager.getBasePiece().getOverlayBuffer()[ind];
	}

	public byte getBasePixel(int x, int y) {
		int ind = y * 128 + x;
		if (_manager.getContextual())
			return _manager.getUserPiece(_currentPlayer).getBaseBuffer()[ind];
		else
			return _manager.getBasePiece().getBaseBuffer()[ind];
	}

	public void markDirty(int x, int startY, int endY) {
		if (_currentPlayer == null)
			((CraftMapView) _craftMap)._worldMap.a(x, startY, endY);
		else
			((CraftMapView) _craftMap)._worldMap.a(x, startY, endY,
					_currentPlayer);
	}

	public void invalidate() {
		for (int x = 0; x < 128; x++) {
			markDirty(x, 0, 128);
		}
	}

	MapType _curRenderType;

	public MapType getRenderType() {
		return _curRenderType;
	}

	public void setMapType(MapType type) {
		_curRenderType = type;

	}

	MapCursorCollection _cursorCollection;

	public MapCursorCollection getCursorCollection() {
		return _cursorCollection;
	}

	public void setPixel(int x, int y, Color rgbColor) {
		setPixel(x, y, matchColor(rgbColor));
	}

	private double getDistance(Color c1, Color c2) {
		double rmean = (c1.getRed() + c2.getRed()) / 2.0;
		double r = c1.getRed() - c2.getRed();
		double g = c1.getGreen() - c2.getGreen();
		int b = c1.getBlue() - c2.getBlue();
		double weightR = 2 + rmean / 256.0;
		double weightG = 4.0;
		double weightB = 2 + (255 - rmean) / 256.0;
		return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);
	}

	private static final Color[] colors = new Color[] { null, null, null, null,
			new Color(89, 125, 39), new Color(109, 153, 48),
			new Color(127, 178, 56), new Color(109, 153, 48),
			new Color(174, 164, 115), new Color(213, 201, 140),
			new Color(247, 233, 163), new Color(213, 201, 140),
			new Color(117, 117, 117), new Color(144, 144, 144),
			new Color(167, 167, 167), new Color(144, 144, 144),
			new Color(180, 0, 0), new Color(220, 0, 0), new Color(255, 0, 0),
			new Color(220, 0, 0), new Color(112, 112, 180),
			new Color(138, 138, 220), new Color(160, 160, 255),
			new Color(138, 138, 220), new Color(117, 117, 117),
			new Color(144, 144, 144), new Color(167, 167, 167),
			new Color(144, 144, 144), new Color(0, 87, 0),
			new Color(0, 106, 0), new Color(0, 124, 0), new Color(0, 106, 0),
			new Color(180, 180, 180), new Color(220, 220, 220),
			new Color(255, 255, 255), new Color(220, 220, 220),
			new Color(115, 118, 129), new Color(141, 144, 158),
			new Color(164, 168, 184), new Color(141, 144, 158),
			new Color(129, 74, 33), new Color(157, 91, 40),
			new Color(183, 106, 47), new Color(157, 91, 40),
			new Color(79, 79, 79), new Color(96, 96, 96),
			new Color(112, 112, 112), new Color(96, 96, 96),
			new Color(45, 45, 180), new Color(55, 55, 220),
			new Color(64, 64, 255), new Color(55, 55, 220),
			new Color(73, 58, 35), new Color(89, 71, 43),
			new Color(104, 83, 50), new Color(89, 71, 43) };

	public byte matchColor(Color color) {
		double closestDistance = -1;
		byte closestIndex = 0;

		for (byte index = 0; index < colors.length; index++) {
			Color testColor = colors[index];

			if (testColor == null) {
				continue;
			}

			double distance = getDistance(testColor, color);

			if (closestDistance == -1 || distance < closestDistance) {
				closestIndex = index;
				closestDistance = distance;
			}
		}
		return closestIndex;
	}
}