package it.univpm.progettoPO.model;

/**
 * Class derived from Record that maps the metadata of a generic record
 * @author Manuel
 *
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
