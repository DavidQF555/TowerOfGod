package io.github.davidqf555.minecraft.towerofgod.common.items;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PredictedShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ClickerEntity;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ServerUpdateAttributePacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClickerItem extends Item {

    public ClickerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ClickerEntity entity = EntityRegistry.CLICKER.get().create(worldIn);
        if (entity != null) {
            Vec3 eye = playerIn.getEyePosition(1);
            BlockHitResult result = worldIn.clip(new ClipContext(eye, eye.add(playerIn.getLookAngle().scale(4)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
            Vec3 spawn = result.getLocation();
            entity.setPos(spawn.x(), spawn.y(), spawn.z());
            ItemStack item = playerIn.getItemInHand(handIn);
            if (!playerIn.isCreative()) {
                item.setCount(item.getCount() - 1);
            }
            if (playerIn instanceof ServerPlayer serverPlayer) {
                ShinsuAttribute attribute = getAttribute(serverPlayer);
                ShinsuShape shape = getShape(serverPlayer);
                ShinsuQualityData stats = ShinsuQualityData.get(serverPlayer);
                stats.setAttribute(attribute);
                stats.setShape(shape);
                entity.setAttribute(attribute);
                entity.setShape(shape);
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, item);
                serverPlayer.awardStat(Stats.ITEM_USED.get(this));
                TowerOfGod.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayer), new ServerUpdateAttributePacket(serverPlayer.getId(), attribute));
            }
            worldIn.addFreshEntity(entity);
            return InteractionResultHolder.sidedSuccess(item, playerIn.level().isClientSide());
        }
        return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
    }

    private ShinsuAttribute getAttribute(ServerPlayer player) {
        ShinsuAttribute predicted = PredictedShinsuQuality.get(player).getAttribute();
        if (predicted == null) {
            return Util.getRandom(ShinsuAttributeRegistry.getRegistry().getValues().toArray(new ShinsuAttribute[0]), player.getRandom());
        }
        return predicted;
    }

    private ShinsuShape getShape(ServerPlayer player) {
        ShinsuShape predicted = PredictedShinsuQuality.get(player).getShape();
        if (predicted == null) {
            return Util.getRandom(ShinsuShapeRegistry.getRegistry().getValues().toArray(new ShinsuShape[0]), player.getRandom());
        }
        return predicted;
    }

}
