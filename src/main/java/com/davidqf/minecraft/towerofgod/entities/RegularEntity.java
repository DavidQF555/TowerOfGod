package com.davidqf.minecraft.towerofgod.entities;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.entities.shinsu.ShinsuQuality;
import com.davidqf.minecraft.towerofgod.entities.shinsu.ShinsuUser;
import com.davidqf.minecraft.towerofgod.entities.shinsu.techinques.ShinsuTechniques;
import com.davidqf.minecraft.towerofgod.util.RegistryHandler;
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

public class RegularEntity extends ShinsuUser {

    private static final String TAG_KEY = TowerOfGod.MOD_ID + ".regularentity";
    private static final double FAMILY_QUALITY_RATE = 0.8;
    private static final double FAMILY_WEAPON_RATE = 0.8;
    private static final List<Class<? extends Item>> WEAPONS = new ArrayList<>(Arrays.asList(SwordItem.class, AxeItem.class));
    public static final DataParameter<String> FAMILY = EntityDataManager.createKey(RegularEntity.class, DataSerializers.STRING);
    private Personality personality;
    private Team team;
    private int level;

    public RegularEntity(World worldIn, int level) {
        super(RegistryHandler.REGULAR_ENTITY.get(), worldIn);
        this.level = level;
        team = new Team(this);
        personality = Personality.NEUTRAL;
        setFamily(Family.ARIE);
    }

    public RegularEntity(World worldIn) {
        this(worldIn, (int) (Math.random() * 100) + 1);
    }

    @Override
    public ILivingEntityData onInitialSpawn(@Nonnull IWorld worldIn, @Nonnull DifficultyInstance difficultyIn, @Nonnull SpawnReason reason, ILivingEntityData spawnDataIn, CompoundNBT dataTag) {
        Personality[] personalities = Personality.values();
        personality = personalities[(int) (Math.random() * personalities.length)];

        Random rand = getRNG();
        Family[] families = Family.values();
        Family family = families[(int) (rand.nextDouble() * families.length)];
        setFamily(family);
        getPreferred().addAll(family.getPreferredTechniques());

        IStats stats = ShinsuUser.getStats(this);
        stats.setLevel(level);
        stats.setResistance(rand.nextDouble() * 0.3 + 0.7 / level * 20 / family.getResistance());
        double shinsu = (level * Math.random() + level) * family.getShinsu();
        stats.setShinsu((int) shinsu + 1);
        stats.setBaangs((int) (shinsu / 35) + 1);
        stats.setTension((int) (shinsu / 75) + 1);

        Map<ShinsuTechniques, Integer> known = getKnownTechniques();
        for (ShinsuTechniques tech : ShinsuTechniques.values()) {
            if (tech.getLevelRequirement() <= level) {
                int skill = Math.min(tech.getMaxLevel(), (level - tech.getLevelRequirement()) / tech.getLevelRequirement() + 1);
                known.put(tech, skill);
            }
        }

        ShinsuQuality[] qualities = family.getQualities();
        if (rand.nextDouble() < FAMILY_QUALITY_RATE && qualities.length > 0) {
            setQuality(qualities[(int) (qualities.length * rand.nextDouble())]);
        } else {
            ShinsuQuality[] values = ShinsuQuality.values();
            setQuality(values[(int) (rand.nextDouble() * values.length)]);
        }

        List<Item> weapon = new ArrayList<>();
        List<Item> pref = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS) {
            if (item instanceof TieredItem && ((TieredItem) item).getTier().getHarvestLevel() > level / 20 + 1) {
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
        if (!pref.isEmpty() && Math.random() < FAMILY_WEAPON_RATE) {
            weap = pref.get((int) (rand.nextDouble() * pref.size())).getDefaultInstance();
        } else if (Math.random() < 1 - 1.0 / weapon.size()) {
            weap = weapon.get((int) (rand.nextDouble() * weapon.size())).getDefaultInstance();
        }
        setItemStackToSlot(EquipmentSlotType.MAINHAND, weap);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return RegularEntity.func_233666_p_()
                .func_233815_a_(Attributes.field_233819_b_, 32)
                .func_233815_a_(Attributes.field_233821_d_, 0.1)
                .func_233815_a_(Attributes.field_233818_a_, 20)
                .func_233815_a_(Attributes.field_233823_f_, 1);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8));
        this.goalSelector.addGoal(1, new FindTeamGoal());
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1, true));
        this.goalSelector.addGoal(8, new FollowLeaderGoal());
        this.goalSelector.addGoal(7, new MoveTowardsTargetGoal(this, 1.5, 32));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new TeamTargetGoal());
        this.targetSelector.addGoal(4, new PersonalityTargetGoal());
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
        if (this.equals(team.getLeader(world)) && members.size() > 1) {
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
            level = nbt.getInt("Level");
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
                RegularEntity reg = nearby.get((int) (getRNG().nextDouble() * nearby.size()));
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
            nbt.putInt("size", members.size());
            for (int i = 1; i <= members.size(); i++) {
                nbt.putUniqueId("member" + i, members.get(i - 1));
            }
            nbt.putUniqueId("leader", leader);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            int size = nbt.getInt("size");
            for (int i = 1; i <= size; i++) {
                members.add(nbt.getUniqueId("member" + i));
            }
            leader = nbt.getUniqueId("leader");
        }
    }
}
