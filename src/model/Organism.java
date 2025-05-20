package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Organism {

	protected int x, y; // Position in the world
	protected int energy; // Life points
	protected World world;
	protected boolean alive;

	public Organism(World world, int x, int y, int initialEnergy) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.energy = initialEnergy;
		this.alive = true;
	}

	public synchronized void update() {
		// TODO ???
	}

	public int[] moveCoordinates(int x, int y, Random random, int range) {
	    int newX = generateBalancedMove(random, range, x, world.getWidth());
	    int newY = generateBalancedMove(random, range, y, world.getHeight());

	    return new int[] { x + newX, y + newY };
	}

	private int generateBalancedMove(Random random, int range, int coord, int max) {
	    List<Integer> posibles = new ArrayList<>();

	    for (int i = -range; i <= range; i++) {
	        if (i == 0) continue;
	        int nuevaCoord = coord + i;
	        if (nuevaCoord >= 0 && nuevaCoord < max) {
	            posibles.add(i);
	        }
	    }

	    if (posibles.isEmpty()) return 0;

	    return posibles.get(random.nextInt(posibles.size()));
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
