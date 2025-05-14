package model;

import java.util.Random;

public class Animal extends Organism {

	private static final int INITIAL_ENERGY = 30;
	private static final int ENERGY_PER_MOVEMENT = 1;
	private static final int REPRODUCTION_ENERGY_BEGINNING = 50;
	private static final int REPRODUCTION_ENERGY_COST = 25;
	private Random random;

	public Animal(World world, int x, int y, int initialEnergy) {
		super(world, x, y, initialEnergy);
		this.random = new Random();
	}

	@Override
	public void update() {
		this.energy -= ENERGY_PER_MOVEMENT;

		if (this.energy <= 0) {
			this.die();
		}
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
		Object organism = this.world.getOrganismOn(this.x, this.y); // TODO: create method to retrieve organism
		if (organism.getClass() == Plant.class) {
			Plant plant = (Plant) organism;
			if (plant.isAlive()) {
				this.energy += plant.beEaten();
				// TODO: remove plant from square in World.class
			}
		}
	}

	public void reproduce() {
		if (this.energy >= REPRODUCTION_ENERGY_BEGINNING) {
			this.energy -= REPRODUCTION_ENERGY_COST;
			// TODO: create new animal on random side field
		}
	}
}
