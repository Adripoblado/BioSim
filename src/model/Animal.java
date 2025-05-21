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

	private static final int INITIAL_ENERGY = 40;
	private static final int ENERGY_PER_MOVEMENT = 1;
	private static final int REPRODUCTION_ENERGY_BEGINNING = 500;
	private static final int REPRODUCTION_ENERGY_COST = 350;
	private static final double REPRODUCTION_CHANCE = 0.075;
	private Random random;

	private int visionRange;
	protected Plant currentObjective;
	protected Position objectivePosition;
	
	private int oldx, oldy;

	public Animal(World world, int x, int y) {
		super(world, x, y, INITIAL_ENERGY);
		this.random = new Random();
		this.energy = INITIAL_ENERGY;
		this.visionRange = 5;
		this.currentObjective = null;
		this.objectivePosition = null;
		this.oldx = x;
		this.oldy = y;
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

		if (currentObjective == null && energy < REPRODUCTION_ENERGY_BEGINNING) {
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
		} else if (this.energy >= REPRODUCTION_ENERGY_BEGINNING) {
			reproduce();
		}
	}

	public void searchNewObjective() {
		Plant nearestPlant = null;
		Plant betterPlant = null;
		double minimumDistance = Double.MAX_VALUE;

		for (int i = -visionRange; i <= visionRange; i++) {
			for (int j = -visionRange; j <= visionRange; j++) {
				if (i == 0 && j == 0)
					continue;

				int seeX = this.x + i;
				int seeY = this.y + j;

				if (world.isPositionValid(seeX, seeY)) {
					Plant plantAtField = world.getPlantAt(seeX, seeY);
					if (plantAtField != null && plantAtField.isAlive() && !plantAtField.isMarked()) {
						double distance = calculateDistance(this.x, this.y, seeX, seeY);
						if (distance < minimumDistance) {
							minimumDistance = distance;
							nearestPlant = plantAtField;

							if (betterPlant == null || nearestPlant.getEnergy() > betterPlant.getEnergy()) {
								betterPlant = nearestPlant;
							}
						}
					}
				}
			}
		}

		if (betterPlant != null) {
			betterPlant.mark();
			this.currentObjective = betterPlant;
			this.objectivePosition = new Position(betterPlant.getX(), betterPlant.getY());
		} else {
			this.currentObjective = null;
			
			if (this.objectivePosition == null || (this.objectivePosition.x == x && this.objectivePosition.y == y)) {
				moveRandomly();
			}
		}
	}

	private void moveTowards(int targetX, int targetY) {
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

		if (world.isPositionValid(newX, newY) && world.getAnimalAt(newX, newY) == null) {
			this.x = newX;
			this.y = newY;
			world.moveOrganismOnGrid(this, prevX, prevY, newX, newY);
			
			if (x == oldx && y == oldy) {
				System.out.println("1 ANIMAL STUCK");
			}
			
			spendEnergy(1);
		} else {
			int justX = prevX;

			if (targetX > prevX)
				justX++;
			else if (targetX < prevX)
				justX--;

			if (justX != prevX && world.isPositionValid(justX, prevY) && world.getAnimalAt(justX, prevY) == null) {
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

			if (justY != prevY && world.isPositionValid(prevX, justY) && world.getAnimalAt(prevX, justY) == null) {
				this.x = prevX;
				this.y = justY;
				world.moveOrganismOnGrid(currentObjective, prevX, prevY, this.x, this.y);
				spendEnergy(1);
				return; 
			}
		}
		
		if (x == objectivePosition.x && y == objectivePosition.y) {
			objectivePosition = null;
			moveRandomly();
		}
		
		oldx = x;
		oldy = y;
	}

	private double calculateDistance(int x, int y, int destX, int destY) {
		return Math.sqrt(Math.pow(x - destX, 2) + Math.pow(y - destY, 2));
	}

	private synchronized void moveRandomly() {
		int moveX, moveY;
		
		if (objectivePosition == null) {
			moveX = random.nextInt(world.getWidth());
			moveY = random.nextInt(world.getHeight());
			
			objectivePosition = new Position(moveX, moveY);
		} else {
			moveX = objectivePosition.x;
			moveY = objectivePosition.y;
		}
		
		moveTowards(moveX, moveY);
	}

	public synchronized void eat(Plant plant) {
		if (plant.isAlive()) {
			int plantEnergy = plant.beEaten();
			this.energy += plantEnergy;
		}
	}

	public void reproduce() { // TODO: improve so only reproduce when other animal is nearby
		if (random.nextDouble() > REPRODUCTION_CHANCE) {
			return;
		}

		if (world.isPositionValid(this.x + 1, this.y + 1) && world.isThereFreeSpace(this.x + 1, this.y + 1)) {
			world.addAnimal(new Animal(world, this.x + 1, this.y + 1));
		} else if (world.isPositionValid(this.x + 1, this.y - 1) && world.isThereFreeSpace(this.x + 1, this.y - 1)) {
			world.addAnimal(new Animal(world, this.x + 1, this.y - 1));
		} else if (world.isPositionValid(this.x - 1, this.y + 1) && world.isThereFreeSpace(this.x - 1, this.y + 1)) {
			world.addAnimal(new Animal(world, this.x - 1, this.y + 1));
		} else if (world.isPositionValid(this.x - 1, this.y - 1) && world.isThereFreeSpace(this.x - 1, this.y - 1)) {
			world.addAnimal(new Animal(world, this.x - 1, this.y - 1));
		} else {
			return;
		}

		this.energy -= REPRODUCTION_ENERGY_COST;
	}
}
