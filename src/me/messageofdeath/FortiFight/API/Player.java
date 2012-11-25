package me.messageofdeath.FortiFight.API;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;

public class Player {
	
	public org.bukkit.entity.Player player;
	
	public Player(String name) {
		player = Bukkit.getPlayer(name);
	}
	
	public String getName() {
		return player.getName();
	}
	
	public int getScore() {
		return 0;
	}
	
	public void setScore(int score) {
		
	}
	
	public void setGameMode(GameMode gm) {
		player.setGameMode(gm);
	}
	
	public void kickPlayer() {
		player.kickPlayer(ChatColor.DARK_RED + Engine.config.getString("Game.kickplayerDeathMessage"));
	}
}
