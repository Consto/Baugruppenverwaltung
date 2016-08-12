package application.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import application.Data;
import application.Piece;
import application.PieceType;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

public class WarehouseOverviewController {
	@FXML
	private TableView<Piece> warehouseTable;
	@FXML
	private TableColumn<Piece, String> nameCol;
	@FXML
	private TableColumn<Piece, String> amountCol;
	@FXML
	private TableColumn<Piece, String> nAmountCol;
	@FXML
	private TableColumn<Piece, String> difCol;
	@FXML
	private TableView<Pair<String, Integer>> ingredientsTable;
	@FXML
	private TableColumn<Pair<String, Integer>, String> ingedientCol;
	@FXML
	private TableColumn<Pair<String, Integer>, String> ingAmountCol;
	@FXML
	private Label nameLabel, nameLabelG;
	@FXML
	private Label amountLabel, amountLabelG;
	@FXML
	private Label nAmountLabel;
	@FXML
	private Label nAmountLabelG;
	@FXML
	private Label pAmountLabelG;
	@FXML
	private Label idLabel, idLabelG;
	@FXML
	private Label typeLabel, typeLabelG;
	@FXML
	private JFXTextArea infoTA;
	@FXML
	private JFXTextArea infoTAG;
	@FXML
	private TabPane detailsTP;
	@FXML
	private JFXTextField amountTF;
	@FXML
	private JFXTextField addAmountTF;
	@FXML
	private JFXTextField planTF;
	@FXML
	private JFXTextField buildTF;
	@FXML
	private JFXTextField checkOutTF;
	@FXML
	private JFXButton updateBtn;

	private Data data;
	private Piece showcasePiece = null;
	private Stage primaryStage;

