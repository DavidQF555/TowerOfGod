package io.github.davidqf555.minecraft.towerofgod.common.items.shinsu;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.items.ModToolTier;
import io.github.davidqf555.minecraft.towerofgod.common.items.SpearItem;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.Manifest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

public class ShinsuSpearItem extends SpearItem {

    public ShinsuSpearItem(Properties properties) {
        super(ModToolTier.SHINSU, 1, -1.2f, properties.setNoRepair());
    }

    @Nullable
    @Override
    protected AbstractArrow launchSpear(LivingEntity user, ItemStack stack) {
        stack.hurtAndBreak(1, user, p -> p.broadcastBreakEvent(user.getUsedItemHand()));
        ShinsuTechniqueRegistry.THROW_SPEAR.get().cast(user, null, user.getLookAngle());
        return null;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (worldIn instanceof ServerLevel) {
            if (!stack.isEmpty()) {
                CompoundTag nbt = stack.getTagElement(TowerOfGod.MOD_ID);
                if (nbt != null && entityIn instanceof LivingEntity) {
                    UUID id = nbt.getUUID("Technique");
                    if (ShinsuTechniqueData.get((LivingEntity) entityIn)
                            .getTechniques().stream()
                            .filter(inst -> inst.getData() instanceof Manifest.Data)
                            .map(inst -> ((Manifest.Data) inst.getData()).id)
                            .anyMatch(id::equals)) {
                        return;
                    }
                }
                IItemHandler inventory = entityIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
                if (inventory.getSlots() > itemSlot) {
                    inventory.extractItem(itemSlot, stack.getCount(), false);
                }
                inventory.extractItem(itemSlot, stack.getCount(), false);
            }
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level world) {
        return 0;
    }

}
