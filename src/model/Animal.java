package model;

import java.util.Random;

public class Animal extends Organism {
	
	/* TODO: IDEAS
	 *  - Sex distinction (male and female)
	 *  - Advanced attributes (attack, defense, aggressiveness, speed)
	 *  - Smart encounters depending on attributes (reproduction, fight)
	 *  - Family recognition
	 *  - Team formation (think about more attributes)
	 */

	private static final int INITIAL_ENERGY = 30;
	private static final int ENERGY_PER_MOVEMENT = 1;
	private static final int REPRODUCTION_ENERGY_BEGINNING = 50;
	private static final int REPRODUCTION_ENERGY_COST = 25;
	private Random random;

	public Animal(World world, int x, int y) {
		super(world, x, y, INITIAL_ENERGY);
		this.random = new Random();
		this.energy = INITIAL_ENERGY;
	}

	@Override
	public void update() {
		this.energy -= ENERGY_PER_MOVEMENT;

		if (this.energy <= 0) {
			this.die();
			return;
		}

		move();
	}

	private void move() {
		int moveX = this.random.nextInt(1);
		int moveY = this.random.nextInt(1);

		switch (moveX) {
		case 0: {
			moveX = -1;
			break;
		}
		case 1: {
			moveX = 1;
			break;
		}
		default: {
			break;
		}
		}

		switch (moveY) {
		case 0: {
			moveY = -1;
			break;
		}
		case 1: {
			moveY = 1;
			break;
		}
		default: {
			break;
		}
		}

		if (this.x <= 0 && moveX == -1) {
			moveX = 1;
		} else if (this.x >= 99 && moveX == 1) {
			moveX = -1;
		}

		if (this.y <= 0 && moveY == -1) {
			moveY = 1;
		} else if (this.y >= 99 && moveY == 1) {
			moveY = -1;
		}

		this.x += moveX;
		this.y += moveY;
	}

	public void eat() {
		Object organism = this.world.getOrganismOn(this.x, this.y);
		if (organism.getClass() == Plant.class) {
			Plant plant = (Plant) organism;
			if (plant.isAlive()) {
				this.energy += plant.beEaten();
			}
		}
	}

	public void reproduce() { // TODO: improve so only reproduce when other animal is nearby
		if (this.energy >= REPRODUCTION_ENERGY_BEGINNING) {
			if (world.isPositionValid(this.x + 1, this.y + 1) && world.isThereFreeSpace(this.x + 1, this.y + 1)) {
				world.addOrganism(new Animal(world, this.x + 1, this.y + 1));
			} else if (world.isPositionValid(this.x + 1, this.y - 1)
					&& world.isThereFreeSpace(this.x + 1, this.y - 1)) {
				world.addOrganism(new Animal(world, this.x + 1, this.y - 1));
			} else if (world.isPositionValid(this.x - 1, this.y + 1)
					&& world.isThereFreeSpace(this.x - 1, this.y + 1)) {
				world.addOrganism(new Animal(world, this.x - 1, this.y + 1));
			} else if (world.isPositionValid(this.x - 1, this.y - 1)
					&& world.isThereFreeSpace(this.x - 1, this.y - 1)) {
				world.addOrganism(new Animal(world, this.x - 1, this.y - 1));
			} else {
				return;
			}

			this.energy -= REPRODUCTION_ENERGY_COST;
		}
	}
}
