package ui;

import core.Simulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Animal;
import model.Organism;
import model.Plant;
import model.World;

public class BioSimApp extends Application {

	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEHGHT = 800;
	private static final int CELL_SIZE = 4;

	private Simulator simulator;
	private Canvas canvas;
	private GraphicsContext gc;
	private AnimationTimer gameLoop;

	private long lastUpdate = 0;

	private static final int STEPS_PER_SECOND = 32;
	private static final long UPDATE_NANO_GAP = 1_000_000_000 / STEPS_PER_SECOND;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("BioSim Java - MVP");

		simulator = new Simulator(this);

		BorderPane root = new BorderPane();
		canvas = new Canvas(World.DEFAULT_WIDTH * CELL_SIZE, World.DEFAULT_HEIGHT * CELL_SIZE);
		gc = canvas.getGraphicsContext2D();
		root.setCenter(canvas);

		Button resumePauseButton = new Button("Start");
		resumePauseButton.setOnAction(e -> {
			if (simulator.isExecuting()) {
				simulator.pause();
				gameLoop.stop();
				resumePauseButton.setText("Resume");
			} else {
				if (simulator.getWorld() == null) {
					simulator.init(World.DEFAULT_WIDTH, World.DEFAULT_HEIGHT, 500, 25);
				} else {
					simulator.resume();
				}

				gameLoop.start();
				resumePauseButton.setText("Pause");
			}
		});

		Button resetButton = new Button("Reset");
		resetButton.setOnAction(e -> {
			if (gameLoop != null) {
				gameLoop.stop();
			}

			simulator.pause();
			simulator.init(World.DEFAULT_WIDTH, World.DEFAULT_HEIGHT, 500, 25);
			if (!simulator.isExecuting() && simulator.getWorld() != null) {
				simulator.resume();
				gameLoop.start();
				resumePauseButton.setText("Pause");
			} else if (simulator.isExecuting()) {
				gameLoop.start();
				resumePauseButton.setText("Pause");
			}

			updateUI();
		});

		HBox boxControls = new HBox(10, resumePauseButton, resetButton);
		root.setBottom(boxControls);

		new Thread() {
			public void run() {
				gameLoop = new AnimationTimer() {
					@Override
					public void handle(long now) {
						if (now - lastUpdate >= UPDATE_NANO_GAP) {
							if (simulator.isExecuting()) {
								simulator.step();
								updateUI();
							}
							lastUpdate = now;
						}
					}
				};
			}
		}.start();
		

		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEHGHT);
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(event -> {
			if (gameLoop != null) {
				gameLoop.stop();
			}
		});

		primaryStage.show();
		updateUI();
	}

	public synchronized void updateUI() { // TODO: Check crash here 2
		if (simulator == null || simulator.getWorld() == null) {
			gc.setFill(Color.LIGHTGRAY);
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			gc.setFill(Color.BLACK);
			gc.fillText("World not initialized. Press Start.", 20, 30);
			return;
		}

		World world = simulator.getWorld();

		gc.setFill(Color.BEIGE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		if (world.getOrganisms() == null) {
			return;
		}

		for (Organism org : world.getOrganisms()) {
			if (org == null || !org.isAlive()) {
				continue;
			}

			if (org instanceof Plant) {
				Plant plant = (Plant) org;
				if (plant.grownFromSeed()) {
					gc.setFill(Color.DARKGREEN);
				} else {
					gc.setFill(Color.GREEN);
				}
			} else if (org instanceof Animal) {
				gc.setFill(Color.BLUE);
			} else {
				gc.setFill(Color.GRAY);
			}

			gc.fillRect(org.getX() * CELL_SIZE, org.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
		}
	}
}
