package io.github.davidqf555.minecraft.towerofgod.common.items.shinsu;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuArrowEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.IDData;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShinsuBowItem extends BowItem {

    public ShinsuBowItem(Properties properties) {
        super(properties.setNoRepair());
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player shooter) {
            int charge = getUseDuration(stack) - timeLeft;
            charge = ForgeEventFactory.onArrowLoose(stack, worldIn, shooter, charge, true);
            if (charge < 0) {
                return;
            }
            float speed = getPowerForTime(charge);
            CompoundTag tag = stack.getTagElement(TowerOfGod.MOD_ID);
            if (tag == null) {
                return;
            }
            if (speed >= 0.1 && !worldIn.isClientSide() && tag.contains("Technique", Tag.TAG_INT_ARRAY)) {
                ShinsuArrowEntity arrow = EntityRegistry.SHINSU_ARROW.get().create(worldIn);
                if (arrow != null) {
                    ShinsuAttribute attribute = ShinsuQualityData.get(shooter).getAttribute();
                    double velocity = speed * 3 * ShinsuAttribute.getSpeed(attribute);
                    arrow.setAttribute(attribute);
                    arrow.setTechnique(tag.getUUID("Technique"));
                    Vec3 dir = shooter.getLookAngle();
                    arrow.shoot(dir.x(), dir.y(), dir.z(), (float) velocity, 1);
                    arrow.setOwner(shooter);
                    arrow.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
                    worldIn.addFreshEntity(arrow);
                worldIn.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1, 1 / (shooter.getRandom().nextFloat() * 0.4f + 1.2f) + speed * 0.5f);
                shooter.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow) {
        ShinsuArrowEntity shinsu = EntityRegistry.SHINSU_ARROW.get().create(arrow.level);
        if (shinsu == null) {
            return arrow;
        } else {
            shinsu.absMoveTo(arrow.getX(), arrow.getY(), arrow.getZ(), arrow.getYRot(), arrow.getXRot());
            return shinsu;
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (worldIn instanceof ServerLevel) {
            if (!stack.isEmpty()) {
                CompoundTag nbt = stack.getTagElement(TowerOfGod.MOD_ID);
                if (nbt != null && entityIn instanceof LivingEntity) {
                    UUID id = nbt.getUUID("Technique");
                    if (IDData.getTechnique((LivingEntity) entityIn, id) != null) {
                        return;
                    }
                }
                IItemHandler inventory = entityIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
                if (inventory.getSlots() > itemSlot) {
                    inventory.extractItem(itemSlot, stack.getCount(), false);
                }
                inventory.extractItem(itemSlot, stack.getCount(), false);
            }
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Vec3 dir = target.getEyePosition(1).subtract(attacker.getEyePosition(1)).normalize();
        ShinsuAttribute attribute = ShinsuAttribute.getAttribute(stack);
        if (attribute != null) {
            attribute.applyEntityEffect(target, new EntityHitResult(target, dir));
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level world) {
        return 0;
    }
}
