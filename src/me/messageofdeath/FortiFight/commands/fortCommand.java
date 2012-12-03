package me.messageofdeath.FortiFight.commands;

import java.io.IOException;

import me.messageofdeath.FortiFight.API.Engine;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class fortCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("fort")) {
			if(sender.hasPermission("fort.admin")) {
				if(args.length == 0) {
					sender.sendMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_RED + "Use /fort help");
				}
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("endGame")) {
						Engine.endGame();
					}
					if(args[0].equalsIgnoreCase("help")) {
						sender.sendMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_GREEN + "/fort help : To get Help");
						sender.sendMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_GREEN + "/fort setspawn lobby : Set spawn for the lobby");
						sender.sendMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_GREEN + "/fort setspawn game : Set spawn for the starting point of warfare");
						sender.sendMessage(ChatColor.GOLD + "[Cheesium] " + ChatColor.DARK_GREEN + "/fort endGame : End the game with rollback");
					}
				}
				if(args.length == 2) {
					if(args[0].equalsIgnoreCase("setspawn")) {
						if(args[1].equalsIgnoreCase("lobby")) {
							if(sender instanceof Player) {
								Player player = (Player)sender;
								Location loc = player.getLocation();
								double x = loc.getX(), y = loc.getY(), z = loc.getZ();
								float yaw = loc.getYaw(), pitch = loc.getPitch();
								String world = loc.getWorld().getName();
								Engine.config.set("Game.preGameSpawn.World", world);
								Engine.config.set("Game.preGameSpawn.X", x);
								Engine.config.set("Game.preGameSpawn.Y", y);
								Engine.config.set("Game.preGameSpawn.Z", z);
								Engine.config.set("Game.preGameSpawn.Yaw", yaw);
								Engine.config.set("Game.preGameSpawn.Pitch", pitch);
								try {
									Engine.config.save(Engine.file);
								} catch (IOException e) {
									e.printStackTrace();
								}
								sender.sendMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_RED + "The spawn for the lobby has been set");
							}else{
								sender.sendMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_RED + "This command can only be use in-game");
							}
						}
						if(args[1].equalsIgnoreCase("game")) {
							if(sender instanceof Player) {
								Player player = (Player)sender;
								Location loc = player.getLocation();
								double x = loc.getX(), y = loc.getY(), z = loc.getZ();
								float yaw = loc.getYaw(), pitch = loc.getPitch();
								String world = loc.getWorld().getName();
								Engine.config.set("Game.gameSpawn.World", world);
								Engine.config.set("Game.gameSpawn.X", x);
								Engine.config.set("Game.gameSpawn.Y", y);
								Engine.config.set("Game.gameSpawn.Z", z);
								Engine.config.set("Game.gameSpawn.Yaw", yaw);
								Engine.config.set("Game.gameSpawn.Pitch", pitch);
								try {
									Engine.config.save(Engine.file);
								} catch (IOException e) {
									e.printStackTrace();
								}
								sender.sendMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_RED + "The spawn for the game has been set");
							}else{
								sender.sendMessage(ChatColor.GOLD + "[Chessium] " + ChatColor.DARK_RED + "This command can only be use in-game");
							}
						}
					}
				}
			}
		}
		return false;
	}
}