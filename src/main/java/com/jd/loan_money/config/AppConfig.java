package com.jd.loan_money.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@ComponentScan("com.jd.loan_money.*")
@PropertySource("classpath:application.properties")
@Configuration
public class AppConfig {

}
