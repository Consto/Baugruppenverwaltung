package application;

public enum PieceType {
	SINGLE("Einzelteil"),GROUP("Baugruppe");
	
	private final String name;
	private PieceType(String name){
		this.name=name;
	}
	public String friendlyName(){
		return name;
	}
}
