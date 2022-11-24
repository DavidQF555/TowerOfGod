package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class MentorEntity extends RankerEntity {

    private static final String DEATH = Util.makeDescriptionId("entity", new ResourceLocation(TowerOfGod.MOD_ID, "mentor")) + ".death";
    private ShinsuTechnique technique;

    public MentorEntity(EntityType<? extends MentorEntity> type, World world) {
        super(type, world);
        technique = ShinsuTechniqueRegistry.BODY_REINFORCEMENT.get();
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return MentorEntity.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.215)
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ATTACK_DAMAGE, 1);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(1, new SwapWeaponToMainHandGoal<>(this, 5));
        goalSelector.addGoal(2, new CastFavoredTechniqueGoal<>(this, 50));
        goalSelector.addGoal(3, new RangedMainHandAttackGoal<>(this, 1, 12, 15));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.5, true));
        goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1));
        goalSelector.addGoal(7, new LookRandomlyGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        List<ShinsuTechnique> all = ShinsuTechnique.getObtainableTechniques();
        technique = all.get(random.nextInt(all.size()));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level.isClientSide()) {
            LivingEntity credit = getKillCredit();
            if (credit instanceof PlayerEntity && PlayerTechniqueData.get((PlayerEntity) credit).unlock(technique)) {
                credit.sendMessage(new TranslationTextComponent(DEATH, technique.getText()), getUUID());
            }
        }
    }

    @Nullable
    @Override
    public Group getInitialGroup(Random random) {
        List<Group> groups = technique.getUsageData().getMentorGroups();
        return groups.isEmpty() ? null : groups.get(random.nextInt(groups.size()));
    }

    @Nullable
    @Override
    public ShinsuAttribute getInitialAttribute(Random random) {
        List<ShinsuAttribute> attributes = technique.getUsageData().getMentorAttributes();
        return attributes.isEmpty() ? null : attributes.get(random.nextInt(attributes.size()));
    }

    @Nullable
    @Override
    public ShinsuShape getInitialShape(Random random) {
        List<ShinsuShape> shapes = technique.getUsageData().getMentorShapes();
        return shapes.isEmpty() ? null : shapes.get(random.nextInt(shapes.size()));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("Technique", technique.getRegistryName().toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Technique", Constants.NBT.TAG_STRING)) {
            technique = ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(compound.getString("Technique")));
        }
    }

    protected class CastFavoredTechniqueGoal<T extends MobEntity & IShinsuUser> extends CastShinsuGoal<T> {

        public CastFavoredTechniqueGoal(T entity, int cooldown) {
            super(entity, cooldown);
        }

        @Override
        protected ShinsuTechnique selectTechnique(List<ShinsuTechnique> possible) {
            return possible.contains(technique) ? technique : super.selectTechnique(possible);
        }

    }

}
