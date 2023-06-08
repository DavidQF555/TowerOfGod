package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public class DeviceItem extends Item {

    private final BiFunction<Level, ItemStack, FlyingDevice> entity;

    public DeviceItem(BiFunction<Level, ItemStack, FlyingDevice> entity, Properties properties) {
        super(properties);
        this.entity = entity;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
        ItemStack item = playerIn.getItemInHand(handIn);
        if (!item.isEmpty()) {
            FlyingDevice device = entity.apply(worldIn, item);
            if (device != null && device.checkSpawnRules(worldIn, MobSpawnType.MOB_SUMMONED)) {
                Vec3 eye = playerIn.getEyePosition(1);
                BlockHitResult result = worldIn.clip(new ClipContext(eye, eye.add(playerIn.getLookAngle().scale(4)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, device));
                Vec3 spawn = result.getLocation();
                device.setPos(spawn.x, spawn.y, spawn.z);
                device.setOwnerID(playerIn.getUUID());
                device.setColor(getColor(item));
                worldIn.addFreshEntity(device);
                if (!playerIn.isCreative()) {
                    item.setCount(item.getCount() - 1);
                }
                if (playerIn instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, item);
                    serverPlayer.awardStat(Stats.ITEM_USED.get(this));
                }
                return InteractionResultHolder.sidedSuccess(item, playerIn.level().isClientSide());
            }
        }
        return InteractionResultHolder.pass(item);
    }

    public DyeColor getColor(ItemStack item) {
        CompoundTag nbt = item.getOrCreateTagElement(TowerOfGod.MOD_ID);
        if (nbt.contains("Color", Tag.TAG_INT)) {
            return DyeColor.byId(nbt.getInt("Color"));
        }
        return DyeColor.WHITE;
    }


}
