package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.goals.RangedMainHandAttackGoal;
import io.github.davidqf555.minecraft.towerofgod.common.entities.goals.SwapWeaponToMainHandGoal;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShootShinsuArrow;
import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorDimensionsHelper;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorProperty;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RankerEntity extends CreatureEntity implements IShinsuUser<RankerEntity>, IGeared<RankerEntity>, IRangedAttackMob {

    private static final int MAX_LEVEL = 1;
    private static final DataParameter<String> GROUP = EntityDataManager.createKey(RankerEntity.class, DataSerializers.STRING);
    private static final String NAME = "entity." + TowerOfGod.MOD_ID + ".ranker_entity.name";
    private static final String DEFEAT = "entity." + TowerOfGod.MOD_ID + ".ranker_entity.defeat";
    private final ServerBossInfo info;
    private int floorLevel;
    private int level;

    public RankerEntity(World worldIn) {
        super(RegistryHandler.RANKER_ENTITY.get(), worldIn);
        floorLevel = 1;
        level = 1;
        info = new ServerBossInfo(getDisplayName(), BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS);
        info.setVisible(false);
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return RankerEntity.func_233666_p_()
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 32)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.215)
                .createMutableAttribute(Attributes.MAX_HEALTH, 20)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1);
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        FloorProperty property = FloorDimensionsHelper.getFloorProperty(worldIn.getWorld());
        floorLevel = property == null ? 1 : property.getLevel();
        initializeShinsuStats(worldIn);
        initializeWeapons();
        setCustomName(new TranslationTextComponent(NAME, getShinsuLevel()).mergeStyle(getGroup().getTextFormattingColor()));
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
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
    public int getInitialMaxBaangs() {
        return floorLevel;
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(GROUP, Group.NONE.name());
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        Entity best = cause.getTrueSource();
        if (best != null) {
            ShinsuStats stats = ShinsuStats.get(best);
            if (stats.getLevel() == floorLevel) {
                stats.addLevel(1);
                best.sendMessage(new TranslationTextComponent(DEFEAT, floorLevel, floorLevel + 1).mergeStyle(getGroup().getTextFormattingColor()), getUniqueID());
            }
        }
    }

    @Override
    public void livingTick() {
        super.livingTick();
        heal(0.025f);
        info.setPercent(getHealth() / getMaxHealth());
        info.setVisible(getAttackTarget() != null);
    }

    @Override
    public void addTrackingPlayer(ServerPlayerEntity player) {
        super.addTrackingPlayer(player);
        info.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(ServerPlayerEntity player) {
        super.removeTrackingPlayer(player);
        info.removePlayer(player);
    }

    @Override
    public int getGearLevel() {
        return getShinsuLevel();
    }

    @Override
    public RankerEntity getGearedEntity() {
        return this;
    }

    @Override
    public boolean isWeaponPreferred(Item weapon) {
        return getGroup().isPreferredWeapon(weapon);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (hasCustomName()) {
            info.setName(getDisplayName());
        }
        if (compound.contains("Group", Constants.NBT.TAG_STRING)) {
            setGroup(Group.valueOf(compound.getString("Group")));
        }
        if (compound.contains("Level", Constants.NBT.TAG_INT)) {
            setShinsuLevel(compound.getInt("Level"));
        }
        if (compound.contains("FloorLevel", Constants.NBT.TAG_INT)) {
            floorLevel = compound.getInt("FloorLevel");
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("Group", getGroup().name());
        compound.putInt("Level", getShinsuLevel());
        compound.putInt("FloorLevel", floorLevel);
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        info.setName(getDisplayName());
    }

    @Override
    public RankerEntity getShinsuUserEntity() {
        return this;
    }

    @Override
    public int getMaxInitialLevel(int floor) {
        return 25 + floor * 25;
    }

    @Override
    public int getMinInitialLevel(int floor) {
        return 10 + floor * 25;
    }

    @Override
    public int getShinsuLevel() {
        return level;
    }

    @Override
    public void setShinsuLevel(int level) {
        this.level = level;
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return level - rand.nextInt(3) + 7;
    }

    @Override
    protected float getDropChance(EquipmentSlotType slotIn) {
        return 0;
    }

    @Override
    public Group getGroup() {
        return Group.valueOf(dataManager.get(GROUP));
    }

    @Override
    public void setGroup(Group group) {
        dataManager.set(GROUP, group.name());
        info.setColor(group.getBossInfoColor());
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack hand = getHeldItem(Hand.MAIN_HAND);
        ItemStack ammo = findAmmo(hand);
        AbstractArrowEntity arrow = ProjectileHelper.fireArrow(this, ammo, distanceFactor);
        if (getHeldItemMainhand().getItem() instanceof BowItem) {
            arrow = ((BowItem) getHeldItemMainhand().getItem()).customArrow(arrow);
        }
        double dX = target.getPosX() - getPosX();
        double dZ = target.getPosZ() - getPosZ();
        double dY = target.getPosYHeight(0.3333333333333333) - arrow.getPosY() + MathHelper.sqrt(dX * dX + dZ * dZ) * 0.2;
        float velocity = 1.6f;
        float inaccuracy = (14 - world.getDifficulty().getId() * 4f) / getShinsuLevel();
        if (arrow instanceof ShinsuArrowEntity) {
            Vector3d dir = new Vector3d(dX, dY, dZ);
            ShinsuStats stats = ShinsuStats.get(this);
            ShinsuTechniqueInstance technique = ShinsuTechnique.SHOOT_SHINSU_ARROW.getBuilder().doBuild(this, ShootShinsuArrow.getLevelForVelocity(velocity, stats.getQuality()), target, dir, null);
            if (technique == null) {
                arrow = ((ArrowItem) Items.ARROW).createArrow(world, new ItemStack(Items.ARROW), this);
            } else {
                stats.cast((ServerWorld) world, technique);
                return;
            }
        }
        arrow.shoot(dX, dY, dZ, velocity, inaccuracy);
        world.addEntity(arrow);
    }

    public static class Factory implements EntityType.IFactory<RankerEntity> {

        @Override
        public RankerEntity create(EntityType<RankerEntity> type, World world) {
            return new RankerEntity(world);
        }
    }
}
