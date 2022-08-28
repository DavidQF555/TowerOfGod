package io.github.davidqf555.minecraft.towerofgod.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SpearEntity extends AbstractArrow {

    private static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(SpearEntity.class, EntityDataSerializers.ITEM_STACK);

    public SpearEntity(EntityType<SpearEntity> type, Level world) {
        this(type, world, ItemStack.EMPTY);
    }

    public SpearEntity(EntityType<SpearEntity> type, Level world, ItemStack stack) {
        super(type, world);
        setStack(stack);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(STACK, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getPickupItem() {
        return getEntityData().get(STACK).copy();
    }

    public void setStack(ItemStack stack) {
        getEntityData().set(STACK, stack);
    }

    public boolean hasEffect() {
        return getPickupItem().hasFoil();
    }

    public Tier getTier() {
        Item item = getPickupItem().getItem();
        return item instanceof TieredItem ? ((TieredItem) item).getTier() : Tiers.IRON;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Stack", Tag.TAG_COMPOUND)) {
            setStack(ItemStack.of(compound.getCompound("Stack")));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("Stack", getPickupItem().save(new CompoundTag()));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        ItemStack stack = getPickupItem();
        Vec3 motion = getDeltaMovement();
        double damage = (((TieredItem) stack.getItem()).getTier().getAttackDamageBonus() * 2 + 2) * motion.length();
        if (target instanceof LivingEntity) {
            damage += EnchantmentHelper.getDamageBonus(stack, ((LivingEntity) target).getMobType());
        }
        Entity shooter = getOwner();
        DamageSource source = DamageSource.trident(this, shooter == null ? this : shooter);
        if (target.hurt(source, (float) damage)) {
            if (target.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (target instanceof LivingEntity) {
                if (shooter instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects((LivingEntity) target, shooter);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) shooter, target);
                }
                doPostHurtEffects((LivingEntity) target);
            }
        }
        setDeltaMovement(motion.multiply(-0.01, -0.1, -0.01));
        playSound(SoundEvents.TRIDENT_HIT, 1, 1);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
