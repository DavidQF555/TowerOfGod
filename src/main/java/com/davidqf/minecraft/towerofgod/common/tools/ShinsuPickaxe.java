package com.davidqf.minecraft.towerofgod.common.tools;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuQuality;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class ShinsuPickaxe extends PickaxeItem {

    public ShinsuPickaxe(int attackDamageIn, float attackSpeedIn) {
        super(ModToolTier.SHINSU, attackDamageIn, attackSpeedIn, new Item.Properties().setNoRepair());
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

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ShinsuQuality quality = ShinsuQuality.get(context.getItem().getOrCreateChildTag(TowerOfGod.MOD_ID).getString("Quality"));
        quality.applyBlockEffect(context.getPlayer(), new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), context.isInside()));
        return super.onItemUse(context);
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
