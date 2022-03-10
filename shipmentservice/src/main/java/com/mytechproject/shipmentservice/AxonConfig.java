package com.mytechproject.shipmentservice;

import com.thoughtworks.xstream.XStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** This config class is created in all Microservices participating in Axon Saga framework
 * to prevent ForbiddenClassException due to Spring Boot 2.6.* and JDK11 to make it
 * compatible for Axon to work
 */

@Configuration
public class AxonConfig {

    @Bean
    public XStream xStream() {
        XStream xStream = new XStream();

        xStream.allowTypesByWildcard(new String[] {
                "com.mytechproject.**"
        });
        return xStream;
    }
}
