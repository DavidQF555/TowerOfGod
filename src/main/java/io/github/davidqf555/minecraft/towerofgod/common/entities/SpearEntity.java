package io.github.davidqf555.minecraft.towerofgod.common.entities;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SpearEntity extends AbstractArrowEntity {

    private static final DataParameter<ItemStack> STACK = EntityDataManager.defineId(SpearEntity.class, DataSerializers.ITEM_STACK);

    public SpearEntity(EntityType<SpearEntity> type, World world) {
        this(type, world, ItemStack.EMPTY);
    }

    public SpearEntity(EntityType<SpearEntity> type, World world, ItemStack stack) {
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

    public IItemTier getTier() {
        Item item = getPickupItem().getItem();
        return item instanceof TieredItem ? ((TieredItem) item).getTier() : ItemTier.IRON;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Stack", Constants.NBT.TAG_COMPOUND)) {
            setStack(ItemStack.of(compound.getCompound("Stack")));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.put("Stack", getPickupItem().save(new CompoundNBT()));
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        Entity target = result.getEntity();
        ItemStack stack = getPickupItem();
        Vector3d motion = getDeltaMovement();
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
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
