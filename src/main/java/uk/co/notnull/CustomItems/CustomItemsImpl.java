package uk.co.notnull.CustomItems;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.notnull.CustomItems.api.CustomItems;
import uk.co.notnull.CustomItems.commands.CommandHandler;
import uk.co.notnull.CustomItems.listeners.Inventories;
import uk.co.notnull.CustomItems.listeners.Join;
import uk.co.notnull.CustomItems.listeners.Loot;
import uk.co.notnull.CustomItems.listeners.Wearables;
import uk.co.notnull.CustomItems.loot.LootManagerImpl;
import uk.co.notnull.messageshelper.MessagesHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class CustomItemsImpl extends JavaPlugin implements CustomItems, Listener {
    private static CustomItemsImpl instance;
    ItemManagerImpl itemManager;
    LootManagerImpl lootManager;
    ChestManager chestManager;
    MessagesHelper messagesHelper = MessagesHelper.getInstance();

    @Override
    public void onEnable() {
        instance = this;

		initConfig();
		createFile("messages.yml");

		ConfigurationSerialization.registerClass(GrantedItem.class, "GrantedItem");

        lootManager = new LootManagerImpl(this, getConfig().getConfigurationSection("loot"));
        itemManager = new ItemManagerImpl(this, lootManager, getConfig().getConfigurationSection("items"));
        chestManager = new ChestManager(this, getConfig());
		getServer().getPluginManager().registerEvents(new Inventories(this), this);
		getServer().getPluginManager().registerEvents(new Wearables(this), this);
		getServer().getPluginManager().registerEvents(new Join(this), this);
		getServer().getPluginManager().registerEvents(new Loot(this), this);

        try {
            messagesHelper.loadMessages(new File(getDataFolder(), "messages.yml"));
            LifecycleEventManager<Plugin> manager = getLifecycleManager();
            manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> new CommandHandler(this, event.registrar()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CustomItemsImpl getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((JavaPlugin) this);
        getItemManager().saveUnclaimedItems();
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

    private void initConfig() {
    	getConfig();
        saveDefaultConfig();
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