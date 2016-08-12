package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import application.controller.ToolbarController;
import application.controller.WarehouseOverviewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	private Data data;
	private BorderPane root;
	private WarehouseOverviewController tableController;
	private ToolbarController toolbarController;
	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setOnCloseRequest(event -> {
			this.data.saveToFile(new File("lastSession.bgm"));
		});
		try {
			root = FXMLLoader.load(getClass().getResource("controller/Root.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Baugruppenverwaltung");
		} catch (Exception e) {
			e.printStackTrace();
		}
		startFromFile(new File("lastSession.bgm"));
		primaryStage.show();
	}

	public void startFromFile(File file) {
		try {
			if (file.exists()) {
				FileInputStream fin = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fin);
				Data lData = (Data) ois.readObject();
				ois.close();
				this.data = lData;
				data.calculateNeeded();
			} else {
				data = new Data();
			}

			root.getChildren().clear();
			showWarehouse();
			showToolbar();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void showWarehouse() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("controller/Main.fxml"));
		try {
			root.setCenter(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
		tableController = loader.getController();
		tableController.setup(data, primaryStage);
	}

	private void showToolbar() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("controller/Toolbar.fxml"));
		try {
			root.setTop(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
		toolbarController = loader.getController();
		toolbarController.setup(data, primaryStage, this);

	}

	public static void main(String[] args) {
		launch(args);

	}

}
