package it.univpm.progettoPO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProgettoPoApplication {

	
	public static void main(String[] args) {
		//avvio la spring application
		SpringApplication.run(ProgettoPoApplication.class, args);
		//avvio il metodo statico del restController che si occupa dell' importazione del dataset
		it.univpm.progettoPO.controller.homeRestController.init();
	}

}
