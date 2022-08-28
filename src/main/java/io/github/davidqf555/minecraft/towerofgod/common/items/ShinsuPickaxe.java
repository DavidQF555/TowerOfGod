package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
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
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (worldIn instanceof ServerLevel) {
            CompoundTag nbt = stack.getTagElement(TowerOfGod.MOD_ID);
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
        Vec3 dir = target.getEyePosition(1).subtract(attacker.getEyePosition(1)).normalize();
        ShinsuAttribute attribute = ShinsuAttribute.getAttribute(stack);
        if (attribute != null) {
            attribute.applyEntityEffect(target, new EntityHitResult(target, dir));
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level world) {
        return 0;
    }


}
