package com.davidqf.minecraft.towerofgod.client.gui;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.render.RenderInfo;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuQuality;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechniques;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public enum ShinsuAdvancement {

    CONTRACT(null, "contract", 1, ShinsuIcons.SWIRL, new ShinsuAdvancementCriteria.ItemCriteria(new Item[] {Items.PAPER}), new Reward(new ShinsuTechniques[]{}, new ShinsuQuality[]{}, 100, 1, 1, 1)),
    REVERSE_FLOW_CONTROL(CONTRACT, "reverse_flow_control", 20, ShinsuIcons.REVERSE, new ShinsuAdvancementCriteria.KillCriteria(new EntityClassification[] {EntityClassification.MONSTER}), new Reward(new ShinsuTechniques[] {ShinsuTechniques.REVERSE_FLOW_CONTROL}, new ShinsuQuality[]{}, 100, 1, 1, 1)),
    BLACK_FISH(CONTRACT, "black_fish", 20, ShinsuIcons.SWIRL, new ShinsuAdvancementCriteria.KillCriteria(new EntityClassification[] {EntityClassification.MONSTER}), new Reward(new ShinsuTechniques[] {ShinsuTechniques.BLACK_FISH}, new ShinsuQuality[]{}, 100, 1, 1, 1.5)),
    FLARE_WAVE_EXPLOSION(CONTRACT, "flare_wave_explosion", 20, ShinsuIcons.SWIRL, new ShinsuAdvancementCriteria.KillCriteria(new EntityClassification[] {EntityClassification.MONSTER}), new Reward(new ShinsuTechniques[] {ShinsuTechniques.FLARE_WAVE_EXPLOSION}, new ShinsuQuality[]{}, 100, 1, 1, 1.5)),
    SHINSU_BLAST(CONTRACT, "shinsu_blast", 20, ShinsuIcons.SWIRL, new ShinsuAdvancementCriteria.KillCriteria(new EntityClassification[] {EntityClassification.MONSTER}), new Reward(new ShinsuTechniques[] {ShinsuTechniques.SHINSU_BLAST}, new ShinsuQuality[]{}, 100, 1, 1, 1.5)),
    BODY_REINFORCEMENT(CONTRACT, "body_reinforcement", 20, ShinsuIcons.SWIRL, new ShinsuAdvancementCriteria.ItemCriteria(new Item[] {Items.IRON_INGOT}), new Reward(new ShinsuTechniques[] {ShinsuTechniques.BODY_REINFORCEMENT}, new ShinsuQuality[]{}, 100, 1, 1.5, 1));

    private final ShinsuAdvancement parent;
    private final TranslationTextComponent name;
    private final int completionAmt;
    private final RenderInfo icon;
    private final ShinsuAdvancementCriteria criteria;
    private final Reward reward;

    ShinsuAdvancement(@Nullable ShinsuAdvancement parent, String name, int completionAmt, RenderInfo icon, ShinsuAdvancementCriteria criteria, Reward reward) {
        this.parent = parent;
        this.name = new TranslationTextComponent("advancement." + TowerOfGod.MOD_ID + "." + name);
        this.completionAmt = completionAmt;
        this.icon = icon;
        this.criteria = criteria;
        criteria.setAdvancement(this);
        this.reward = reward;
    }

    @Nullable
    public static ShinsuAdvancement get(String name){
        for(ShinsuAdvancement advancement : values()) {
            if(advancement.getName().getKey().equals(name)){
                return advancement;
            }
        }
        return null;
    }

    public static List<ShinsuAdvancement> getUnlocked(Entity user) {
        List<ShinsuAdvancement> unlocked = new ArrayList<>();
        for(ShinsuAdvancement advancement : ShinsuAdvancement.values()) {
            if(advancement.getParent() == null) {
                unlocked.addAll(getUnlocked(user, advancement));
            }
        }
        return unlocked;
    }

    private static List<ShinsuAdvancement> getUnlocked(Entity user, ShinsuAdvancement advancement) {
        List<ShinsuAdvancement> unlocked = new ArrayList<>();
        unlocked.add(advancement);
        IShinsuStats stats = IShinsuStats.get(user);
            if (stats.getAdvancements().get(advancement).isComplete()) {
                for (ShinsuAdvancement child : advancement.getDirectChildren()) {
                    unlocked.addAll(getUnlocked(user, child));
                }
            }
        return unlocked;
    }

    @Nullable
    public ShinsuAdvancement getParent(){
        return parent;
    }

    public List<ShinsuAdvancement> getDirectChildren() {
        List<ShinsuAdvancement> children = new ArrayList<>();
        for(ShinsuAdvancement adv : values()) {
            if(this == adv.parent) {
                children.add(adv);
            }
        }
        return children;
    }

    public TranslationTextComponent getName(){
        return name;
    }

    public int getCompletionAmount(){
        return completionAmt;
    }

    public RenderInfo getIcon(){
        return icon;
    }

    public ShinsuAdvancementCriteria getCriteria(){
        return criteria;
    }

    public Reward getReward(){
        return reward;
    }

    public static class Reward {

        private final ShinsuTechniques[] techniques;
        private final ShinsuQuality[] qualities;
        private final int shinsu;
        private final int baangs;
        private final double resistance;
        private final double tension;

        private Reward(ShinsuTechniques[] techniques, ShinsuQuality[] qualities, int shinsu, int baangs, double resistance, double tension) {
            this.techniques = techniques;
            this.qualities = qualities;
            this.shinsu = shinsu;
            this.baangs = baangs;
            this.resistance = resistance;
            this.tension = tension;
        }

        public ShinsuTechniques[] getTechniques(){
            return techniques;
        }

        public ShinsuQuality[] getQualities(){
            return qualities;
        }

        public int getShinsu(){
            return shinsu;
        }

        public int getBaangs(){
            return baangs;
        }

        public double getResistance(){
            return resistance;
        }

        public double getTension(){
            return tension;
        }
    }
}
