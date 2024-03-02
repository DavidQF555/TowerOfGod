package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.Util;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuArrowEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

// TODO: FIX
public class ShootShinsuArrow extends ShinsuTechniqueType<ShinsuTechniqueConfig, ShootShinsuArrow.Data> {

    private Vec3 direction;
    private float velocity;
    private UUID arrow;

    public ShootShinsuArrow(Entity user, Vec3 direction) {
        super(user);
        this.direction = direction;
        arrow = null;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    @Override
    public int getDuration() {
        return 200;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.SHOOT_SHINSU_ARROW.get();
    }

    @Override
    public void onUse(ServerLevel world) {
        Entity user = getUser(world);
        if (user != null) {
            ShinsuArrowEntity arrow = EntityRegistry.SHINSU_ARROW.get().create(world);
            if (arrow != null) {
                ShinsuAttribute attribute = ShinsuQualityData.get(user).getAttribute();
                arrow.setAttribute(attribute);
                arrow.setTechnique(getID());
                arrow.shoot(direction.x(), direction.y(), direction.z(), velocity, 1);
                arrow.setOwner(user);
                arrow.setPos(user.getX(), user.getEyeY() - 0.1, user.getZ());
                this.arrow = arrow.getUUID();
                world.addFreshEntity(arrow);
            }
        }
        super.onUse(world);
    }

    @Override
    public int getShinsuUse() {
        return 5;
    }

    @Override
    public void tick(ServerLevel world) {
        if (arrow == null || world.getEntity(arrow) == null) {
            remove(world);
        }
        super.tick(world);
    }

    public static class Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Util.UUID_CODEC.fieldOf("id").forGetter(data -> data.id),
                Util.UUID_CODEC.fieldOf("arrow").forGetter(data -> data.arrow)
        ).apply(inst, Data::new));
        public final UUID id, arrow;

        public Data(UUID id, UUID arrow) {
            this.id = id;
            this.arrow = arrow;
        }

    }

}
