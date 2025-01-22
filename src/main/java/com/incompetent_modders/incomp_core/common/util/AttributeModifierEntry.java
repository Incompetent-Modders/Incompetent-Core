package com.incompetent_modders.incomp_core.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

@SuppressWarnings({"unchecked", "rawtypes"})
public record AttributeModifierEntry(Holder<Attribute> attributeHolder, AttributeModifier attributeModifier, Optional<LootItemCondition> when) {

    public static Codec<LootItemCondition> conditionCodec() {
        return LootItemCondition.DIRECT_CODEC.validate((lootItemCondition) -> {
            ProblemReporter.Collector problemreporter$collector = new ProblemReporter.Collector();
            ValidationContext validationcontext = new ValidationContext(problemreporter$collector, LootContextParamSets.ALL_PARAMS);
            lootItemCondition.validate(validationcontext);
            return (DataResult)problemreporter$collector.getReport().map((s) -> DataResult.error(() -> "Validation error in species attribute modifier condition: " + s)).orElseGet(() -> DataResult.success(lootItemCondition));
        });
    }

    public static final Codec<AttributeModifierEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(AttributeModifierEntry::attributeHolder),
            AttributeModifier.CODEC.fieldOf("modifier").forGetter(AttributeModifierEntry::attributeModifier),
            conditionCodec().optionalFieldOf("when").forGetter((attributeModifierEntry) -> attributeModifierEntry.when)
    ).apply(instance, AttributeModifierEntry::new));


    public static AttributeModifierEntry of(Holder<Attribute> attributeHolder, AttributeModifier attributeModifier) {
        return new AttributeModifierEntry(attributeHolder, attributeModifier, Optional.empty());
    }

    public static AttributeModifierEntry of(Holder<Attribute> attributeHolder, AttributeModifier attributeModifier, LootItemCondition when) {
        return new AttributeModifierEntry(attributeHolder, attributeModifier, Optional.of(when));
    }

    public boolean hasCondition() {
        return when.isPresent();
    }

    public LootItemCondition getCondition() {
        return when.orElse(null);
    }

    public boolean shouldApply(LootContext context) {
        return when.map((lootItemCondition) -> lootItemCondition.test(context)).orElse(true);
    }
}
