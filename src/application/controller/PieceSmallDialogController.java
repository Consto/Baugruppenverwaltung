package application.controller;

import java.io.IOException;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import application.Main;
import application.Piece;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PieceSmallDialogController {
	 	@FXML
	 	private JFXTextField nameTF;
	 	@FXML
	 	private JFXTextField idTF;
	 	@FXML
	 	private JFXTextField typeTF;
	 	@FXML
	 	private JFXTextArea informationTA;
	 	private Stage dialogStage;
	    private Piece piece;
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
	    public void setPiece(Piece piece) {
	        this.piece= piece;
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
	            okClicked = true;
	            dialogStage.close();
	        }
	    }
	    @FXML
	    private void handleCancel() {
	        dialogStage.close();
	    }

	    private boolean isInputValid() {
	    	if(nameTF.getText().isEmpty()){
	    		Alert alert=new Alert(AlertType.ERROR);
	    		alert.setTitle("Fehler");
	    		alert.setHeaderText("Nicht alle Felder ausgefüllt");
	    		alert.setContentText("Feld \"Name\" darf nicht leer sein.");
	    		alert.showAndWait();
	    		return false;
	    	}
	    	return true;
	    }
		public boolean showEditDialog(Piece piece,Stage primaryStage) {
		    try {
		        // Load the fxml file and create a new stage for the popup dialog.
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(Main.class.getResource("controller/PieceSmallDialog.fxml"));
		        Parent page =  loader.load();

		        // Create the dialog Stage.
		        Stage dialogStage = new Stage();
		        dialogStage.setTitle("Bearbeiten - "+ piece.getName());
		        dialogStage.initModality(Modality.WINDOW_MODAL);
		        dialogStage.initOwner(primaryStage);
		        Scene scene = new Scene(page);
		        dialogStage.setScene(scene);

		        // Set the piece into the controller.
		        PieceSmallDialogController controller = loader.getController();
		        controller.setDialogStage(dialogStage);
		        controller.setPiece(piece);

		        // Show the dialog and wait until the user closes it
		        dialogStage.showAndWait();
		        
		       
		        return controller.isOkClicked();
		    } catch (IOException e) {
		        e.printStackTrace();
		        return false;
		    }
		}
		public void setPrimaryStage(Stage primaryStage) {
		}

}
