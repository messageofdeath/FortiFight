package me.messageofdeath.FortiFight.Listeners;

import me.messageofdeath.FortiFight.API.Engine;
import me.messageofdeath.FortiFight.API.FortBlock;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class blockListener implements Listener {
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Block block1 = event.getBlock();
		Location loc = block1.getLocation();
		FortBlock block = new FortBlock(loc.getWorld().getName(), block1.getTypeId(), block1.getData(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		Engine.registerBlockChange(block);
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Block block1 = event.getBlock();
		Location loc = block1.getLocation();
		FortBlock block = new FortBlock(loc.getWorld().getName(), block1.getTypeId(), block1.getData(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		Engine.registerBlockChange(block);
	}
	
	@EventHandler
	public void onBurn(BlockBurnEvent event) {
		Block block1 = event.getBlock();
		Location loc = block1.getLocation();
		FortBlock block = new FortBlock(loc.getWorld().getName(), block1.getTypeId(), block1.getData(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		Engine.registerBlockChange(block);
	}
}
