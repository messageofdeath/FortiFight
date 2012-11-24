package me.messageofdeath.FortiFight.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.block.Block;

public class Blocks {

	private static HashMap<String, ArrayList<String>> blocks = new HashMap<String, ArrayList<String>>();
	private static ArrayList<String> extra = new ArrayList<String>();
	
	public static void addBlockToPlayer(String name, String block) {
		if(blocks.containsKey(name)) {
			ArrayList<String> bloc = blocks.get(name);
			bloc.add(block);
			blocks.remove(name);
			blocks.put(name, bloc);
			extra.add(block);
		}else{
			ArrayList<String> bloc = new ArrayList<String>();
			bloc.add(block);
			blocks.put(name, bloc);
			extra.add(block);
		}
	}
	
	public static void removeBlockFromPlayer(String name, String block) {
		if(blocks.containsKey(name)) {
			ArrayList<String> bloc = blocks.get(name);
			int i = bloc.size() - 1;
			while(i > -1) {
				if(bloc.get(i).equalsIgnoreCase(block)) {
					bloc.remove(i);
					i = 0;
				}
				i--;
			}
			int id = extra.size() - 1;
			while(id > -1) {
				if(extra.get(id).equalsIgnoreCase(block)) {
					extra.remove(id);
					id = 0;
				}
				id--;
			}
		}
	}
	
	public static boolean checkIfHeOwnsIt(String name, String block) {
		Engine.log(Level.SEVERE, "0");
		if(blocks.containsKey(name)) {
			Engine.log(Level.SEVERE, "1");
			ArrayList<String> bloc = blocks.get(name);
			int i = bloc.size() - 1;
			while(i > -1) {
				Engine.log(Level.SEVERE, "Loop:  " + i);
				if(bloc.get(i).equalsIgnoreCase(block)) {
					return true;
				}
				i--;
			}
			Engine.log(Level.SEVERE, "2");
		}
		return false;
	}
	
	protected static void clear() {
		blocks.clear();
		extra.clear();
	}
	
	public static boolean checkBlock(String block) {
		if(extra.contains(block)) {
			return true;
		}
		return false;
	}
}
