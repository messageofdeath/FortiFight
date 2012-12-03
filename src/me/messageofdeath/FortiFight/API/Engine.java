package me.messageofdeath.FortiFight.API;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.coreprotect.CoreProtectAPI;
import net.milkycraft.Scheduler.PlayerTimer;
import net.milkycraft.Scheduler.Schedule;
import net.milkycraft.Scheduler.Scheduler;
import net.milkycraft.Scheduler.Time;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import me.messageofdeath.FortiFight.FortiFight;

public class Engine {
	
	public static boolean canJoin = true;
	public static boolean isInPreLobby = true;
	public static boolean isInSuddenDeath = false;
	public static FortiFight plugin = FortiFight.plugin;
	public static File file;
	public static FileConfiguration config;
	private static int amountOfPlayers;
	public static ArrayList<String> players = new ArrayList<String>(), allowed = new ArrayList<String>(), adminallowed = new ArrayList<String>()
			, dead = new ArrayList<String>();
	
	public static void log(Level level, String log) {
		FortiFight.log(level, log);
	}
	
	public static int getAmountOfPlayers() {
		return amountOfPlayers;
	}
	
	public static void amountOfPlayerDeath() {
		if(Engine.getAmountOfPlayers() <= 1) {
			Engine.endGame();
		}
		if(Engine.getAmountOfPlayers() == 2) {
			for(org.bukkit.entity.Player players : Bukkit.getOnlinePlayers()) {
				players.sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + "Sudden Death! You have 5 minutes to kill the other player!");
			}
			isInSuddenDeath = true;
			PlayerTimer.clearTimers();
			Scheduler.schedulePlayerCooldown(Scheduler.schedule(plugin, "suddenDeathTimer", Time.suddenDeathTime));
		}
	}
	
	public static boolean isGameInSuddenDeath() {
		return isInSuddenDeath;
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
		}catch(Exception e) {}
	}
	
	public static boolean isAllowedToJoin(int id, String name) {
		if(id == 0) {
			int i = allowed.size() - 1;
			while(i > -1) {
				if(allowed.get(i).equalsIgnoreCase(name)) {
					return true;
				}
				i--;
			}	
		}
		if(id == 1) {
			int i = adminallowed.size() - 1;
			while(i > -1) {
				if(adminallowed.get(i).equalsIgnoreCase(name)) {
					return true;
				}
				i--;
			}
		}
		return false;
	}
	
	public static void addToAllowed(int i, String name) {
		if(i == 0) {
			allowed.add(name);
		}
		if(i == 1) {
			adminallowed.add(name);
		}
	}
	
	public static void removeFromAllowed(int i, String name) {
		if(i == 0) {
			allowed.remove(name);
		}
		if(i == 1) {
			adminallowed.remove(name);
		}
	}
	
	public static Location getLocation(int i) {
		if(i == 1) {
			return new Location(Bukkit.getWorld(config.getString("Game.preGameSpawn.World")), config.getDouble("Game.preGameSpawn.X")
					, config.getDouble("Game.preGameSpawn.Y"), config.getDouble("Game.preGameSpawn.Z")
					, Float.parseFloat(String.valueOf(config.getDouble("Game.preGameSpawn.Yaw")))
					, Float.parseFloat(String.valueOf(config.getDouble("Game.preGameSpawn.Pitch"))));
		}
		if(i == 2) {
			return new Location(Bukkit.getWorld(config.getString("Game.gameSpawn.World")), config.getDouble("Game.gameSpawn.X")
					, config.getDouble("Game.gameSpawn.Y"), config.getDouble("Game.gameSpawn.Z")
					, Float.parseFloat(String.valueOf(config.getDouble("Game.gameSpawn.Yaw")))
					, Float.parseFloat(String.valueOf(config.getDouble("Game.gameSpawn.Pitch"))));
		}
		return null;
	}
	
	public static String getMOTD() {
		if(Engine.isInPreLobby()) {
			return ChatColor.DARK_RED + "The game is the in lobby.";
		}else if(Engine.isGameInSuddenDeath()) {
			return ChatColor.DARK_RED + "The game is in Sudden Death!                     Time Left: " + Engine.getTimeLeft("suddenDeathTimer", Time.suddenDeathTime);
		}else if(Engine.isGameInSession()) {
			return ChatColor.DARK_RED + "The game is in session.                           Time Left: " + Engine.getTimeLeft("gameTimer", Time.gameTime);
		}else if(!Engine.isInPreLobby()) {
			return ChatColor.DARK_RED + "You can still join! Create your fort!            Time Left: " + Engine.getTimeLeft("preGameTimer", Time.preGameTime);
		}
		return ChatColor.DARK_RED + "There is an error, inform an admin.";
	}
	
	public static String getTimeLeft(String name, Time time) {
		int i = PlayerTimer.getRemainingTime(name, time);
		int remainder = i % 3600, minutes = remainder / 60, seconds = remainder % 60; 
		return ChatColor.GOLD + String.valueOf(minutes) + " minutes and " + seconds + " seconds";
	}
	
	public static void onShutDown() {
		for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
			player.kickPlayer(ChatColor.DARK_RED + "You have been kicked due to a reload/shutdown");
		}
		Engine.endGame();
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
	
	@SuppressWarnings("unchecked")
	public static void startGame() {
		isInPreLobby = false;
		adminallowed = (ArrayList<String>)config.getList("Game.canJoinWhenGameIsInSession");
		Scheduler.schedulePlayerCooldown(Scheduler.schedule(plugin, "preGameTimer", Time.preGameTime));
		for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers())  {
			player.setGameMode(GameMode.CREATIVE);
			try {
				player.teleport(getLocation(2));
			}catch(NullPointerException e) {
				log(Level.SEVERE, "The spawn for the game is unknown");
				player.sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + "There was an error, please inform your admin that they did not set up the config correctly.");
			}
			player.setHealth(20);
			player.setFoodLevel(20);
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
								int i = 0;
								for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers())  {
									player.setGameMode(GameMode.SURVIVAL);
									player.sendMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_RED + "The game has started! You have 20 minutes!");
									i++;
								}
								Engine.setAmountOfPlayers(i);
								Engine.amountOfPlayerDeath();
								canJoin = false;
								Engine.startTimer();
							}
						}, 1200);//1200
					}
				}, 4800);//4800
			}
		}, 6000);//6000
	}
	
	public static void startTimer() {
		Schedule s = Scheduler.schedule(plugin, "gameTimer", Time.gameTime);
		Scheduler.schedulePlayerCooldown(s);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				if(Engine.isGameInSession()) {
					for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers())  {
						player.sendMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_RED + "You have 10 minutes left!");
					}
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							if(Engine.isGameInSession()) {
								for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers())  {
									player.sendMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_RED + "You have 5 minutes left!");
								}
								Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									@Override
									public void run() {
										if(Engine.isGameInSession()) {
											Engine.endGame();
										}
									}
								}, 6000);
							}
						}
					}, 6000);
				}
			}
		}, 12000);
	}
	
	public static void endGame() {
		for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
			player.kickPlayer(ChatColor.RED + "Map Resetting. You will be able to join in a second.");
		}
		Engine.removeItemsFromWorld();
		Engine.rollBackWorld();
		Blocks.clear();
		allowed.clear();
		canJoin = true;
		isInPreLobby = true;
		PlayerTimer.clearTimers();
	}
	
	public static void erasePlayerData(org.bukkit.entity.Player player) {
		player.getInventory().clear();
	}
	
	public static void removeItemsFromWorld() {
		World world = Bukkit.getWorld(config.getString("Game.gameSpawn.World"));
		final List<Entity> entities = world.getEntities();
		for(Entity entity : entities) {
			if(entity instanceof Item) {
				entity.remove();
			}
		}
	}
	
	public static void rollBackWorld() {
		for(String name : players) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "co rollback u:" + name + " t:24h");
		}
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "co rollback u:explosion t:24");
		players.clear();
	}
}
