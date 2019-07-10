package it.univpm.progettoPO.model;

public class RecordData extends Record {

	public RecordData() {
		super();
	}
	
	public RecordData(String freq, String geo, String unit, String obj, double[] years) {
		super(freq, geo, unit, obj, years);
	}

	public String toJson() {
		int i;
		String years = "{";
		for(i = 0; i < this.years.length-1; i++)
			years += '"' + String.valueOf(2000+i) + '"' + ':' + this.years[i] + ',';
		years += '"' + String.valueOf(2000+i) + '"' + ':' + this.years[i];
		years += '}';
		return "{" + 
				"\"freq\":\"" + freq +'"' +
				", \"geo\":\"" + geo +'"' +
				", \"unit\":\"" + unit +'"' +
				", \"obj\":\"" + obj +'"' +
				", \"years\":" + years +'}';
	}

	public String getFreq() {
		return freq;
	}

	public void setFreq(String freq) {
		this.freq = freq;
	}

	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getObj() {
		return obj;
	}

	public void setObj(String obj) {
		this.obj = obj;
	}

	public double[] getYears() {
		return years;
	}

	public void setYears(double[] years) {
		this.years = years;
	}

}
