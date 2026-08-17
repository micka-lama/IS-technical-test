package com.micka.lama.istt.scheduler.tasks.partners.configuration;

import com.micka.lama.istt.scheduler.tasks.partners.DataSourceType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

/**
 * Scheduler properties.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.scheduler")
public class SchedulerProperties {

    /**
     * Poolsize to perform the tasks.
     *
     * @implNote If there is a lot of waiting tasks (due to a bottleneck on the HTTP requests for example),
     * increase this value to not block the remaining tasks.
     */
    private int poolSize = 4;

    /**
     * The tasks.
     */
    private Map<DataSourceType, TaskConfig> tasks = new EnumMap<>(DataSourceType.class);

    /**
     * Task configuration.
     */
    @Data
    public static class TaskConfig {
        /**
         * CRON.
         */
        private String cron;

        /**
         * If enabled, the task will be performed.
         */
        private boolean enabled;

        /**
         * URL of the partner to retrieve the data.
         */
        private String url;
    }
}