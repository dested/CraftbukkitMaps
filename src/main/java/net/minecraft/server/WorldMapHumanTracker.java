package net.minecraft.server;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.maps.CraftMapManager;
//Craftbukkit start
import org.bukkit.maps.MapPrintOrder;
import org.bukkit.maps.MapView;

//Craftbukkit end

public class WorldMapHumanTracker {

	public final EntityHuman a;
	public int[] b;
	public int[] c;
	public int e;// Craftbukkit Make Public
	private int f;
	private byte[] g;

	final WorldMap d;

	public WorldMapHumanTracker(WorldMap worldmap, EntityHuman entityhuman) {
		this.d = worldmap;
		this.b = new int[128];
		this.c = new int[128];
		this.e = 0;
		this.f = 0;
		this.a = entityhuman;

		for (int i = 0; i < this.b.length; ++i) {
			this.b[i] = 0;
			this.c[i] = 127;
		}
	}

	int packetIndex = 0;

	public byte[] a(ItemStack itemstack) {
		int i;
		int j;
		if (--this.f < 0) {
			this.f = 4;
			byte[] abyte = new byte[this.d.i.size() * 3 + 1];

			abyte[0] = 1;

			for (i = 0; i < this.d.i.size(); ++i) {
				WorldMapOrienter worldmaporienter = (WorldMapOrienter) this.d.i
						.get(i);

				abyte[i * 3 + 1] = (byte) (worldmaporienter.a + (worldmaporienter.d & 15) * 16);
				abyte[i * 3 + 2] = worldmaporienter.b;
				abyte[i * 3 + 3] = worldmaporienter.c;
			}

			boolean flag = true;

			if (this.g != null && this.g.length == abyte.length) {
				for (j = 0; j < abyte.length; ++j) {
					if (abyte[j] != this.g[j]) {
						flag = false;
						break;
					}
				}
			} else {
				flag = false;
			}

			if (!flag) {
				this.g = abyte;
				return abyte;
			}
		}

		// Craftbukkit start
		MapView m = CraftMapManager.getSingleton().getMapView(this.d.a);
		byte[] buffer;
		if (m == null || m.getMapViewManager() == null
				|| (buffer = m.getMapViewManager().getMergedBuffer()) == null)
			return null;
		boolean randomOrder = m.getPrintOrder() == MapPrintOrder.Random;

		// Craftbukkit end


		for (int k = 0; k < 10; ++k) {
			i = this.e * (randomOrder ? 11 : 1) % 128; // Craftbukkit change
														// from i = this.e * 11
														// % 128;

			++this.e;
			if (this.b[i] >= 0) {
				j = this.c[i] - this.b[i] + 1;
				int l = this.b[i];
				byte[] abyte1 = new byte[j + 3];

				abyte1[0] = 0;
				abyte1[1] = (byte) i;
				abyte1[2] = (byte) l;

				for (int i1 = 0; i1 < abyte1.length - 3; ++i1) {

					abyte1[i1 + 3] = buffer[(i1 + l) * 128 + i]; // Craftbukkit
																	// change
																	// this.d.f
																	// to buffer
				}

				this.c[i] = -1;
				this.b[i] = -1;

				if (abyte1 != null) {

					/*
					 * ((CraftPlayer) ((EntityPlayer) this.a).getBukkitEntity())
					 * .sendMessage("Sending packet for map " + m.getId() +
					 * " length: " + abyte1.length + " x:" + i + " topY:" + l +
					 * " count: " + (packetIndex++));
					 */
				}

				return abyte1;
			}
		}

		return null;
	}
}
