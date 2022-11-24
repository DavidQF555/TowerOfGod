package io.github.davidqf555.minecraft.towerofgod.common.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RankerEntity extends BasicShinsuUserEntity {

    private final ServerBossInfo info;

    public RankerEntity(EntityType<? extends RankerEntity> type, World world) {
        super(type, world);
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

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(1, new SwapWeaponToMainHandGoal<>(this, 5));
        goalSelector.addGoal(2, new CastShinsuGoal<>(this, 50));
        goalSelector.addGoal(3, new RangedMainHandAttackGoal<>(this, 1, 12, 15));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.5, true));
        goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1));
        goalSelector.addGoal(7, new LookRandomlyGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
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
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        info.setName(getDisplayName());
    }

    @Override
    public int getMaxInitialLevel() {
        return 100;
    }

    @Override
    public int getMinInitialLevel() {
        return 60;
    }

    @Override
    protected int getExperienceReward(PlayerEntity player) {
        return getLevel() - random.nextInt(3) + 7;
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
