# Progetto Programmazione Oggetti Manuel Manelli s1077802

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

Il programma, all'avvio, scarica il json tramite l'URL fornito dal docente e, utilizzando un oggetto della classe `ResourceSelector`, questo oggetto necessita, oltre all'URL, di un deep_path che indica la posizione, riferita alla gerarchia di contenimento, dell'array di risorse (es "result-resources" indica che resources è contenuto all'interno dell'oggetto result) estrae quest'ultimo e tramite il metodo `getUrlByFormat()` è possibile estrarre l'URL del dataset espresso nel formato desiderato specificando quest'ultimo come unico parametro del metodo.

**Esempio:**
```java
		ResourceSelector rs = new ResourceSelector(myURL,"result-resources");
		String url = rs.getUrlByFormat("CSV");  //get URL of CSV dataset
```
Successivamente il programma procede a scaricare il dataset dall'URL ottenuto.

# Importazione del dataset

La gerarchia delle classi progettata per mappare il dataset è la seguente:
- `Record` > classe astratta che definisce la struttura di un generico record del dataset csv da essa derivano 2 sottoclassi, RecordMeta e RecordData
- `RecordData` > utilizzata per mappare ogni record del DataSet
- `RecordMeta` > mappa i metadati di un record generico
- `DataSet` > Classe che implementa il vero e proprio data set tramite un ArrayList di tipo RecordData e una serie di metodi per realizzare una serie di operazioni
- `ResourceSelector` > Descritta precedentemente, ha il compito di estrarre l'URL del dataset nel formato desiderato (CSV in questo caso)
    
Oltre a queste classi è stata definita un'interfaccia chiamata DataStats che definisce i metodi riguardanti i metodi delle statistiche, la classe DataSet implementa questa interfaccia.

Nella fase di importazione del dataset, per tenere conto degli errori di forma che il file CSV può presentare, è stato implementato un meccanismo che consente di definire un numero massimo di errori di forma che la procedura può incontrane (di default 3) prima di scartare automaticamente il file e arrestare l'esecuzione dell'applicazione per formato CSV non corretto.

Gli errori previsti sono principalmente di 3 tipi:
- Numero di attributi stringhe diverso da 4 (FREQ;GEO;UNIT;OBJECTIV\TIME_PERIOD)
- numero di attributi numerici diverso da 18
- errore nella conversione di un valore numerico da `String` in tipo `Double`

Ogni volta che un errore di questo tipo si verifica, la riga affetta da errore viene segnalata in console, scartata e decrementato il contatore degli errori ammessi.
Quando quest'ultimo non è più maggiore di zero, viene abortita la procedura e il file è considerato come non valido.

```java
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
```

Un sovraccarico di questo costruttore permette di sovrascrivere il numero predefinito di errori massimi.
```java
public DataSet(String path, int maxErr)
```

# Implementazione delle statistiche

Le statistiche sono state implementate a partire dai metodi definiti nell'interfaccia DataStats:

```java
	String getNumberStats(int year);
	String getStringStats(int sc);
```

Il primo implementa il calcolo delle statistiche numeriche a partire da un dato Anno (AVG,MIN,MAX,DEV STD,COUNT), mentre il secondo si occupa di calcolare le occorrenze di elementi unici dei campi Stringa a partire dal parametro di controllo calcolato sulla base dei campi Stringa ammessi.
```java
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
			return setDati.getStringStats(sc); //else print string stats
```
All'interno della classe DataSet sono definiti 6 metodi privati che implementano le funzioni elaborative delle statistiche sui dati numerici descritte in precedenza, questi metodi vengono utilizzati per implementare `getNumberStats()`.

Per quanto riguarda l'implementazione delle statistiche sui dati di tipo stringa, per eseguire il conteggio delle occorrenze nel campo richiesto, è stata utilizzata una HashMap di tipo `<String,Integer>`, utilizzando come Chiave l'occorrenza del campo stringa e come valore un intero che contava quante volte questo è incontrato nella colonna.

