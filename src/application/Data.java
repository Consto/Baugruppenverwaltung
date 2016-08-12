package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.util.Pair;
import javafx.util.converter.NumberStringConverter;


public class Data implements Serializable {

	
	private static final long serialVersionUID = 2542539458156123470L;
	private ObservableList<Piece> warehouse = FXCollections.observableArrayList();
	private int warehouseID = 0;
	private ObservableMap<Piece, IntegerProperty> plans = FXCollections
			.observableMap(new HashMap<Piece, IntegerProperty>());
	private ObservableList<Pair<String,String>> plansList=FXCollections.observableArrayList();
	


	public ObservableList<Piece> getWarehouse() {
		return warehouse;
	}

	public void addPieceToWarehouse(Piece piece) {
		warehouse.add(piece);
	}

	public void setWarehouseID(int warehouseID) {
		this.warehouseID = warehouseID;
	}

	public int getNextWarehouseID() {
		warehouseID++;
		return warehouseID;
	}

	public void addPlan(Piece piece, int amount) {
		if (plans.containsKey(piece)) {
			plans.get(piece).set(amount + plans.get(piece).get());
		} else {
			plans.put(piece, new SimpleIntegerProperty(amount));
		}
	}

	public int getWarehouseID() {
		return warehouseID;
	}

	public ObservableMap<Piece, IntegerProperty> getPlans() {
		return plans;
	}

	public void buildPlan(Piece piece, int amount) {
		if (plans.containsKey(piece)) {
			if (plans.get(piece).get() > amount) {
				plans.get(piece).set(-amount + plans.get(piece).get());
			} else {
				plans.get(piece).set(0);
			}
		} else {
			plans.put(piece, new SimpleIntegerProperty(0));
		}
		piece.build(amount);
	}

	public StringProperty getPlan(Piece piece) {
		StringProperty bind = new SimpleStringProperty();
		if (!plans.containsKey(piece)) {
			plans.put(piece, new SimpleIntegerProperty(0));
		}
		int initValue = plans.get(piece).get();
		Bindings.bindBidirectional(bind, plans.get(piece), new NumberStringConverter());
		bind.set(Integer.toString(initValue));
		return bind;
	}

	public void calculateNeeded() {
		// reset all previous values
		for (Piece p : warehouse) {
			p.setNAmount(0);
		}
		// compute new values
		for (Entry<Piece, IntegerProperty> e : plans.entrySet()) {
			e.getKey().updateNeeded(e.getValue().get());
		}
	}

	public void saveToFile(File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exportExcel(File file) {
		String out="Name\t"
				+ "Benötigt\t"
				+ "Vorhanden\t"
				+ "Differenz\n";
		
		for (Piece p : warehouse) {
			out+=p.getName()+"\t"
					+p.getNAmount()+"\t"
					+p.getAmount()+"\t"
					+(p.getAmount() - p.getNAmount())+"\n";
		}
		try {
			PrintWriter pw=new PrintWriter(file);
			pw.print(out);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
		ArrayList<Piece> w = new ArrayList<>();
		w.addAll(warehouse);
		HashMap<Piece, Integer> p = new HashMap<>();
		for (Entry<Piece, IntegerProperty> e : plans.entrySet()) {
			p.put(e.getKey(), e.getValue().get());
		}
		stream.writeObject(w);
		stream.writeInt(warehouseID);
		stream.writeObject(p);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {

		warehouse = FXCollections.observableArrayList();
		warehouse.addAll((ArrayList<Piece>) stream.readObject());
		warehouseID = stream.readInt();
		plans = FXCollections.observableMap(new HashMap<Piece, IntegerProperty>());
		HashMap<Piece, Integer> p = (HashMap<Piece, Integer>) stream.readObject();
		for (Entry<Piece, Integer> e : p.entrySet()) {
			plans.put(e.getKey(), new SimpleIntegerProperty(e.getValue()));
		}
		
	}

	public void setWarehouse(ObservableList<Piece> warehouse) {
		this.warehouse = warehouse;
	}

	public void setPlans(ObservableMap<Piece, IntegerProperty> plans) {
		this.plans = plans;
	}
	public ObservableList<Pair<String, String>> getPlansAsList(){
		
		return plansList;
	}

}
