package model;

import java.util.Random;

import javafx.application.Platform;

public class Plant extends Organism {

	private static final int INITIAL_ENERGY = 1;
	private static final int MAX_ENERGY = 100;
	private static final int GROWTH_RATE = 1;
	private static final double SEED_CHANCE = 0.1;
	private static final double SEED_COST = 60;

	private Random random;

	private boolean fromSeed;
	private boolean marked;

	public Plant(World world, int x, int y, boolean fromSeed) {
		super(world, x, y, INITIAL_ENERGY);
		this.energy = INITIAL_ENERGY;
		this.random = new Random();

		this.fromSeed = fromSeed;
		this.marked = false;
	}

	@Override
	public synchronized void update() {
		if (this.energy < MAX_ENERGY) {
			this.energy += GROWTH_RATE;
			if (energy > MAX_ENERGY) {
				energy = MAX_ENERGY;
			}
		}

		if (energy >= MAX_ENERGY * 0.8) {
			if (random.nextDouble() < SEED_CHANCE) {
				energy -= SEED_COST;

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						int randX = 0;
						int randY = 0;

						do {
							int[] newCoords = moveCoordinates(x, y, random, 5);
							randX = newCoords[0];
							randY = newCoords[1];
						} while (!world.isPositionValid(randX, randY));


						world.getFieldAt(randX, randY).putSeed();
					}
				});
			}
		}
	}

	public synchronized int beEaten() {
		die();
		return this.energy;
	}

	public boolean grownFromSeed() {
		return this.fromSeed;
	}
	
	public synchronized boolean isMarked() {
		return this.marked;
	}
	
	public synchronized void mark() {
		this.marked = true;
	}
	
	public int getEnergy() {
		return this.energy;
	}
}
