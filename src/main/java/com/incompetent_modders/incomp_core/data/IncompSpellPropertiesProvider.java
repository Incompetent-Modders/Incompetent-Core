package com.incompetent_modders.incomp_core.data;

import com.incompetent_modders.incomp_core.api.spell.Spells;
import com.incompetent_modders.incomp_core.data.spell_properties.SpellPropertiesProvider;
import com.incompetent_modders.incomp_core.registry.dev.DevSpells;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class IncompSpellPropertiesProvider extends SpellPropertiesProvider {
    public IncompSpellPropertiesProvider(PackOutput packOutput, String modid) {
        super(packOutput, modid);
    }
    
    @Override
    public void provideSpellProperties() {
        addSpell(Spells.EMPTY.get(), 0, 0, 0, ItemStack.EMPTY);
        addSpell(DevSpells.TEST_SPELL.get(), 25, 20, 120, Items.BLAZE_POWDER.getDefaultInstance());
        addSpell(DevSpells.TEST_PROJECTILE_SPELL.get(), 50, 40, 120, ItemStack.EMPTY);
        addSpell(DevSpells.TEST_BLOCK_PRE_CAST_SPELL.get(), 75, 60, 120, ItemStack.EMPTY);
        addSpell(DevSpells.TEST_PRE_CAST_SPELL.get(), 90, 30, 120, ItemStack.EMPTY);
    }
}
