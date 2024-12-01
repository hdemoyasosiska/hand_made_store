package com.example.hm_store;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableJpaRepositories(basePackages = {"com.example.hm_store.repo"})
@EntityScan(basePackages = "com.example.hm_store.entity")
@ComponentScan(basePackages = "com.example.hm_store")
@EnableAspectJAutoProxy
public class AppConfig {
}