package uk.co.notnull.CustomItems;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.notnull.CustomItems.listeners.Inventories;
import uk.co.notnull.CustomItems.listeners.Join;
import uk.co.notnull.CustomItems.listeners.Loot;
import uk.co.notnull.CustomItems.listeners.Wearables;
import uk.co.notnull.CustomItems.messages.Messages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.function.Function;

public class CustomItems extends JavaPlugin implements Listener {
    ItemManager itemManager;
    ChestManager chestManager;

    @Override
    public void onEnable() {
		initConfig();
		createFile("messages.yml");

		ConfigurationSerialization.registerClass(GrantedItem.class, "GrantedItem");

        itemManager = new ItemManager(this, getConfig().getConfigurationSection("items"));
        chestManager = new ChestManager(this, getConfig());
		getServer().getPluginManager().registerEvents(new Inventories(this), this);
		getServer().getPluginManager().registerEvents(new Wearables(this), this);
		getServer().getPluginManager().registerEvents(new Join(this), this);
		getServer().getPluginManager().registerEvents(new Loot(this), this);

        try {
            ConfigurationSection messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));
            Messages.set(messages);

            registerCommands();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((JavaPlugin) this);
        getItemManager().saveUnclaimedItems();
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public ChestManager getChestManager() {
        return chestManager;
    }

    private void registerCommands() throws Exception {
        CommandManager<CommandSender> manager = new PaperCommandManager<>(
                this,
                CommandExecutionCoordinator.simpleCoordinator(),
                Function.identity(),
                Function.identity());

        new MinecraftExceptionHandler<CommandSender>()
            .withArgumentParsingHandler()
            .withInvalidSenderHandler()
            .withInvalidSyntaxHandler()
            .withNoPermissionHandler()
            .withCommandExecutionHandler()
            .withDecorator(message -> message)
            .apply(manager, p -> p);

        AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(
                manager,
                CommandSender.class,
                parameters -> SimpleCommandMeta.empty()
        );

        annotationParser.parse(new Commands(this, manager));
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