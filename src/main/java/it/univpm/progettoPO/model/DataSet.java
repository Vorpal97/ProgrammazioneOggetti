package it.univpm.progettoPO.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * 
 * @author Manuel Manelli
 *
 */

public class DataSet implements DataStats {
	
	private ArrayList<RecordData> data = new ArrayList<RecordData>();
		
	public DataSet() {
		data = null;
	}
	//primo costruttore con numero di errori massimi consentiti nel csv pari a 3
	public DataSet(String path) {
		//setData ritorna il numero di errori del json trovati nella procedura di importazione, se si verificano più di 3 errori l'importazione fallisce
		int e = this.setData(path, 3);
		if(e > 0)
			System.out.println("importazione terminata con " + ((e == 3)? "successo" : (3-e) + " errori"));
			else {
				System.out.println("importazione fallita con più di " + e + "errori");
			this.data = null;
		}
		
	}
	
	//
	/**
	 * Metodo sovraccaricato che consente di definire un numero massimo di errori tollerabile, per nessun'errore maxErr = 0
	 * @param path downloaded csv path name
	 * @param maxErr max number of error allowed during data import process
	 */
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
	/**
	 * Real data import method
	 * @param path		|	same of
	 * @param maxErr	|	above
	 * @return	return maxErr, if maxErr = 3 imply no error, if maxErr is between 0 and 3 (extremes excluded) imply data import completed with errors else maxErr = 0 import failed due to csv incompatible format.
	 */
	public int setData(String path, int maxErr) {
		try {
			BufferedReader fp = new BufferedReader(new FileReader(path));
			String line = fp.readLine();
			//scarto la riga di intestazione
			int k;
			for(line = fp.readLine(), k = 1; line != null && maxErr >= 0; line = fp.readLine(), k++) {
				//splitta la riga con il carattere ;
				String[] meta = line.split(";");
				//controllo se sono presenti 4 campi, se il numero è diverso decremento maxErr (aggiungo un errore) e scarto la riga passando alla prossima iterazione
				if(meta.length != 4) {
					System.out.println("### Numero di attributi errato! Scarto la riga: " + k);
					maxErr--;
					continue;	//skip at the next line
				}
				String[] datis = meta[3].split(",");
				if(datis.length != 19) { 							//18 anni da 2000 a 2017 + ultimo campo stringa che estraggo grazie alla ','
					System.out.println("###Numero di anni errato! Scarto la riga: " + k);
					maxErr--;
					continue;	//skip at the next line
				}
				meta[3] = datis[0];									// assegno il 4 campo stringa al vettore delle stringhe
				double[] datid = new double[datis.length];
				try {
					for(int i=1;i<datis.length;i++) {
						datid[i] = Double.parseDouble(datis[i]);
				}
				}catch(Exception e) {
					System.out.println("###Errore nella conversione dati numerici! Scarto la riga: " + k);
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
	//verifica che la procedura di importazione sia andata a buon fine, se così non fosse l'ArrayList data = null
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
		
	/**
	 * methods to calculate statistics on the dataset
	 * @param year year to be processed
	 * @return calculated statistic to return
	 */
	
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
	
	
	/**
	 * Provides the json that contains all the numeric statistics
	 * @param year for numerical stats
	 */
	
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
	
	/**
	 * provides the json that contains all the string statistics depending on the specified column
	 * @param sc indicates the string field to be processed
	 */
	public String getStringStats(int sc) {
		

		HashMap <String,Integer> hm = new HashMap<String,Integer>();
		
		//based on the selected column, I take the occurrences of the elements of the field chosen using a hash map <String,Integer>
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
		
		//finished counting of occurrences return the result in json
		
		String out = "{";
		for(Entry<String, Integer> val : hm.entrySet()) {
			out += "\"" + val.getKey() + "\" : " + String.valueOf(val.getValue()) + ", ";
		}
		
		return (out.substring(0, out.length()-2)) + "}";
	}
	
}
