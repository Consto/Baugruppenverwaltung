package application.controller;

import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import application.Piece;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class PieceGroupRowController {
	@FXML
	private JFXComboBox<String> nameCB;
	@FXML
	private JFXTextField amountTF;

	private AnchorPane node;
	private VBox parent;

	private List<Piece> pieces = new ArrayList<>();

	@FXML
	private void initialize() {
		amountTF.textProperty().addListener((o, oldValue, newValue) -> {
			if (!newValue.matches("^-?[0-9]\\d*(\\.\\d+)?$")) {
				amountTF.setText(newValue.replaceAll("^-?[0-9]\\d*(\\.\\d+)?$", ""));
			}
		});
	}

	@FXML
	public void handleDelete() {
		parent.getChildren().remove(node);
	}

	public void setNode(AnchorPane node) {
		this.node = node;
	}

	public void setParent(VBox parent) {
		this.parent = parent;
	}

	public void setPieces(ObservableList<Piece> warehouse, Piece editedPiece) {

		for (Piece p : warehouse) {
			if (p != editedPiece) {
				pieces.add(p);
				nameCB.getItems().add(p.getName());
			}
		}

	}

	public void setPiece(Piece piece) {
		nameCB.getSelectionModel().select(pieces.indexOf(piece));

	}

	public Piece getPiece() {
		int index = nameCB.getSelectionModel().getSelectedIndex();
		if (index > -1) {
			return pieces.get(index);
		}
		return null;
	}

	public void setAmount(int amount) {
		amountTF.setText(Integer.toString(amount));
	}

	public int getAmount() {
		try {
			return Integer.parseInt(amountTF.getText());
		} catch (NumberFormatException e) {
			return 0;
		}

	}

}
