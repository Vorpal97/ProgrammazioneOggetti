package it.univpm.progettoPO.model;

/*
 * Classe derivata da Record che mappa i metadati di un record generico
 */

public class RecordMeta extends Record {
	
	public String toJson() {
		String st = "{\"freq\": \"String\"," +
					" \"geo\": \"String\"," +
					" \"unit\": \"String\"," +
					" \"obj\": \"String\"," +
					" \"years\": { ";
		int i;
		for(i = 0; i <= 17; i++)
			st += "\""+ String.valueOf(2000+i) +"\": \"Double\", ";
		st += "\""+ String.valueOf(2000+i) +"\": \"Double\"}";
		st += "}";
		return st;
	}
	
	public String getFreq() {
		return freq;
	}

	public String getGeo() {
		return geo;
	}

	public String getUnit() {
		return unit;
	}

	public String getObj() {
		return obj;
	}

	public double[] getYears() {
		return years;
	}

}
