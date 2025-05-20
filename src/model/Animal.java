package model;

import java.util.Random;

public class Animal extends Organism {

	/*
	 * TODO: IDEAS - Sex distinction (male and female) - Advanced attributes
	 * (attack, defense, aggressiveness, speed) - Smart encounters depending on
	 * attributes (reproduction, fight) - Family recognition - Team formation (think
	 * about more attributes)
	 * 
	 * Food search priority depending on energy level
	 */

	private static final int INITIAL_ENERGY = 200;
	private static final int ENERGY_PER_MOVEMENT = 1;
	private static final int REPRODUCTION_ENERGY_BEGINNING = 80;
	private static final int REPRODUCTION_ENERGY_COST = 40;
	private Random random;

	private int visionRange;
	protected Plant currentObjective;
	protected Position objectivePosition;

	public Animal(World world, int x, int y) {
		super(world, x, y, INITIAL_ENERGY);
		this.random = new Random();
		this.energy = INITIAL_ENERGY;
		this.visionRange = 4;
		this.currentObjective = null;
		this.objectivePosition = null;
	}

	@Override
	public synchronized void update() {
		if (currentObjective != null) {
			if (!currentObjective.isAlive()
					|| !world.isOrganismPresent(currentObjective, currentObjective.getX(), currentObjective.getY())) {
				currentObjective = null;
				objectivePosition = null;
			} else {
				if (this.x == currentObjective.getX() && this.y == currentObjective.getY()) {
					eat(currentObjective);
					currentObjective = null;
					objectivePosition = null;
				} else {
					moveTowards(currentObjective.getX(), currentObjective.getY());
					return;
				}
			}
		}

		if (currentObjective == null) {
			searchNewObjective();
		}

		if (currentObjective != null) {
			if (this.x == currentObjective.getX() && this.y == currentObjective.getY()) {
				eat(currentObjective);
				currentObjective = null;
				objectivePosition = null;
			} else {
				moveTowards(currentObjective.getX(), currentObjective.getY());
			}
		} else {
			moveRandomly();
		}

		this.energy -= ENERGY_PER_MOVEMENT;

		if (this.energy <= 0) {
			this.die();
			return;
		}
	}

	public void searchNewObjective() {
		System.out.println("Animal looking for a new objective");
		Plant nearestPlant = null;
		double minimumDistance = Double.MAX_VALUE;

		for (int i = -visionRange; i <= visionRange; i++) {
			for (int j = -visionRange; j <= visionRange; j++) {
				if (i == 0 && j == 0)
					continue;

				int seeX = this.x + i;
				int seeY = this.y + j;

				if (world.isPositionValid(seeX, seeY)) {
					Plant plantAtField = world.getPlantAt(seeX, seeY);

					if (plantAtField != null && plantAtField.isAlive()) {
						double distance = calculateDistance(this.x, this.y, seeX, seeY);
						if (distance < minimumDistance) {
							minimumDistance = distance;
							System.out.println("Animal found a new objective");
							nearestPlant = plantAtField;
							break;
						}
					}
				}
			}
			if (nearestPlant != null) {
				break;
			}
		}

		if (nearestPlant != null) {
			this.currentObjective = nearestPlant;
			this.objectivePosition = new Position(nearestPlant.getX(), nearestPlant.getY());
		} else {
			this.currentObjective = null;
			this.objectivePosition = null;
		}
	}

	private void moveTowards(int targetX, int targetY) {
		System.out.println("Animal moving towards " + targetX + "-" + targetY);
		int prevX = this.x;
		int prevY = this.y;
		int newX = this.x;
		int newY = this.y;

		if (targetX > this.x) {
			newX++;
		} else if (targetX < this.x) {
			newX--;
		}

		if (targetY > this.y) {
			newY++;
		} else if (targetY < this.y) {
			newY--;
		}

		if (newX == prevX && newY == prevY)
			return;

		if (world.isPositionValid(newX, newY) && world.getOrganismAt(newX, newY) == null) {
			this.x = newX;
			this.y = newY;
			world.moveOrganismOnGrid(this, prevX, prevY, newX, newY);
			spendEnergy(1);
		} else {
			int justX = prevX;

			if (targetX > prevX)
				justX++;
			else if (targetX < prevX)
				justX--;

			if (justX != prevX && world.isPositionValid(justX, prevY) && world.getOrganismAt(justX, prevY) == null) {
				this.x = justX;
				this.y = prevY;
				world.moveOrganismOnGrid(currentObjective, prevX, prevY, this.x, this.y);
				spendEnergy(1);
				return;
			}
			
			int justY = prevY;

			if (targetY > prevY)
				justY++;
			else if (targetY < prevY)
				justY--;

			if (justY != prevY && world.isPositionValid(prevX, justY) && world.getOrganismAt(prevX, justY) == null) {
				this.x = prevX;
				this.y = justY;
				world.moveOrganismOnGrid(currentObjective, prevX, prevY, this.x, this.y);
				spendEnergy(1);
				return;
			}
		}
	}

	private double calculateDistance(int x, int y, int destX, int destY) {
		return Math.sqrt(Math.pow(x - destX, 2) + Math.pow(y - destY, 2));
	}

	private synchronized void moveRandomly() {
		int moveX = 0;
		int moveY = 0;

		do {
			int[] newCoords = moveCoordinates(this.x, this.y, random, 3);
			moveX = newCoords[0];
			moveY = newCoords[1];
		} while (world.getOrganismAt(moveX, moveY) instanceof Animal);

		this.x = moveX;
		this.y = moveY;

	}

	public synchronized void eat(Plant plant) {
		if (plant.isAlive()) {
			int plantEnergy = plant.beEaten();
			this.energy += plantEnergy;
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
