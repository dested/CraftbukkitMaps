package net.minecraft.server;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

// CraftBukkit start
import java.util.ArrayList;
import org.bukkit.Bukkit;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenedClosedEvent;
import org.bukkit.event.inventory.InventoryTransactionEvent;

// CraftBukkit end

public class EntityPlayer extends EntityHuman implements ICrafting {

	public NetServerHandler netServerHandler;
	public MinecraftServer b;
	public ItemInWorldManager itemInWorldManager;
	public double d;
	public double e;
	public List f = new LinkedList();
	public Set g = new HashSet();
	private int bK = -99999999;
	private int bL = 60;
	private ItemStack[] bM = new ItemStack[] { null, null, null, null, null };
	private int bN = 0;
	public boolean h;

	public EntityPlayer(MinecraftServer minecraftserver, World world, String s,
			ItemInWorldManager iteminworldmanager) {
		super(world);
		iteminworldmanager.player = this;
		this.itemInWorldManager = iteminworldmanager;
		ChunkCoordinates chunkcoordinates = world.getSpawn();
		int i = chunkcoordinates.x;
		int j = chunkcoordinates.z;
		int k = chunkcoordinates.y;

		if (!world.worldProvider.e) {
			i += this.random.nextInt(20) - 10;
			k = world.f(i, j);
			j += this.random.nextInt(20) - 10;
		}

		this.setPositionRotation((double) i + 0.5D, (double) k,
				(double) j + 0.5D, 0.0F, 0.0F);
		this.b = minecraftserver;
		this.bs = 0.0F;
		this.name = s;
		this.height = 0.0F;

		// CraftBukkit start
		this.displayName = this.name;
	}

	public String displayName;
	public Location compassTarget;

	// CraftBukkit end

	public void a(World world) {
		super.a(world);
		// CraftBukkit - world fallback code, either respawn location or global
		// spawn
		if (world == null) {
			dead = false;
			ChunkCoordinates position = null;
			if (!spawnWorld.isEmpty()) {
				CraftWorld cw = (CraftWorld) Bukkit.getServer().getWorld(
						spawnWorld);
				if (cw != null && M() != null) {
					world = cw.getHandle();
					position = EntityHuman.getBed(cw.getHandle(), M());
				}
			}
			if (world == null || position == null) {
				world = ((CraftWorld) Bukkit.getServer().getWorlds().get(0))
						.getHandle();
				position = world.getSpawn();
			}
			this.world = world;
			setPosition(position.x + 0.5, position.y, position.z + 0.5);
		}
		this.dimension = ((WorldServer) this.world).dimension;
		// CraftBukkit end
		this.itemInWorldManager = new ItemInWorldManager((WorldServer) world);
		this.itemInWorldManager.player = this;
	}

	public void syncInventory() {
		this.activeContainer.a((ICrafting) this);
	}

	public ItemStack[] getEquipment() {
		return this.bM;
	}

	protected void j_() {
		this.height = 0.0F;
	}

	public float s() {
		return 1.62F;
	}

	public void o_() {
		this.itemInWorldManager.a();
		--this.bL;
		this.activeContainer.a();

		for (int i = 0; i < 5; ++i) {
			ItemStack itemstack = this.b_(i);

			if (itemstack != this.bM[i]) {
				this.b.b(this.dimension).a(this,
						new Packet5EntityEquipment(this.id, i, itemstack));
				this.bM[i] = itemstack;
			}
		}
	}

	public ItemStack b_(int i) {
		return i == 0 ? this.inventory.getItemInHand()
				: this.inventory.armor[i - 1];
	}

	public void a(Entity entity) {
		// CraftBukkit start
		List<org.bukkit.inventory.ItemStack> loot = new ArrayList<org.bukkit.inventory.ItemStack>();

		for (int i = 0; i < inventory.items.length; ++i) {
			if (inventory.items[i] != null) {
				loot.add(new CraftItemStack(inventory.items[i]));
			}
		}

		for (int i = 0; i < inventory.armor.length; ++i) {
			if (inventory.armor[i] != null) {
				loot.add(new CraftItemStack(inventory.armor[i]));
			}
		}

		CraftEntity craftEntity = (CraftEntity) getBukkitEntity();
		CraftWorld cworld = ((WorldServer) world).getWorld();
		Server server = ((WorldServer) world).getServer();

		EntityDeathEvent event = new EntityDeathEvent(craftEntity, loot);
		server.getPluginManager().callEvent(event);

		// CraftBukkit - we clean the player's inventory after the
		// EntityDeathEvent is called so plugins can get the exact state of the
		// inventory.
		for (int i = 0; i < inventory.items.length; ++i) {
			inventory.items[i] = null;
		}

		for (int i = 0; i < inventory.armor.length; ++i) {
			inventory.armor[i] = null;
		}

		for (org.bukkit.inventory.ItemStack stack : event.getDrops()) {
			cworld.dropItemNaturally(craftEntity.getLocation(), stack);
		}

		this.x();
		// CraftBukkit end
	}

