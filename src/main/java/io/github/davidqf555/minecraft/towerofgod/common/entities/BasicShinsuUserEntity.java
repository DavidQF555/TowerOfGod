package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShootShinsuArrow;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public abstract class BasicShinsuUserEntity extends CreatureEntity implements IShinsuUser<BasicShinsuUserEntity>, IGeared<BasicShinsuUserEntity>, IRangedAttackMob {

    private static final DataParameter<String> GROUP = EntityDataManager.createKey(BasicShinsuUserEntity.class, DataSerializers.STRING);

    public BasicShinsuUserEntity(EntityType<? extends BasicShinsuUserEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        initializeShinsuStats(worldIn);
        initializeWeapons();
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(GROUP, Group.NONE.name());
    }

    @Override
    public void livingTick() {
        super.livingTick();
        heal(0.025f);
        if (isServerWorld()) {
            setCustomName(new TranslationTextComponent("entity." + TowerOfGod.MOD_ID + "." + getType().getRegistryName().getPath() + ".name", ShinsuStats.get(this).getLevel()).mergeStyle(getGroup().getTextFormattingColor()));
        }
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("Group", Constants.NBT.TAG_STRING)) {
            setGroup(Group.valueOf(nbt.getString("Group")));
        }
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putString("Group", getGroup().name());
    }

    @Override
    public BasicShinsuUserEntity getShinsuUserEntity() {
        return this;
    }

    @Override
    public Group getGroup() {
        return Group.valueOf(dataManager.get(GROUP));
    }

    @Override
    public void setGroup(Group group) {
        dataManager.set(GROUP, group.name());
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
        ShinsuStats stats = ShinsuStats.get(this);
        float velocity = 1.6f;
        float inaccuracy = (14 - world.getDifficulty().getId() * 4f) / stats.getLevel();
        if (arrow instanceof ShinsuArrowEntity) {
            Vector3d dir = new Vector3d(dX, dY, dZ);
            Optional<? extends ShinsuTechniqueInstance> technique = ShinsuTechniqueRegistry.SHOOT_SHINSU_ARROW.get().getFactory().doCreate(this, target, dir).left();
            if (technique.isPresent()) {
                ShootShinsuArrow inst = (ShootShinsuArrow) technique.get();
                inst.setVelocity(velocity);
                stats.cast((ServerWorld) world, inst);
                return;
            } else {
                arrow = ((ArrowItem) Items.ARROW).createArrow(world, new ItemStack(Items.ARROW), this);
            }
        }
        arrow.shoot(dX, dY, dZ, velocity, inaccuracy);
        world.addEntity(arrow);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public int getGearLevel() {
        return ShinsuStats.get(this).getLevel();
    }

    @Override
    public BasicShinsuUserEntity getGearedEntity() {
        return this;
    }

    @Override
    public boolean isWeaponPreferred(Item weapon) {
        return getGroup().isPreferredWeapon(weapon);
    }


}
