package com.davidqf.towerofgodmod.armor;

import com.davidqf.towerofgodmod.*;
import com.davidqf.towerofgodmod.util.RegistryHandler;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.*;

import javax.annotation.Nonnull;

public enum ModArmorTier implements IArmorMaterial {

	SUSPENDIUM(TowerOfGod.MOD_ID + ":suspendium", 15, new int[] {2, 6, 5, 2}, 15, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.5f, Ingredient.fromItems(RegistryHandler.SUSPENDIUM.get()), -2);

	private static final int[] MAX_DAMAGE_ARRAY = new int[] {11, 16, 15, 13};
	private final String name;
	private final int max;
	private final int[] damageReduction;
	private final int enchantability;
	private final SoundEvent sound;
	private final float toughness;
	private final Ingredient repair;
	private final float knockback;

	ModArmorTier(String name, int max, int[] damageReduction, int enchantability, SoundEvent sound, float toughness, Ingredient repair, float knockback) {
		this.name = name;
		this.max = max;
		this.damageReduction = damageReduction;
		this.enchantability = enchantability;
		this.sound = sound;
		this.toughness = toughness;
		this.repair = repair;
		this.knockback = knockback;
	}

	@Override
	public int getDurability(EquipmentSlotType slotIn) {
		return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * max;
	}

	@Override
	public int getDamageReductionAmount(EquipmentSlotType slotIn) {
		return damageReduction[slotIn.getIndex()];
	}

	@Override
	public int getEnchantability() {
		return enchantability;
	}

	@Nonnull
	@Override
	public SoundEvent getSoundEvent() {
		return sound;
	}

	@Nonnull
	@Override
	public Ingredient getRepairMaterial() {
		return repair;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Override
	public float getToughness() {
		return toughness;
	}

	@Override
	public float func_230304_f_() {
		return knockback;
	}

}
