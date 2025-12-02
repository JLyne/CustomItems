package uk.co.notnull.CustomItems;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.notnull.CustomItems.api.CustomItems;
import uk.co.notnull.CustomItems.commands.CommandHandler;
import uk.co.notnull.CustomItems.creativeitemfilter.CreativeItemFilterHandler;
import uk.co.notnull.CustomItems.listeners.Inventories;
import uk.co.notnull.CustomItems.listeners.Join;
import uk.co.notnull.CustomItems.listeners.Loot;
import uk.co.notnull.CustomItems.loot.LootManagerImpl;
import uk.co.notnull.messageshelper.MessagesHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public final class CustomItemsImpl extends JavaPlugin implements CustomItems, Listener {
    private static CustomItemsImpl instance;
    ItemManagerImpl itemManager;
    LootManagerImpl lootManager;
    ChestManager chestManager;
	ConfigItemProvider configItemProvider = new ConfigItemProvider(this);
    final MessagesHelper messagesHelper = MessagesHelper.getInstance(this);
    private CreativeItemFilterHandler creativeItemFilterHandler;

    @Override
    public void onEnable() {
        instance = this;

		ConfigurationSerialization.registerClass(GrantedItem.class, "GrantedItem");

        lootManager = new LootManagerImpl(this);
        chestManager = new ChestManager(this);
        itemManager = new ItemManagerImpl(this, lootManager, chestManager);

        getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new Inventories(this), this);
		getServer().getPluginManager().registerEvents(new Join(this), this);
		getServer().getPluginManager().registerEvents(new Loot(this), this);

        LifecycleEventManager<@org.jetbrains.annotations.NotNull Plugin> manager = getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> new CommandHandler(this, event.registrar()));

        try {
            initConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }

		itemManager.registerProvider(configItemProvider);
    }

    public static CustomItemsImpl getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((JavaPlugin) this);
        getItemManager().saveUnclaimedItems();
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
		if (event.getPlugin().getName().equals("CreativeItemFilter")) {
			getLogger().info("Initialising CreativeItemFilter handler");
			creativeItemFilterHandler = new CreativeItemFilterHandler(this);
		}
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
		if (event.getPlugin().getName().equals("CreativeItemFilter")) {
			if (creativeItemFilterHandler != null) {
				getLogger().info("Disabling CreativeItemFilter handler");
				creativeItemFilterHandler = null;
			}
		}
    }

    public ItemManagerImpl getItemManager() {
        return itemManager;
    }

    public LootManagerImpl getLootManager() {
        return lootManager;
    }

    public ChestManager getChestManager() {
        return chestManager;
    }

    public MessagesHelper getMessagesHelper() {
        return messagesHelper;
    }

    public void initConfig() {
    	reloadConfig();
        saveDefaultConfig();

        createFile("messages.yml");
		try {
			messagesHelper.loadMessages(new File(getDataFolder(), "messages.yml"));
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Failed to load messages", e);
		}

		chestManager.closeAllGUIS();
        lootManager.loadLootConfig(getConfig().getConfigurationSection("loot"));
        configItemProvider.loadItemConfig(getConfig().getConfigurationSection("items"));

        try {
            ConfigurationSection chestLocationConfig = getConfig().getConfigurationSection("claimChestLocation");
            Location location = new Location(
                    getServer().getWorld(chestLocationConfig.getString("world", null)),
                    chestLocationConfig.getInt("x", 0),
                    chestLocationConfig.getInt("y", 0),
                    chestLocationConfig.getInt("z", 0)
            );

            chestManager.setChestLocation(location);
        } catch (IllegalArgumentException e) {
            getLogger().warning("Invalid claim chest location: " + e.getMessage());
        }
	}

    /**
     * Create a file to be used in the plugin
     * @param name the name of the file
     */
    @SuppressWarnings({"SameParameterValue", "ResultOfMethodCallIgnored", "ConstantConditions"})
    private void createFile(String name) {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), name);

        if (!file.exists()) {
            try (InputStream in = getClassLoader().getResourceAsStream(name)) {
                Files.copy(in, file.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}