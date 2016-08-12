package application;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import javafx.util.converter.NumberStringConverter;

public class Piece implements Serializable {

	private static final long serialVersionUID = 2908805072858181454L;
	private IntegerProperty id;
	private StringProperty name;
	private StringProperty amount;
	private StringProperty nAmount;
	private ObjectProperty<PieceType> type;
	private StringProperty information;
	private IntegerProperty difference;
	private HashMap<Piece, Integer> recipe;

	public Piece(int id, String name, int amount, int nAmount, PieceType type, String info,
			HashMap<Piece, Integer> recipe) {
		this.id = new SimpleIntegerProperty(id);
		this.name = new SimpleStringProperty(name);
		this.amount = new SimpleStringProperty(Integer.toString(amount));
		this.nAmount = new SimpleStringProperty(Integer.toString(nAmount));
		this.difference = new SimpleIntegerProperty(amount - nAmount);
		this.type = new SimpleObjectProperty<PieceType>(type);
		this.information = new SimpleStringProperty(info);
		this.recipe = recipe;
		bindDifference();

	}

	public Piece(int id, String name, PieceType type) {
		this.id = new SimpleIntegerProperty(id);
		this.name = new SimpleStringProperty(name);
		this.amount = new SimpleStringProperty("0");
		this.nAmount = new SimpleStringProperty("0");
		this.difference = new SimpleIntegerProperty(0);
		this.type = new SimpleObjectProperty<PieceType>(type);
		this.information = new SimpleStringProperty("");
		this.recipe = new HashMap<>();
		bindDifference();
	}

	public void bindDifference() {
		this.amount.addListener((o, oldValue, newValue) -> {
			calculateDifference();
		});
		this.nAmount.addListener((o, oldValue, newValue) -> {
			calculateDifference();
		});
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty amountProperty() {
		return amount;
	}

	public StringProperty nAmountProperty() {
		return nAmount;
	}

	public ObjectProperty<PieceType> typeProperty() {
		return type;
	}

	public StringProperty informationProperty() {
		return information;
	}

	public StringProperty differenceProperty() {
		StringProperty bind = new SimpleStringProperty();
		int initValue = difference.get();
		Bindings.bindBidirectional(bind, difference, new NumberStringConverter());
		bind.set(Integer.toString(initValue));
		return bind;
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public int getAmount() {
		return Integer.parseInt(amount.get());
	}

	public void setAmount(int amount) {
		this.amount.set(Integer.toString(amount));
	}

	public int getNAmount() {
		return Integer.parseInt(nAmount.get());
	}

	public void setNAmount(int pAmount) {
		this.nAmount.set(Integer.toString(pAmount));
	}

	public int getId() {
		return id.get();
	}

	public PieceType getType() {
		return type.get();
	}

	public void setInformation(String info) {
		information.set(info);
	}

	public String getInformation() {
		return information.get();
	}

	public HashMap<Piece, Integer> getRecipe() {
		return recipe;
	}

	public void setRecipe(HashMap<Piece, Integer> recipe) {
		this.recipe = recipe;
	}

	public void addRecipe(Piece p, int amount) {
		recipe.put(p, amount);
	}

	public void removeRecipe(Piece p) {
		recipe.remove(p);
	}

	public Piece getCopy() {
		HashMap<Piece, Integer> rec = new HashMap<>(recipe);
		return new Piece(id.get(), name.get(), this.getAmount(), Integer.parseInt(nAmount.get()), type.get(),
				information.get(), rec);
	}

	public void addAmount(int amount) {
		this.amount.set(Integer.toString(Integer.parseInt(this.amount.get()) + amount));

	}

	public void updateNeeded(int amount) {
		if ((amount + Integer.parseInt(nAmount.get())) > Integer.parseInt(this.amount.get())) {
			int toBuildNum = amount + Integer.parseInt(nAmount.get()) - Integer.parseInt(this.amount.get());
			if (toBuildNum > amount) {
				toBuildNum = amount;
			}
			for (Entry<Piece, Integer> e : recipe.entrySet()) {
				e.getKey().updateNeeded(toBuildNum * e.getValue());
			}
		}
		nAmount.set(Integer.toString(Integer.parseInt(nAmount.get()) + amount));
	}

	private void calculateDifference() {
		difference.set(-Integer.parseInt(nAmount.get()) + Integer.parseInt(amount.get()));
	}

	public void build(int request) {
		int current = Integer.parseInt(this.amount.get());
		this.amount.set(Integer.toString(current + request));
		for (Entry<Piece, Integer> e : recipe.entrySet()) {
			e.getKey().buildIntermediate(request * e.getValue());
		}
	}

	public void buildIntermediate(int request) {
		int current = Integer.parseInt(this.amount.get());
		if (type.get() == PieceType.GROUP) {
			if (request > current) {
				for (Entry<Piece, Integer> e : recipe.entrySet()) {
					e.getKey().buildIntermediate((request - current) * e.getValue());
				}
				this.amount.set("0");
			} else {
				this.amount.set(Integer.toString(current - request));
			}
		} else {
			this.amount.set(Integer.toString(current - request));
		}
	}

	private void writeObject(java.io.ObjectOutputStream stream) {
		try {
			stream.writeInt(id.get());
			stream.writeObject(name.get());
			stream.writeObject(amount.get());
			stream.writeObject(nAmount.get());
			stream.writeObject(type.get());
			stream.writeObject(information.get());
			stream.writeInt(difference.get());
			stream.writeObject(recipe);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		id = new SimpleIntegerProperty(stream.readInt());
		name = new SimpleStringProperty((String) stream.readObject());
		amount = new SimpleStringProperty((String) stream.readObject());
		nAmount = new SimpleStringProperty((String) stream.readObject());
		type = new SimpleObjectProperty<PieceType>((PieceType) stream.readObject());
		information = new SimpleStringProperty((String) stream.readObject());
		difference = new SimpleIntegerProperty(stream.readInt());
		recipe = (HashMap<Piece, Integer>) stream.readObject();
		bindDifference();
	}

	public ObservableList<Pair<String, Integer>> getRecipeAsList() {
		ObservableList<Pair<String, Integer>> result = FXCollections.observableArrayList();
		for (Entry<Piece, Integer> e : recipe.entrySet()) {
			result.add(new Pair<String, Integer>(e.getKey().getName(), e.getValue()));
		}
		return result;
	}

}
