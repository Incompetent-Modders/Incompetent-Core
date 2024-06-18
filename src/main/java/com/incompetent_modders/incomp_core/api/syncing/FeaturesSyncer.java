package com.incompetent_modders.incomp_core.api.syncing;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeProperties;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesProperties;
import com.incompetent_modders.incomp_core.api.json.species.diet.DietListener;
import com.incompetent_modders.incomp_core.api.json.species.diet.DietProperties;
import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import com.incompetent_modders.incomp_core.api.json.spell.SpellProperties;
import com.incompetent_modders.incomp_core.api.network.SyncHandler;
import com.incompetent_modders.incomp_core.api.network.features.MessageClassTypesSync;
import com.incompetent_modders.incomp_core.api.network.features.MessageDietsSync;
import com.incompetent_modders.incomp_core.api.network.features.MessageSpeciesSync;
import com.incompetent_modders.incomp_core.api.network.features.MessageSpellsSync;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;

public class FeaturesSyncer {
    public static void syncAllToPlayer(ServerPlayer player) {
        syncSpells(player);
        syncSpecies(player);
        syncClassTypes(player);
        syncDiets(player);
    }
    public static void syncAllToAll(MinecraftServer server) {
        syncSpellsToAll(server);
        syncSpeciesToAll(server);
        syncClassTypesToAll(server);
        syncDietsToAll(server);
    }
    public static void syncSpells(ServerPlayer player) {
        MessageSpellsSync packet = createSpellPacket();
        IncompCore.LOGGER.debug("Syncing spells to player {} with {} spells", player.getGameProfile().getName(), packet.spellsList().size());
        SyncHandler.DEFAULT_CHANNEL.sendToPlayer(packet, player);
    }
    public static void syncSpecies(ServerPlayer player) {
        MessageSpeciesSync packet = createSpeciesPacket();
        IncompCore.LOGGER.debug("Syncing species to player {} with {} species", player.getGameProfile().getName(), packet.speciesIDList().size());
        SyncHandler.DEFAULT_CHANNEL.sendToPlayer(packet, player);
    }
    public static void syncClassTypes(ServerPlayer player) {
        MessageClassTypesSync packet = createClassTypePacket();
        IncompCore.LOGGER.debug("Syncing class types to player {} with {} class types", player.getGameProfile().getName(), packet.classTypesIDList().size());
        SyncHandler.DEFAULT_CHANNEL.sendToPlayer(packet, player);
    }
    public static void syncDiets(ServerPlayer player) {
        MessageDietsSync packet = createDietPacket();
        IncompCore.LOGGER.debug("Syncing diets to player {} with {} diets", player.getGameProfile().getName(), packet.dietsIDList().size());
        SyncHandler.DEFAULT_CHANNEL.sendToPlayer(packet, player);
    }
    public static void syncSpellsToAll(MinecraftServer server) {
        MessageSpellsSync packet = createSpellPacket();
        IncompCore.LOGGER.debug("Syncing spells to all players with {} spells", packet.spellsList().size());
        SyncHandler.DEFAULT_CHANNEL.sendToAllPlayers(packet, server);
    }
    public static void syncSpeciesToAll(MinecraftServer server) {
        MessageSpeciesSync packet = createSpeciesPacket();
        IncompCore.LOGGER.debug("Syncing species to all players with {} species", packet.speciesIDList().size());
        SyncHandler.DEFAULT_CHANNEL.sendToAllPlayers(packet, server);
    }
    public static void syncClassTypesToAll(MinecraftServer server) {
        MessageClassTypesSync packet = createClassTypePacket();
        IncompCore.LOGGER.debug("Syncing class types to all players with {} class types", packet.classTypesIDList().size());
        SyncHandler.DEFAULT_CHANNEL.sendToAllPlayers(packet, server);
    }
    public static void syncDietsToAll(MinecraftServer server) {
        MessageDietsSync packet = createDietPacket();
        IncompCore.LOGGER.debug("Syncing diets to all players with {} diets", packet.dietsIDList().size());
        SyncHandler.DEFAULT_CHANNEL.sendToAllPlayers(packet, server);
    }
    
    private static MessageSpellsSync createSpellPacket() {
        Map<ResourceLocation, SpellProperties> spellProperties = SpellListener.getProperties();
        List<ResourceLocation> spells = SpellListener.getAllSpells();
        return new MessageSpellsSync(spellProperties);
    }
    private static MessageSpeciesSync createSpeciesPacket() {
        Map<ResourceLocation, SpeciesProperties> speciesProperties = SpeciesListener.getProperties();
        List<ResourceLocation> species = SpeciesListener.getAllSpecies();
        return new MessageSpeciesSync(species);
    }
    private static MessageClassTypesSync createClassTypePacket() {
        Map<ResourceLocation, ClassTypeProperties> classTypeProperties = ClassTypeListener.getProperties();
        List<ResourceLocation> classTypes = ClassTypeListener.getAllClassTypes();
        return new MessageClassTypesSync(classTypes);
    }
    private static MessageDietsSync createDietPacket() {
        Map<ResourceLocation, DietProperties> dietProperties = DietListener.getProperties();
        List<ResourceLocation> diets = DietListener.getAllDiets();
        return new MessageDietsSync(diets);
    }
    
}
