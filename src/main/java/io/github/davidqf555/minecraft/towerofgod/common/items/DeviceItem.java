package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.client.render.DeviceItemColor;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public class DeviceItem extends Item {

    private final BiFunction<World, ItemStack, FlyingDevice> entity;

    public DeviceItem(BiFunction<World, ItemStack, FlyingDevice> entity) {
        super(new Properties().group(TowerOfGod.TAB));
        this.entity = entity;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        ItemStack item = playerIn.getHeldItem(handIn);
        if (!item.isEmpty()) {
            FlyingDevice device = entity.apply(worldIn, item);
            if (device != null && device.canSpawn(worldIn, SpawnReason.MOB_SUMMONED)) {
                Vector3d eye = playerIn.getEyePosition(1);
                BlockRayTraceResult result = worldIn.rayTraceBlocks(new RayTraceContext(eye, eye.add(playerIn.getLookVec().scale(4)), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, device));
                Vector3d spawn = result.getHitVec();
                device.setPosition(spawn.x, spawn.y, spawn.z);
                device.setOwnerID(playerIn.getUniqueID());
                device.setColor(DeviceItemColor.getColor(item));
                worldIn.addEntity(device);
                if (!playerIn.isCreative()) {
                    item.setCount(item.getCount() - 1);
                }
                if (playerIn instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerIn;
                    CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, item);
                    serverPlayer.addStat(Stats.ITEM_USED.get(this));
                }
                return ActionResult.func_233538_a_(item, playerIn.world.isRemote());
            }
        }
        return ActionResult.resultPass(item);
    }

}
