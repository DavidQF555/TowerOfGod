package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public class DeviceItem extends Item {

    private final BiFunction<World, ItemStack, FlyingDevice> entity;

    public DeviceItem(BiFunction<World, ItemStack, FlyingDevice> entity, Properties properties) {
        super(properties);
        this.entity = entity;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        ItemStack item = playerIn.getItemInHand(handIn);
        if (!item.isEmpty()) {
            FlyingDevice device = entity.apply(worldIn, item);
            if (device != null && device.checkSpawnRules(worldIn, SpawnReason.MOB_SUMMONED)) {
                Vector3d eye = playerIn.getEyePosition(1);
                BlockRayTraceResult result = worldIn.clip(new RayTraceContext(eye, eye.add(playerIn.getLookAngle().scale(4)), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, device));
                Vector3d spawn = result.getLocation();
                device.setPos(spawn.x, spawn.y, spawn.z);
                device.setOwnerID(playerIn.getUUID());
                device.setColor(getColor(item));
                worldIn.addFreshEntity(device);
                if (!playerIn.isCreative()) {
                    item.setCount(item.getCount() - 1);
                }
                if (playerIn instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerIn;
                    CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, item);
                    serverPlayer.awardStat(Stats.ITEM_USED.get(this));
                }
                return ActionResult.sidedSuccess(item, playerIn.level.isClientSide());
            }
        }
        return ActionResult.pass(item);
    }

    public DyeColor getColor(ItemStack item) {
        CompoundNBT nbt = item.getOrCreateTagElement(TowerOfGod.MOD_ID);
        if (nbt.contains("Color", Constants.NBT.TAG_INT)) {
            return DyeColor.byId(nbt.getInt("Color"));
        }
        return DyeColor.WHITE;
    }


}
