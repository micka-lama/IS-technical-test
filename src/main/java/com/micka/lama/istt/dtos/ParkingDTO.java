package com.micka.lama.istt.dtos;

import com.micka.lama.istt.entities.Parking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the entity {@link Parking}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingDTO {

    /**
     * Identifier.
     */
    private String id;

    /**
     * External identifier.
     * <p>
     * This is the identifier from the partner.
     */
    private String externalId;

    /**
     * Latitude of the parking.
     */
    private double latitude;

    /**
     * Longitude of the parking.
     */
    private double longitude;

    /**
     * Available spots for the parking.
     */
    private int availableSpots;

    /**
     * Distance in meters.
     */
    private double distance;

}
