package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World implements Serializable {

	/*
	 * TODO: IDEAS - Weather (rain, snow, dry stations, temperatures) - Stations
	 * (advanced weather sessions) - Plant growing (randomly at first, then use
	 * weather and stations) - Natural disasters (long term idea)
	 * 
	 * NEED to implement weather events ASAP
	 */

	private static final long serialVersionUID = 658023946262118355L;
	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 100;

	private int width, height;
	private Field[][] grid;
	private List<Organism> organisms;
	private List<Plant> plants;
	private List<Animal> animals, newAnimals;
	private Random random;

	public World(int width, int height) {
		if (width <= 0 || height <= 0) {
			this.width = DEFAULT_WIDTH;
			this.height = DEFAULT_HEIGHT;
		} else {
			this.width = width;
			this.height = height;
		}
		this.random = new Random();
		this.grid = new Field[this.width][this.height];
		this.organisms = new ArrayList<Organism>();
		this.plants = new ArrayList<Plant>();
		this.animals = new ArrayList<Animal>();
		this.newAnimals = new ArrayList<Animal>();
	}

	public void populate(int numPlants, int numAnimals) {
		List<Field> availableFields = new ArrayList<Field>();

		for (int width = 0; width < this.width; width++) {
			for (int height = 0; height < this.height; height++) {
				Field field = new Field(this, width, height, null, random);
				availableFields.add(field);
				grid[width][height] = field;
			}
		}

		for (int n = 0; n < numPlants; n++) {
			Field field = availableFields.get(random.nextInt(availableFields.size()));
			String coordinates = field.getCoordinates();			int width = Integer.parseInt(coordinates.split("-")[0]);
			int height = Integer.parseInt(coordinates.split("-")[1]);

			availableFields.remove(field);

			Plant plant = new Plant(this, width, height, false);
			grid[width][height].addOrganism(plant);
			organisms.add(plant);
			plants.add(plant);
		}

		for (int n = 0; n < numAnimals; n++) {
			Field field = availableFields.get(random.nextInt(availableFields.size()));
			String coordinates = field.getCoordinates();
			int width = Integer.parseInt(coordinates.split("-")[0]);
			int height = Integer.parseInt(coordinates.split("-")[1]);

			availableFields.remove(field);

			Animal animal = new Animal(this, width, height);
			grid[width][height].addOrganism(animal);
			organisms.add(animal);
			animals.add(animal);
		}
	}

	public synchronized void updateSimulation() { 	
		List<Organism> deadOrganisms = new ArrayList<Organism>();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				this.grid[x][y].update();
			}
		}

		for (Organism organism : organisms) {
			if (!organism.isAlive()) {
				if (organism instanceof Animal) {
					animals.remove(organism);
				} else {
					plants.remove(organism);
				}
				deadOrganisms.add(organism);
			}

			organism.update();
		}

		for (Organism organism : deadOrganisms) {
			grid[organism.getX()][organism.getY()].removeOrganism(organism);
			organisms.remove(organism);
		}

		animals.addAll(newAnimals);
		organisms.addAll(newAnimals); 
		newAnimals.clear();
	}

	public synchronized Organism getOrganismAt(int width, int height) {
		try {
			return grid[width][height].getOrganism();
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.err.println("X: " + width + ", Y: " + height);
			ex.printStackTrace();
			System.exit(0);
			return null;
		}
	}
	
	public synchronized Animal getAnimalAt(int x, int y) {
		return grid[x][y].getAnimal();
	}
	
	public synchronized Plant getPlantAt(int x, int y) {
		return grid[x][y].getPlant();
	}

	public boolean isPositionValid(int width, int height) {
		if (width < this.width && width >= 0 && height < this.height && height >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isThereFreeSpace(int width, int height) {
		return grid[width][height].isEmpty();
	}

	public synchronized void addPlant(Plant plant) {
		grid[plant.getX()][plant.getY()].addOrganism(plant);
		organisms.add(plant);
	}
	
	public synchronized void addAnimal(Animal animal) {
		grid[animal.getX()][animal.getY()].addOrganism(animal);
		newAnimals.add(animal);
	}

	public synchronized void moveOrganismOnGrid(Organism organism, int previousWidth, int previousHeight, int newWidth,
			int newHeight) {
		grid[previousWidth][previousHeight].removeOrganism(organism);
		grid[newWidth][newHeight].addOrganism(organism);
	}
	
	public boolean isOrganismPresent(Organism organism, int x, int y) {
		if (organism instanceof Plant) {
			return getPlantAt(x, y) == organism;
		} else {
			return getOrganismAt(x, y) == organism;
		}
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public List<Organism> getOrganisms() {
		return this.organisms;
	}

	public Field getFieldAt(int x, int y) {
		return grid[x][y];
	}
}
