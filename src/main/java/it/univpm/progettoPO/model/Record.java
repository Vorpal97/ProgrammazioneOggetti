package it.univpm.progettoPO.model;

/*
 * classe astratta che definisce la struttura di un generico record del dataset csv
 * da essa derivano 2 sottoclassi
 * RecordData che è utilizzata per mappare ogni record del DataSet
 * RecordMeta che mappa i metadati di un record generico
 */

abstract class Record {
	
	//Metadati
	protected String freq;
	protected String geo;
	protected String unit;
	protected String obj;
	//Dati
	protected double[] years = new double[18];

	public Record() {
		this.freq = null;
		this.geo = null;
		this.unit = null;
		this.obj = null;
		this.years = null;
	}
	
	public Record(String freq, String geo, String unit, String obj, double[] years) {
		this.freq = freq;
		this.geo = geo;
		this.unit = unit;
		this.obj = obj;
		this.years = years;
	}

	
	abstract String toJson();
	
}
