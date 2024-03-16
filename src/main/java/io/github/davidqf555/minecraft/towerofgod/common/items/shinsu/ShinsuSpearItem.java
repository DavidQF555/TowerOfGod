package io.github.davidqf555.minecraft.towerofgod.common.items.shinsu;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuSpearEntity;
import io.github.davidqf555.minecraft.towerofgod.common.items.ModToolTier;
import io.github.davidqf555.minecraft.towerofgod.common.items.SpearItem;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.UUID;
import java.util.function.Consumer;

public class ShinsuSpearItem extends SpearItem {

    public ShinsuSpearItem(Properties properties) {
        super(ModToolTier.SHINSU, 1, -1.2f, properties.setNoRepair());
    }

    @Override
    protected AbstractArrow createProjectile(Level world, LivingEntity shooter, ItemStack stack) {
        ShinsuSpearEntity spear = EntityRegistry.SHINSU_SPEAR.get().create(world);
        if (spear != null) {
            CompoundTag tag = stack.getTagElement(TowerOfGod.MOD_ID);
            if (tag != null && tag.contains("Technique", Tag.TAG_INT_ARRAY)) {
                spear.setTechnique(tag.getUUID("Technique"));
                ShinsuAttribute attribute = ShinsuQualityData.get(shooter).getAttribute();
                spear.setAttribute(attribute);
                return spear;
            }
        }
        return null;
    }

    @Override
    protected float getSpeedFactor(AbstractArrow arrow) {
        if (arrow instanceof ShinsuSpearEntity) {
            return (float) ShinsuAttribute.getSpeed(((ShinsuSpearEntity) arrow).getAttribute());
        }
        return 1;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (worldIn instanceof ServerLevel) {
            if (!stack.isEmpty()) {
                CompoundTag nbt = stack.getTagElement(TowerOfGod.MOD_ID);
                if (nbt != null && entityIn instanceof LivingEntity) {
                    UUID id = nbt.getUUID("Technique");
                    if (ShinsuTechniqueInstance.getById((LivingEntity) entityIn, id) != null) {
                        return;
                    }
                }
                IItemHandler inventory = entityIn.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseGet(ItemStackHandler::new);
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
