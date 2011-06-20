package org.bukkit.craftbukkit.maps;

import org.bukkit.maps.MapViewPiece;

public class CraftMapViewPiece implements MapViewPiece {
	public byte[] bottomBuffer;
	public byte[] overlayBuffer;

	public CraftMapViewPiece() {
		bottomBuffer = new byte[128 * 128];
	}

	public CraftMapViewPiece(byte[] bottom) {
		bottomBuffer = bottom;
	}

	public void createOverlayBuffer() {
		overlayBuffer = new byte[128 * 128];
	}

	public byte[] getBaseBuffer() {
		return bottomBuffer;
	}

	public byte[] getOverlayBuffer() {
		return overlayBuffer;
	}

	public byte[] getMergedBuffer() {
		if (overlayBuffer == null)
			return bottomBuffer;
		byte[] merge = new byte[128 * 128];
		for (int i = 0; i < bottomBuffer.length; i++) {
			merge[i] = overlayBuffer[i] < 3 ? bottomBuffer[i]
					: overlayBuffer[i];
		}
		return merge;
	}
}