package it.univpm.progettoPO.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.core.util.Loader;
import it.univpm.progettoPO.model.DataSet;
import it.univpm.progettoPO.model.ResourceSelector;
import it.univpm.progettoPO.model.RecordMeta;




@RestController
public class homeRestController {

	static DataSet setDati;
	
	/*
	 * Static method for the dataset initialization
	 */
	
	public static String init() {

		//FormatSelector(url,deep) maps the resource array list of main json		
		ResourceSelector rs = new ResourceSelector("http://data.europa.eu/euodp/data/api/3/action/package_show?id=KrER4bHeFuOMIMDyXfrpQ","result-resources");
		String url = rs.getUrlByFormat("CSV");  //get URL of CSV dataset
		try{
			rs.download(url, "t1.csv");			//download it
		}catch(Exception e) {
			System.out.println("Si è verificato un errore!");
			e.printStackTrace();
		}
		
		
		/*
		 * create the dataset with max possible max error (0 in this case), if not specified, max possible error is 10
		 * an error is intended as:
		 * number of string attribute is != 4, if so the line is discarded and error counter is incremented
		 * number of number attribute is != 17, 	  "
		 * value of number attribute is not a double, "
		 */
		setDati = new DataSet("t1.csv");
		if(setDati.isDataNull()) {
			System.out.println("Errore nell'importazione del dataset!");
			System.exit(1);
		} else
			return "Dataset caricato correttamente!";
		return null;
		
		
	}
		
	@GetMapping("/getdata")
	public String getData() {
		return setDati.toString();				//get all dataset in JSON
	}
	
	@GetMapping("/getmeta")
	public String getMeta() {
		RecordMeta rm = new RecordMeta();		//get all MetaData in Json
		return rm.toJson();
	}
	
	//get stats divided by string field and numerical field
	@RequestMapping(value="/stats/{col}", method = RequestMethod.GET)
	public String getStats(@PathVariable("col") String column) {
		if (column == "")
			return"Parametro non valido";
		try {
		if (Integer.valueOf(column)>=2000 && Integer.valueOf(column) <= 2017)	//try to convert request in a valid year
			return setDati.getNumberStats(Integer.valueOf(column));
		}catch(Exception e) {
			//if fails do nothing
		}
		
		int sc = -1;
			//if is not a number, try to select the string field
		
		if (column.matches("FREQ|freq"))
			sc = 0;
		if (column.matches("GEO|geo"))
			sc = 1;
		if (column.matches("UNIT|unit"))
			sc = 2;
		if (column.matches("OBJECTIV|TIME_PERIOD|objectiv|time_period"))
			sc = 3;
		
		//if fail print message
		
		if(sc == -1)
			return "Parametro non valido, quelli validi sono:\n {FREQ|freq|GEO|geo|UNIT|unit|OBJECTIV|TIME_PERIOD|objectiv|time_period}\n o un anno compreso tra 2000 e 2017 inclusi.";
		else 
			return setDati.getStringStats(column, sc); //else print string stats

	}
}
