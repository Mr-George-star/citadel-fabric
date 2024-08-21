package net.george.citadel;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.george.citadel.config.ConfigHolder;
import net.george.citadel.config.ServerConfig;
import net.george.citadel.item.ItemCitadelBook;
import net.george.citadel.item.ItemCitadelDebug;
import net.george.citadel.item.ItemCustomRender;
import net.george.citadel.server.CitadelEvents;
import net.george.citadel.server.block.CitadelLecternBlock;
import net.george.citadel.server.block.CitadelLecternBlockEntity;
import net.george.citadel.server.block.LecternBooks;
import net.george.citadel.server.generation.VillageHouseManager;
import net.george.citadel.server.message.AnimationMessage;
import net.george.citadel.server.message.DanceJukeboxMessage;
import net.george.citadel.server.message.PropertiesMessage;
import net.george.citadel.server.message.SyncClientTickRateMessage;
import net.george.citadel.server.world.ExpandedBiomeSource;
import net.george.citadel.server.world.ExpandedBiomes;
import net.george.citadel.web.WebHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvents;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public class Citadel implements ModInitializer {
	public static final String MOD_ID = "citadel";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	private static final Identifier PACKET_NETWORK_NAME = new Identifier("citadel:main_channel");
	public static final SimpleChannel NETWORK_WRAPPER = new SimpleChannel(PACKET_NETWORK_NAME);
	public static List<String> PATRONS = new ArrayList<>();

	public static final LazyRegistrar<Item> ITEMS = LazyRegistrar.create(Registry.ITEM, "citadel");
	public static final LazyRegistrar<Block> BLOCKS = LazyRegistrar.create(Registry.BLOCK, "citadel");
	public static final LazyRegistrar<BlockEntityType<?>> BLOCK_ENTITIES = LazyRegistrar.create(Registry.BLOCK_ENTITY_TYPE, "citadel");

	public static final RegistryObject<Item> DEBUG_ITEM = ITEMS.register("debug", () -> new ItemCitadelDebug(new Item.Settings()));
	public static final RegistryObject<Item> CITADEL_BOOK = ITEMS.register("citadel_book", () -> new ItemCitadelBook(new Item.Settings().maxCount(1)));
	public static final RegistryObject<Item> EFFECT_ITEM = ITEMS.register("effect_item", () -> new ItemCustomRender(new Item.Settings().maxCount(1)));
	public static final RegistryObject<Item> FANCY_ITEM = ITEMS.register("fancy_item", () -> new ItemCustomRender(new Item.Settings().maxCount(1)));
	public static final RegistryObject<Item> ICON_ITEM = ITEMS.register("icon_item", () -> new ItemCustomRender(new Item.Settings().maxCount(1)));

	public static final RegistryObject<Block> LECTERN = BLOCKS.register("lectern", () -> new CitadelLecternBlock(AbstractBlock.Settings.copy(Blocks.LECTERN)));
	public static final RegistryObject<BlockEntityType<CitadelLecternBlockEntity>> LECTERN_BLOCK_ENTITY = BLOCK_ENTITIES.register("lectern", () -> BlockEntityType.Builder.create(CitadelLecternBlockEntity::new, LECTERN.get()).build(null));

	@Override
	public void onInitialize() {
		ModLoadingContext.registerConfig(MOD_ID, ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);

		ITEMS.register();
		BLOCKS.register();
		BLOCK_ENTITIES.register();
		this.setup();
		this.registerCallbacks();
	}

	public void setup() {
		ServerHandler.HANDLER.onPreInit();
		LecternBooks.init();

		int packetsRegistered = 0;

		NETWORK_WRAPPER.registerC2SPacket(PropertiesMessage.class, ++packetsRegistered, PropertiesMessage::decode);
		NETWORK_WRAPPER.registerS2CPacket(AnimationMessage.class, ++packetsRegistered, AnimationMessage::decode);
		NETWORK_WRAPPER.registerS2CPacket(SyncClientTickRateMessage.class, ++packetsRegistered, SyncClientTickRateMessage::decode);
		NETWORK_WRAPPER.registerC2SPacket(DanceJukeboxMessage.class, ++packetsRegistered, DanceJukeboxMessage::decode);

		BufferedReader urlContents = WebHelper.getURLContents("https://raw.githubusercontent.com/Alex-the-666/Citadel/master/src/main/resources/assets/citadel/patreon.txt", "assets/citadel/patreon.txt");
		if (urlContents != null) {
			try {
				String line;
				while ((line = urlContents.readLine()) != null) {
					PATRONS.add(line);
				}
			} catch (IOException exception) {
				LOGGER.warn("Failed to load patreon contributor perks");
			}
		} else LOGGER.warn("Failed to load patreon contributor perks");
	}

	public void registerCallbacks() {
		CitadelEvents.register();
		ModConfigEvents.loading(MOD_ID).register(config -> {
			ServerConfig.skipWarnings = ConfigHolder.SERVER.skipDatapackWarnings.get();
			if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
				ServerConfig.citadelEntityTrack = ConfigHolder.SERVER.citadelEntityTracker.get();
				ServerConfig.chunkGenSpawnModifierVal = ConfigHolder.SERVER.chunkGenSpawnModifier.get();
				ServerConfig.aprilFools = ConfigHolder.SERVER.aprilFoolsContent.get();
			}
		});
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			DynamicRegistryManager registryAccess = server.getRegistryManager();
			VillageHouseManager.addAllHouses(registryAccess);
			Registry<Biome> allBiomes = registryAccess.get(Registry.BIOME_KEY);
			Map<RegistryKey<Biome>, RegistryEntry<Biome>> biomeMap = new HashMap<>();
			for (RegistryKey<Biome> biomeResourceKey : allBiomes.getKeys()){
				Optional<RegistryEntry<Biome>> holderOptional = allBiomes.getEntry(biomeResourceKey);
				holderOptional.ifPresent(biomeHolder -> biomeMap.put(biomeResourceKey, biomeHolder));
			}
			for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : server.getSaveProperties().getGeneratorOptions().getDimensions().getEntrySet()) {
				if (entry.getValue().getChunkGenerator().getBiomeSource() instanceof ExpandedBiomeSource expandedBiomeSource){
					expandedBiomeSource.setResourceKeyMap(biomeMap);
					Set<RegistryEntry<Biome>> biomeHolders = ExpandedBiomes.buildBiomeList(registryAccess, entry.getKey());
					expandedBiomeSource.expandBiomesWith(biomeHolders);
				}
			}
		});
	}

	public static <MSG extends C2SPacket> void sendMSGToServer(MSG message) {
		NETWORK_WRAPPER.sendToServer(message);
	}

	public static <MSG extends S2CPacket> void sendMSGToAll(MSG message) {
		for (ServerPlayerEntity player : PlayerLookup.all(ServerLifecycleHooks.getCurrentServer())) {
			sendNonLocal(message, player);
		}
	}

	public static <MSG extends S2CPacket> void sendNonLocal(MSG message, ServerPlayerEntity player) {
		NETWORK_WRAPPER.sendToClient(message, player);
	}
}
