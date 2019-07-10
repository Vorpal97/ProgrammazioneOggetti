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
import it.univpm.progettoPO.model.RecordMeta;



@RestController
public class homeRestController {

	static DataSet setDati;
	
	public static String init() {
		//setDati = new DataSet("/Users/Manuel/eclipse-workspace/Prototipo_programmazione_oggetti/COMP_COMP_AG_X_1.0.csv");
		
		return"Dataset caricato correttamente!";
	}
	
	  @RequestMapping(value = "/files/", method = RequestMethod.GET)
	    public void getFile(
	        @PathVariable("fileID") String fileName, 
	        HttpServletResponse response) throws IOException {
	            String src= "https://webgate.ec.europa.eu/comp/redisstat/api/dissemination/sdmx/2.1/data/comp_ag_x?format=csv&compressed=false";
	            InputStream is = new FileInputStream(src);
	            IOUtils.copy(is, response.getOutputStream());
	            response.flushBuffer();
	    }
	
	@GetMapping("/getdata")
	public String getData() {
		return setDati.toString();
	}
	
	@GetMapping("/getmeta")
	public String getMeta() {
		RecordMeta rm = new RecordMeta();
		return rm.toJson();
	}
	
	@GetMapping("/getstats")
	public String getStats(@RequestParam(name="column", defaultValue="") String column) {
		if (column == "")
			return"Parametro non valido";
		try {
		if (Integer.valueOf(column)>=2000 && Integer.valueOf(column) <= 2017)
			return setDati.getNumberStats(Integer.valueOf(column));
		}catch(Exception e) {
			
		}
		
		int sc = -1;
		
		if (column.matches("FREQ|freq"))
			sc = 0;
		if (column.matches("GEO|geo"))
			sc = 1;
		if (column.matches("UNIT|unit"))
			sc = 2;
		if (column.matches("OBJECTIV|TIME_PERIOD|objectiv|time_period"))
			sc = 3;
		
		if(sc == -1)
			return "Parametro non valido";
		else 
			return setDati.getStringStats(column, sc);

	}
}
