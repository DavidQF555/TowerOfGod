package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class ShinsuPickaxe extends PickaxeItem {

    public ShinsuPickaxe(int attackDamageIn, float attackSpeedIn) {
        super(ModToolTier.SHINSU, attackDamageIn, attackSpeedIn, new Item.Properties().setNoRepair());
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
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Vector3d dir = target.getEyePosition(1).subtract(attacker.getEyePosition(1)).normalize();
        ShinsuQuality quality = ShinsuQuality.getQuality(stack);
        if (quality != null) {
            quality.applyEntityEffect(target, new EntityRayTraceResult(target, dir));
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 0;
    }


}
