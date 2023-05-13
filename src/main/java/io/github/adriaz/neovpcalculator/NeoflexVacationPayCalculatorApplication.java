package io.github.adriaz.neovpcalculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class NeoflexVacationPayCalculatorApplication {
	public static void main(String[] args) {
		SpringApplication.run(NeoflexVacationPayCalculatorApplication.class, args);
	}
}
