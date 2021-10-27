package com.example.streams.demo;

import com.example.streams.demo.services.KafkaProducerService;
import com.example.streams.demo.services.datagenerator.StockOrderGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements ApplicationRunner {

	Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);
	}

	@Autowired
	private KafkaProducerService producerService;

	@Autowired
	private StockOrderGenerator stockOrderGenerator;


	@Value("${application.configs.message.count}")
	private int MESSAGE_COUNT;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		/*
		   Generate sample events
		 */
		logger.info("Inside run method...");
		for (int i = 0; i < MESSAGE_COUNT; i++) {
			producerService.generateStockOrderEvent(stockOrderGenerator.getNextProduct());
			//Extended Sleep to test windowed events
			Thread.sleep(5000);
		}
	}

}

