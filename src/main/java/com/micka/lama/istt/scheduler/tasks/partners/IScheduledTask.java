package com.micka.lama.istt.scheduler.tasks.partners;

import com.micka.lama.istt.entities.Parking;

import java.util.List;

/**
 * Scheduled task interface.
 */
public interface IScheduledTask {

    /**
     * Retrieve the type of the data source.
     *
     * @return The data source type.
     */
    DataSourceType getType();

    /**
     * The parkings found from the partner.
     *
     * @return The parkings.
     */
    List<Parking> getParkings();
}
