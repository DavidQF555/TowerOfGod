package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorDimensionsHelper;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RankerEntity extends BasicShinsuUserEntity {

    private static final String DEFEAT = "entity." + TowerOfGod.MOD_ID + ".ranker.defeat";
    private final ServerBossInfo info;
    private int floorLevel;

    public RankerEntity(EntityType<RankerEntity> type, World world) {
        super(type, world);
        floorLevel = 1;
        info = new ServerBossInfo(getDisplayName(), BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS);
        info.setVisible(false);
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return RankerEntity.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.215)
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ATTACK_DAMAGE, 1);
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        FloorProperty property = FloorDimensionsHelper.getFloorProperty(worldIn.getLevel());
        floorLevel = property == null ? 1 : property.getLevel();
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(1, new SwapWeaponToMainHandGoal<>(this, 5));
        goalSelector.addGoal(2, new CastShinsuGoal<>(this));
        goalSelector.addGoal(3, new RangedMainHandAttackGoal<>(this, 1, 12, 15));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.5, true));
        goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1));
        goalSelector.addGoal(7, new LookRandomlyGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        Entity best = cause.getEntity();
        if (best instanceof PlayerEntity || best instanceof IShinsuUser) {
            ShinsuStats stats = ShinsuStats.get(best);
            if (stats.getLevel() == floorLevel) {
                stats.addLevel(1);
                IFormattableTextComponent text = new TranslationTextComponent(DEFEAT, floorLevel, floorLevel + 1);
                Group group = getGroup();
                if (group != null) {
                    text = text.withStyle(group.getTextFormattingColor());
                }
                best.sendMessage(text, getUUID());
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        info.setPercent(getHealth() / getMaxHealth());
        info.setVisible(getTarget() != null);
    }

    @Override
    public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        info.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayerEntity player) {
        super.stopSeenByPlayer(player);
        info.removePlayer(player);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (hasCustomName()) {
            info.setName(getDisplayName());
        }
        if (compound.contains("Floor", Constants.NBT.TAG_INT)) {
            floorLevel = compound.getInt("Floor");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Floor", floorLevel);
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        info.setName(getDisplayName());
    }

    @Override
    public int getMaxInitialLevel(int floor) {
        return 40 + floor * 3;
    }

    @Override
    public int getMinInitialLevel(int floor) {
        return 20 + floor * 3;
    }

    @Override
    protected int getExperienceReward(PlayerEntity player) {
        return ShinsuStats.get(this).getLevel() - random.nextInt(3) + 7;
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlotType slotIn) {
        return 0;
    }

    @Override
    public void setGroup(Group group) {
        super.setGroup(group);
        info.setColor(group.getBossInfoColor());
    }

}
