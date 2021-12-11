package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.client.render.SpearTileEntityRenderer;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.SpearEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.UseAction;
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

    public SpearItem(IItemTier tier, float attackDamage, float attackSpeed) {
        super(attackDamage, attackSpeed, tier, new HashSet<>(), new Properties().group(TowerOfGod.TAB).setISTER(() -> SpearTileEntityRenderer::new));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entity, int timeLeft) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            int duration = getUseDuration(stack) - timeLeft;
            if (duration >= 10) {
                if (!worldIn.isRemote()) {
                    stack.damageItem(1, player, p -> p.sendBreakAnimation(entity.getActiveHand()));
                    AbstractArrowEntity proj = createProjectile(worldIn, stack);
                    proj.setShooter(player);
                    proj.setDirectionAndMovement(player, player.rotationPitch, player.rotationYaw, 0, 2.5f, 1);
                    proj.setPosition(player.getPosX(), player.getPosYEye(), player.getPosZ());
                    if (player.abilities.isCreativeMode) {
                        proj.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                    }
                    worldIn.addEntity(proj);
                    worldIn.playMovingSound(null, proj, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1, 1);
                    if (!player.abilities.isCreativeMode) {
                        player.inventory.deleteStack(stack);
                    }
                    player.addStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    protected AbstractArrowEntity createProjectile(World world, ItemStack stack) {
        return new SpearEntity(world, stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack item = playerIn.getHeldItem(handIn);
        if (item.getDamage() >= item.getMaxDamage() - 1) {
            return ActionResult.resultFail(item);
        }
        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(item);
    }


}
