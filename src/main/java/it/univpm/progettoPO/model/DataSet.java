package it.univpm.progettoPO.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class DataSet implements DataStats {
	
	private ArrayList<RecordData> data = new ArrayList<RecordData>();
	
	public DataSet() {
		data = null;
	}
	
	public DataSet(String path) {
		int e = this.setData(path, 10);
		if(e > 0)
			System.out.println("importazione terminata con " + ((e == 10)? "successo" : (10-e) + " errori"));
			else {
				System.out.println("importazione fallita con più di " + e + "errori");
			this.data = null;
		}
		
	}

	public DataSet(String path, int maxErr) {
		int e = this.setData(path, maxErr);
		if(e >= 0)
			System.out.println("importazione terminata con " + ((e == maxErr)? "successo" : (maxErr-e) + " errori"));
			else {
				System.out.println("importazione fallita");
				this.data = null;
		}
		
	}

	public ArrayList<RecordData> getData() {
		return data;
	}

	public int setData(String path, int maxErr) {
		try {
			BufferedReader fp = new BufferedReader(new FileReader(path));
			String line = fp.readLine();
			//scarto la riga di intestazione
			for(line = fp.readLine(); line != null && maxErr >= 0; line = fp.readLine()) {
				String[] meta = line.split(";");
				if(meta.length != 4) {
					System.out.println("Numero di attributi errato!");
					maxErr--;
					continue;	//skip at the next line
				}
				String[] datis = meta[3].split(",");
				if(!(datis.length != 17)) {
					System.out.println("Numero di anni errato!");
					maxErr--;
					continue;	//skip at the next line
				}
				meta[3] = datis[0];
				double[] datid = new double[datis.length];
				try {
					for(int i=1;i<datis.length;i++) {
						datid[i] = Double.parseDouble(datis[i]);
				}
				}catch(Exception e) {
					System.out.println("Errore nella conversione dati numerici");
					maxErr--;
					continue;	//skip at the next line
				}
				data.add(new RecordData(meta[0],meta[1],meta[2],meta[3],datid));
			}
			fp.close();
		} catch (FileNotFoundException e) {
			System.out.println("File non trovato!");
		} catch (IOException e) {
			System.out.println("Errore di I/O");
		}
		return maxErr;
	}
	
	public boolean isDataNull() {
		if(data == null)
			return true;
		else
			return false;
	}
	
	@Override
	public String toString() {
		
		int i;
		String out = "{";
		for(i = 0; i < data.size()-1; i++)
			out += "\"" + i + "\":" + data.get(i).toJson() + ",";
		out += "\"" + i + "\":" + data.get(i).toJson();		
		out += "}";
		
		return out;
	}
	
	private double getAvg(int year) {
		return this.getSum(year) / data.size();
	}
	
	private double getMin(int year) {
		int y = year - 2000;
		double min = data.get(0).years[y];
		for(RecordData row : data)
			if(row.years[y] < min)
				min = row.years[y];
		return min;
	}
	
	private double getMax(int year) {
		int y = year - 2000;
		double min = data.get(0).years[y];
		for(RecordData row : data)
			if(row.years[y] > min)
				min = row.years[y];
		return min;
		
	}
	
	private double getDevStd(int year) {
		double sum = this.getSum(year);
	    double newSum = 0; 
	    double [] newArray = new double [data.size()]; 
	    int y = year - 2000;

	    double mean = (sum) / (data.size()); 

	    for (int j = 0; j<data.size(); j++){
	        newArray[j] = ((data.get(j).years[y] - mean) * (data.get(j).years[y] - mean)); 
	        newSum = newSum + newArray[j]; 
	    }
	    double squaredDiffMean = (newSum) / (data.size()); 
	    double standardDev = (Math.sqrt(squaredDiffMean)); 

	    return standardDev; 
	}
	
	private double getSum(int year) {
		double sum = 0;
		int y = year - 2000;
		for(int i=0;i<data.size();i++)
			sum += data.get(i).years[y];
		return sum;
	}
	
	private double getCount(int year) {
		return data.size();
	}
	
	public String getNumberStats(int year) {
		String out = "{\"Year\":" + String.valueOf(year) +", " +
					 "\"Avg\":" + String.valueOf(this.getAvg(year)) +", " +
					 "\"Min\":" + String.valueOf(this.getMin(year)) +", " +
					 "\"Max\":" + String.valueOf(this.getMax(year)) +", " +
					 "\"DevStd\":" + String.valueOf(this.getDevStd(year)) +", " +
					 "\"Sum\":" + String.valueOf(this.getSum(year)) +", " +
					 "\"Count\":" + String.valueOf(this.getCount(year)) +"}";
		return out;
	}
	
	public String getStringStats(String field, int sc) {
		
		field.toUpperCase();

		HashMap <String,Integer> hm = new HashMap<String,Integer>();
		
		switch(sc) {
			case 0:
				for(RecordData rd : data) {
					if (hm.containsKey(rd.freq)){
		                int count = hm.get(rd.freq);
		                count++;
		                hm.put(rd.freq, count);
		            } else
		            	hm.put(rd.freq, 1);
				}
			break;

			case 1:
				for(RecordData rd : data) {
					if (hm.containsKey(rd.geo)){
		                int count = hm.get(rd.geo);
		                count++;
		                hm.put(rd.geo, count);
		            } else
		            	hm.put(rd.geo, 1);
				}
			break;

			case 2:
				for(RecordData rd : data) {
					if (hm.containsKey(rd.unit)){
		                int count = hm.get(rd.unit);
		                count++;
		                hm.put(rd.unit, count);
		            } else
		            	hm.put(rd.unit, 1);
				}
			break;

			case 3:
				for(RecordData rd : data) {
					if (hm.containsKey(rd.obj)){
		                int count = hm.get(rd.obj);
		                count++;
		                hm.put(rd.obj, count);
		            } else
		            	hm.put(rd.obj, 1);
				}
			break;

		}
		
		String out = "{";
		for(Entry<String, Integer> val : hm.entrySet()) {
			out += "\"" + val.getKey() + "\" : " + String.valueOf(val.getValue()) + ", ";
		}
		
		return (out.substring(0, out.length()-2)) + "}";
	}
	
}
