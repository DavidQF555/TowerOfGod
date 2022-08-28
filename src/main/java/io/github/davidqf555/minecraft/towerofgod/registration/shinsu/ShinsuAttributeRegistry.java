package io.github.davidqf555.minecraft.towerofgod.registration.shinsu;

import com.google.common.collect.ImmutableList;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects.*;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.filter.DropsFilter;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.filter.FactorDropsFilter;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.filter.SmeltDropsFilter;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
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
public final class ShinsuAttributeRegistry {

    public static final ResourceLocation ADVANCEMENT = new ResourceLocation(TowerOfGod.MOD_ID, "attributes");

    public static final DeferredRegister<ShinsuAttribute> QUALITIES = DeferredRegister.create(ShinsuAttribute.class, TowerOfGod.MOD_ID);

    public static final RegistryObject<ShinsuAttribute> LIGHTNING = register("lightning", () -> new ShinsuAttribute(ParticleTypes.INSTANT_EFFECT, DamageSource.LIGHTNING_BOLT, 1.5, 1, 0xFFfbff85, ShinsuAttributeEffect.combination(ImmutableList.of(new FireEntityAttributeEffect(3), new PotionAttributeEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20, 3, true, false, false)))), new SummonAttributeEffect<>(EntityType.LIGHTNING_BOLT::create), DropsFilter.NONE));
    public static final RegistryObject<ShinsuAttribute> FIRE = register("fire", () -> new ShinsuAttribute(ParticleTypes.FLAME, DamageSource.ON_FIRE, 1, 1, 0xFFff8119, new FireEntityAttributeEffect(7), new PlaceBlockAttributeEffect(AbstractFireBlock::getState), SmeltDropsFilter.INSTANCE));
    public static final RegistryObject<ShinsuAttribute> ICE = register("ice", () -> new ShinsuAttribute(ParticleTypes.POOF, DamageSource.MAGIC, 1, 1, 0xFFa8fbff, new PotionAttributeEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 60, 2, true, false, false)), new PlaceSphereAttributeEffect<>(5, (world, pos) -> {
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
    }), DropsFilter.NONE));
    public static final RegistryObject<ShinsuAttribute> STONE = register("stone", () -> new ShinsuAttribute(new BlockParticleData(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()), DamageSource.FALLING_BLOCK, 0.8, 1.5, 0xFF999999, (user, clip) -> {
    }, new DestroyBlockAttributeEffect((world, pos) -> {
        float hardness = world.getBlockState(pos).getDestroySpeed(world, pos);
        return hardness >= 0 && hardness <= Blocks.STONE.defaultBlockState().getDestroySpeed(world, pos);
    }), DropsFilter.NONE));
    public static final RegistryObject<ShinsuAttribute> WIND = register("wind", () -> new ShinsuAttribute(ParticleTypes.AMBIENT_ENTITY_EFFECT, DamageSource.CRAMMING, 1.4, 1, 0xAAabffac, new KnockbackAttributeEffect(1), new PushAttributeEffect<>(5, dist -> 3 / (dist + 1)), DropsFilter.NONE));
    public static final RegistryObject<ShinsuAttribute> CRYSTAL = register("crystal", () -> new ShinsuAttribute(new BlockParticleData(ParticleTypes.BLOCK, Blocks.GLASS.defaultBlockState()), DamageSource.MAGIC, 0.9, 2, 0xFFf7f7f7, (user, clip) -> {
    }, (entity, clip) -> {
    }, DropsFilter.NONE));
    public static final RegistryObject<ShinsuAttribute> PLANT = register("plant", () -> new ShinsuAttribute(ParticleTypes.COMPOSTER, DamageSource.CACTUS, 1, 1, 0xFF03ff2d, new PotionAttributeEffect(new EffectInstance(Effects.POISON, 140, 2, true, false, false)), GrowthAttributeEffect.INSTANCE, new FactorDropsFilter(stack -> Tags.Items.CROPS.contains(stack.getItem()) ? 2.0 : 1.0)));

    private static IForgeRegistry<ShinsuAttribute> registry = null;

    private ShinsuAttributeRegistry() {
    }

    public static IForgeRegistry<ShinsuAttribute> getRegistry() {
        return registry;
    }

    private static RegistryObject<ShinsuAttribute> register(String name, Supplier<ShinsuAttribute> attribute) {
        return QUALITIES.register(name, attribute);
    }

    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        registry = new RegistryBuilder<ShinsuAttribute>().setName(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_attributes")).setType(ShinsuAttribute.class).create();
    }

}