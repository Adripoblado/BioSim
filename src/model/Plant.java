package model;

import java.util.Random;

public class Plant extends Organism {

	private static final int INITIAL_ENERGY = 5;
	private static final int MAX_ENERGY = 50;
	private static final int GROWTH_RATE = 1;
	private static final double SEED_CHANCE = 0.05;
	private static final double SEED_COST = 30;

	private Random random;

	private boolean fromSeed;

	public Plant(World world, int x, int y, boolean fromSeed) {
		super(world, x, y, INITIAL_ENERGY);
		this.energy = INITIAL_ENERGY;
		this.random = new Random();

		this.fromSeed = fromSeed;
	}

	@Override
	public void update() {
		if (this.energy < MAX_ENERGY) {
			this.energy += GROWTH_RATE;
			if (energy > MAX_ENERGY) {
				energy = MAX_ENERGY;
			}
		}

		if (energy == MAX_ENERGY) {
			if (random.nextDouble() < SEED_CHANCE) {
				energy -= SEED_COST;

				int randX = random.nextInt(2) + 1;
				int randY = random.nextInt(2) + 1;

				do {
					if (random.nextBoolean()) {
						randX = this.x + randX;
					} else {
						randX = this.x - randX;
					}

					if (random.nextBoolean()) {
						randY = this.y + randY;
					} else {
						randY = this.x - randY;
					}
				} while (!world.isPositionValid(randX, randY));

				world.getFieldAt(randX, randY).putSeed();
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
