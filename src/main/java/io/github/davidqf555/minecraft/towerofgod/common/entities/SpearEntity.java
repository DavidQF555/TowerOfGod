package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SpearEntity extends AbstractArrowEntity {

    private static final DataParameter<ItemStack> STACK = EntityDataManager.createKey(SpearEntity.class, DataSerializers.ITEMSTACK);

    public SpearEntity(World worldIn, ItemStack stack) {
        super(RegistryHandler.SPEAR_ENTITY.get(), worldIn);
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
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static class Factory implements EntityType.IFactory<SpearEntity> {

        @Nonnull
        @Override
        public SpearEntity create(EntityType<SpearEntity> type, World world) {
            return new SpearEntity(world, ItemStack.EMPTY);
        }
    }

}
