package org.fintech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeLogConfiguration {
    @Bean
    public TimeLogAspectBeanPostProcessor timeLogAspectBeanPostProcessor (){
        return new TimeLogAspectBeanPostProcessor();
    }

}
