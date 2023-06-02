package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.client.render.SpearItemStackRenderer;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.SpearEntity;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SpearItem extends ToolItem implements IVanishable {

    public static final ResourceLocation THROWING = new ResourceLocation(TowerOfGod.MOD_ID, "throwing");

    public SpearItem(IItemTier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, tier, new HashSet<>(), properties.setISTER(() -> SpearItemStackRenderer::new));
    }

    @Override
    public boolean canAttackBlock(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, World worldIn, LivingEntity entity, int timeLeft) {
        if (entity instanceof PlayerEntity) {
            int duration = getUseDuration(stack) - timeLeft;
            if (duration >= 10 && !worldIn.isClientSide()) {
                launchSpear((PlayerEntity) entity, stack);
            }
        }
    }

    protected AbstractArrowEntity createProjectile(World world, ItemStack stack) {
        return new SpearEntity(EntityRegistry.SPEAR.get(), world, stack);
    }

    protected AbstractArrowEntity launchSpear(LivingEntity user, ItemStack stack) {
        stack.hurtAndBreak(1, user, p -> p.broadcastBreakEvent(user.getUsedItemHand()));
        AbstractArrowEntity proj = createProjectile(user.level, stack);
        proj.setOwner(user);
        proj.shootFromRotation(user, user.xRot, user.yRot, 0, 2.5f, 1);
        proj.setPos(user.getX(), user.getEyeY(), user.getZ());
        user.level.addFreshEntity(proj);
        user.level.playSound(null, proj, SoundEvents.TRIDENT_THROW, SoundCategory.PLAYERS, 1, 1);
        return proj;
    }

    protected void launchSpear(PlayerEntity player, ItemStack stack) {
        AbstractArrowEntity proj = launchSpear((LivingEntity) player, stack);
        if (player.abilities.instabuild) {
            proj.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
        }
        if (!player.abilities.instabuild) {
            player.inventory.removeItem(stack);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack item = playerIn.getItemInHand(handIn);
        if (item.getDamageValue() >= item.getMaxDamage() - 1) {
            return ActionResult.fail(item);
        }
        playerIn.startUsingItem(handIn);
        return ActionResult.consume(item);
    }

}
