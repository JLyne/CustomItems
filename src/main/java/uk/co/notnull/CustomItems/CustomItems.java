package uk.co.notnull.CustomItems;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.notnull.CustomItems.Commands.CustomItemsCommand;
import uk.co.notnull.CustomItems.Listeners.Inventories;
import uk.co.notnull.CustomItems.Listeners.Join;
import uk.co.notnull.CustomItems.Listeners.Loot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;

public class CustomItems extends JavaPlugin implements Listener {
    ItemManager itemManager;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
		initConfig();
		createFile("languages/en-US.yml");

		ConfigurationSerialization.registerClass(GrantedItem.class, "GrantedItem");

        itemManager = new ItemManager(this, getConfig().getConfigurationSection("items"));
		getServer().getPluginManager().registerEvents(new Inventories(this), this);
		getServer().getPluginManager().registerEvents(new Join(this), this);
		getServer().getPluginManager().registerEvents(new Loot(this), this);

        registerCommands();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((JavaPlugin) this);
        getItemManager().saveUnclaimedItems();
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    private void registerCommands() {
        commandManager = new PaperCommandManager(this);
        registerLanguages();

        commandManager.enableUnstableAPI("help");
        commandManager.getCommandCompletions().registerAsyncCompletion("itemids", c ->
               itemManager.getItemIds()
        );
        commandManager.getCommandCompletions().registerAsyncCompletion("categories", c ->
               itemManager.getCategories()
        );
        commandManager.registerCommand(new CustomItemsCommand());
    }

    private void initConfig() {
    	getConfig();
        saveDefaultConfig();
	}

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Create a file to be used in the plugin
     * @param name the name of the file
     */
    private void createFile(String name) {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File languageFolder = new File(getDataFolder(), "languages");
        if (!languageFolder.exists()) {
            languageFolder.mkdirs();
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

    /**
     * Load all the language files for the plugin
     */
    public void registerLanguages() {
        try {
            File languageFolder = new File(getDataFolder(), "languages");
            for (File file : Objects.requireNonNull(languageFolder.listFiles())) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".yml")) {
                        String updatedName = file.getName().replace(".yml", "");
                        commandManager.addSupportedLanguage(Locale.forLanguageTag(updatedName));
                        commandManager.getLocales().loadYamlLanguageFile(new File(languageFolder, file.getName()), Locale.forLanguageTag(updatedName));
                    }
                }
            }
            commandManager.getLocales().setDefaultLocale(Locale.forLanguageTag("en-US"));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}