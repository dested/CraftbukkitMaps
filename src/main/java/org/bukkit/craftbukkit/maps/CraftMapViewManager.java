package org.bukkit.craftbukkit.maps;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.maps.MapType;
import org.bukkit.maps.MapViewManager;
import org.bukkit.maps.MapViewPiece;

public class CraftMapViewManager implements MapViewManager {
	private boolean isUserSpecific;
	private MapViewPiece buffer;
	private HashMap<Player, MapViewPiece> userBuffers;
	private final CraftMapView _craftMapView;
	public byte[] cachedBuffer;// cache MUST be cleared at the beginning of the
								// draw

	private CraftMapViewManager(CraftMapView craftMapView) {
		this._craftMapView = craftMapView;
	}

	public static CraftMapViewManager CreateRegular(CraftMapView craftMapView) {
		CraftMapViewManager cmvm = new CraftMapViewManager(craftMapView);
		cmvm.isUserSpecific = false;
		if (craftMapView.isVirtual())
			cmvm.buffer = new CraftMapViewPiece();
		else
			cmvm.buffer = new CraftMapViewPiece(craftMapView._worldMap.f);

		return cmvm;
	}

	public static CraftMapViewManager CreateUserSpecific(
			CraftMapView craftMapView) {
		CraftMapViewManager cmvm = new CraftMapViewManager(craftMapView);
		cmvm.isUserSpecific = true;
		cmvm.userBuffers = new HashMap<Player, MapViewPiece>();
		return cmvm;
	}

	public boolean getContextual() {
		return isUserSpecific;
	}

	public MapViewPiece getBasePiece() {
		return buffer;
	}

	public MapViewPiece getUserPiece(Player player) {
		return userBuffers.get(player);
	}

	public void removeUserBuffer(Player player) {
		userBuffers.remove(player);
	}

	public void pushUserBuffer(Player player, MapViewPiece mapViewPiece) {
		userBuffers.put(player, mapViewPiece);
	}

	public byte[] getMergedBuffer() {
		if (cachedBuffer != null) {
			return cachedBuffer;
		}
		MapViewManager manager = _craftMapView.getMapViewManager();
		if (manager.getContextual()) {
			Player curUser = _craftMapView.getCanvas(MapType.Base)
					.getCurrentUser();
			MapViewPiece curPiece = null;
			if (curUser == null
					|| (curPiece = manager.getUserPiece(_craftMapView
							.getCanvas(MapType.Base).getCurrentUser())) == null)
				return null;
			cachedBuffer = curPiece.getMergedBuffer();
		} else
			cachedBuffer = manager.getBasePiece().getMergedBuffer();
		return cachedBuffer;
	}

	public void createOverlayBuffer() {

		if (isUserSpecific) {
			for (Map.Entry<Player, MapViewPiece> pairs : userBuffers.entrySet()) {
				pairs.getValue().createOverlayBuffer();
			}

		} else {
			buffer.createOverlayBuffer();

		}

	}
}