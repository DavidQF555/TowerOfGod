package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.PredictedShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ClickerEntity;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientAttributePacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClickerItem extends Item {

    public ClickerItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ClickerEntity entity = EntityRegistry.CLICKER.get().create(worldIn);
        if (entity != null) {
            Vector3d eye = playerIn.getEyePosition(1);
            BlockRayTraceResult result = worldIn.clip(new RayTraceContext(eye, eye.add(playerIn.getLookAngle().scale(4)), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
            Vector3d spawn = result.getLocation();
            entity.setPos(spawn.x(), spawn.y(), spawn.z());
            ItemStack item = playerIn.getItemInHand(handIn);
            if (!playerIn.isCreative()) {
                item.setCount(item.getCount() - 1);
            }
            if (playerIn instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerIn;
                ShinsuAttribute attribute = getAttribute(serverPlayer);
                ShinsuShape shape = getShape(serverPlayer);
                ShinsuStats stats = ShinsuStats.get(serverPlayer);
                stats.setAttribute(attribute);
                stats.setShape(shape);
                entity.setAttribute(attribute);
                entity.setShape(shape);
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, item);
                serverPlayer.awardStat(Stats.ITEM_USED.get(this));
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new UpdateClientAttributePacket(attribute));
            }
            worldIn.addFreshEntity(entity);
            return ActionResult.sidedSuccess(item, playerIn.level.isClientSide());
        }
        return ActionResult.pass(playerIn.getItemInHand(handIn));
    }

    private ShinsuAttribute getAttribute(ServerPlayerEntity player) {
        ShinsuAttribute predicted = PredictedShinsuQuality.get(player).getAttribute();
        if (predicted == null) {
            return Util.getRandom(ShinsuAttributeRegistry.getRegistry().getValues().toArray(new ShinsuAttribute[0]), player.getRandom());
        }
        return predicted;
    }

    private ShinsuShape getShape(ServerPlayerEntity player) {
        ShinsuShape predicted = PredictedShinsuQuality.get(player).getShape();
        if (predicted == null) {
            return Util.getRandom(ShinsuShapeRegistry.getRegistry().getValues().toArray(new ShinsuShape[0]), player.getRandom());
        }
        return predicted;
    }

}
