package model;

import java.util.List;
import java.util.Random;

public class Field {

	/*
	 * TODO: IDEAS - Smell trace so hunting animals can trace other animals back -
	 * Seed spreading - Pollution / toxins
	 * 
	 * NEED to handle humidity to lower the seed growing rate
	 */

	private final int x;
	private final int y;

	private Plant currentPlant;
//	private List<Animal> presentAnimals;
	private Animal currentAnimal;
//	private List<Organism> presentOrganisms;
	private World world;

	private final double BASE_FERTILITY;
	private TerrainType terrainType; // TODO: use

	private double humidityLevel;
	private double nutrientLevel;
	private double currentTemperature;

	private boolean hasSeed;

	private final double PLANT_FERTILITY_BEGINNIGN;
	private final double PLANT_HUMIDITY_BEGINNIGN;
	private final double NUTRIENT_REGENERATION_RATE;
	private final double PLANT_NUTRIENT_CONSUMPTION_FACTOR;
	private final double MAX_NUTRIENT;
//	private final double TERRAIN_HEIGHT;

	private Random random;

	public Field(World world, int x, int y, TerrainType terrainType, Random random) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.terrainType = terrainType;
		this.random = random;
		this.hasSeed = false;

		this.BASE_FERTILITY = random.nextDouble();
		this.PLANT_FERTILITY_BEGINNIGN = random.nextDouble();
		this.PLANT_HUMIDITY_BEGINNIGN = random.nextDouble();
		this.NUTRIENT_REGENERATION_RATE = random.nextDouble();
		this.PLANT_NUTRIENT_CONSUMPTION_FACTOR = random.nextDouble();
		this.MAX_NUTRIENT = random.nextInt(100) + 100;
	}

	public synchronized boolean addOrganism(Organism organism) {
		if (organism instanceof Plant) {
			this.currentPlant = (Plant) organism;
			return true;
		} else if (organism instanceof Animal) {
			this.currentAnimal = (Animal) organism;
			return true;
		} else {
			return false;
		}
	}

	public synchronized boolean removeOrganism(Organism organism) {
		if (organism instanceof Plant) {
			this.currentPlant = null;
			return true;
		} else if (organism instanceof Animal) {
			this.currentAnimal = null;
			return true;
		} else {
			return false;
		}
	}

	public synchronized void update() {
		regenerateNutrients();
		// TODO: humidity change depending on weather > changeHumidity();

		if (getPlant() == null) {
			double probability = calculatePlantGenerationProbability();

			if (random.nextDouble() < probability) {
				Plant newPlant = new Plant(this.world, this.x, this.y, true);
				this.addOrganism(newPlant);
				world.addPlant(newPlant);
				this.currentPlant = newPlant;
				this.hasSeed = false;
			}
		}
	}

	public double calculatePlantGenerationProbability() {
		if (hasSeed) {
			return BASE_FERTILITY * 0.01 /* humidityLevel */ * nutrientLevel;
		} else {
			return 0.0;
		}
	}

	public Plant getPlant() {
		return this.currentPlant;
	}

	public Animal getAnimal() {
		return this.currentAnimal;
	}

	public Organism getOrganism() {
		if (this.currentAnimal == null) {
			return this.currentAnimal;
		} else {
			return this.currentPlant;
		}
	}

	public List<Animal> getAnimalList() {
		return null; // TODO
	}

	public boolean isEmpty() {
		return this.currentAnimal == null && this.currentPlant == null;
	}

	public double getFertility() {
		return this.BASE_FERTILITY;
	}

	public double getHumidity() {
		return this.humidityLevel;
	}

	public void setHumidity(double humidityLevel) {
		this.humidityLevel = humidityLevel;
	}

	public double getNutrients() {
		return this.nutrientLevel;
	}

	public void consumeNutrients(double amount) {
		this.nutrientLevel -= amount;
	}

	public void regenerateNutrients() {
		if (this.nutrientLevel < MAX_NUTRIENT) {
			this.nutrientLevel += NUTRIENT_REGENERATION_RATE;
		} else {
			this.nutrientLevel = MAX_NUTRIENT;
		}
	}

	public void putSeed() {
		if (!hasSeed) {
			this.hasSeed = true;
		}
	}

	public TerrainType getTerrainType() {
		return this.terrainType; // TODO
	}

	public double getTemperature() {
		return this.currentTemperature; // TODO
	}

	public void setTemperature(double temperature) {
		this.currentTemperature = temperature; // TODO
	}

	public String getCoordinates() {
		return new String(this.x + "-" + this.y);
	}
}