```java
	public String getStringStats(int sc) {
		
		HashMap <String,Integer> hm = new HashMap<String,Integer>();
		
		//in base alla colonna selezionata conto le occorrenze degli elementi del campo scelto tramite una hash map <String,Integer>
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
		//terminato il conteggio delle occorrenze ritorno il risultato in json
		String out = "{";
		for(Entry<String, Integer> val : hm.entrySet()) {
			out += "\"" + val.getKey() + "\" : " + String.valueOf(val.getValue()) + ", ";
		}
		return (out.substring(0, out.length()-2)) + "}";
	}

```
Al termine del conteggio delle occorrenze viene convertito il contenuto della HashMap in Json e ritornato al chiamante.

```java
	private double getAvg(int year);
	private double getMin(int year);
	private double getMax(int year);
	private double getDevStd(int year);
	private double getSum(int year);
	private double getCount(int year);
	//----------------------------------
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
```
### Possibili Test

Una volta avviata l'applicazione, se il download del dataset e l'importazione vanno a buon fine, è possibile effettuare delle richieste RES di tipo GET;
```sh
    http://localhost:8080/getdata
```
Con questa richiesta si ottiene tutto il dataset in formato json
```json
{
    "0": {
        "freq": "A",
        "geo": "EU28",
        "unit": "MEUR_KP_PRE",
        "obj": "OBJ01",
        "years": {
            "2000": 0,
            "2001": 0,
            "2002": 0,
            "2003": 0,
            "2004": 0,
            "2005": 0,
            "2006": 0,
            "2007": 0,
            "2008": 0,
            "2009": 0,
            "2010": 0,
            "2011": 0,
            "2012": 0,
            "2013": 0,
            "2014": 4.852,
            "2015": 0,
            "2016": 0,
            "2017": 0,
            "2018": 0
        }
    },  
```
getmeta consente di ottenere l'elenco dei metadati.
```sh
    http://localhost:8080/getmeta
```
```json
{
    "freq": "String",
    "geo": "String",
    "unit": "String",
    "obj": "String",
    "years": {
        "2000": "Double",
        "2001": "Double",
        "2002": "Double",
        "2003": "Double",
        "2004": "Double",
        "2005": "Double",
        "2006": "Double",
        "2007": "Double",
        "2008": "Double",
        "2009": "Double",
        "2010": "Double",
        "2011": "Double",
        "2012": "Double",
        "2013": "Double",
        "2014": "Double",
        "2015": "Double",
        "2016": "Double",
        "2017": "Double",
        "2018": "Double"
    }
}
```

Per ottenere le statistiche bisogna richiedere /stats/ passando come parametro un anno compreso tra 2000 e 2017 per ottenere le statistiche numeriche per quell'anno oppure un campo Stringa per le statistiche sul numero di occorrenze.
```sh
http://localhost:8080/stats/2013
```
```json
{
    "Year": 2013,
    "Avg": 17.299113089509163,
    "Min": 0,
    "Max": 8989.984,
    "DevStd": 216.8938882154337,
    "Sum": 35947.55700000004,
    "Count": 2078
}
```

NOTA: È possibile richiedere le statistiche numeriche specificando il campo stringa sia in minuscolo che in maiuscolo.

```sh
http://localhost:8080/stats/geo || http://localhost:8080/stats/GEO
```

```json
{
    "DE": 116,
    "BE": 104,
    "FI": 68,
    "PT": 34,
    "BG": 62,
    "DK": 48,
    "LT": 60,
    "LU": 64,
    "LV": 72,
    "HR": 24,
    "FR": 104,
    "HU": 82,
    "SE": 52,
    "EU28": 158,
    "SI": 74,
    "UK": 86,
    "SK": 52,
    "IE": 70,
    "EE": 56,
    "EL": 22,
    "MT": 16,
    "IT": 122,
    "ES": 108,
    "AT": 90,
    "CY": 50,
    "CZ": 84,
    "PL": 66,
    "RO": 42,
    "NL": 92
}
```

Se viene digitato un parametro sconosciuto il programma fornisce le istruzioni sui parametri ammessi.
```sh
http://localhost:8080/stats/prova
```

```
Parametro non valido, quelli validi sono:
 {FREQ|freq|GEO|geo|UNIT|unit|OBJECTIV|TIME_PERIOD|objectiv|time_period}
 o un anno compreso tra 2000 e 2017 inclusi.
```