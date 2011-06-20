package org.bukkit.craftbukkit.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.WorldMap;
import net.minecraft.server.WorldMapHumanTracker;
import net.minecraft.server.WorldMapOrienter;

import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Type;
import org.bukkit.event.map.MapInitializeEvent;
import org.bukkit.maps.ContextualMapRenderer;
import org.bukkit.maps.MapCanvas;
import org.bukkit.maps.MapCursor;
import org.bukkit.maps.MapCursorCollection;
import org.bukkit.maps.MapManager;
import org.bukkit.maps.MapRenderer;
import org.bukkit.maps.MapRendererBase;
import org.bukkit.maps.MapType;
import org.bukkit.maps.MapView;

public class CraftMapManager implements MapManager {
	static CraftMapManager singleton;

	public static CraftMapManager getSingleton() {
		return singleton;// will be null before init
	}

	public static CraftMapManager createMapManager(CraftServer server) {
		singleton = new CraftMapManager(server);
		return singleton;
	}

	private CraftServer _craftServer;

	private CraftMapManager(CraftServer craftServer) {
		_craftServer = craftServer;
	}

	HashMap<String, MapView> _mapsView = new HashMap<String, MapView>();

	public MapView getMapView(String mapID) {
		return _mapsView.get(mapID);
	}

	public boolean MapUpdated(WorldMap worldmap, Entity entity, MapType type) {
		MapView m;
		if ((m = _mapsView.get(worldmap.a)) == null) {
			return false;
		}

		switch (type) {
		case Base:
			((CraftMapViewManager) m.getMapViewManager()).cachedBuffer = null;

			repopulateCursors(m);

			byte[] startingState = null;

			MapCanvas cv = m.getCanvas(MapType.Base);

			MapRendererBase r = m.getBase();
			if (r != null) {
				if (r instanceof MapRenderer) {
					if (m.isVirtual()) {
						startingState = cv.getMapView().getMapViewManager()
								.getMergedBuffer().clone();
					}
					((MapRenderer) r).render(m, m.getCanvas(MapType.Base));
				} else if (r instanceof ContextualMapRenderer) {

					Player cp = ((CraftPlayer) ((EntityPlayer) entity)
							.getBukkitEntity());
					cv.setCurrentUser(cp);
					if (m.isVirtual()) {
						byte[] buf;
						if ((buf = cv.getMapView().getMapViewManager()
								.getMergedBuffer()) != null)
							startingState = buf.clone();
						else
							return false;
					}
					((ContextualMapRenderer) r).render(m, cv, cp);
				}
			}

			if (m.isVirtual()) {
				for (MapRendererBase renderer : m.getOverlays()) {
					if (renderer instanceof MapRenderer)
						((MapRenderer) renderer).render(m,
								m.getCanvas(MapType.Overlay));
					else if (renderer instanceof ContextualMapRenderer) {
						cv = m.getCanvas(MapType.Overlay);
						Player cp = ((CraftPlayer) ((EntityPlayer) entity)
								.getBukkitEntity());
						cv.setCurrentUser(cp);
						((ContextualMapRenderer) renderer).render(m, cv, cp);
					}
				}

				reAddCursors(m);

				// handledirty
				boolean dirty = false;
				byte[] newBuf = cv.getMapView().getMapViewManager()
						.getMergedBuffer();
				for (int x = 0; x < 128; x++) {
					int bottomY = -1;
					int topY = -1;
					for (int y = 0; y < 128; y++) {
						int pos = x + y * 128;
						if (startingState[pos] != newBuf[pos]) {
							topY = y;
							break;
						}
					}
					if (topY == -1)
						continue;
					for (int y = 127; y >= topY; y--) {
						int pos = x + y * 128;
						if (startingState[pos] != newBuf[pos]) {
							bottomY = y;
							break;
						}
					}
					cv.markDirty(x, topY, bottomY);
					dirty = true;
				}

				if (dirty) {
					List humanList = ((CraftMapView) m)._worldMap.h;
					for (int i = 0; i < humanList.size(); i++) {
						WorldMapHumanTracker b = (WorldMapHumanTracker) humanList
								.get(i);
						b.e = 0;
					}
				}

				return dirty;
			}

			return false;

		case Overlay:
			for (MapRendererBase renderer : m.getOverlays()) {

				if (renderer instanceof MapRenderer)
					((MapRenderer) renderer).render(m,
							m.getCanvas(MapType.Overlay));
				else if (renderer instanceof ContextualMapRenderer) {
					cv = m.getCanvas(MapType.Overlay);
					Player cp = ((CraftPlayer) ((EntityPlayer) entity)
							.getBukkitEntity());
					cv.setCurrentUser(cp);
					((ContextualMapRenderer) renderer).render(m, cv, cp);
				}
			}

			reAddCursors(m);

			return false;
		default:
			return false;
		}
	}

	private void repopulateCursors(MapView m) {
		// the cursors are fresh, remove the default ones from our collection
		// and
		// re add them back in (with newer values)
		MapCursorCollection col = m.getCanvas(MapType.Base)
				.getCursorCollection();
		// Repopulate the default cursors
		for (int i = col.cursorLength() - 1; i >= 0; i--) {
			if (col.getCursor(i).isDefault()) {
				col.removeCursor(i);
			}
		}
		int index = 0;
		for (Iterator iterator = ((CraftMapView) m)._worldMap.i.iterator(); iterator
				.hasNext();) {
			WorldMapOrienter cursor = (WorldMapOrienter) iterator.next();
			MapCursor cu = new CraftMapCursor(true);
			cu.setCurstorType(cursor.a);
			cu.setDirection(cursor.d);
			cu.setX(cursor.b);
			cu.setY(cursor.c);
			col.insertCursor(index++, cu);
		}
	}

	private void reAddCursors(MapView m) {
		// everyone is done handling cursors,
		// readd them back to notches collection
		MapCursorCollection col = m.getCanvas(MapType.Base)
				.getCursorCollection();
		for (int i = 0; i < col.cursorLength(); i++) {
			MapCursor curs = col.getCursor(i);
			if (!curs.isDefault()) {
				if (curs.getVisible()) {
					((CraftMapView) m)._worldMap.i.add(new WorldMapOrienter(
							((CraftMapView) m)._worldMap, curs.getCursorType(),
							(byte) curs.getX(), (byte) curs.getY(), curs
									.getDirection()));
				}
			} else {
				if (!curs.getVisible()) {
					// ((CraftMapView) m)._worldMap.i.remove(i);
				}
			}
		}
	}

	public boolean activate(MapView worldmap) {
		boolean isVirtual = false;
		MapView m;
		if ((m = _mapsView.get("map_" + worldmap.getId())) == null) {

			_mapsView.put("map_" + worldmap.getId(), m = worldmap);

			_craftServer.getPluginManager().callEvent(
					new MapInitializeEvent(Type.MAP_INITIALIZE, m));

			if (m.getBase() != null)
				m.getBase().initialize(m);
			
			List<MapRendererBase> overlays = m.getOverlays();
			for (int i = 0; i < overlays.size(); i++) {
				overlays.get(i).initialize(m);
			}
		}
		return isVirtual;

	}

	public Server getServer() {
		// TODO Auto-generated method stub
		return this._craftServer;
	}

}
