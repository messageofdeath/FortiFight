package me.messageofdeath.FortiFight.API;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import me.messageofdeath.FortiFight.FortiFight;

public class Engine {
	
	public static boolean canJoin = true;
	public static boolean isInPreLobby = true;
	public static FortiFight plugin = FortiFight.plugin;
	private static File file;
	private static FileConfiguration config;
	private static int amountOfPlayers;
	private static CoreProtectAPI api;
	public static ArrayList<String> players = new ArrayList<String>();
	public static ArrayList<Block> blocks =  new ArrayList<Block>();
	
	public static void log(Level level, String log) {
		FortiFight.log(level, log);
	}
	
	public static void addToBlocks(Block block) {
		blocks.add(block);
	}
	
	public static int getAmountOfPlayers() {
		return amountOfPlayers;
	}
	
	public static void setAmountOfPlayers(int i) {
		amountOfPlayers = i;
	}
	
	public static Player getPlayer(String name) {
		return new Player(name);
	}
	
	public static boolean isInPreLobby() {
		return isInPreLobby;
	}
	
	public static boolean isGameInSession() {
		if(canJoin == false) {
			return true;
		}return false;
	}
	
	public static void onStartUp() {
		file = new File(plugin.getDataFolder(), "config.yml");
		try {
			// *** Config ***
			if(!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
				copy(plugin.getResource("config.yml"), file);
			}
			config = new YamlConfiguration();
			config.load(file);
			api = getCoreProtect();
		}catch(Exception e) {}
	}
	
	public static void onShutDown() {
	}
	
	private static void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static CoreProtectAPI getCoreProtect() {
		Plugin plugin = Engine.plugin.getServer().getPluginManager().getPlugin("CoreProtect");
		     
		// Check that CoreProtect is loaded
		if (plugin == null || !(plugin instanceof CoreProtect)) {
		  return null;
		}
		        
		// Check that a compatible version of CoreProtect is loaded
		if (Double.parseDouble(plugin.getDescription().getVersion()) < 1.6){
		  return null;
		}
		        
		// Check that the API is enabled
		CoreProtectAPI CoreProtect = ((CoreProtect)plugin).getAPI();
		if (CoreProtect.isEnabled()==false){
		  return null;
		}
		         
		return CoreProtect;
	}
	
	public static void startGame() {
		isInPreLobby = false;
		for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers())  {
			player.setGameMode(GameMode.CREATIVE);
			player.teleport(new Location(Bukkit.getWorld("world"), 86, 67, 253));
			player.sendMessage(ChatColor.GOLD + "[Chessium] " +  ChatColor.DARK_RED + "Go build your forts you have 10 minutes!");
		}
		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers())  {
					player.sendMessage(ChatColor.GOLD + "[Chessium] " +  ChatColor.DARK_RED + "You have 5 minutes!");
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers())  {
							player.sendMessage(ChatColor.GOLD + "[Chessium] " +  ChatColor.DARK_RED + "You have 1 minute!");
						}
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							@Override
							public void run() {
								for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers())  {
									player.setGameMode(GameMode.SURVIVAL);
									player.sendMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_RED + "The game has started!");
								}
								Engine.setAmountOfPlayers(Bukkit.getOnlinePlayers().length);
								canJoin = false;
							}
						}, 5);//1200
					}
				}, 5);//4800
			}
		}, 5);//6000
	}
	
	public static void endGame() {
		for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
			player.kickPlayer(ChatColor.RED + "Map Resetting. You will be able to join in a second.");
		}
		Engine.erasePlayerData();
		Engine.rollBackWorld();
		canJoin = true;
	}
	
	public static void erasePlayerData() {
		
	}
	
	public static void rollBackWorld() {
		ArrayList<Integer> n = new ArrayList<Integer>();
		int i = players.size() - 1;
		while(i > -1) {
			api.performRollback(players.get(i), 7200, 0, new Location(Bukkit.getWorld("world"), 0,0,0), n, n);
			players.remove(i);
			i--;
		}
	}
}
