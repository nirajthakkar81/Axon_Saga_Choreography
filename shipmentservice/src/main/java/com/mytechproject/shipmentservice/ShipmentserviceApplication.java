package com.mytechproject.shipmentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ AxonConfig.class })
public class ShipmentserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShipmentserviceApplication.class, args);
	}

}
