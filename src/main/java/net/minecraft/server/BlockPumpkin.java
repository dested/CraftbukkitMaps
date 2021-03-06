package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.block.BlockRedstoneEvent;
// CraftBukkit end

public class BlockPumpkin extends Block {

    private boolean a;

    protected BlockPumpkin(int i, int j, boolean flag) {
        super(i, Material.PUMPKIN);
        this.textureId = j;
        this.a(true);
        this.a = flag;
    }

    public int a(int i, int j) {
        if (i == 1) {
            return this.textureId;
        } else if (i == 0) {
            return this.textureId;
        } else {
            int k = this.textureId + 1 + 16;

            if (this.a) {
                ++k;
            }

            return j == 2 && i == 2 ? k : (j == 3 && i == 5 ? k : (j == 0 && i == 3 ? k : (j == 1 && i == 4 ? k : this.textureId + 16)));
        }
    }

    public int a(int i) {
        return i == 1 ? this.textureId : (i == 0 ? this.textureId : (i == 3 ? this.textureId + 1 + 16 : this.textureId + 16));
    }

    public void e(World world, int i, int j, int k) {
        super.e(world, i, j, k);
    }

    public boolean canPlace(World world, int i, int j, int k) {
        int l = world.getTypeId(i, j, k);

        return (l == 0 || Block.byId[l].material.isReplacable()) && world.d(i, j - 1, k);
    }

    public void postPlace(World world, int i, int j, int k, EntityLiving entityliving) {
        int l = MathHelper.floor((double) (entityliving.yaw * 4.0F / 360.0F) + 2.5D) & 3;

        world.setData(i, j, k, l);
    }

    // CraftBukkit start
    public void doPhysics(World world, int i, int j, int k, int l) {
        if (net.minecraft.server.Block.byId[l] != null && net.minecraft.server.Block.byId[l].isPowerSource()) {
            CraftWorld craftWorld = ((WorldServer) world).getWorld();
            CraftServer server = ((WorldServer) world).getServer();
            org.bukkit.block.Block block = craftWorld.getBlockAt(i, j, k);
            int power = block.getBlockPower();

            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, power, power);
            server.getPluginManager().callEvent(eventRedstone);
        }
    }
    // CraftBukkit end
}
