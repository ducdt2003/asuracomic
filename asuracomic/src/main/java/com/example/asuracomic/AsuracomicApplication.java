package com.example.asuracomic;

import com.example.asuracomic.entity.VipConfig;
import com.example.asuracomic.repository.VipConfigRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootApplication
public class AsuracomicApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsuracomicApplication.class, args);
	}

}
