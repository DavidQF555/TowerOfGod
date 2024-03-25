package io.github.davidqf555.minecraft.towerofgod.common.entities;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ServerUpdateUnlockedPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class MentorEntity extends RankerEntity {

    private static final String DEATH = Util.makeDescriptionId("entity", new ResourceLocation(TowerOfGod.MOD_ID, "mentor")) + ".death";
    private ResourceKey<ConfiguredShinsuTechniqueType<?, ?>> technique;

    public MentorEntity(EntityType<? extends MentorEntity> type, Level world) {
        super(type, world);
    }

    public static AttributeSupplier.Builder setAttributes() {
        return createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.215)
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ATTACK_DAMAGE, 1);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new SwapWeaponToMainHandGoal<>(this, 5));
        goalSelector.addGoal(2, new CastFavoredTechniqueGoal<>(this, 20, 50));
        goalSelector.addGoal(3, new RangedMainHandAttackGoal<>(this, 1, 12, 15));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.5, true));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        List<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>> all = ConfiguredTechniqueTypeRegistry.getRegistry(worldIn.registryAccess()).keySet().stream()
                .map(loc -> ResourceKey.create(ConfiguredTechniqueTypeRegistry.REGISTRY, loc))
                .toList();
        if (all.isEmpty()) {
            discard();
        } else {
            technique = all.get(random.nextInt(all.size()));
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        LivingEntity credit = getKillCredit();
        if (credit instanceof ServerPlayer && technique != null) {
            PlayerTechniqueData data = PlayerTechniqueData.get((Player) credit);
            if (data.unlock(technique)) {
                ConfiguredShinsuTechniqueType<?, ?> type = ConfiguredTechniqueTypeRegistry.getRegistry(credit.getServer().registryAccess()).getOrThrow(technique);
                credit.sendMessage(new TranslatableComponent(DEATH, type.getConfig().getDisplay().name()), getUUID());
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) credit), new ServerUpdateUnlockedPacket(data.getUnlocked()));
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (technique != null) {
            nbt.putString("Technique", technique.location().toString());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Technique", Tag.TAG_STRING)) {
            technique = ResourceKey.create(ConfiguredTechniqueTypeRegistry.REGISTRY, new ResourceLocation(compound.getString("Technique")));
        }
    }

    protected class CastFavoredTechniqueGoal<T extends Mob & IShinsuUser<T>> extends CastShinsuGoal<T> {

        public CastFavoredTechniqueGoal(T entity, int cast, int cooldown) {
            super(entity, cast, cooldown);
        }

        @Override
        protected Pair<ConfiguredShinsuTechniqueType<?, ?>, BaangEntity> selectTechnique(List<Pair<ConfiguredShinsuTechniqueType<?, ?>, BaangEntity>> possible) {
            for (Pair<ConfiguredShinsuTechniqueType<?, ?>, BaangEntity> pair : possible) {
                if (technique.location().equals(pair.getFirst().getRegistryName())) {
                    return pair;
                }
            }
            return super.selectTechnique(possible);
        }

    }

}