	public boolean damageEntity(Entity entity, int i) {
		if (this.bL > 0) {
			return false;
		} else {
			// CraftBukkit - this.b.pvpMode -> this.world.pvpMode
			if (!this.world.pvpMode) {
				if (entity instanceof EntityHuman) {
					return false;
				}

				if (entity instanceof EntityArrow) {
					EntityArrow entityarrow = (EntityArrow) entity;

					if (entityarrow.shooter instanceof EntityHuman) {
						return false;
					}
				}
			}

			return super.damageEntity(entity, i);
		}
	}

	protected boolean t() {
		return this.b.pvpMode;
	}

	public void b(int i) {
		super.b(i);
	}

	public void a(boolean flag) {
		super.o_();

		for (int i = 0; i < this.inventory.getSize(); ++i) {
			ItemStack itemstack = this.inventory.getItem(i);

			if (itemstack != null && Item.byId[itemstack.id].b()
					&& this.netServerHandler.b() <= 2) {
				Packet packet = ((ItemWorldMapBase) Item.byId[itemstack.id]).b(
						itemstack, this.world, this);

				if (packet != null) {
					this.netServerHandler.sendPacket(packet);
				}
			}
		}

		if (flag && !this.f.isEmpty()) {
			ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) this.f
					.get(0);

			if (chunkcoordintpair != null) {
				boolean flag1 = false;

				if (this.netServerHandler.b() < 4) {
					flag1 = true;
				}

				if (flag1) {
					WorldServer worldserver = this.b.a(this.dimension);

					this.f.remove(chunkcoordintpair);
					this.netServerHandler
							.sendPacket(new Packet51MapChunk(
									chunkcoordintpair.x * 16, 0,
									chunkcoordintpair.z * 16, 16, 128, 16,
									worldserver));
					List list = worldserver.getTileEntities(
							chunkcoordintpair.x * 16, 0,
							chunkcoordintpair.z * 16,
							chunkcoordintpair.x * 16 + 16, 128,
							chunkcoordintpair.z * 16 + 16);

					for (int j = 0; j < list.size(); ++j) {
						this.a((TileEntity) list.get(j));
					}
				}
			}
		}

		if (this.E) {
			if (this.b.propertyManager.getBoolean("allow-nether", true)) {
				if (this.vehicle != null) {
					this.mount(this.vehicle);
				} else {
					this.F += 0.0125F;
					if (this.F >= 1.0F) {
						this.F = 1.0F;
						this.D = 10;
						this.b.serverConfigurationManager.f(this);
					}
				}

				this.E = false;
			}
		} else {
			if (this.F > 0.0F) {
				this.F -= 0.05F;
			}

			if (this.F < 0.0F) {
				this.F = 0.0F;
			}
		}

		if (this.D > 0) {
			--this.D;
		}

