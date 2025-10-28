package com.incompetent_modders.incomp_core.core.def.attributes.class_type;

import com.incompetent_modders.incomp_core.api.class_type.attribute.ClassAttribute;
import com.incompetent_modders.incomp_core.api.class_type.attribute.ClassAttributeType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.DefaultManaRegenCondition;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.ManaRegenCondition;
import com.incompetent_modders.incomp_core.common.registry.ModClassAttributes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.HashMap;
import java.util.Map;

public class ClassSpellCasting extends ClassAttribute {
    public static final Codec<ClassSpellCasting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_mana").forGetter(att -> att.maxMana),
            ManaRegenCondition.DIRECT_CODEC.optionalFieldOf("mana_regen", new DefaultManaRegenCondition()).forGetter(att -> att.manaRegen),
            SpellRestriction.CODEC.fieldOf("restrictions").forGetter(att -> att.spellRestriction)
    ).apply(instance, ClassSpellCasting::new));

    public final int maxMana;
    public final ManaRegenCondition manaRegen;
    public final SpellRestriction spellRestriction;

    public ClassSpellCasting(int maxMana, ManaRegenCondition manaRegen, SpellRestriction spellRestriction) {
        this.maxMana = maxMana;
        this.manaRegen = manaRegen;
        this.spellRestriction = spellRestriction;
    }

    public boolean canUseSpell(ResourceLocation category) {
        return spellRestriction.isAllowed(category);
    }

    @Override
    public ClassAttributeType<? extends ClassAttribute> getType() {
        return ModClassAttributes.SPELL_CASTING.get();
    }

    public record SpellRestriction(Map<ResourceLocation, Boolean> entries, boolean whitelist) {
        public static final Codec<Map<ResourceLocation, Boolean>> ENTRY_CODEC = Codec.unboundedMap(
                ResourceLocation.CODEC, Codec.BOOL);

        public static final Codec<SpellRestriction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ENTRY_CODEC.fieldOf("entries").forGetter(r -> r.entries),
                Codec.BOOL.optionalFieldOf("whitelist", false).forGetter(r -> r.whitelist)
        ).apply(instance, SpellRestriction::new));

        public boolean isAllowed(ResourceLocation category) {
            if (!entries.containsKey(category)) {
                return !whitelist;
            }
            return whitelist != entries.get(category);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Map<ResourceLocation, Boolean> entries = new HashMap<>();
            private boolean whitelist = false;

            public Builder whitelist() {
                this.whitelist = true;
                return this;
            }

            public Builder add(ResourceLocation category, boolean value) {
                entries.put(category, value);
                return this;
            }

            public Builder allow(ResourceLocation category) {
                return add(category, true);
            }

            public Builder deny(ResourceLocation category) {
                return add(category, false);
            }

            public SpellRestriction build() {
                return new SpellRestriction(entries, whitelist);
            }
        }
    }
}
