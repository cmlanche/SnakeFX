package eu.lestard.snakefx.view.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import eu.lestard.snakefx.util.Function;
import eu.lestard.snakefx.view.FXMLFile;
import eu.lestard.snakefx.viewmodel.ViewModel;

/**
 * UI-Controller for the fxml file {@link FXMLFile#MENU}. This controller
 * handles the menu actions.
 * 
 * @author manuel.mauky
 * 
 */
public class MenuController {

	private final ViewModel viewModel;
	private final Function newGameFunction;

	public MenuController(ViewModel viewModel, Function newGameFunction) {
		this.viewModel = viewModel;
		this.newGameFunction = newGameFunction;
	}

	@FXML
	public void newGame() {
		System.out.println("new game");
		newGameFunction.call();
	}

	@FXML
	public void showHighscores() {
		System.out.println("highscore");
		viewModel.highscoreWindowOpenProperty().set(true);
	}

	@FXML
	public void about() {
		System.out.println("about");
		viewModel.aboutWindowOpenProperty().set(true);
	}

	@FXML
	public void exit() {
		System.out.println("Exit");
		Platform.exit();
	}

}