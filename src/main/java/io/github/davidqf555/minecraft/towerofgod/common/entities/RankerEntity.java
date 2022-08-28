package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RankerEntity extends BasicShinsuUserEntity {

    private final ServerBossEvent info;

    public RankerEntity(EntityType<RankerEntity> type, Level world) {
        super(type, world);
        info = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS);
        info.setVisible(false);
    }

    public static AttributeSupplier.Builder setAttributes() {
        return RankerEntity.createMobAttributes()
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
        goalSelector.addGoal(2, new CastShinsuGoal<>(this));
        goalSelector.addGoal(3, new RangedMainHandAttackGoal<>(this, 1, 12, 15));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.5, true));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        info.setProgress(getHealth() / getMaxHealth());
        info.setVisible(getTarget() != null);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        info.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        info.removePlayer(player);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (hasCustomName()) {
            info.setName(getDisplayName());
        }
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        info.setName(getDisplayName());
    }

    @Override
    public int getMaxInitialLevel() {
        return 60;
    }

    @Override
    public int getMinInitialLevel() {
        return 100;
    }

    @Override
    protected int getExperienceReward(Player player) {
        return ShinsuStats.get(this).getLevel() - random.nextInt(3) + 7;
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot slotIn) {
        return 0;
    }

    @Override
    public void setGroup(Group group) {
        super.setGroup(group);
        info.setColor(group.getBossInfoColor());
    }

}
