package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects.ShinsuAttributeEffect;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.filter.DropsFilter;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ShinsuAttribute {

    private final ParticleOptions particleType;
    private final Function<Level, DamageSource> source;
    private final double speed;
    private final double damage;
    private final int color;
    private final ShinsuAttributeEffect<EntityHitResult> entityEffect;
    private final ShinsuAttributeEffect<BlockHitResult> blockEffect;
    private final DropsFilter dropsFilter;
    private ResourceLocation id;

    public ShinsuAttribute(ParticleOptions particleType, Function<Level, DamageSource> source, double speed, double damage, int color, ShinsuAttributeEffect<EntityHitResult> entityEffect, ShinsuAttributeEffect<BlockHitResult> blockEffect, DropsFilter dropsFilter) {
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

    public static DamageSource getDamageSource(Level world, @Nullable ShinsuAttribute attribute) {
        return attribute == null ? world.damageSources().drown() : attribute.getSource(world);
    }

    public static void setAttribute(ItemStack item, @Nullable ShinsuAttribute attribute) {
        CompoundTag tag = item.getOrCreateTagElement(TowerOfGod.MOD_ID);
        if (attribute == null) {
            tag.remove("Attribute");
        } else {
            tag.putString("Attribute", attribute.getId().toString());
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

    public ResourceLocation getId() {
        if (id == null) {
            id = ShinsuAttributeRegistry.getRegistry().getKey(this);
        }
        return id;
    }

    protected ParticleOptions getParticleType() {
        return particleType;
    }

    protected DamageSource getSource(Level world) {
        return source.apply(world);
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

    public ObjectArrayList<ItemStack> filterDrops(ObjectArrayList<ItemStack> drops, LootContext context) {
        return dropsFilter.apply(context, drops);
    }

    public MutableComponent getName() {
        return Component.translatable(Util.makeDescriptionId("attribute", getId()));
    }

}
