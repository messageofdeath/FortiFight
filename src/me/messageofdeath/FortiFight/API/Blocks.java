package me.messageofdeath.FortiFight.API;

import java.util.ArrayList;
import java.util.HashMap;

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
		if(blocks.containsKey(name)) {
			ArrayList<String> bloc = blocks.get(name);
			int i = bloc.size() - 1;
			while(i > -1) {
				if(bloc.get(i).equalsIgnoreCase(block)) {
					return true;
				}
				i--;
			}
		}
		return false;
	}
	
	public static boolean isWithinAnotherBlock(String name, String loc) {
		int radius = Engine.config.getInt("Game.blockProtectionRadius");
		String[] loc22 = loc.split(",");
		int startX = Integer.parseInt(loc22[0]), startY = Integer.parseInt(loc22[1]), startZ = Integer.parseInt(loc22[2]) ;
		for (int x = startX - radius; x <= startX + radius; x++) {
			for (int y = startY - radius; y <= startY + radius; y++) {
				for (int z = startZ - radius; z <= startZ + radius; z++) {
					String newLoc = x + "," + y + "," + z;
					if(Blocks.checkBlock(newLoc)) {
						if(!Blocks.checkIfHeOwnsIt(name, newLoc)) {
							return true;
						}
					}
				}
			}
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
