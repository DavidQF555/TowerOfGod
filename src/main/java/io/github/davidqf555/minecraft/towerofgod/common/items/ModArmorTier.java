package io.github.davidqf555.minecraft.towerofgod.common.items;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public enum ModArmorTier implements ArmorMaterial {

    SUSPENDIUM(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium").toString(), 15, new int[]{2, 6, 5, 2}, 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.5f, () -> Ingredient.of(ItemRegistry.SUSPENDIUM.get()), -2);

    private static final int[] MAX_DAMAGE_ARRAY = new int[]{11, 16, 15, 13};
    private final String name;
    private final int max;
    private final int[] damageReduction;
    private final int enchantability;
    private final SoundEvent sound;
    private final float toughness;
    private final Supplier<Ingredient> repair;
    private final float knockback;

    ModArmorTier(String name, int max, int[] damageReduction, int enchantability, SoundEvent sound, float toughness, Supplier<Ingredient> repair, float knockback) {
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
    public int getDurabilityForType(ArmorItem.Type slotIn) {
        return MAX_DAMAGE_ARRAY[slotIn.getSlot().getIndex()] * max;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type slotIn) {
        return damageReduction[slotIn.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return sound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repair.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockback;
    }
}
