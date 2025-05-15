package core;

import model.World;
import ui.BioSimApp;

public class Simulator {

	private World world;
	private boolean executing;
	private BioSimApp uiUpdater;
	
	public Simulator(BioSimApp uiUpdater) {
		this.uiUpdater = uiUpdater;
	}
	
	public void init(int worldWidht, int worldHeight, int plantNum, int animalNum) {
		world = new World(worldWidht, worldHeight);
		world.populate(plantNum, animalNum);
		
		executing = true;
	}
	
	public void step() {
		if (executing) {
			world.updateSimulation();
			uiUpdater.updateUI(); // TODO
		}
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public void pause() {
		this.executing = false;
	}
	
	public void resume() {
		this.executing = true;
	}
	
	public boolean isExecuting() {
		return this.executing;
	}
}
