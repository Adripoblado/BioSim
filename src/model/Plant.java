package model;

import java.util.Random;

import javafx.application.Platform;

public class Plant extends Organism {

	private static final int INITIAL_ENERGY = 40;
	private static final int MAX_ENERGY = 120;
	private static final int GROWTH_RATE = 1;
	private static final double SEED_CHANCE = 0.14;
	private static final double SEED_COST = 50;

	private Random random;

	private boolean fromSeed;

	public Plant(World world, int x, int y, boolean fromSeed) {
		super(world, x, y, INITIAL_ENERGY);
		this.energy = INITIAL_ENERGY;
		this.random = new Random();

		this.fromSeed = fromSeed;
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

	public int beEaten() {
		die();
		return this.energy;
	}

	public boolean grownFromSeed() {
		return this.fromSeed;
	}
}
