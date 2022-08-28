package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects.ShinsuAttributeEffect;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.filter.DropsFilter;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.List;

public class ShinsuAttribute extends ForgeRegistryEntry<ShinsuAttribute> {

    private final ParticleOptions particleType;
    private final DamageSource source;
    private final double speed;
    private final double damage;
    private final int color;
    private final ShinsuAttributeEffect<EntityHitResult> entityEffect;
    private final ShinsuAttributeEffect<BlockHitResult> blockEffect;
    private final DropsFilter dropsFilter;

    public ShinsuAttribute(ParticleOptions particleType, DamageSource source, double speed, double damage, int color, ShinsuAttributeEffect<EntityHitResult> entityEffect, ShinsuAttributeEffect<BlockHitResult> blockEffect, DropsFilter dropsFilter) {
        this.particleType = particleType;
        this.source = source;
        this.speed = speed;
        this.damage = damage;
        this.color = color;
        this.entityEffect = entityEffect;
        this.blockEffect = blockEffect;
        this.dropsFilter = dropsFilter;
    }

    public static int getColor(@Nullable ShinsuAttribute attribute) {
        return attribute == null ? 0xAA24a6d1 : attribute.getColor();
    }

    public static ParticleOptions getParticles(@Nullable ShinsuAttribute attribute) {
        return attribute == null ? ParticleTypes.DRIPPING_WATER : attribute.getParticleType();
    }

    public static DamageSource getDamageSource(@Nullable ShinsuAttribute attribute) {
        return attribute == null ? DamageSource.DROWN : attribute.getSource();
    }

    public static void setAttribute(ItemStack item, @Nullable ShinsuAttribute attribute) {
        CompoundTag tag = item.getOrCreateTagElement(TowerOfGod.MOD_ID);
        if (attribute == null) {
            tag.remove("Attribute");
        } else {
            tag.putString("Attribute", attribute.getRegistryName().toString());
        }
    }

    @Nullable
    public static ShinsuAttribute getAttribute(ItemStack item) {
        CompoundTag nbt = item.getOrCreateTagElement(TowerOfGod.MOD_ID);
        if (nbt.contains("Attribute", Tag.TAG_STRING)) {
            try {
                return ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Attribute")));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    protected ParticleOptions getParticleType() {
        return particleType;
    }

    protected DamageSource getSource() {
        return source;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDamage() {
        return damage;
    }

    protected int getColor() {
        return color;
    }

    public void applyEntityEffect(Entity user, EntityHitResult rayTrace) {
        entityEffect.apply(user, rayTrace);
    }

    public void applyBlockEffect(Entity user, BlockHitResult rayTrace) {
        blockEffect.apply(user, rayTrace);
    }

    public List<ItemStack> filterDrops(List<ItemStack> drops, LootContext context) {
        return dropsFilter.apply(context, drops);
    }

    public TranslatableComponent getName() {
        return new TranslatableComponent(Util.makeDescriptionId("attribute", getRegistryName()));
    }

}
