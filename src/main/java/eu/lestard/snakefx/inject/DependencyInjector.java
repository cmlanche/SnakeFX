package eu.lestard.snakefx.inject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import eu.lestard.snakefx.config.Configurator;
import eu.lestard.snakefx.config.IntegerKey;
import eu.lestard.snakefx.core.FoodGenerator;
import eu.lestard.snakefx.core.GameLoop;
import eu.lestard.snakefx.core.Grid;
import eu.lestard.snakefx.core.Snake;
import eu.lestard.snakefx.core.SpeedLevel;
import eu.lestard.snakefx.util.Callback;
import eu.lestard.snakefx.view.ApplicationStarter;
import eu.lestard.snakefx.view.Keyboard;
import eu.lestard.snakefx.view.controller.HighScoreController;
import eu.lestard.snakefx.view.controller.MainController;
import eu.lestard.snakefx.view.controller.NewGameController;
import eu.lestard.snakefx.view.controller.PlayPauseController;
import eu.lestard.snakefx.view.controller.PointsController;
import eu.lestard.snakefx.view.controller.SpeedChangeController;

/**
 * The DependencyInjector is the Class that manages all instances of all classes and
 * the creation process.
 *
 * @author manuel.mauky
 *
 */
public class DependencyInjector {

	private static final String FXML_ID_SPEED_CHOICE_BOX = "#speedChoiceBox";
	private static final String FXML_ID_POINTS_LABEL = "#pointsLabel";
	private static final String FXML_ID_PLAY_PAUSE_BUTTON = "#playPauseButton";
	private static final String MAIN_FXML_FILENAME = "/fxml/main.fxml";
	private static final String HIGHSCORE_FXML_FILENAME = "/fxml/highscore.fxml";

	private ApplicationStarter starter;

	private Stage primaryStage;

	public DependencyInjector(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	/**
	 * Creates the object graph for the application with Dependency Injection.
	 */
	public void createObjectGraph() {
		Configurator configurator = createConfigurator();

		Grid grid = createGrid(configurator);

		Snake snake = createSnake(configurator, grid);

		GameLoop gameLoop = createGameLoop(snake);

		FoodGenerator foodGenerator = createFoodGenerator(grid, snake);

		PlayPauseController playPauseController = createPlayPauseController(gameLoop);

		NewGameController newGameController = createNewGameController(grid,
				snake, gameLoop, foodGenerator, playPauseController);

		Parent highScoreRoot = createHighScoreRoot();

		Stage highScoreStage = createHighScoreStage(highScoreRoot);

		MainController mainController = createMainController(grid,
				newGameController, highScoreStage);

		Parent root = createRoot(mainController);

		createSpeedChangeController(gameLoop, root);

		createPointsController(snake, root);

		initPlayPauseController(playPauseController, snake, root);

		Keyboard keyboard = createKeyboard(snake);
		Scene mainScene = createMainScene(root, keyboard);

		initMainController(mainController);

		createApplicationStarter(mainScene);
	}

	private Stage createHighScoreStage(Parent highScoreRoot) {
		Stage highScoreStage = new Stage();

		highScoreStage.initModality(Modality.WINDOW_MODAL);
		highScoreStage.initOwner(primaryStage);
		highScoreStage.setScene(new Scene(highScoreRoot));
		return highScoreStage;
	}

	private Parent createHighScoreRoot() {
		FxmlFactory fxmlFactory = createFxmlFactory();
		HighScoreController highScoreController = new HighScoreController();
		return fxmlFactory.getFxmlRoot(HIGHSCORE_FXML_FILENAME, highScoreController);
	}

	private void createApplicationStarter(Scene mainScene) {
		starter = new ApplicationStarter(mainScene, primaryStage);
	}

	private void initMainController(MainController mainController) {
		mainController.newGame();
	}

	private Scene createMainScene(Parent root, Keyboard keyboard) {
		Scene mainScene = new Scene(root);
		mainScene.setOnKeyPressed(keyboard);
		return mainScene;
	}

	private Keyboard createKeyboard(Snake snake) {
		return new Keyboard(snake);
	}

	private void initPlayPauseController(
			final PlayPauseController playPauseController, Snake snake,
			Parent root) {
		final Button playPauseButton = (Button) root.lookup(FXML_ID_PLAY_PAUSE_BUTTON);

		playPauseButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				playPauseController.togglePlayPause();
			}
		});

		playPauseController.buttonLabelProperty().bindBidirectional(
				playPauseButton.textProperty());
		playPauseController.disabledProperty().bindBidirectional(
				playPauseButton.disableProperty());

		snake.addCollisionEventListener(new Callback() {
			@Override
			public void call() {
				playPauseController.onCollision();
			}
		});
	}

	private void createPointsController(Snake snake, Parent root) {
		Label pointsLabel = (Label) root.lookup(FXML_ID_POINTS_LABEL);

		final PointsController pointsController = new PointsController(
				pointsLabel);
		snake.addPointsEventListener(new Callback() {
			@Override
			public void call() {
				pointsController.addPoint();
			}
		});
	}

	private void createSpeedChangeController(GameLoop gameLoop, Parent root) {
		@SuppressWarnings("unchecked")
		ChoiceBox<SpeedLevel> speedChoiceBox = (ChoiceBox<SpeedLevel>) root
				.lookup(FXML_ID_SPEED_CHOICE_BOX);
		SpeedChangeController speedChangeController = new SpeedChangeController(
				gameLoop, speedChoiceBox);

		speedChangeController.init();
	}

	private Parent createRoot(MainController mainController) {
		FxmlFactory fxmlFactory = createFxmlFactory();
		return fxmlFactory.getFxmlRoot(MAIN_FXML_FILENAME,
				mainController);
	}

	private FxmlFactory createFxmlFactory() {
		FXMLLoader fxmlLoader = new FXMLLoader();
		return new FxmlFactory(fxmlLoader);
	}

	private MainController createMainController(Grid grid,
			NewGameController newGameController, Stage highScoreStage) {
		return new MainController(grid, newGameController, highScoreStage);
	}

	private NewGameController createNewGameController(Grid grid, Snake snake,
			GameLoop gameLoop, FoodGenerator foodGenerator,
			final PlayPauseController playPauseController) {
		return new NewGameController(grid,
				snake, foodGenerator, gameLoop, playPauseController);
	}

	private PlayPauseController createPlayPauseController(GameLoop gameLoop) {
		return new PlayPauseController(gameLoop);
	}

	private FoodGenerator createFoodGenerator(Grid grid, Snake snake) {
		return new FoodGenerator(grid, snake);
	}

	private GameLoop createGameLoop(Snake snake) {
		return new GameLoop(snake);
	}

	private Configurator createConfigurator() {
		return new Configurator();
	}

	private Snake createSnake(Configurator configurator, Grid grid) {
		int snakeStartX = configurator.getValue(IntegerKey.SNAKE_START_X);
		int snakeStartY = configurator.getValue(IntegerKey.SNAKE_START_Y);
		return new Snake(grid, snakeStartX, snakeStartY);
	}

	private Grid createGrid(Configurator configurator) {
		int gridSizeInPixel = configurator
				.getValue(IntegerKey.GRID_SIZE_IN_PIXEL);
		int rowAndColumnCount = configurator
				.getValue(IntegerKey.ROW_AND_COLUMN_COUNT);

		return new Grid(rowAndColumnCount, gridSizeInPixel);
	}

	public ApplicationStarter getApplicationStarter() {
		return starter;
	}

}