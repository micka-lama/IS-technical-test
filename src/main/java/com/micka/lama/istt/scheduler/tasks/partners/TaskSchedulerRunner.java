package com.micka.lama.istt.scheduler.tasks.partners;

import com.micka.lama.istt.entities.Parking;
import com.micka.lama.istt.requests.IParkingRequest;
import com.micka.lama.istt.scheduler.tasks.partners.configuration.SchedulerProperties;
import com.mongodb.bulk.BulkWriteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Task scheduler runner.
 * <p>
 * Execute periodically tasks.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskSchedulerRunner implements ApplicationRunner {

    /**
     * Task scheduler.
     */
    private final TaskScheduler taskScheduler;

    /**
     * Scheduler properties.
     */
    private final SchedulerProperties properties;

    /**
     * All the Bean tasks. Some might be disabled.
     */
    private final List<IScheduledTask> tasks;

    /**
     * Parking request.
     */
    private final IParkingRequest parkingRequest;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final @NonNull ApplicationArguments args) {
        final Map<DataSourceType, IScheduledTask> tasksByDataSource = tasks.stream()
                .collect(Collectors.toMap(IScheduledTask::getType, Function.identity()));

        tasksByDataSource.forEach((type, task) -> {
            final SchedulerProperties.TaskConfig config = properties.getTasks().get(type);

            // Verify if the task is eligible to be scheduled.
            if (config == null) {
                log.warn("No configuration found for task type {}, skipping.", type);
                return;
            }

            if (!config.isEnabled()) {
                log.info("Task {} is disabled, skipping.", type);
                return;
            }

            final String cron = config.getCron();
            if (cron == null || cron.isBlank()) {
                log.warn("No cron expression configured for task type {}, skipping.", type);
                return;
            }

            // The task can be scheduled.
            taskScheduler.schedule(() -> taskParkings(task), new CronTrigger(cron));
            log.info("Scheduled task {} with cron '{}'", type, cron);
        });
    }

    /**
     * Retrieve the parkings from the partner and save the data in the database.
     *
     * @param task The task to perform.
     */
    private void taskParkings(final IScheduledTask task) {
        final List<Parking> parkings = task.getParkings();

        final BulkWriteResult bulkWriteResult = parkingRequest.upsert(parkings);

        if (!bulkWriteResult.getUpserts().isEmpty()) {
            log.info("[{}] {} new parking(s) inserted.", task.getType().name(), bulkWriteResult.getUpserts().size());
        }
        if (bulkWriteResult.getModifiedCount() > 0) {
            log.debug("[{}] {} parking(s) updated.", task.getType().name(), bulkWriteResult.getModifiedCount());
        }
    }
}
