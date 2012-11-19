package me.messageofdeath.FortiFight.API;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import lib.PatPeter.SQLibrary.MySQL;
import me.messageofdeath.FortiFight.FortiFight;

public class Engine {
	
	public static boolean canJoin = true;
	public static FortiFight plugin = FortiFight.plugin;
	private static File file;
	private static MySQL mysql;
	private static FileConfiguration config;
	
	public static void log(Level level, String log) {
		FortiFight.log(level, log);
	}
	
	public static Player getPlayer(String name) {
		return new Player(name);
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
			// *** MySQL ***
			mysql = new MySQL(Bukkit.getLogger(), "[FortiFight]", config.getString("MySQL.Host"), config.getString("MySQL.Port"), config.getString("MySQL.Database"), config.getString("MySQL.Username"), config.getString("MySQL.Password"));
			mysql.open();
			if(!mysql.checkTable("FortiFight")) {
				mysql.createTable("CREATE TABLE FortiFight (World VARCHAR(255), Id INT, IdData INT, X INT, Y INT, Z INT)");
			}
		}catch(Exception e) {}
	}
	
	public static void onShutDown() {
		mysql.close();
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
	
	public static void startGame() {
		for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers())  {
			player.setGameMode(GameMode.CREATIVE);
			player.sendMessage(ChatColor.GOLD + "Go build your forts! YOU HAVE 10 MINUTES!");
		}
		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers())  {
					player.setGameMode(GameMode.SURVIVAL);
				}
				canJoin = false;
			}
		}, 12000);
	}
	
	public static void endGame() {
		Engine.rollBackWorld();
		canJoin = true;
	}
	
	public static void registerBlockChange(FortBlock block) {
		try {
			mysql.query("INSERT INTO FortiFight (World, Id, IdData, X, Y, Z) VALUES ("+block.getWorld()+","+block.getId()+","+block.getIdData()+","+block.getX()+","+block.getY()+","+block.getZ()+")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void rollBackWorld() {
		try {
			ResultSet rs = mysql.query("SELECT * FROM FortiFight");
			while(rs.next()) {
				World world = Bukkit.getWorld(rs.getString("World"));
				int id = rs.getInt("Id");
				byte idData =  rs.getByte("IdData");
				int x = rs.getInt("X"), y = rs.getInt("Y"), z = rs.getInt("Z");
				Location loc = new Location(world, x, y, z);
				loc.getBlock().setTypeId(id);
				loc.getBlock().setData(idData);
			}
			mysql.query("DELETE * FROM FortiFight");
		} catch (SQLException e) {e.printStackTrace();}
	}
}
