package application.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import application.Data;
import application.Main;
import application.Piece;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.ScrollPane;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PieceGroupDialogController {
	@FXML
	private JFXTextField nameTF;
	@FXML
	private JFXTextField idTF;
	@FXML
	private JFXTextField typeTF;
	@FXML
	private JFXTextArea informationTA;
	@FXML
	private VBox recipesL;
	
	private PieceGroupDialogController controller;

	private Stage dialogStage;
	private Piece piece;
	private Data data;
	private List<PieceGroupRowController> rowControllers= new ArrayList<>();
	private boolean okClicked = false;

	@FXML
	private void initialize() {
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Sets the person to be edited in the dialog.
	 * 
	 * @param person
	 */
	private void setPiece(Piece piece) {
		this.piece = piece;
		this.nameTF.setText(piece.getName());
		this.idTF.setText(Integer.toString(piece.getId()));
		this.typeTF.setText(piece.getType().friendlyName());
		this.informationTA.setText(piece.getInformation());
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	private void handleSave() {

		if (isInputValid()) {
			piece.setName(nameTF.getText());
			piece.setInformation(informationTA.getText());
			piece.setRecipe(getRecipes());
			data.calculateNeeded();
			okClicked = true;
			dialogStage.close();
		}
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	private boolean isInputValid() {
		if (nameTF.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Fehler");
			alert.setHeaderText("Nicht alle Felder ausgefüllt");
			alert.setContentText("Feld \"Name\" darf nicht leer sein.");
			alert.showAndWait();
			return false;
		}
		return true;
	}

	public PieceGroupDialogController showEditDialog(Piece piece, Stage primaryStage, Data data) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("controller/PieceGroupDialog.fxml"));
			BorderPane page = loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Bearbeiten - " + piece.getName());
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the piece into the controller.
			controller = loader.getController();
			controller.setData(data);
			controller.setDialogStage(dialogStage);
			controller.setPiece(piece);

			try {

				AnchorPane ap = (AnchorPane) page.getChildren().get(0);
				VBox vb = (VBox) ap.getChildren().get(0);
				ScrollPane sp = (ScrollPane) vb.getChildren().get(4);
				AnchorPane ap2 = (AnchorPane) sp.getContent();
				VBox recepies = (VBox) ap2.getChildren().get(0);
				//add existing ingrediants 
				for (java.util.Map.Entry<Piece, Integer> e : piece.getRecipe().entrySet()) {
					FXMLLoader l = new FXMLLoader();
					l.setLocation(Main.class.getResource("controller/PieceGroupRow.fxml"));
					AnchorPane row = l.load();
					PieceGroupRowController c = l.getController();
					controller.rowControllers.add(c);
					c.setNode(row);
					c.setParent(recepies);
					c.setPieces(data.getWarehouse(),piece);
					c.setPiece(e.getKey());
					c.setAmount(e.getValue());
					recepies.getChildren().add(row);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			return controller;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@FXML
	public void addRecipeRow() {
		try {
			FXMLLoader l = new FXMLLoader();
			l.setLocation(Main.class.getResource("controller/PieceGroupRow.fxml"));
			AnchorPane row;
			row = l.load();
			PieceGroupRowController c = l.getController();
			rowControllers.add(c);
			c.setNode(row);
			c.setParent(recipesL);
			c.setPieces(data.getWarehouse(),piece);
			// row.setPrefWidth(recipesL.getPrefWidth());
			recipesL.getChildren().add(row);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPrimaryStage(Stage primaryStage) {
	}

	public void setData(Data data) {
		this.data = data;
	}

	private HashMap<Piece, Integer> getRecipes() {
		HashMap<Piece, Integer> result = new HashMap<>();
		for (PieceGroupRowController c : rowControllers) {
			if (c.getPiece() != null) {
				result.put(c.getPiece(), c.getAmount());
			}
		}
		return result;
	}

}