		if (this.health != this.bK) {
			this.netServerHandler.sendPacket(new Packet8UpdateHealth(
					this.health));
			this.bK = this.health;
		}
	}

	private void a(TileEntity tileentity) {
		if (tileentity != null) {
			Packet packet = tileentity.e();

			if (packet != null) {
				this.netServerHandler.sendPacket(packet);
			}
		}
	}

	public void u() {
		super.u();
	}

	public void receive(Entity entity, int i) {
		if (!entity.dead) {
			EntityTracker entitytracker = this.b.b(this.dimension);

			if (entity instanceof EntityItem) {
				entitytracker
						.a(entity, new Packet22Collect(entity.id, this.id));
			}

			if (entity instanceof EntityArrow) {
				entitytracker
						.a(entity, new Packet22Collect(entity.id, this.id));
			}
		}

		super.receive(entity, i);
		this.activeContainer.a();
	}

	public void k_() {
		if (!this.p) {
			this.q = -1;
			this.p = true;
			EntityTracker entitytracker = this.b.b(this.dimension);

			entitytracker.a(this, new Packet18ArmAnimation(this, 1));
		}
	}

	public void w() {
	}

	public EnumBedError a(int i, int j, int k) {
		EnumBedError enumbederror = super.a(i, j, k);

		if (enumbederror == EnumBedError.OK) {
			EntityTracker entitytracker = this.b.b(this.dimension);
			Packet17 packet17 = new Packet17(this, 0, i, j, k);

			entitytracker.a(this, packet17);
			this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw,
					this.pitch);
			this.netServerHandler.sendPacket(packet17);
		}

		return enumbederror;
	}

	public void a(boolean flag, boolean flag1, boolean flag2) {
		if (this.isSleeping()) {
			EntityTracker entitytracker = this.b.b(this.dimension);

			entitytracker.b(this, new Packet18ArmAnimation(this, 3));
		}

		super.a(flag, flag1, flag2);
		if (this.netServerHandler != null) {
			this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw,
					this.pitch);
		}
	}

	public void mount(Entity entity) {
		// CraftBukkit start
		setPassengerOf(entity);
	}

	public void setPassengerOf(Entity entity) {
		// mount(null) doesn't really fly for overloaded methods,
		// so this method is needed

		super.setPassengerOf(entity);
		// CraftBukkit end

		this.netServerHandler.sendPacket(new Packet39AttachEntity(this,
				this.vehicle));
		this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw,
				this.pitch);
	}

	protected void a(double d0, boolean flag) {
	}

	public void b(double d0, boolean flag) {
		super.a(d0, flag);
	}

	private void ah() {
		this.bN = this.bN % 100 + 1;
	}

	public void b(int i, int j, int k) {
		this.ah();
		this.netServerHandler.sendPacket(new Packet100OpenWindow(this.bN, 1,
				"Crafting", 9));
		this.activeContainer = new ContainerWorkbench(this.inventory,
				this.world, i, j, k);
		this.activeContainer.f = this.bN;
		this.activeContainer.a((ICrafting) this);
	}

	public void a(IInventory iinventory) {

		InventoryOpenedClosedEvent evt;
		// CraftBukkit start
		((WorldServer) this.world)
				.getServer()
				.getPluginManager()
				.callEvent(
						evt = new InventoryOpenedClosedEvent(
								Type.INVENTORY_OPEN, new CraftInventory(
										iinventory)));

		if (evt.isCancled())
			return;
		// CraftBukkit end

		this.ah();
		this.netServerHandler.sendPacket(new Packet100OpenWindow(this.bN, 0,
				iinventory.getName(), iinventory.getSize()));
		this.activeContainer = new ContainerChest(this.inventory, iinventory);
		this.activeContainer.f = this.bN;
		this.activeContainer.a((ICrafting) this);
	}

	public void a(TileEntityFurnace tileentityfurnace) {
		this.ah();
		this.netServerHandler.sendPacket(new Packet100OpenWindow(this.bN, 2,
				tileentityfurnace.getName(), tileentityfurnace.getSize()));
		this.activeContainer = new ContainerFurnace(this.inventory,
				tileentityfurnace);
		this.activeContainer.f = this.bN;
		this.activeContainer.a((ICrafting) this);
	}

	public void a(TileEntityDispenser tileentitydispenser) {
		this.ah();
		this.netServerHandler.sendPacket(new Packet100OpenWindow(this.bN, 3,
				tileentitydispenser.getName(), tileentitydispenser.getSize()));
		this.activeContainer = new ContainerDispenser(this.inventory,
				tileentitydispenser);
		this.activeContainer.f = this.bN;
		this.activeContainer.a((ICrafting) this);
	}

	public void a(Container container, int i, ItemStack itemstack) {
		if (!(container.b(i) instanceof SlotResult)) {
			if (!this.h) {
				this.netServerHandler.sendPacket(new Packet103SetSlot(
						container.f, i, itemstack));
			}
		}
	}

	public void a(Container container) {
		this.a(container, container.b());
	}

	public void a(Container container, List list) {
		this.netServerHandler.sendPacket(new Packet104WindowItems(container.f,
				list));
		this.netServerHandler.sendPacket(new Packet103SetSlot(-1, -1,
				this.inventory.j()));
	}

	public void a(Container container, int i, int j) {
		this.netServerHandler.sendPacket(new Packet105CraftProgressBar(
				container.f, i, j));
	}

	public void a(ItemStack itemstack) {
	}

	public void x() {
		this.netServerHandler.sendPacket(new Packet101CloseWindow(
				this.activeContainer.f));
		this.z();
	}

	public void y() {
		if (!this.h) {
			this.netServerHandler.sendPacket(new Packet103SetSlot(-1, -1,
					this.inventory.j()));
		}
	}

	public void z() {
		this.activeContainer.a((EntityHuman) this);
		this.activeContainer = this.defaultContainer;
	}

	public void a(float f, float f1, boolean flag, boolean flag1, float f2,
			float f3) {
		this.az = f;
		this.aA = f1;
		this.aC = flag;
		this.setSneak(flag1);
		this.pitch = f2;
		this.yaw = f3;
	}

	public void a(Statistic statistic, int i) {
		if (statistic != null) {
			if (!statistic.g) {
				while (i > 100) {
					this.netServerHandler.sendPacket(new Packet200Statistic(
							statistic.e, 100));
					i -= 100;
				}

				this.netServerHandler.sendPacket(new Packet200Statistic(
						statistic.e, i));
			}
		}
	}

	public void A() {
		if (this.vehicle != null) {
			this.mount(this.vehicle);
		}

		if (this.passenger != null) {
			this.passenger.mount(this);
		}

		if (this.sleeping) {
			this.a(true, false, false);
		}
	}

	public void B() {
		this.bK = -99999999;
	}

	public void a(String s) {
		StatisticStorage statisticstorage = StatisticStorage.a();
		String s1 = statisticstorage.a(s);

		this.netServerHandler.sendPacket(new Packet3Chat(s1));
	}

	// CraftBukkit start
	public long timeOffset = 0;
	public boolean relativeTime = true;

	public long getPlayerTime() {
		if (relativeTime) {
			// Adds timeOffset to the current server time.
			return world.getTime() + timeOffset;
		} else {
			// Adds timeOffset to the beginning of this day.
			return world.getTime() - (world.getTime() % 24000) + timeOffset;
		}
	}

	@Override
	public String toString() {
		return super.toString() + "(" + name + " at " + locX + "," + locY + ","
				+ locZ + ")";
	}
	// CraftBukkit end
}
