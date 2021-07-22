package io.github.davidqf555.minecraft.towerofgod.common.entities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface IGeared<T extends LivingEntity> {

    static Set<Item> getAllCraftableItems(World world, Predicate<Item> filter) {
        return world.getRecipeManager().getRecipes().stream()
                .map(IRecipe::getRecipeOutput)
                .map(ItemStack::getItem)
                .filter(filter)
                .collect(Collectors.toSet());
    }

    static double getAttribute(Attribute attribute, LivingEntity entity, ItemStack item, EquipmentSlotType slot) {
        ModifiableAttributeInstance instance = entity.getAttribute(attribute);
        double base = instance == null ? 0 : instance.getBaseValue();
        double total = base;
        for (AttributeModifier modifier : item.getAttributeModifiers(slot).get(attribute)) {
            switch (modifier.getOperation()) {
                case ADDITION:
                    total += modifier.getAmount();
                    break;
                case MULTIPLY_BASE:
                    total += modifier.getAmount() * base;
                    break;
                case MULTIPLY_TOTAL:
                    total *= modifier.getAmount() + 1;
            }
        }
        return total;
    }

    default void initializeWeapons() {
        T entity = getGearedEntity();
        entity.setItemStackToSlot(EquipmentSlotType.MAINHAND, getInitialWeapon());
        entity.setItemStackToSlot(EquipmentSlotType.HEAD, getInitialArmor(EquipmentSlotType.HEAD));
        entity.setItemStackToSlot(EquipmentSlotType.CHEST, getInitialArmor(EquipmentSlotType.CHEST));
        entity.setItemStackToSlot(EquipmentSlotType.LEGS, getInitialArmor(EquipmentSlotType.LEGS));
        entity.setItemStackToSlot(EquipmentSlotType.FEET, getInitialArmor(EquipmentSlotType.FEET));
    }

    default double getPreferredWeaponChance() {
        return 0.8;
    }

    default Map<Item, Double> getInitialItemChances(List<Item> items, Comparator<Item> sort) {
        items.sort(sort);
        Map<Item, Double> chances = new HashMap<>();
        double rate = MathHelper.clamp(getGearLevel() / 100.0, 0.025, 0.975);
        int size = items.size();
        double choose = 1;
        double success = 1;
        double fail = Math.pow(1 - rate, size - 1);
        for (int i = 0; i < size; i++) {
            double chance = choose * success * fail;
            chances.put(items.get(i), chance);
            choose *= (size - i - 1.0) / (i + 1);
            success *= rate;
            fail /= 1 - rate;
        }
        return chances;
    }

    default ItemStack getInitialArmor(EquipmentSlotType slot) {
        T entity = getGearedEntity();
        IItemHandler inventory = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
        int index = slot.getSlotIndex();
        double base = entity.getBaseAttributeValue(Attributes.ARMOR);
        Predicate<Item> condition = item -> inventory.isItemValid(index, item.getDefaultInstance()) && getAttribute(Attributes.ARMOR, entity, item.getDefaultInstance(), slot) > base;
        List<Item> armors = new ArrayList<>(getAllCraftableItems(entity.world, condition));
        armors.add(Items.AIR);
        Map<Item, Double> chances = getInitialItemChances(armors, (item1, item2) -> {
            double dif = getAttribute(Attributes.ARMOR, entity, item1.getDefaultInstance(), slot) - getAttribute(Attributes.ARMOR, entity, item2.getDefaultInstance(), slot);
            if (dif == 0) {
                dif = getAttribute(Attributes.ARMOR_TOUGHNESS, entity, item1.getDefaultInstance(), slot) - getAttribute(Attributes.ARMOR_TOUGHNESS, entity, item2.getDefaultInstance(), slot);
            }
            return dif == 0 ? 0 : (dif > 0 ? 1 : -1);
        });
        double rand = entity.getRNG().nextDouble();
        double current = 0;
        for (Item item : chances.keySet()) {
            current += chances.get(item);
            if (rand < current) {
                return item.getDefaultInstance();
            }
        }
        return ItemStack.EMPTY;
    }

    default ItemStack getInitialWeapon() {
        T entity = getGearedEntity();
        IItemHandler inventory = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
        int index = EquipmentSlotType.MAINHAND.getSlotIndex();
        double base = entity.getBaseAttributeValue(Attributes.ATTACK_DAMAGE);
        Predicate<Item> filter = item -> inventory.isItemValid(index, item.getDefaultInstance()) && getAttribute(Attributes.ATTACK_DAMAGE, entity, item.getDefaultInstance(), EquipmentSlotType.MAINHAND) > base;
        List<Item> weapons = new ArrayList<>(getAllCraftableItems(entity.world, filter));
        weapons.add(Items.AIR);
        if (!weapons.isEmpty()) {
            List<Item> preferred = weapons.stream().filter(this::isWeaponPreferred).collect(Collectors.toList());
            Random random = entity.getRNG();
            List<Item> choices;
            if (!preferred.isEmpty() && random.nextDouble() < getPreferredWeaponChance()) {
                choices = preferred;
            } else {
                choices = weapons;
            }
            Map<Item, Double> chances = getInitialItemChances(choices, (item1, item2) -> {
                double dif = getAttribute(Attributes.ATTACK_DAMAGE, entity, item1.getDefaultInstance(), EquipmentSlotType.MAINHAND) - getAttribute(Attributes.ATTACK_DAMAGE, entity, item2.getDefaultInstance(), EquipmentSlotType.MAINHAND);
                if (dif == 0) {
                    dif = getAttribute(Attributes.ATTACK_SPEED, entity, item1.getDefaultInstance(), EquipmentSlotType.MAINHAND) - getAttribute(Attributes.ATTACK_SPEED, entity, item2.getDefaultInstance(), EquipmentSlotType.MAINHAND);
                }
                return dif == 0 ? 0 : (dif > 0 ? 1 : -1);
            });
            double rand = random.nextDouble();
            double current = 0;
            for (Item item : chances.keySet()) {
                current += chances.get(item);
                if (rand < current) {
                    return item.getDefaultInstance();
                }
            }
        }
        return ItemStack.EMPTY;
    }

    default boolean isWeaponPreferred(Item weapon) {
        return false;
    }

    int getGearLevel();

    T getGearedEntity();
}
