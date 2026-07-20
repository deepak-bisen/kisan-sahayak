package com.kisan.knowledge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.kisan.knowledge")
public class KnowledgeAppApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(KnowledgeAppApplication.class, args);
    }
}
