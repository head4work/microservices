package com.head4work.companyservice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration // Marks this class as a source of bean definitions
@EnableAsync
// Enables Spring's asynchronous method execution capability (though not strictly required for manual supplyAsync with Executor)
public class AsyncConfig {

    @Bean(name = "applicationTaskExecutor") // Defines a Spring bean named "applicationTaskExecutor"
    public Executor applicationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);        // Minimum number of threads to keep alive
        executor.setMaxPoolSize(10);        // Maximum number of threads the pool can grow to
        executor.setQueueCapacity(25);      // Capacity of the queue for tasks waiting to be executed
        executor.setThreadNamePrefix("AppAsync-"); // Prefix for the names of threads in this pool
        executor.initialize();              // Initializes the thread pool
        return executor;
    }
}
