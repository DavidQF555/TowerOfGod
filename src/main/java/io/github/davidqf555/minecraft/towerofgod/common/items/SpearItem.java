package io.github.davidqf555.minecraft.towerofgod.common.items;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.SpearEntity;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SpearItem extends DiggerItem implements Vanishable {

    public static final ResourceLocation THROWING = new ResourceLocation(TowerOfGod.MOD_ID, "throwing");

    public SpearItem(Tier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, tier, TagRegistry.MINEABLE_WITH_SPEAR, properties);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int duration = getUseDuration(stack) - timeLeft;
            if (duration >= 10) {
                if (!worldIn.isClientSide()) {
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(entity.getUsedItemHand()));
                    AbstractArrow proj = createProjectile(worldIn, stack);
                    proj.setOwner(player);
                    proj.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 2.5f, 1);
                    proj.setPos(player.getX(), player.getEyeY(), player.getZ());
                    if (player.getAbilities().instabuild) {
                        proj.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }
                    worldIn.addFreshEntity(proj);
                    worldIn.playSound(null, proj, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1, 1);
                    if (!player.getAbilities().instabuild) {
                        player.getInventory().removeItem(stack);
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    protected AbstractArrow createProjectile(Level world, ItemStack stack) {
        return new SpearEntity(EntityRegistry.SPEAR.get(), world, stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack item = playerIn.getItemInHand(handIn);
        if (item.getDamageValue() >= item.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(item);
        }
        playerIn.startUsingItem(handIn);
        return InteractionResultHolder.consume(item);
    }

}