	@FXML
	private void initialize() {

		nAmountCol.setCellValueFactory(cellData -> cellData.getValue().nAmountProperty());
		difCol.setCellValueFactory(cellData -> cellData.getValue().differenceProperty());
		difCol.setCellFactory(column -> {
			return new TableCell<Piece, String>() {
				@Override
				protected void updateItem(String cellValue, boolean empty) {
					super.updateItem(cellValue, empty);
					if (cellValue == null || empty) {
						setText(null);
					} else {
						setText(cellValue);
						if (Integer.parseInt(cellValue) < 0) {
							setTextFill(Color.RED);
						} else {
							setTextFill(Color.BLACK);
						}
					}
				}
			};
		});
		nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		amountCol.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
		ingedientCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
		ingAmountCol.setCellValueFactory(
				cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getValue())));
		showPiece(null);
		warehouseTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			showPiece(newValue);
			amountTF.textProperty().bind(showcasePiece.amountProperty());
		});
		addAmountTF.setPromptText("0");
		addAmountTF.focusedProperty().addListener((o, oldValue, newValue) -> {
			if (!newValue && showcasePiece != null && !addAmountTF.textProperty().get().isEmpty()) {
				showcasePiece.setAmount(Integer.parseInt(addAmountTF.textProperty().get())
						+ Integer.parseInt(amountTF.textProperty().get()));
				addAmountTF.textProperty().set("");
			}
		});
		amountTF.focusedProperty().addListener((o, oldValue, newValue) -> {
			if (newValue) {
				amountTF.textProperty().unbind();
			} else {
				if (showcasePiece != null) {
					showcasePiece.setAmount(Integer.parseInt(amountTF.textProperty().get()));
					amountTF.textProperty().bind(showcasePiece.amountProperty());
				}
			}
		});
		planTF.focusedProperty().addListener((o, oldValue, newValue) -> {
			if (!newValue && !planTF.textProperty().get().isEmpty()) {
				data.addPlan(showcasePiece, Integer.parseInt(planTF.textProperty().get()));
				data.calculateNeeded();
				planTF.textProperty().set("");
			}
		});
		buildTF.focusedProperty().addListener((o, oldValue, newValue) -> {
			if (!newValue && !buildTF.textProperty().get().isEmpty()) {
				data.buildPlan(showcasePiece, Integer.parseInt(buildTF.textProperty().get()));
				data.calculateNeeded();
				buildTF.textProperty().set("");
			}
		});
		checkOutTF.focusedProperty().addListener((o, oldValue, newValue) -> {
			if (!newValue && !checkOutTF.textProperty().get().isEmpty()) {
				if (showcasePiece != null) {
				showcasePiece.addAmount(-Integer.parseInt(checkOutTF.textProperty().get()));
				data.calculateNeeded();
				checkOutTF.textProperty().set("");}
			}
		});
		addAmountTF.textProperty().addListener((o, oldValue, newValue) -> {
			if (!newValue.matches("-?\\d")) {
				addAmountTF.setText(newValue.replaceAll("[^-?\\d]", ""));
			}
		});
		amountTF.textProperty().addListener((o, oldValue, newValue) -> {
			if (!newValue.matches("-?\\d")) {
				amountTF.textProperty().unbind();
				amountTF.setText(newValue.replaceAll("[^-?\\d]", ""));
			}
		});
		planTF.textProperty().addListener((o, oldValue, newValue) -> {
			if (!newValue.matches("-?\\d")) {
				planTF.setText(newValue.replaceAll("[^-?\\d]", ""));
			}
		});
		buildTF.textProperty().addListener((o, oldValue, newValue) -> {
			if (!newValue.matches("-?\\d")) {
				buildTF.setText(newValue.replaceAll("[^-?\\d]", ""));
			}
		});
		checkOutTF.textProperty().addListener((o, oldValue, newValue) -> {
			if (!newValue.matches("-?\\d")) {
				checkOutTF.setText(newValue.replaceAll("[^-?\\d]", ""));
			}
		});

	}

	public void setup(Data data, Stage primaryStage) {
		this.data = data;
		this.primaryStage = primaryStage;
		warehouseTable.setItems(data.getWarehouse());
	}

	private void showPiece(Piece piece) {
		if (piece != null) {
			showcasePiece = piece;
			if (piece.getType() == PieceType.SINGLE) {
				detailsTP.getSelectionModel().select(0);
				nameLabel.setText(piece.getName());
				amountLabel.textProperty().bind(piece.amountProperty());
				nAmountLabel.setText(Integer.toString(piece.getNAmount()));
				idLabel.setText(Integer.toString(piece.getId()));
				typeLabel.setText(piece.getType().friendlyName());
				infoTA.setText(piece.getInformation());
			} else if (piece.getType() == PieceType.GROUP) {
				ingredientsTable.setItems(piece.getRecipeAsList());
				detailsTP.getSelectionModel().select(1);
				nameLabelG.setText(piece.getName());
				amountLabelG.setText(Integer.toString(piece.getAmount()));
				nAmountLabelG.textProperty().bind(piece.nAmountProperty());
				pAmountLabelG.textProperty().bind(data.getPlan(piece));
				idLabelG.setText(Integer.toString(piece.getId()));
				typeLabelG.setText(piece.getType().friendlyName());
				infoTAG.setText(piece.getInformation());
			}
		} else {
			nameLabel.setText("-");
			amountLabel.setText("0");
			nAmountLabel.setText("-");
			idLabel.setText("-");
			typeLabel.setText("-");
		}
	}

	@FXML
	public void editHandle() {
		if (showcasePiece != null) {
			if (showcasePiece.getType() == PieceType.SINGLE) {
				// all attributes are properties, hence everything can be done
				// in the controller
				(new PieceSmallDialogController()).showEditDialog(showcasePiece, primaryStage);

			} else if (showcasePiece.getType() == PieceType.GROUP) {
				PieceGroupDialogController dialog = (new PieceGroupDialogController()).showEditDialog(showcasePiece,
						primaryStage, data);
				if (dialog != null) {
					// showcasePiece.setRecipe(dialog.getRecipes());
					showPiece(showcasePiece);
				}
			}
		}

	}

	@FXML
	public void deleteHandle() {
		if (showcasePiece != null) {
			data.getPlans().remove(showcasePiece);
			data.getWarehouse().remove(showcasePiece);
		}

	}

}
