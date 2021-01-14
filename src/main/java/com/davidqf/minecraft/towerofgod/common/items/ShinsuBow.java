package com.davidqf.minecraft.towerofgod.common.items;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.entities.ShinsuArrowEntity;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuQuality;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import com.davidqf.minecraft.towerofgod.common.capabilities.IShinsuStats;
import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShinsuBow extends BowItem {

    public ShinsuBow() {
        super(new Item.Properties().setNoRepair());
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entity, int timeLeft) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity shooter = (PlayerEntity) entity;
            int charge = getUseDuration(stack) - timeLeft;
            charge = ForgeEventFactory.onArrowLoose(stack, worldIn, shooter, charge, true);
            if (charge < 0) {
                return;
            }
            float speed = getArrowVelocity(charge);
            if (speed >= 0.1 && !worldIn.isRemote()) {
                IShinsuStats stats = IShinsuStats.get(shooter);
                if (ShinsuTechnique.SHOOT_SHINSU_ARROW.getBuilder().canCast(ShinsuTechnique.SHOOT_SHINSU_ARROW, shooter, charge, null, shooter.getLookVec())) {
                    stats.cast(shooter, ShinsuTechnique.SHOOT_SHINSU_ARROW, charge, null, shooter.getLookVec());
                }
                worldIn.playSound(null, shooter.getPosX(), shooter.getPosY(), shooter.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1, 1 / (random.nextFloat() * 0.4f + 1.2f) + speed * 0.5f);
                shooter.addStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    @Override
    public AbstractArrowEntity customArrow(AbstractArrowEntity arrow) {
        ShinsuArrowEntity shinsu = RegistryHandler.SHINSU_ARROW_ENTITY.get().create(arrow.world);
        return shinsu == null ? arrow : shinsu;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        boolean canShoot = ShinsuTechnique.SHOOT_SHINSU_ARROW.getBuilder().canCast(ShinsuTechnique.SHOOT_SHINSU_ARROW, playerIn, 1, null, playerIn.getLookVec());
        ActionResult<ItemStack> ret = ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, handIn, true);
        if (ret != null) {
            return ret;
        } else if (canShoot) {
            playerIn.setActiveHand(handIn);
            return ActionResult.resultConsume(itemstack);
        } else {
            return ActionResult.resultFail(itemstack);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (worldIn instanceof ServerWorld) {
            CompoundNBT nbt = stack.getChildTag(TowerOfGod.MOD_ID);
            if (!stack.isEmpty() && nbt != null) {
                boolean contains = false;
                UUID id = nbt.getUniqueId("Technique");
                IShinsuStats stats = IShinsuStats.get(entityIn);
                for (ShinsuTechniqueInstance technique : stats.getTechniques()) {
                    if (technique.getID().equals(id)) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    IItemHandler inventory = entityIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
                    inventory.extractItem(itemSlot, stack.getCount(), false);
                }
            }
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Vector3d dir = target.getEyePosition(1).subtract(attacker.getEyePosition(1)).normalize();
        ShinsuQuality quality = ShinsuQuality.get(stack.getOrCreateChildTag(TowerOfGod.MOD_ID).getString("Quality"));
        quality.applyEntityEffect(target, new EntityRayTraceResult(target, dir));
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 0;
    }
}