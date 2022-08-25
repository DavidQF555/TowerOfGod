package io.github.davidqf555.minecraft.towerofgod.common.entities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class SwapWeaponToMainHandGoal<T extends MobEntity> extends Goal {

    private static final int MAIN_HAND = EquipmentSlotType.MAINHAND.getIndex();
    private final T entity;
    private final float closeRange;
    private int swap;

    public SwapWeaponToMainHandGoal(T entity, float closeRange) {
        this.entity = entity;
        this.closeRange = closeRange;
        swap = -1;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        if (target == null || !target.isAlive() || target.distanceToSqr(entity) > closeRange * closeRange) {
            swap = getBestShootableItemSlot();
        }
        if (swap == -1) {
            swap = getBestMeleeItemSlot();
        }
        if (swap == MAIN_HAND) {
            swap = -1;
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void stop() {
        swap = -1;
    }

    @Override
    public void start() {
        IItemHandler inventory = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
        ItemStack hand = inventory.extractItem(MAIN_HAND, inventory.getSlotLimit(MAIN_HAND), false);
        ItemStack swap = inventory.extractItem(this.swap, inventory.getSlotLimit(this.swap), false);
        inventory.insertItem(this.swap, hand, false);
        inventory.insertItem(MAIN_HAND, swap, false);
    }

    private int getBestMeleeItemSlot() {
        int index = MAIN_HAND;
        ItemStack held = entity.getMainHandItem();
        double maxDamage = IGeared.getAttribute(Attributes.ATTACK_DAMAGE, entity, held, EquipmentSlotType.MAINHAND);
        double maxSpeed = IGeared.getAttribute(Attributes.ATTACK_SPEED, entity, held, EquipmentSlotType.MAINHAND);
        IItemHandler inventory = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (inventory.isItemValid(MAIN_HAND, stack) && inventory.isItemValid(i, held)) {
                double attackDamage = IGeared.getAttribute(Attributes.ATTACK_DAMAGE, entity, stack, EquipmentSlotType.MAINHAND);
                double attackSpeed = IGeared.getAttribute(Attributes.ATTACK_SPEED, entity, stack, EquipmentSlotType.MAINHAND);
                if (attackDamage > maxDamage || (attackDamage == maxDamage && maxSpeed < attackSpeed)) {
                    maxDamage = attackDamage;
                    maxSpeed = attackSpeed;
                    index = i;
                }
            }
        }
        return index;
    }

    private int getBestShootableItemSlot() {
        ItemStack held = entity.getMainHandItem();
        IItemHandler inventory = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && inventory.isItemValid(MAIN_HAND, stack) && inventory.isItemValid(i, held) && stack.getItem() instanceof ShootableItem) {
                return i;
            }
        }
        return -1;
    }
}
