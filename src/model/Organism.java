package model;

public abstract class Organism {
	
	protected int x, y; //Position in the world
	protected int energy; //Life points
	protected World world;
	protected boolean alive;

	public Organism(World world, int x, int y, int initialEnergy) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.energy = initialEnergy;
		this.alive = true;
	}
	
	public void update() {
		
	}
	
	public void spendEnergy(int amount) {
		this.energy -= amount;
	}
	
	public void earnEnergy(int amount) {
		this.energy += amount;
	}
	
	public boolean isAlive() {
		return this.alive;
	}
	
	public void die() {
		this.alive = false;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
