package me.messageofdeath.FortiFight.Listeners;

import java.util.logging.Level;

import me.messageofdeath.FortiFight.API.Blocks;
import me.messageofdeath.FortiFight.API.Engine;
import me.messageofdeath.FortiFight.API.Player;
import net.milkycraft.Scheduler.PlayerTimerEndEvent;

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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class playerListener implements Listener {
	
	
	@EventHandler
	public void onTimerEnd(PlayerTimerEndEvent event) {
		if(event.getPlayer().getName().equalsIgnoreCase("suddenDeathTimer")) {
			if(Engine.isGameInSession()) {
				Engine.endGame();
			}
		}
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if(Engine.isInPreLobby()) {
			try {
				event.getPlayer().teleport(Engine.getLocation(1));
			}catch(NullPointerException e) {
				Engine.log(Level.SEVERE, "The spawn for the lobby does not exist!");
				event.getPlayer().sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + "There was an error, please inform your admin that they did not set up the config correctly.");
			}
			if(Bukkit.getOnlinePlayers().length == Engine.config.getInt("Game.minPlayersToStart")) {
				Engine.startGame();
			}
		}else if(!Engine.isGameInSession()) {
			event.getPlayer().sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + "Go build your fort! You have a limited amount of time!");
			event.getPlayer().setHealth(20);
			event.getPlayer().setFoodLevel(20);
			Engine.erasePlayerData(event.getPlayer());
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
		if(Engine.isGameInSession()) {
			if(Engine.isAllowedToJoin(0, event.getPlayer().getName())) {
				Engine.removeFromAllowed(0, event.getPlayer().getName());
				event.setJoinMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + event.getPlayer().getName() + " isn't mad any more lol...");
			}else{
				event.setJoinMessage(ChatColor.GOLD + "[Admin Cheesium] " + ChatColor.DARK_RED + event.getPlayer().getName() + " joined the server.");
			}
		}else{
			event.setJoinMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + event.getPlayer().getName() + " joined the server.");
		}
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent event) {
		event.setMotd(Engine.getMOTD());
		event.setMaxPlayers(Engine.config.getInt("Game.maxPlayers"));
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Location loc = event.getBlock().getLocation();
		String loc2 =  loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
		if(!Engine.isInPreLobby()) {
			if(!Engine.isGameInSession()) {
				if(!Blocks.isWithinAnotherBlock(event.getPlayer().getName(), loc2)) {
					if(Blocks.checkBlock(loc2)) {
						event.setCancelled(true);
					}else{
						Blocks.addBlockToPlayer(event.getPlayer().getName(), loc2);
					}
				}else{
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + "You cannot build within 5 blocks of another players block!");
				}
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
			if(!Engine.isGameInSession()) {
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
	}
	
	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		if(Engine.isGameInSession()) {
			if(Engine.isAllowedToJoin(1, event.getPlayer().getName())) {
				event.setQuitMessage(ChatColor.GOLD + "[Admin Chessium] " + ChatColor.DARK_RED + event.getPlayer().getName() + " left the server.");
			}else{
				event.setQuitMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_RED + event.getPlayer().getName() + " raged quit.");
			}
			if(!Engine.isAllowedToJoin(0, event.getPlayer().getName())) {
				Engine.addToAllowed(0, event.getPlayer().getName());
				Bukkit.getScheduler().scheduleSyncDelayedTask(Engine.plugin, new Runnable() {
					@Override
					public void run() {
						if(Engine.isAllowedToJoin(0, event.getPlayer().getName())) {
							Engine.removeFromAllowed(0, event.getPlayer().getName());
							Engine.setAmountOfPlayers(Engine.getAmountOfPlayers() - 1);
							Engine.amountOfPlayerDeath();
							for(org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
								player.sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + event.getPlayer().getName() + " did not make it back from his adventure home.");
							}
						}
					}
				}, 1800);
			}
		}else{
			event.setQuitMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_RED + event.getPlayer().getName() + " left the server.");
		}
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		if(Engine.dead.contains(event.getPlayer().getName())) {
			event.disallow(Result.KICK_OTHER, ChatColor.RED + "You cannot join when the game is in session! You Died!");
			return;
		}
		if(Engine.isGameInSession()) {
			if(Engine.isAllowedToJoin(0, event.getPlayer().getName()) || Engine.isAllowedToJoin(1, event.getPlayer().getName())) {
				return;
			}else{
				event.disallow(Result.KICK_OTHER, ChatColor.RED + "You cannot join when the game is in session!");
			}
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
			Engine.dead.add(player.getName());
			if(event.getEntity().getKiller() instanceof Player) {
				event.setDeathMessage(ChatColor.GOLD + "[Chessium] " +  ChatColor.AQUA + player.getName() + ChatColor.DARK_RED + " was killed by " + event.getEntity().getKiller().getName());
			}else if(event.getEntity().getKiller() instanceof LivingEntity) {
				LivingEntity entity = event.getEntity().getKiller();
				event.setDeathMessage(ChatColor.GOLD + "[Chessium] " +  ChatColor.AQUA + player.getName() + ChatColor.DARK_RED + " was killed by " + entity.getType().name().toLowerCase());
	
			}
			Engine.amountOfPlayerDeath();
			player.setHealth(20);
			player.setFoodLevel(20);
		}
	}
}
