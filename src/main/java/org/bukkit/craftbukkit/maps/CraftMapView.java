package org.bukkit.craftbukkit.maps;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.WorldMap;

import org.bukkit.entity.Player;
import org.bukkit.maps.BaseRegisteredException;
import org.bukkit.maps.ContextualMapRenderer;
import org.bukkit.maps.MapCanvas;
import org.bukkit.maps.MapDrawer;
import org.bukkit.maps.MapPrintOrder;
import org.bukkit.maps.MapRenderer;
import org.bukkit.maps.MapRendererBase;
import org.bukkit.maps.MapStringDrawer;
import org.bukkit.maps.MapType;
import org.bukkit.maps.MapView;
import org.bukkit.maps.MapViewManager;
import org.bukkit.maps.RenderPriority;
import org.bukkit.plugin.Plugin;

import com.sun.org.apache.bcel.internal.generic.ISUB;

public class CraftMapView implements MapView {
	private boolean _isVirtual;
	private boolean _shouldRenderMap;
	private int _printRate = 1;
	private MapCanvas _canvas;
	private MapRendererBase _baseRenderer;
	private ArrayList<MapRendererBase> _overlays = new ArrayList<MapRendererBase>();
	private CraftMapDrawer _drawer;
	private CraftMapStringDrawer _stringDrawer;
	private MapViewManager _manager;
	MapPrintOrder _printOrder = MapPrintOrder.Random;

	WorldMap _worldMap;

	public CraftMapView(WorldMap worldMap) {
		_worldMap = worldMap;
		_manager = CraftMapViewManager.CreateRegular(this);
		_canvas = new CraftMapCanvas(this);
		_drawer = new CraftMapDrawer(_canvas);
		_stringDrawer = new CraftMapStringDrawer(_canvas);

	}

	public boolean isVirtual() {
		return _isVirtual;
	}

	public short getId() {
		return Short.parseShort(_worldMap.a.replace("map_", ""));
	}

	public void setScale(byte scale) {
		_worldMap.e = scale;

	}

	public byte getScale() {
		return _worldMap.e;
	}

	public int getCenterX() {
		return _worldMap.b;
	}

	public int getCenterZ() {
		return _worldMap.c;
	}

	public void setCenterX(int x) {
		_worldMap.b = x;

	}

	public void setCenterZ(int z) {
		_worldMap.c = z;

	}

	public void registerOverlay(MapRenderer renderer, RenderPriority priority,
			Plugin plugin) {
		if (_overlays.size() == 0) {
			_manager.createOverlayBuffer();
		}
		_overlays.add(renderer);
		if (_manager.getContextual()) {
			// throw i think
		}
	}

	public void registerOverlay(ContextualMapRenderer renderer,
			RenderPriority priority, Plugin plugin) {

		if (_overlays.size() == 0) {
			_manager.createOverlayBuffer();
		}

		_overlays.add(renderer);
		if (!_manager.getContextual()) {
			// throw i think
		}

	}

	public void registerVirtualBase(MapRenderer renderer, Plugin plugin) {
		_baseRenderer = renderer;
		setVirtual(true);

		_manager = CraftMapViewManager.CreateRegular(this);
		_canvas.setManager(_manager);
	}

	public void registerVirtualBase(ContextualMapRenderer renderer,
			Plugin plugin) {
		setVirtual(true);
		_baseRenderer = renderer;
		_manager = CraftMapViewManager.CreateUserSpecific(this);

		_canvas.setManager(_manager);
	}

	public void setRenderMap(boolean renderMap) {
		_shouldRenderMap = renderMap;
	}

	public boolean shouldRenderingMap() {
		return _shouldRenderMap;
	}

	public void setRate(int rate) {
		_printRate = rate;
	}

	public int getRate() {
		return _printRate;
	}

	public MapDrawer getDrawer() {
		return _drawer;
	}

	public MapRendererBase getBase() {
		return _baseRenderer;
	}

	public List<MapRendererBase> getOverlays() {
		return _overlays;
	}

	public void setVirtual(boolean b) {
		_isVirtual = b;
	}

	public MapStringDrawer getStringDrawer() {
		return _stringDrawer;
	}

	public MapCanvas getCanvas(MapType type) {
		_canvas.setMapType(type);
		return _canvas;
	}

	public boolean isContextual() {
		return _manager.getContextual();
	}

	public void removeUser(Player player) {
		_manager.removeUserBuffer(player);
	}

	public void addUser(Player player) {
		_manager.pushUserBuffer(player, new CraftMapViewPiece());
	}

	public MapViewManager getMapViewManager() {
		return _manager;
	}

	public void setPrintOrder(MapPrintOrder order) {
		_printOrder = order;
	}

	public MapPrintOrder getPrintOrder() {
		return _printOrder;
	}

}
