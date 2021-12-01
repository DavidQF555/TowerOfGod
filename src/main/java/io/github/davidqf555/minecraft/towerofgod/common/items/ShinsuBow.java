package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuArrowEntity;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
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
                ShinsuStats stats = ShinsuStats.get(shooter);
                ShinsuTechnique.SHOOT_SHINSU_ARROW.getBuilder().doBuild(shooter, charge, null, shooter.getLookVec()).ifLeft(instance -> {
                    stats.cast((ServerWorld) worldIn, instance);
                    worldIn.playSound(null, shooter.getPosX(), shooter.getPosY(), shooter.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1, 1 / (random.nextFloat() * 0.4f + 1.2f) + speed * 0.5f);
                    shooter.addStat(Stats.ITEM_USED.get(this));
                });
            }
        }
    }

    @Override
    public AbstractArrowEntity customArrow(AbstractArrowEntity arrow) {
        ShinsuArrowEntity shinsu = RegistryHandler.SHINSU_ARROW_ENTITY.get().create(arrow.world);
        if (shinsu == null) {
            return arrow;
        } else {
            shinsu.setPositionAndRotation(arrow.getPosX(), arrow.getPosY(), arrow.getPosZ(), arrow.rotationYaw, arrow.rotationPitch);
            return shinsu;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        ActionResult<ItemStack> ret = ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, handIn, true);
        if (ret != null) {
            return ret;
        } else if (ShinsuTechnique.SHOOT_SHINSU_ARROW.getBuilder().doBuild(playerIn, 1, null, playerIn.getLookVec()) != null) {
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
                UUID id = nbt.getUniqueId("Technique");
                ShinsuTechniqueInstance technique = ShinsuTechniqueInstance.get(entityIn, id);
                if (technique == null) {
                    IItemHandler inventory = entityIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
                    if (inventory.getSlots() > itemSlot) {
                        inventory.extractItem(itemSlot, stack.getCount(), false);
                    }
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
        ShinsuQuality quality = ShinsuQuality.getQuality(stack);
        quality.applyEntityEffect(target, new EntityRayTraceResult(target, dir));
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 0;
    }
}
