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

    private static final DataParameter<ItemStack> STACK = EntityDataManager.createKey(SpearEntity.class, DataSerializers.ITEMSTACK);

    public SpearEntity(EntityType<SpearEntity> type, World world) {
        this(type, world, ItemStack.EMPTY);
    }

    public SpearEntity(EntityType<SpearEntity> type, World world, ItemStack stack) {
        super(type, world);
        setStack(stack);
    }

    @Override
    protected void registerData() {
        super.registerData();
        getDataManager().register(STACK, ItemStack.EMPTY);
    }

    @Override
    protected ItemStack getArrowStack() {
        return getDataManager().get(STACK).copy();
    }

    public void setStack(ItemStack stack) {
        getDataManager().set(STACK, stack);
    }

    public boolean hasEffect() {
        return getArrowStack().hasEffect();
    }

    public IItemTier getTier() {
        Item item = getArrowStack().getItem();
        return item instanceof TieredItem ? ((TieredItem) item).getTier() : ItemTier.IRON;
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("Stack", Constants.NBT.TAG_COMPOUND)) {
            setStack(ItemStack.read(compound.getCompound("Stack")));
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.put("Stack", getArrowStack().write(new CompoundNBT()));
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult result) {
        Entity target = result.getEntity();
        ItemStack stack = getArrowStack();
        Vector3d motion = getMotion();
        double damage = (((TieredItem) stack.getItem()).getTier().getAttackDamage() * 2 + 2) * motion.length();
        if (target instanceof LivingEntity) {
            damage += EnchantmentHelper.getModifierForCreature(stack, ((LivingEntity) target).getCreatureAttribute());
        }
        Entity shooter = getShooter();
        DamageSource source = DamageSource.causeTridentDamage(this, shooter == null ? this : shooter);
        if (target.attackEntityFrom(source, (float) damage)) {
            if (target.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (target instanceof LivingEntity) {
                if (shooter instanceof LivingEntity) {
                    EnchantmentHelper.applyThornEnchantments((LivingEntity) target, shooter);
                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity) shooter, target);
                }
                arrowHit((LivingEntity) target);
            }
        }
        setMotion(motion.mul(-0.01, -0.1, -0.01));
        playSound(SoundEvents.ITEM_TRIDENT_HIT, 1, 1);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
