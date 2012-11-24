package me.messageofdeath.FortiFight.Listeners;

import java.util.logging.Level;

import me.messageofdeath.FortiFight.API.Blocks;
import me.messageofdeath.FortiFight.API.Engine;
import me.messageofdeath.FortiFight.API.Player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;

public class playerListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if(Engine.isInPreLobby()) {
			try {
				event.getPlayer().teleport(Engine.getLocation(1));
			}catch(NullPointerException e) {
				Engine.log(Level.SEVERE, "The spawn for the lobby does not exist!");
			}
			if(Bukkit.getOnlinePlayers().length == Engine.config.getInt("Game.minPlayersToStart")) {
				Engine.startGame();
			}
		}else if(!Engine.isGameInSession()) {
			event.getPlayer().sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + "Go build your fort! You have a limited amount of time!");
			try {
				event.getPlayer().teleport(Engine.getLocation(2));
			}catch(NullPointerException e) {
				Engine.log(Level.SEVERE, "The spawn for the game does not exist!");
			}
			event.getPlayer().setGameMode(GameMode.CREATIVE);
		}
		if(!Engine.players.contains(event.getPlayer().getName())) {
			Engine.players.add(event.getPlayer().getName());
		}
		event.getPlayer().setHealth(20);
		event.getPlayer().setFoodLevel(20);
		Engine.erasePlayerData(event.getPlayer());
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent event) {
		event.setMotd(Engine.getMOTD());
		event.setMaxPlayers(Engine.config.getInt("Game.maxPlayers"));
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if(!Engine.isInPreLobby()) {
			Location loc = event.getBlock().getLocation();
			String loc2 =  loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
			if(Blocks.checkBlock(loc2)) {
				event.setCancelled(true);
			}else{
				Blocks.addBlockToPlayer(event.getPlayer().getName(), loc2);
			}
		}else{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(Engine.isInPreLobby()) {
			event.setCancelled(true);
		}else{
			Location loc = event.getBlock().getLocation();
			String loc2 =  loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
			if(Blocks.checkBlock(loc2)) {
				if(Blocks.checkIfHeOwnsIt(event.getPlayer().getName(), loc2)) {
					Blocks.removeBlockFromPlayer(event.getPlayer().getName(), loc2);
				}else{
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + "You can only break your own blocks!");
				}
			}
		}
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		if(Engine.isGameInSession()) {
			event.disallow(Result.KICK_OTHER, ChatColor.RED + "You cannot join when the game is in session!");
		}else{
			if(Bukkit.getOnlinePlayers().length == Engine.config.getInt("Game.maxPlayers")) {
				event.disallow(Result.KICK_FULL, ChatColor.DARK_RED + "The server has reached its maximum capacity.");
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if(Engine.isGameInSession()) {
			Player play = Engine.getPlayer(event.getEntity().getName());
			org.bukkit.entity.Player player = event.getEntity();
			play.kickPlayer();
			Engine.setAmountOfPlayers(Engine.getAmountOfPlayers() - 1);
			if(event.getEntity().getKiller() instanceof Player) {
				event.setDeathMessage(ChatColor.GOLD + "[Chessium] " +  ChatColor.AQUA + player.getName() + ChatColor.DARK_RED + " was killed by " + event.getEntity().getKiller().getName());
			}else if(event.getEntity().getKiller() instanceof LivingEntity) {
				LivingEntity entity = event.getEntity().getKiller();
				event.setDeathMessage(ChatColor.GOLD + "[Chessium] " +  ChatColor.AQUA + player.getName() + ChatColor.DARK_RED + " was killed by " + entity.getType().name().toLowerCase());
	
			}
			if(Engine.getAmountOfPlayers() == 1) {
				Engine.endGame();
			}
			if(Engine.getAmountOfPlayers() == 2) {
				for(org.bukkit.entity.Player players : Bukkit.getOnlinePlayers()) {
					players.sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + "Sudden Death! You have 5 minutes to kill the other player!");
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(Engine.plugin, new Runnable() {
					@Override
					public void run() {
						if(Engine.isGameInSession()) {
							Engine.endGame();
						}
					}
				}, 6000);
			}
			player.setHealth(20);
			player.setFoodLevel(20);
		}
	}
}
