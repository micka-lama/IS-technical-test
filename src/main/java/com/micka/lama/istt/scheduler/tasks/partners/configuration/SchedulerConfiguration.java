package com.micka.lama.istt.scheduler.tasks.partners.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Scheduler bean(s) configuration.
 */
@Configuration
@RequiredArgsConstructor
public class SchedulerConfiguration {

    /**
     * Configure the task scheduler bean.
     *
     * @param properties The properties.
     * @return The bean.
     */
    @Bean
    public TaskScheduler taskScheduler(final SchedulerProperties properties) {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(properties.getPoolSize());
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.initialize();
        return scheduler;
    }
}
