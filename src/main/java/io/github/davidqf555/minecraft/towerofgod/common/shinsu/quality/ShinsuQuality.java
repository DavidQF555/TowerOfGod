package io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.effects.ShinsuQualityEffect;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.filter.DropsFilter;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.suitability.SuitabilityCalculator;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuQualityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.List;

public class ShinsuQuality extends ForgeRegistryEntry<ShinsuQuality> {

    private final IParticleData particleType;
    private final DamageSource source;
    private final double speed;
    private final double damage;
    private final int color;
    private final ShinsuQualityEffect<EntityRayTraceResult> entityEffect;
    private final ShinsuQualityEffect<BlockRayTraceResult> blockEffect;
    private final DropsFilter dropsFilter;
    private final SuitabilityCalculator suitability;

    public ShinsuQuality(IParticleData particleType, DamageSource source, double speed, double damage, int color, ShinsuQualityEffect<EntityRayTraceResult> entityEffect, ShinsuQualityEffect<BlockRayTraceResult> blockEffect, DropsFilter dropsFilter, SuitabilityCalculator suitability) {
        this.particleType = particleType;
        this.source = source;
        this.speed = speed;
        this.damage = damage;
        this.color = color;
        this.entityEffect = entityEffect;
        this.blockEffect = blockEffect;
        this.dropsFilter = dropsFilter;
        this.suitability = suitability;
    }

    public static int getColor(@Nullable ShinsuQuality quality) {
        return quality == null ? 0xAA24a6d1 : quality.getColor();
    }

    public static IParticleData getParticles(@Nullable ShinsuQuality quality) {
        return quality == null ? ParticleTypes.DRIPPING_WATER : quality.getParticleType();
    }

    public static DamageSource getDamageSource(@Nullable ShinsuQuality quality) {
        return quality == null ? DamageSource.DROWN : quality.getSource();
    }

    public static void setQuality(ItemStack item, @Nullable ShinsuQuality quality) {
        CompoundNBT tag = item.getOrCreateTagElement(TowerOfGod.MOD_ID);
        if (quality == null) {
            tag.remove("Quality");
        } else {
            tag.putString("Quality", quality.getRegistryName().toString());
        }
    }

    @Nullable
    public static ShinsuQuality getQuality(ItemStack item) {
        CompoundNBT nbt = item.getOrCreateTagElement(TowerOfGod.MOD_ID);
        if (nbt.contains("Quality", Constants.NBT.TAG_STRING)) {
            try {
                return ShinsuQualityRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Quality")));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    protected IParticleData getParticleType() {
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

    public double getSuitability(ServerPlayerEntity player) {
        return suitability.calculate(player);
    }

    public void applyEntityEffect(Entity user, EntityRayTraceResult rayTrace) {
        entityEffect.apply(user, rayTrace);
    }

    public void applyBlockEffect(Entity user, BlockRayTraceResult rayTrace) {
        blockEffect.apply(user, rayTrace);
    }

    public List<ItemStack> filterDrops(List<ItemStack> drops, LootContext context) {
        return dropsFilter.apply(context, drops);
    }

    public TranslationTextComponent getName() {
        return new TranslationTextComponent(Util.makeDescriptionId("quality", getRegistryName()));
    }

}
