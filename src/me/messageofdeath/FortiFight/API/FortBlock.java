package me.messageofdeath.FortiFight.API;

public class FortBlock {
	
	private String world;
	private int prevID, prevData, x, y, z;
	public FortBlock(String world, int prevID, int prevData, int x, int y, int z) {
		this.prevID = prevID;
		this.prevData = prevData;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String getWorld() {
		return world;
	}
	
	public int getId() {
		return prevID;
	}
	
	public int getIdData() {
		return prevData;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
}
