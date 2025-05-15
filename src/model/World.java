package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World implements Serializable {

	private static final long serialVersionUID = 658023946262118355L;
	public static final int DEFAULT_WIDTH = 50;
	public static final int DEFAULT_HEIGHT = 50;

	private int width, height;
	private Organism[][] grid;
	private List<Organism> organisms;
	private List<Plant> plants;
	private List<Animal> animals;
	private List<Organism> newOrganisms;
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
		this.grid = new Organism[this.width][this.height];
		this.organisms = new ArrayList<Organism>();
		this.plants = new ArrayList<Plant>();
		this.animals = new ArrayList<Animal>();
	}

	public void populate(int numPlants, int numAnimals) {
		List<String> availableFields = new ArrayList<String>();

		for (int width = 0; width < this.width; width++) {
			for (int height = 0; height < this.height; height++) {
				availableFields.add(new String(width + "-" + height));
			}
		}

		for (int n = 0; n < numPlants; n++) {
			String coordinates = availableFields.get(random.nextInt(availableFields.size()));
			int width = Integer.parseInt(coordinates.split("-")[0]);
			int height = Integer.parseInt(coordinates.split("-")[1]);

			availableFields.remove(coordinates);

			Plant plant = new Plant(this, width, height);
			grid[width][height] = plant;
			organisms.add(plant);
			plants.add(plant);
		}

		for (int n = 0; n < numAnimals; n++) {
			String coordinates = availableFields.get(random.nextInt(availableFields.size()));
			int width = Integer.parseInt(coordinates.split("-")[0]);
			int height = Integer.parseInt(coordinates.split("-")[1]);

			availableFields.remove(coordinates);

			Animal animal = new Animal(this, width, height);
			grid[width][height] = animal;
			organisms.add(animal);
			animals.add(animal);
		}
	}

	public void updateSimulation() {
		List<Organism> deadOrganisms = new ArrayList<Organism>();

		for (Organism organism : organisms) {
			if (!organism.isAlive()) {
				if (organism.getClass() == Animal.class) {
					animals.remove(organism);
					System.out.println("An animal died, " + animals.size() + " remaining");
				} else {
					plants.remove(organism);
					System.out.println("A plant died, " + plants.size() + " remaining");
				}
				deadOrganisms.add(organism);
			}

			organism.update();
		}
		
		organisms.removeAll(deadOrganisms);

		for (Organism organism : deadOrganisms) {
			grid[organism.getX()][organism.getY()] = null;
		}

		// TODO: add new organisms
	}

	public Organism getOrganismOn(int width, int height) {
		return grid[width][height];
	}

	public boolean isPositionValid(int width, int height) {
		if (width <= this.width && width >= 0 && height <= this.height && height >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isThereFreeSpace(int width, int height) {
		if (grid[width][height] == null) {
			return true;
		} else {
			return false;
		}
	}

	public void addOrganism(Organism organism) {
		newOrganisms.add(organism);
		grid[organism.getX()][organism.getY()] = organism;
	}

	public void moveOrganismOnGrid(Organism organism, int previousWidth, int previousHeight, int newWidth,
			int newHeight) {
		grid[previousWidth][previousHeight] = null;
		grid[newWidth][newHeight] = organism;
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
}
