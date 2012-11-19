package me.messageofdeath.FortiFight.Listeners;

import me.messageofdeath.FortiFight.API.Engine;
import me.messageofdeath.FortiFight.API.Player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class playerListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if(!Engine.isGameInSession()) {
			if((Bukkit.getOnlinePlayers().length - 1) == 2) {
				Engine.startGame();
			}
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
	}
}
