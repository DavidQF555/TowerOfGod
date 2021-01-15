package com.davidqf.minecraft.towerofgod.common.entities;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuQuality;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuShape;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.common.capabilities.IShinsuStats;
import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class RegularEntity extends ShinsuUserEntity {

    private static final String TAG_KEY = TowerOfGod.MOD_ID + ".regular_entity";
    private static final double FAMILY_TECHNIQUE_RATE = 0.8;
    private static final double FAMILY_QUALITY_RATE = 0.8;
    private static final double FAMILY_SHAPE_RATE = 0.8;
    private static final double FAMILY_WEAPON_RATE = 0.8;
    private static final List<Class<? extends Item>> WEAPONS = new ArrayList<>(Arrays.asList(SwordItem.class, AxeItem.class));
    public static final DataParameter<String> FAMILY = EntityDataManager.createKey(RegularEntity.class, DataSerializers.STRING);
    private Personality personality;
    private Team team;
    private int level;

    public RegularEntity(World worldIn) {
        super(RegistryHandler.REGULAR_ENTITY.get(), worldIn);
        level = 1;
        team = new Team(this);
        personality = Personality.NEUTRAL;
        setFamily(Family.ARIE);
    }

    @Override
    public ILivingEntityData onInitialSpawn(@Nonnull IWorld worldIn, @Nonnull DifficultyInstance difficultyIn, @Nonnull SpawnReason reason, ILivingEntityData spawnDataIn, CompoundNBT dataTag) {
        level = rand.nextInt(ShinsuTechnique.getObtainableTechniques().size()) + 1;
        Personality[] personalities = Personality.values();
        personality = personalities[rand.nextInt(personalities.length)];
        Family[] families = Family.values();
        Family family = families[rand.nextInt(families.length)];
        setFamily(family);
        setStats();
        setWeapon();
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return RegularEntity.func_233666_p_()
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 32)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.215)
                .createMutableAttribute(Attributes.MAX_HEALTH, 20)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1);
    }

    private void setStats() {
        IShinsuStats stats = IShinsuStats.get(this);
        Family family = getFamily();
        stats.addMaxShinsu(10 + (int) (15 * rand.nextDouble() * level * family.getShinsu()));
        stats.addMaxBaangs(1 + (int) (rand.nextDouble() * level * family.getBaangs() / 3));
        stats.multiplyResistance(1 + rand.nextDouble() * level * family.getResistance() / 2);
        stats.multiplyTension(1 + rand.nextDouble() * level * family.getTension() / 2);
        List<ShinsuTechnique> all = ShinsuTechnique.getObtainableTechniques();
        ShinsuTechnique[] preferred = family.getPreferredTechniques();
        int amount = level - rand.nextInt(level);
        for (int i = 0; i < amount; i++) {
            double chance = rand.nextDouble();
            ShinsuTechnique technique;
            if (preferred.length > 0 && chance < FAMILY_TECHNIQUE_RATE) {
                technique = preferred[rand.nextInt(preferred.length)];
            } else {
                technique = all.get(rand.nextInt(all.size()));
            }
            stats.addKnownTechnique(technique, 1);
        }
        ShinsuQuality[] pref = family.getQualities();
        ShinsuQuality quality;
        if (pref.length > 0 && rand.nextDouble() < FAMILY_QUALITY_RATE) {
            quality = pref[rand.nextInt(pref.length)];
        } else {
            ShinsuQuality[] qualities = ShinsuQuality.values();
            quality = qualities[rand.nextInt(qualities.length)];
        }
        stats.setQuality(quality);
        ShinsuShape[] prefShapes = family.getShapes();
        ShinsuShape shape;
        if (prefShapes.length > 0 && rand.nextDouble() < FAMILY_SHAPE_RATE) {
            shape = prefShapes[rand.nextInt(prefShapes.length)];
        } else {
            ShinsuShape[] shapes = ShinsuShape.values();
            shape = shapes[rand.nextInt(shapes.length)];
        }
        stats.setShape(shape);
    }

    private void setWeapon() {
        List<Item> weapon = new ArrayList<>();
        List<Item> pref = new ArrayList<>();
        Family family = getFamily();
        for (Item item : ForgeRegistries.ITEMS) {
            if (item instanceof TieredItem && ((TieredItem) item).getTier().getAttackDamage() > level + 1) {
                continue;
            }
            for (Class<? extends Item> clazz : family.getWeapons()) {
                if (clazz.isInstance(item)) {
                    pref.add(item);
                    break;
                }
            }
            for (Class<? extends Item> clazz : WEAPONS) {
                if (clazz.isInstance(item)) {
                    weapon.add(item);
                    break;
                }
            }
        }
        ItemStack weap = Items.AIR.getDefaultInstance();
        if (!pref.isEmpty() && rand.nextDouble() < FAMILY_WEAPON_RATE) {
            weap = pref.get(rand.nextInt(pref.size())).getDefaultInstance();
        } else if (rand.nextDouble() < 1 - 1.0 / weapon.size()) {
            weap = weapon.get(rand.nextInt(weapon.size())).getDefaultInstance();
        }
        setItemStackToSlot(EquipmentSlotType.MAINHAND, weap);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(1, new FindTeamGoal());
        goalSelector.addGoal(10, new LookRandomlyGoal(this));
        goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1));
        goalSelector.addGoal(5, new MeleeAttackGoal(this, 1, true));
        goalSelector.addGoal(8, new FollowLeaderGoal());
        goalSelector.addGoal(7, new MoveTowardsTargetGoal(this, 1.5, 32));
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
        targetSelector.addGoal(3, new TeamTargetGoal());
        targetSelector.addGoal(4, new PersonalityTargetGoal());
    }

    @Override
    public void registerData() {
        super.registerData();
        dataManager.register(FAMILY, "");
    }

    public Family getFamily() {
        return Family.get(dataManager.get(FAMILY));
    }

    public void setFamily(Family family) {
        dataManager.set(FAMILY, family.name());
    }

    @Override
    public void onDeathUpdate() {
        super.onDeathUpdate();
        List<RegularEntity> members = team.getEntityMembers(world);
        if (equals(team.getLeader(world)) && members.size() > 1) {
            members.remove(this);
            team.setLeader(members.get((int) (Math.random() * members.size())));
        }
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains(TAG_KEY, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT reg = (CompoundNBT) nbt.get(TAG_KEY);
            setFamily(Family.get(reg.getString("Family")));
            personality = Personality.get(reg.getString("Personality"));
            CompoundNBT com = (CompoundNBT) reg.get("Team");
            team.deserializeNBT(com);
            level = reg.getInt("Level");
        }
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT nbt) {
        super.writeAdditional(nbt);
        CompoundNBT reg = new CompoundNBT();
        reg.putString("Family", getFamily().name());
        reg.putString("Personality", personality.name());
        reg.put("Team", team.serializeNBT());
        reg.putInt("Level", level);
        nbt.put(TAG_KEY, reg);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    private class FollowLeaderGoal extends Goal {

        private static final double RANGE = 8;

        @Override
        public boolean shouldExecute() {
            return team.getLeader(world) != null && getAttackTarget() == null && getDistanceSq(team.getLeader(world)) > RANGE * RANGE;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return shouldExecute() && !getNavigator().noPath();
        }

        @Override
        public void tick() {
            getNavigator().tryMoveToEntityLiving(team.getLeader(world), 1);
        }
    }

    private class PersonalityTargetGoal extends NearestAttackableTargetGoal<MobEntity> {

        public PersonalityTargetGoal() {
            super(RegularEntity.this, MobEntity.class, true);
        }

        @Override
        public boolean shouldExecute() {
            return super.shouldExecute() && getAttackTarget() == null && personality == Personality.AGGRESSIVE;
        }

        @Override
        protected boolean isSuitableTarget(@Nullable LivingEntity potentialTarget, @Nonnull EntityPredicate targetPredicate) {
            return super.isSuitableTarget(potentialTarget, targetPredicate) && !team.getEntityMembers(world).contains(potentialTarget) && (potentialTarget instanceof RegularEntity || potentialTarget instanceof PlayerEntity);
        }
    }

    private class TeamTargetGoal extends TargetGoal {

        public TeamTargetGoal() {
            super(RegularEntity.this, false);
        }

        @Override
        public boolean shouldExecute() {
            return getAttackTarget() == null;
        }

        @Override
        public void startExecuting() {
            List<LivingEntity> targets = new ArrayList<>();
            for (RegularEntity reg : team.getEntityMembers(world)) {
                if (reg.getAttackTarget() != null) {
                    targets.add(reg.getAttackTarget());
                }
            }
            if (!targets.isEmpty()) {
                LivingEntity best = targets.get(0);
                for (LivingEntity target : targets) {
                    if (getDistanceSq(target) < getDistanceSq(best) && getNavigator().getPathToEntity(target, 1) != null) {
                        best = target;
                    }
                }
                setAttackTarget(best);
            }
        }

    }

    private class FindTeamGoal extends Goal {

        private static final int MAX_SIZE = 5;
        private static final double RANGE = 16;

        @Override
        public boolean shouldExecute() {
            return team.getEntityMembers(world).size() < MAX_SIZE;
        }

        @Override
        public void startExecuting() {
            List<RegularEntity> nearby = world.getEntitiesWithinAABB(RegularEntity.class, new AxisAlignedBB(getPosX() - RANGE, getPosY() - RANGE, getPosZ() - RANGE, getPosX() + RANGE, getPosY() + RANGE, getPosZ() + RANGE));
            List<RegularEntity> rem = new ArrayList<>();
            for (RegularEntity reg : nearby) {
                List<RegularEntity> members = reg.team.getEntityMembers(world);
                if (members.size() + members.size() > MAX_SIZE || members.size() < team.getEntityMembers(world).size() || team.getEntityMembers(world).contains(reg)) {
                    rem.addAll(members);
                } else if (reg.personality == Personality.NEUTRAL) {
                    for (RegularEntity team : members) {
                        if (team.personality != Personality.NEUTRAL && team.personality != personality) {
                            rem.addAll(members);
                            break;
                        }
                    }
                } else if (reg.personality != personality) {
                    rem.addAll(members);
                }
            }
            nearby.removeAll(rem);
            if (!nearby.isEmpty()) {
                RegularEntity reg = nearby.get((getRNG().nextInt(nearby.size())));
                List<RegularEntity> members = team.getEntityMembers(world);
                reg.team.getEntityMembers(world).addAll(members);
                for (RegularEntity mem : members) {
                    mem.team = reg.team;
                }
            }
        }
    }

    public static class Factory implements EntityType.IFactory<RegularEntity> {
        @Nonnull
        @Override
        public RegularEntity create(@Nullable EntityType<RegularEntity> type, @Nonnull World world) {
            return new RegularEntity(world);
        }
    }

    private enum Personality {
        AGGRESSIVE(),
        PASSIVE(),
        NEUTRAL();

        public static Personality get(String name) {
            for (Personality personality : values()) {
                if (personality.name().equals(name)) {
                    return personality;
                }
            }
            return null;
        }
    }

    private static class Team implements INBTSerializable<CompoundNBT> {

        private final List<UUID> members;
        private UUID leader;

        public Team(RegularEntity leader) {
            members = new ArrayList<>();
            members.add(leader.getUniqueID());
            this.leader = leader.getUniqueID();
        }

        public List<RegularEntity> getEntityMembers(World world) {
            if (world instanceof ServerWorld) {
                List<RegularEntity> out = new ArrayList<>();
                for (UUID uuid : members) {
                    Entity e = ((ServerWorld) world).getEntityByUuid(uuid);
                    if (e instanceof RegularEntity) {
                        out.add((RegularEntity) e);
                    }
                }
                return out;
            }
            return null;
        }

        public Entity getLeader(World world) {
            if (world instanceof ServerWorld) {
                return ((ServerWorld) world).getEntityByUuid(leader);
            }
            return null;
        }

        public void setLeader(RegularEntity reg) {
            leader = reg.getUniqueID();
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("Size", members.size());
            for (int i = 0; i < members.size(); i++) {
                nbt.putUniqueId("Member" + i, members.get(i));
            }
            nbt.putUniqueId("Leader", leader);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            int size = nbt.getInt("Size");
            for (int i = 0; i < size; i++) {
                members.add(nbt.getUniqueId("Member" + i));
            }
            leader = nbt.getUniqueId("Leader");
        }
    }
}
