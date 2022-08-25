package io.github.davidqf555.minecraft.towerofgod.registration.shinsu;

import com.google.common.collect.ImmutableList;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.effects.*;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.filter.DropsFilter;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.filter.FactorDropsFilter;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.filter.SmeltDropsFilter;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.suitability.StatSuitabilityCalculator;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.suitability.SuitabilityCalculator;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ShinsuQualityRegistry {

    public static final DeferredRegister<ShinsuQuality> QUALITIES = DeferredRegister.create(ShinsuQuality.class, TowerOfGod.MOD_ID);

    public static final RegistryObject<ShinsuQuality> LIGHTNING = register("lightning", () -> new ShinsuQuality(ParticleTypes.INSTANT_EFFECT, DamageSource.LIGHTNING_BOLT, 1.5, 1, 0xFFfbff85, ShinsuQualityEffect.combination(ImmutableList.of(new FireEntityQualityEffect(3), new PotionQualityEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20, 3, true, false, false)))), new SummonQualityEffect<>(EntityType.LIGHTNING_BOLT::create), DropsFilter.NONE, new StatSuitabilityCalculator(Stats.CUSTOM.get(Stats.DAMAGE_DEALT)).scale(0.1)));
    public static final RegistryObject<ShinsuQuality> FIRE = register("fire", () -> new ShinsuQuality(ParticleTypes.FLAME, DamageSource.ON_FIRE, 1, 1, 0xFFff8119, new FireEntityQualityEffect(7), new PlaceBlockQualityEffect(AbstractFireBlock::getState), SmeltDropsFilter.INSTANCE, SuitabilityCalculator.sum(new StatSuitabilityCalculator(Stats.CUSTOM.get(Stats.INTERACT_WITH_BLAST_FURNACE)), new StatSuitabilityCalculator(Stats.CUSTOM.get(Stats.INTERACT_WITH_CAMPFIRE)), new StatSuitabilityCalculator(Stats.CUSTOM.get(Stats.INTERACT_WITH_FURNACE)), new StatSuitabilityCalculator(Stats.ENTITY_KILLED.get(EntityType.BLAZE)), new StatSuitabilityCalculator(Stats.BLOCK_MINED.get(Blocks.NETHERRACK)))));
    public static final RegistryObject<ShinsuQuality> ICE = register("ice", () -> new ShinsuQuality(ParticleTypes.POOF, DamageSource.MAGIC, 1, 1, 0xFFa8fbff, new PotionQualityEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 60, 2, true, false, false)), new PlaceSphereQualityEffect<>(5, (world, pos) -> {
        BlockState state = world.getBlockState(pos);
        if (state.getFluidState().is(FluidTags.WATER)) {
            return Blocks.ICE.defaultBlockState();
        }
        if (state.getBlock().isAir(state, world, pos)) {
            BlockState snow = Blocks.SNOW.defaultBlockState();
            if (snow.canSurvive(world, pos)) {
                return snow;
            }
        } else if (state.getBlock() instanceof SnowBlock) {
            BlockState snow = state.setValue(SnowBlock.LAYERS, Math.min(state.getValue(SnowBlock.LAYERS) + 1, 8));
            if (snow.canSurvive(world, pos)) {
                return snow;
            }
        }
        return null;
    }), DropsFilter.NONE, new StatSuitabilityCalculator(Stats.BLOCK_MINED.get(Blocks.ICE))));
    public static final RegistryObject<ShinsuQuality> STONE = register("stone", () -> new ShinsuQuality(new BlockParticleData(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()), DamageSource.FALLING_BLOCK, 0.8, 1.5, 0xFF999999, (user, clip) -> {
    }, new DestroyBlockQualityEffect((world, pos) -> {
        float hardness = world.getBlockState(pos).getDestroySpeed(world, pos);
        return hardness >= 0 && hardness <= Blocks.STONE.defaultBlockState().getDestroySpeed(world, pos);
    }), DropsFilter.NONE, new StatSuitabilityCalculator(Stats.BLOCK_MINED.get(Blocks.STONE)).scale(0.1)));
    public static final RegistryObject<ShinsuQuality> WIND = register("wind", () -> new ShinsuQuality(ParticleTypes.AMBIENT_ENTITY_EFFECT, DamageSource.CRAMMING, 1.4, 1, 0xAAabffac, new KnockbackQualityEffect(1), new PushQualityEffect<>(5, dist -> 3 / (dist + 1)), DropsFilter.NONE, new StatSuitabilityCalculator(Stats.CUSTOM.get(Stats.AVIATE_ONE_CM)).scale(0.1)));
    public static final RegistryObject<ShinsuQuality> CRYSTAL = register("crystal", () -> new ShinsuQuality(new BlockParticleData(ParticleTypes.BLOCK, Blocks.GLASS.defaultBlockState()), DamageSource.MAGIC, 0.9, 2, 0xFFf7f7f7, (user, clip) -> {
    }, (entity, clip) -> {
    }, DropsFilter.NONE, new StatSuitabilityCalculator(Stats.ITEM_USED.get(Items.GLASS)).scale(3)));
    public static final RegistryObject<ShinsuQuality> PLANT = register("plant", () -> new ShinsuQuality(ParticleTypes.COMPOSTER, DamageSource.CACTUS, 1, 1, 0xFF03ff2d, new PotionQualityEffect(new EffectInstance(Effects.POISON, 140, 2, true, false, false)), GrowthQualityEffect.INSTANCE, new FactorDropsFilter(stack -> Tags.Items.CROPS.contains(stack.getItem()) ? 2.0 : 1.0), new StatSuitabilityCalculator(Stats.ITEM_USED.get(Items.WHEAT_SEEDS))));

    private static IForgeRegistry<ShinsuQuality> registry = null;

    private ShinsuQualityRegistry() {
    }

    public static IForgeRegistry<ShinsuQuality> getRegistry() {
        return registry;
    }

    private static RegistryObject<ShinsuQuality> register(String name, Supplier<ShinsuQuality> quality) {
        return QUALITIES.register(name, quality);
    }

    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        registry = new RegistryBuilder<ShinsuQuality>().setName(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_qualities")).setType(ShinsuQuality.class).create();
    }

}