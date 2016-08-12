package application.controller;

import java.io.File;
import application.Data;
import application.Main;
import application.Piece;
import application.PieceType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ToolbarController {
	@FXML
	private javafx.scene.control.MenuItem createSimpleMI;
	@FXML
	private javafx.scene.control.MenuItem createGroupMI;
	private Stage primaryStage;
	private PieceSmallDialogController pieceSmallDialogController;
	private PieceGroupDialogController pieceGroupDialogController;
	private Data data;
	private Main main;

	@FXML
	private void initialize() {
		pieceSmallDialogController = new PieceSmallDialogController();
		pieceGroupDialogController = new PieceGroupDialogController();

		createSimpleMI.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Piece piece = new Piece(data.getNextWarehouseID(), "", PieceType.SINGLE);
				if (pieceSmallDialogController.showEditDialog(piece, primaryStage)) {
					data.getWarehouse().add(piece);
				}

			}
		});
		createGroupMI.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Piece piece = new Piece(data.getNextWarehouseID(), "", PieceType.GROUP);
				PieceGroupDialogController dialog = pieceGroupDialogController.showEditDialog(piece, primaryStage,
						data);
				if (dialog != null) {
					// piece.setRecipe(dialog.getRecipes()); moved to dialog
					data.getWarehouse().add(piece);
				}

			}
		});
	}

	@FXML
	public void handleSave() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("BGM", "*.bgm"));
		fileChooser.setTitle("Save");
		fileChooser.setInitialDirectory(new File("./"));
		File file = fileChooser.showSaveDialog(primaryStage);
		if (file != null) {
			data.saveToFile(file);
		}

	}

	@FXML
	public void handleLoad() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("BGM", "*.bgm"));
		fileChooser.setTitle("Laden");
		fileChooser.setInitialDirectory(new File("./"));
		File file = fileChooser.showOpenDialog(primaryStage);
		if (file != null) {
			main.startFromFile(file);
		}
	}
	@FXML
	public void handleExport() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel", "*.xls"));
		fileChooser.setTitle("Export Excel File");
		fileChooser.setInitialDirectory(new File("./"));
		File file = fileChooser.showSaveDialog(primaryStage);
		if (file != null) {
			data.exportExcel(file);
		}
	}

	public void setup(Data data, Stage primaryStage, Main main) {
		this.data = data;
		this.primaryStage = primaryStage;
		this.main = main;
	}

}
