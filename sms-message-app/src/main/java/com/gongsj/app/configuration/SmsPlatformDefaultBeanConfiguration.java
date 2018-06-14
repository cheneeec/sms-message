package com.gongsj.app.configuration;

import com.gongsj.core.property.SmsPlatformProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@PropertySource(value = {"classpath:platform.properties"}, encoding = "UTF-8")
@Configuration
public class SmsPlatformDefaultBeanConfiguration {


    @Bean
    @ConfigurationProperties(prefix = "cdgs.lt")
    public SmsPlatformProperties ltSmsPlatformProperties() {
        return new SmsPlatformProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "cdgs.yd")
    public SmsPlatformProperties ydSmsPlatformProperties() {
        return new SmsPlatformProperties();
    }



}
