package com.micka.lama.istt.services;

import com.micka.lama.istt.dtos.ParkingDTO;

import java.util.List;

/**
 * Parking service interface.
 */
public interface IParkingService {

    /**
     * Retrieve the nearest parkings from a given position.
     *
     * @param latitude    The latitude of the user.
     * @param longitude   The longitude of the user.
     * @param minDistance The minimum distance (in meter) before starting the research.
     * @param maxDistance The maximum distance (in meter) before stopping the research.
     * @param limit       The maximum items returned.
     * @return The parkings found based on the criteria.
     */
    List<ParkingDTO> findNearestParkings(final double latitude, final double longitude, final double minDistance, final double maxDistance, final int limit);

}