package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.DirectionalLightningBoltEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class ChannelLightning extends RayTraceTechnique {

    private static final double RANGE = 64;
    private float damage;

    public ChannelLightning(LivingEntity user, Vector3d direction, double range, float damage) {
        super(user, direction, range, true);
        this.damage = damage;
    }

    @Override
    public int getCooldown() {
        return 160;
    }

    @Override
    public void doEffect(ServerWorld world, RayTraceResult result) {
        DirectionalLightningBoltEntity lightning = EntityRegistry.DIRECTIONAL_LIGHTNING.get().create(world);
        if (lightning != null) {
            Entity user = getUser(world);
            if (user instanceof ServerPlayerEntity) {
                lightning.setCause((ServerPlayerEntity) user);
            }
            lightning.setDamage(damage);
            Vector3d pos = result.getLocation();
            lightning.setPos(pos.x(), pos.y(), pos.z());
            lightning.setStart(new Vector3f(user.getEyePosition(1)));
            world.addFreshEntity(lightning);
        }
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.CHANNEL_LIGHTNING.get();
    }

    @Override
    public int getShinsuUse() {
        return 10;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Damage", Constants.NBT.TAG_FLOAT)) {
            damage = nbt.getFloat("Damage");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putFloat("Damage", damage);
        return nbt;
    }

    public static class Factory implements ShinsuTechnique.IFactory<ChannelLightning> {

        @Override
        public Either<ChannelLightning, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            int level = ShinsuStats.get(user).getData(ShinsuTechniqueType.CONTROL).getLevel();
            return Either.left(new ChannelLightning(user, dir, RANGE, level - 4));
        }

        @Override
        public ChannelLightning blankCreate() {
            return new ChannelLightning(null, Vector3d.ZERO, 0, 0);
        }

    }
}
