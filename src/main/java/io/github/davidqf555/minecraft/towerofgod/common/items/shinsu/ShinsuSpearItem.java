package io.github.davidqf555.minecraft.towerofgod.common.items.shinsu;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.ModToolTier;
import io.github.davidqf555.minecraft.towerofgod.common.items.SpearItem;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
    protected AbstractArrowEntity launchSpear(LivingEntity user, ItemStack stack) {
        stack.hurtAndBreak(1, user, p -> p.broadcastBreakEvent(user.getUsedItemHand()));
        ShinsuTechniqueRegistry.THROW_SPEAR.get().cast(user, null, user.getLookAngle());
        return null;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (worldIn instanceof ServerWorld) {
            CompoundNBT nbt = stack.getTagElement(TowerOfGod.MOD_ID);
            if (!stack.isEmpty() && nbt != null) {
                UUID id = nbt.getUUID("Technique");
                ShinsuTechniqueInstance technique = ShinsuTechniqueInstance.get(entityIn, id);
                if (technique == null) {
                    IItemHandler inventory = entityIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
                    if (inventory.getSlots() > itemSlot) {
                        inventory.extractItem(itemSlot, stack.getCount(), false);
                    }
                }
            }
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 0;
    }


}
