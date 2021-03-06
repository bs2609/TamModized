package tamaized.tammodized.common.capabilities.dimTracker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import tamaized.tammodized.TamModized;
import tamaized.tammodized.common.config.ConfigHandler;
import tamaized.tammodized.registry.PortalHandlerRegistry;

import java.lang.reflect.InvocationTargetException;

public class DimensionCapabilityHandler implements IDimensionCapability {

	public static final ResourceLocation ID = new ResourceLocation(TamModized.modid, "DimensionCapabilityHandler");
	private static final int teleportTick = 20 * 5;
	private int lastDim = 0;
	private boolean hasTeleported = false;
	private int tick = 0;

	@Override
	public void update(EntityPlayer player) {
		if (player != null && player.world != null && !player.world.isRemote && player instanceof EntityPlayerMP) {
			if (PortalHandlerRegistry.contains(player.world.getBlockState(player.getPosition()))) {
				if (!hasTeleported) {
					tick++;
					if (tick % teleportTick == 0 || ConfigHandler.instantPortal == ConfigHandler.InstantPortal.ALWAYS || ((player.isCreative() || player.isSpectator()) && ConfigHandler.instantPortal == ConfigHandler.InstantPortal.CREATIVE)) {
						hasTeleported = true;
						try {
							PortalHandlerRegistry.doTeleport(this, (EntityPlayerMP) player, PortalHandlerRegistry.getTeleporter(player.world.getBlockState(player.getPosition())));
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				if (tick > 0)
					tick -= Math.min(2, tick);
				else
					hasTeleported = false;
			}
		}
	}

	@Override
	public int getTick() {
		return tick;
	}

	@Override
	public int getLastDimension() {
		return lastDim;
	}

	@Override
	public void setLastDimension(int dim) {
		lastDim = dim;
	}

	@Override
	public boolean hasTeleported() {
		return hasTeleported;
	}

	@Override
	public void copyFrom(IDimensionCapability cap) {
		lastDim = cap.getLastDimension();
		tick = cap.getTick();
		hasTeleported = cap.hasTeleported();
	}
}
