package com.davidqf.minecraft.towerofgod.client.gui;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;

public abstract class ShinsuAdvancementCriteria {

    private ShinsuAdvancement advancement;
    private final boolean checksEvents;

    public ShinsuAdvancementCriteria(boolean checksEvents) {
        advancement = null;
        this.checksEvents = checksEvents;
    }

    public void onEvent(Entity user, Event event) {
    }

    public void onCompletion(Entity user) {
    }

    public ShinsuAdvancement getAdvancement() {
        return advancement;
    }

    public int getCount(Entity user) {
        IShinsuStats stats = IShinsuStats.get(user);
        if (stats instanceof IShinsuStats.AdvancementShinsuStats) {
            return ((IShinsuStats.AdvancementShinsuStats) stats).getAdvancements().get(getAdvancement()).getProgress();
        }
        return 0;
    }

    public boolean canComplete(Entity user) {
        if (user instanceof PlayerEntity && ((PlayerEntity) user).isCreative()) {
            return true;
        }
        IShinsuStats stats = IShinsuStats.get(user);
        return stats instanceof IShinsuStats.AdvancementShinsuStats && ((IShinsuStats.AdvancementShinsuStats) stats).getAdvancements().get(advancement).getProgress() >= getAdvancement().getCompletionAmount();
    }

    public boolean correctEvent(Event event) {
        return checksEvents;
    }

    public abstract ITextComponent[] getText(Entity user);

    public void setAdvancement(ShinsuAdvancement advancement) {
        this.advancement = advancement;
    }

    public static class KillCriteria extends ShinsuAdvancementCriteria {

        private static final String TRANSLATION_KEY = "criteria." + TowerOfGod.MOD_ID + ".kill";
        private final Class<? extends LivingEntity>[] types;
        private final EntityClassification[] classifications;

        public KillCriteria(Class<? extends LivingEntity>[] types) {
            super(true);
            this.types = types;
            classifications = null;
        }

        public KillCriteria(EntityClassification[] classifications) {
            super(true);
            types = null;
            this.classifications = classifications;
        }

        @Override
        public void onEvent(Entity user, Event event) {
            if (event instanceof LivingDeathEvent) {
                LivingDeathEvent death = (LivingDeathEvent) event;
                IShinsuStats stats = IShinsuStats.get(user);
                if (stats instanceof IShinsuStats.AdvancementShinsuStats) {
                    ShinsuAdvancementProgress progress = ((IShinsuStats.AdvancementShinsuStats) stats).getAdvancements().get(getAdvancement());
                    if (user.equals(death.getSource().getTrueSource())) {
                        if (types != null) {
                            Class<? extends LivingEntity> clazz = death.getEntityLiving().getClass();
                            for (Class<? extends LivingEntity> type : types) {
                                if (clazz == type) {
                                    progress.setProgress(progress.getProgress() + 1);
                                }
                            }
                        } else if (classifications != null) {
                            EntityClassification c = death.getEntityLiving().getClassification(false);
                            for (EntityClassification classification : classifications) {
                                if (c.equals(classification)) {
                                    progress.setProgress(progress.getProgress() + 1);
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public ITextComponent[] getText(Entity user) {
            ITextComponent[] arr = new ITextComponent[1];
            if (types != null) {
                arr = new ITextComponent[types.length + 1];
                for (int i = 0; i < types.length; i++) {
                    arr[i + 1] = new StringTextComponent(types[i].getName());
                }

            } else if (classifications != null) {
                arr = new ITextComponent[classifications.length + 1];
                for (int i = 0; i < classifications.length; i++) {
                    arr[i + 1] = new StringTextComponent(classifications[i].getName());
                }
            }
            arr[0] = new TranslationTextComponent(TRANSLATION_KEY, getAdvancement().getCompletionAmount());
            return arr;
        }

        @Override
        public boolean correctEvent(Event event) {
            return super.correctEvent(event) && event instanceof LivingDeathEvent;
        }
    }

    public static class ItemCriteria extends ShinsuAdvancementCriteria {

        private static final String TRANSLATION_KEY = "criteria." + TowerOfGod.MOD_ID + ".item";
        private final Item[] types;

        public ItemCriteria(Item[] types) {
            super(false);
            this.types = types;
        }

        @Override
        public void onCompletion(Entity user) {
            if (user instanceof PlayerEntity && !((PlayerEntity) user).isCreative()) {
                PlayerEntity player = (PlayerEntity) user;
                int completion = getAdvancement().getCompletionAmount();
                for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
                    ItemStack item = player.inventory.mainInventory.get(i);
                    if (correctType(item)) {
                        int count = item.getCount();
                        if (count < completion) {
                            player.inventory.removeStackFromSlot(i);
                            completion -= count;
                        } else {
                            item.setCount(count - completion);
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public int getCount(Entity user) {
            int count = 0;
            if (user instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) user;
                for (ItemStack item : player.inventory.mainInventory) {
                    if (correctType(item)) {
                        count += item.getCount();
                    }
                }
            }
            return count;
        }

        @Override
        public boolean canComplete(Entity user) {
            return (user instanceof PlayerEntity && ((PlayerEntity) user).isCreative()) || getCount(user) >= getAdvancement().getCompletionAmount();
        }

        @Override
        public ITextComponent[] getText(Entity user) {
            ITextComponent[] arr = new ITextComponent[types.length + 1];
            for (int i = 0; i < types.length; i++) {
                arr[i + 1] = types[i].getDisplayName(types[i].getDefaultInstance());
            }
            arr[0] = new TranslationTextComponent(TRANSLATION_KEY, getAdvancement().getCompletionAmount());
            return arr;
        }

        private boolean correctType(ItemStack itemStack) {
            Item item = itemStack.getItem();
            for (Item type : types) {
                if (item.equals(type)) {
                    return true;
                }
            }
            return false;
        }
    }
}
