package ru.largusshop.internal_orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

@SpringBootApplication
public class InternalOrdersApplication {

	public static void main(String[] args) throws FileNotFoundException {
		System.setErr(new PrintStream(new File("log.txt")));
		SpringApplication.run(InternalOrdersApplication.class, args);
	}
}
