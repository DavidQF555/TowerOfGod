package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ShinsuQuality {

    NONE(ParticleTypes.DRIPPING_WATER, DamageSource.DROWN, 1, 1, 0xAA24a6d1, (target, direction) -> {
    }, (entity, rayTrace) -> {
    }, (drops, context) -> drops, player -> -1.0),
    LIGHTNING(ParticleTypes.INSTANT_EFFECT, DamageSource.LIGHTNING_BOLT, 1.5, 1, 0xFFfbff85, (entity, rayTrace) -> {
        Entity target = rayTrace.getEntity();
        target.setFire(3);
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 20, 3, true, false, false));
        }
    }, (entity, rayTrace) -> {
        LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(entity.world);
        if (lightning != null) {
            lightning.moveForced(Vector3d.copyCenteredHorizontally(rayTrace.getPos().offset(rayTrace.getFace())));
            entity.world.addEntity(lightning);
        }
    }, (drops, context) -> drops, player -> player.getStats().getValue(Stats.CUSTOM, Stats.DAMAGE_DEALT) / 10.0),
    FIRE(ParticleTypes.FLAME, DamageSource.ON_FIRE, 1, 1, 0xFFff8119, (entity, rayTrace) -> rayTrace.getEntity().setFire(7), (entity, rayTrace) -> {
        World world = entity.getEntityWorld();
        BlockPos pos = rayTrace.getPos().offset(rayTrace.getFace());
        BlockState fire = AbstractFireBlock.getFireForPlacement(world, pos);
        world.setBlockState(pos, fire, 11);
    }, (drops, context) -> {
        List<ItemStack> smelted = new ArrayList<>();
        World world = context.getWorld();
        RecipeManager manager = context.getWorld().getRecipeManager();
        for (ItemStack drop : drops) {
            smelted.add(manager.getRecipe(IRecipeType.SMELTING, new Inventory(drop), world).map(FurnaceRecipe::getRecipeOutput).filter(item -> !item.isEmpty()).map(item -> ItemHandlerHelper.copyStackWithSize(item, drop.getCount() * item.getCount())).orElse(drop));
        }
        return smelted;
    }, player -> {
        StatisticsManager stats = player.getStats();
        double total = stats.getValue(Stats.CUSTOM, Stats.INTERACT_WITH_BLAST_FURNACE) + stats.getValue(Stats.CUSTOM, Stats.INTERACT_WITH_CAMPFIRE) + stats.getValue(Stats.CUSTOM, Stats.INTERACT_WITH_FURNACE) + stats.getValue(Stats.CUSTOM, Stats.INTERACT_WITH_SMOKER) + stats.getValue(Stats.ENTITY_KILLED, EntityType.BLAZE);
        total += 0.25 * Tags.Blocks.NETHERRACK.getAllElements().stream().mapToInt(block -> stats.getValue(Stats.BLOCK_MINED, block) + stats.getValue(Stats.ITEM_USED, block.asItem()) + stats.getValue(Stats.ITEM_CRAFTED, block.asItem())).sum();
        return total;
    }),
    ICE(ParticleTypes.POOF, DamageSource.MAGIC, 1, 1, 0xFFa8fbff, (entity, rayTrace) -> {
        Entity target = rayTrace.getEntity();
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 60, 2, true, false, false));
        }
    }, (entity, rayTrace) -> {
        BlockPos hitPos = rayTrace.isInside() ? rayTrace.getPos() : rayTrace.getPos().offset(rayTrace.getFace());
        int radius = 5;
        for (int y = -radius; y <= radius; y++) {
            double xRadius = Math.sqrt(radius * radius - y * y);
            int xRounded = (int) xRadius;
            for (int x = -xRounded; x <= xRounded; x++) {
                int zRounded = (int) Math.sqrt(xRadius * xRadius - x * x);
                for (int z = -zRounded; z <= zRounded; z++) {
                    BlockPos pos = hitPos.add(x, y, z);
                    BlockState state = entity.world.getBlockState(pos);
                    if (state.getFluidState().isTagged(FluidTags.WATER)) {
                        entity.world.setBlockState(pos, Blocks.ICE.getDefaultState());
                    }
                    BlockPos down = pos.down();
                    if (entity.world.getBlockState(down).isTopSolid(entity.world, down, entity, Direction.UP)) {
                        if (state.getBlock().isAir(state, entity.world, pos)) {
                            entity.world.setBlockState(pos, Blocks.SNOW.getDefaultState());
                        } else if (state.getBlock() instanceof SnowBlock) {
                            entity.world.setBlockState(pos, state.with(SnowBlock.LAYERS, Math.min(state.get(SnowBlock.LAYERS) + 1, 8)));
                        }
                    }
                }
            }
        }
    }, (drops, context) -> drops, player -> {
        StatisticsManager stats = player.getStats();
        return 1.0 * BlockTags.ICE.getAllElements().stream().mapToInt(block -> stats.getValue(Stats.BLOCK_MINED, block) + stats.getValue(Stats.ITEM_USED, block.asItem()) + stats.getValue(Stats.ITEM_CRAFTED, block.asItem())).sum();
    }),
    STONE(new BlockParticleData(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState()), DamageSource.FALLING_BLOCK, 0.8, 1.5, 0xFF999999, (entity, rayTrace) -> {
    }, (entity, rayTrace) -> {
        BlockPos pos = rayTrace.getPos();
        double hardness = entity.world.getBlockState(pos).getBlockHardness(entity.world, pos);
        if (hardness >= 0 && hardness <= Blocks.STONE.getDefaultState().getBlockHardness(entity.world, pos)) {
            entity.world.destroyBlock(pos, true, entity);
        }
    }, (drops, context) -> drops, player -> {
        Set<Block> blocks = new HashSet<>(Tags.Blocks.STONE.getAllElements());
        blocks.addAll(Tags.Blocks.COBBLESTONE.getAllElements());
        blocks.addAll(Tags.Blocks.SANDSTONE.getAllElements());
        blocks.addAll(Tags.Blocks.END_STONES.getAllElements());
        blocks.addAll(BlockTags.STONE_BRICKS.getAllElements());
        StatisticsManager stats = player.getStats();
        int total = blocks.stream().mapToInt(block -> stats.getValue(Stats.BLOCK_MINED, block)).sum();
        Set<Item> items = blocks.stream().map(Block::asItem).collect(Collectors.toSet());
        items.addAll(ItemTags.STONE_CRAFTING_MATERIALS.getAllElements());
        items.addAll(ItemTags.STONE_TOOL_MATERIALS.getAllElements());
        items.addAll(ItemTags.STONE_BRICKS.getAllElements());
        total += items.stream().mapToInt(item -> stats.getValue(Stats.ITEM_USED, item) + stats.getValue(Stats.ITEM_CRAFTED, item)).sum();
        return total * 0.1;
    }),
    WIND(ParticleTypes.AMBIENT_ENTITY_EFFECT, DamageSource.CRAMMING, 1.4, 1, 0xAAabffac, (entity, rayTrace) -> {
        Vector3d dir = rayTrace.getHitVec().normalize();
        Entity target = rayTrace.getEntity();
        target.addVelocity(dir.x, dir.y, dir.z);
    }, (entity, rayTrace) -> {
        double radius = 5;
        BlockPos center = rayTrace.isInside() ? rayTrace.getPos() : rayTrace.getPos().offset(rayTrace.getFace());
        AxisAlignedBB box = new AxisAlignedBB(center.add(-radius, -radius, -radius), center.add(radius, radius, radius));
        for (Entity target : entity.world.getEntitiesWithinAABBExcludingEntity(entity, box)) {
            Vector3d dir = target.getPositionVec().subtract(center.getX(), center.getY(), center.getZ());
            double length = dir.length();
            if (length <= radius) {
                double inverse = 3 / (length + 1);
                Vector3d vec = dir.normalize().scale(inverse);
                target.addVelocity(vec.x, vec.y, vec.z);
            }
        }
    }, (drops, context) -> drops, player -> {
        StatisticsManager stats = player.getStats();
        return 0.1 * stats.getValue(Stats.CUSTOM, Stats.AVIATE_ONE_CM);
    }),
    CRYSTAL(new BlockParticleData(ParticleTypes.BLOCK, Blocks.GLASS.getDefaultState()), DamageSource.MAGIC, 0.9, 2, 0xFFf7f7f7, (entity, rayTrace) -> {
    }, (entity, rayTrace) -> {
    }, (drops, context) -> drops, player -> {
        Set<Block> blocks = new HashSet<>(Tags.Blocks.ORES.getAllElements());
        blocks.addAll(Tags.Blocks.GLASS.getAllElements());
        blocks.addAll(Tags.Blocks.GLASS_PANES.getAllElements());
        StatisticsManager stats = player.getStats();
        int total = blocks.stream().mapToInt(block -> stats.getValue(Stats.BLOCK_MINED, block) + stats.getValue(Stats.ITEM_USED, block.asItem()) + stats.getValue(Stats.ITEM_CRAFTED, block.asItem())).sum();
        return total * 0.5;
    }),
    PLANT(ParticleTypes.COMPOSTER, DamageSource.CACTUS, 1, 1, 0xFF03ff2d, (entity, rayTrace) -> {
        Entity target = rayTrace.getEntity();
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).addPotionEffect(new EffectInstance(Effects.POISON, 140, 2, true, false, false));
        }
    }, (entity, rayTrace) -> {
        BlockPos hit = rayTrace.getPos();
        BlockPos pos = hit.offset(rayTrace.getFace());
        BlockState state = entity.world.getBlockState(pos);
        Block b = state.getBlock();
        if (b instanceof IGrowable) {
            if (entity.world instanceof ServerWorld && ((IGrowable) b).canGrow(entity.world, pos, state, entity.world.isRemote())) {
                ((IGrowable) b).grow((ServerWorld) entity.world, entity.world.rand, pos, state);
            }
        } else {
            state = entity.world.getBlockState(hit);
            b = state.getBlock();
            if (b instanceof IGrowable) {
                if (entity.world instanceof ServerWorld && ((IGrowable) b).canGrow(entity.world, hit, state, entity.world.isRemote())) {
                    ((IGrowable) b).grow((ServerWorld) entity.world, entity.world.rand, hit, state);
                }
            }
        }
    }, (drops, context) -> {
        List<ItemStack> increased = new ArrayList<>();
        for (ItemStack drop : drops) {
            if (drop.getItem().isIn(Tags.Items.CROPS)) {
                drop.setCount(drop.getCount() * 2);
            }
            increased.add(drop);
        }
        return increased;
    }, player -> {
        StatisticsManager stats = player.getStats();
        int amount = BlockTags.FLOWERS.getAllElements().stream().mapToInt(block -> stats.getValue(Stats.BLOCK_MINED, block)).sum();
        Set<Item> items = new HashSet<>(ItemTags.FLOWERS.getAllElements());
        items.addAll(Tags.Items.CROPS.getAllElements());
        amount += items.stream().mapToInt(item -> stats.getValue(Stats.ITEM_USED, item) + stats.getValue(Stats.ITEM_CRAFTED, item)).sum();
        return amount * 2.0;
    });

    private final IParticleData particleType;
    private final DamageSource source;
    private final double speed;
    private final double damage;
    private final int color;
    private final BiConsumer<Entity, EntityRayTraceResult> entityEffect;
    private final BiConsumer<Entity, BlockRayTraceResult> blockEffect;
    private final BiFunction<List<ItemStack>, LootContext, List<ItemStack>> dropsFilter;
    private final Function<ServerPlayerEntity, Double> suitability;

    ShinsuQuality(IParticleData particleType, DamageSource source, double speed, double damage, int color, BiConsumer<Entity, EntityRayTraceResult> entityEffect, BiConsumer<Entity, BlockRayTraceResult> rightClickEffect, BiFunction<List<ItemStack>, LootContext, List<ItemStack>> dropsFilter, Function<ServerPlayerEntity, Double> suitability) {
        this.particleType = particleType;
        this.source = source;
        this.speed = speed;
        this.damage = damage;
        this.color = color;
        this.entityEffect = entityEffect;
        this.blockEffect = rightClickEffect;
        this.dropsFilter = dropsFilter;
        this.suitability = suitability;
    }

    public static void setQuality(ItemStack item, ShinsuQuality quality) {
        item.getOrCreateChildTag(TowerOfGod.MOD_ID).putString("Quality", quality.name());
    }

    public static ShinsuQuality getQuality(ItemStack item) {
        CompoundNBT nbt = item.getOrCreateChildTag(TowerOfGod.MOD_ID);
        if (nbt.contains("Quality", Constants.NBT.TAG_STRING)) {
            try {
                return valueOf(nbt.getString("Quality"));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return ShinsuQuality.NONE;
    }

    public IParticleData getParticleType() {
        return particleType;
    }

    public DamageSource getSource() {
        return source;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDamage() {
        return damage;
    }

    public int getColor() {
        return color;
    }

    public double getSuitability(ServerPlayerEntity player) {
        return suitability.apply(player);
    }

    public void applyEntityEffect(Entity user, EntityRayTraceResult rayTrace) {
        entityEffect.accept(user, rayTrace);
    }

    public void applyBlockEffect(Entity user, BlockRayTraceResult rayTrace) {
        blockEffect.accept(user, rayTrace);
    }

    public List<ItemStack> filterDrops(List<ItemStack> drops, LootContext context) {
        return dropsFilter.apply(drops, context);
    }

}
