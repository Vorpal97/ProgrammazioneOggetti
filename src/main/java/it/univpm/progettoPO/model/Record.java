package it.univpm.progettoPO.model;


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
	
	/*
	 * abstract String getFreq();
	 * 
	 * abstract String getGeo();
	 * 
	 * abstract String getUnit();
	 * 
	 * abstract String getObj();
	 * 
	 * abstract double[] getYears();
	 */	
}
