package model;

public class Plant extends Organism {
	
	private static final int INITIAL_ENERGY = 5;
	private static final int MAX_ENERGY = 50;
	private static final int GROWTH_RATE = 1;

	public Plant(World world, int x, int y) {
		super(world, x, y, INITIAL_ENERGY);
		this.energy = INITIAL_ENERGY;
	}
	
	@Override
	public void update() {
		if (this.energy < MAX_ENERGY) {
			this.energy += GROWTH_RATE;
		}
	}
	
	public int beEaten() {
		die();
		return this.energy;
	}
}
