package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RecipeRowController {
	@FXML
	private Label nameL;
	@FXML
	private Label amountL;
	
	
	public void setName(String name) {
		nameL.setText(name);
	}
	public void setAmount(int amount) {
		amountL.setText(Integer.toString(amount));
	}

}
