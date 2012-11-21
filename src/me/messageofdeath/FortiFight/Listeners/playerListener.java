package me.messageofdeath.FortiFight.Listeners;

import me.messageofdeath.FortiFight.API.Engine;
import me.messageofdeath.FortiFight.API.Player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class playerListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if(Engine.isInPreLobby()) {
			if(Bukkit.getOnlinePlayers().length == 2) {
				Engine.startGame();
			}
		}else{
			event.getPlayer().sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_RED + "Go build your fort! You have a limited amount of time!");
			event.getPlayer().setGameMode(GameMode.CREATIVE);
		}
		if(!Engine.players.contains(event.getPlayer().getName())) {
			Engine.players.add(event.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if(!Engine.isInPreLobby()) {
			Engine.addToBlocks(event.getBlock());
		}else{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(Engine.isInPreLobby()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		if(Engine.isGameInSession()) {
			event.disallow(Result.KICK_OTHER, ChatColor.RED + "You cannot join when the game is in session!");
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = Engine.getPlayer(event.getEntity().getName());
		player.kickPlayer();
		Engine.setAmountOfPlayers(Engine.getAmountOfPlayers() - 1);
		event.setDeathMessage(ChatColor.GOLD + "[Chessium] " +  ChatColor.AQUA + player.getName() + ChatColor.DARK_RED + " was killed by " + event.getEntity().getKiller().getName());
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
	}
}
