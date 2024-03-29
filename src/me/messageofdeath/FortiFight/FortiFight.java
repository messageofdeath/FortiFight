package me.messageofdeath.FortiFight;

import java.util.logging.Level;

import me.messageofdeath.FortiFight.API.Engine;
import me.messageofdeath.FortiFight.Listeners.playerListener;
import me.messageofdeath.FortiFight.commands.fortCommand;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class FortiFight extends JavaPlugin {
	
	public static String prefix;
	public static PluginDescriptionFile file;
	public static FortiFight plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		file = getDescription();
		prefix = "[" + file.getName() + "] " +  file.getVersion() + ": ";
		Engine.onStartUp();
		getServer().getPluginManager().registerEvents(new playerListener(), this);
		getCommand("fort").setExecutor(new fortCommand());
		log(Level.INFO, "is now enabled!");
	}
	
	@Override
	public void onDisable() {
		Engine.onShutDown();
	}
	
	public static void log(Level level, String log) {
		Bukkit.getLogger().log(level, prefix + log);
	}
}
