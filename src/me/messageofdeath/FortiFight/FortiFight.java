package me.messageofdeath.FortiFight;

import java.util.logging.Level;

import me.messageofdeath.FortiFight.Listeners.playerListener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class FortiFight extends JavaPlugin {
	
	public static String prefix;
	public static PluginDescriptionFile file;
	
	@Override
	public void onEnable() {
		file = getDescription();
		prefix = "[" + file.getName() + "] " +  file.getVersion() + ": ";
		getServer().getPluginManager().registerEvents(new playerListener(), this);
		log(Level.INFO, "is now enabled!");
	}
	
	public static void log(Level level, String log) {
		Bukkit.getLogger().log(level, prefix + log);
	}
}
