package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
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
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        double total = 0;
        Map<ShinsuAttribute, Double> suitabilities = new HashMap<>();
        for (ShinsuAttribute attribute : ShinsuAttributeRegistry.getRegistry()) {
            double suitability = attribute.getSuitability(player);
            if (suitability > 0) {
                total += suitability;
                suitabilities.put(attribute, suitability);
            }
        }
        double current = 0;
        double random = player.getRandom().nextDouble() * total;
        for (ShinsuAttribute attribute : suitabilities.keySet()) {
            current += suitabilities.get(attribute);
            if (random < current) {
                return attribute;
            }
        }
        List<ShinsuAttribute> all = new ArrayList<>(ShinsuAttributeRegistry.getRegistry().getValues());
        return all.get(player.getRandom().nextInt(all.size()));
    }

    private ShinsuShape getShape(ServerPlayerEntity player) {
        double total = 0;
        Map<ShinsuShape, Double> suitabilities = new HashMap<>();
        for (ShinsuShape shape : ShinsuShapeRegistry.getRegistry()) {
            double suitability = shape.getSuitability(player);
            if (suitability > 0) {
                total += suitability;
                suitabilities.put(shape, suitability);
            }
        }
        double current = 0;
        double random = player.getRandom().nextDouble() * total;
        for (ShinsuShape shape : suitabilities.keySet()) {
            current += suitabilities.get(shape);
            if (random < current) {
                return shape;
            }
        }
        ShinsuShape[] all = ShinsuShapeRegistry.getRegistry().getValues().toArray(new ShinsuShape[0]);
        return all[player.getRandom().nextInt(all.length)];
    }
}
