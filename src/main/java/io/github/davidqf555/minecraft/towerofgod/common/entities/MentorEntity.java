package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ServerUpdateUnlockedPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
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
    private ShinsuTechnique technique;

    public MentorEntity(EntityType<? extends MentorEntity> type, Level world) {
        super(type, world);
        technique = ShinsuTechniqueRegistry.BODY_REINFORCEMENT.get();
    }

    public static AttributeSupplier.Builder setAttributes() {
        return MentorEntity.createMobAttributes()
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
        goalSelector.addGoal(2, new CastFavoredTechniqueGoal<>(this, 50));
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
        List<ShinsuTechnique> all = ShinsuTechnique.getObtainableTechniques();
        technique = all.get(random.nextInt(all.size()));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        LivingEntity credit = getKillCredit();
        if (credit instanceof ServerPlayer) {
            PlayerTechniqueData data = PlayerTechniqueData.get((Player) credit);
            if (data.unlock(technique)) {
                credit.sendSystemMessage(Component.translatable(DEATH, technique.getText()));
                TowerOfGod.CHANNEL.send(new ServerUpdateUnlockedPacket(data.getUnlocked()), PacketDistributor.PLAYER.with((ServerPlayer) credit));
            }
        }
    }

    @Nullable
    @Override
    public Group getInitialGroup(RandomSource random) {
        List<Group> groups = technique.getUsageData().getMentorGroups();
        return groups.isEmpty() ? null : groups.get(random.nextInt(groups.size()));
    }

    @Nullable
    @Override
    public ShinsuAttribute getInitialAttribute(RandomSource random) {
        List<ShinsuAttribute> attributes = technique.getUsageData().getMentorAttributes();
        return attributes.isEmpty() ? null : attributes.get(random.nextInt(attributes.size()));
    }

    @Nullable
    @Override
    public ShinsuShape getInitialShape(RandomSource random) {
        List<ShinsuShape> shapes = technique.getUsageData().getMentorShapes();
        return shapes.isEmpty() ? null : shapes.get(random.nextInt(shapes.size()));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("Technique", technique.getId().toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Technique", Tag.TAG_STRING)) {
            technique = ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(compound.getString("Technique")));
        }
    }

    protected class CastFavoredTechniqueGoal<T extends Mob & IShinsuUser> extends CastShinsuGoal<T> {

        public CastFavoredTechniqueGoal(T entity, int cooldown) {
            super(entity, cooldown);
        }

        @Override
        protected ShinsuTechnique selectTechnique(List<ShinsuTechnique> possible) {
            return possible.contains(technique) ? technique : super.selectTechnique(possible);
        }

    }

}
