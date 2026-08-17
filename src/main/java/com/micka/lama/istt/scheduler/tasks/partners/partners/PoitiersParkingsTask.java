package com.micka.lama.istt.scheduler.tasks.partners.partners;

import com.micka.lama.istt.clients.IPoitiersParkingClient;
import com.micka.lama.istt.clients.dtos.PoitiersParkingDTO;
import com.micka.lama.istt.entities.Parking;
import com.micka.lama.istt.scheduler.tasks.partners.DataSourceType;
import com.micka.lama.istt.scheduler.tasks.partners.IScheduledTask;
import com.micka.lama.istt.scheduler.tasks.partners.configuration.SchedulerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Task to retrieve the parkings from Poitiers.
 *
 * @see <a href="https://data.grandpoitiers.fr/datasets/mobilites-stationnement-des-parkings-en-temps-reel/api-doc?operation=readLines">API documentation</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PoitiersParkingsTask implements IScheduledTask {

    /**
     * HTTP client.
     */
    private final IPoitiersParkingClient poitiersParkingClient;

    /**
     * Properties to setup the task.
     */
    private final SchedulerProperties properties;

    /**
     * Convert the plain string "0.0, 0.0" to {@link GeoJsonPoint}.
     *
     * @param geoPoint The plain string.
     * @return The {@link GeoJsonPoint}. Can be null if the format is not valid.
     */
    private static GeoJsonPoint toGeoJsonPoint(final String geoPoint) {
        if (geoPoint == null) {
            return null;
        }
        final String[] positions = geoPoint.split(", ");
        if (positions.length != 2) {
            return null;
        }
        final double longitude = Double.parseDouble(positions[1]);
        final double latitude = Double.parseDouble(positions[0]);
        return new GeoJsonPoint(longitude, latitude);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSourceType getType() {
        return DataSourceType.POITIERS_PARKING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Parking> getParkings() {
        final String url = properties.getTasks().get(getType()).getUrl();

        final List<Parking> parkingsToSave = new ArrayList<>();
        String nextUrl = url;
        do {
            final PoitiersParkingDTO parkings = poitiersParkingClient.fetchParkings(URI.create(nextUrl));
            log.debug("Task {} retrieved {} parkings", getType(), parkings.getResults().size());

            for (final PoitiersParkingDTO.ParkingDetails parkingDTO : parkings.getResults()) {
                final Parking parking = mapToEntity(parkingDTO);
                if (parking.getExternalId() == null || parking.getLocation() == null) {
                    log.warn("Failed to save the parking: {}", parkingDTO);
                    continue;
                }

                parkingsToSave.add(parking);
            }

            nextUrl = parkings.getNext();
        } while (nextUrl != null && !nextUrl.isBlank());

        return parkingsToSave;
    }

    /**
     * Convert the DTO from the partner to a more abstract model (the entity).
     *
     * @param parkingDTO The DTO from the partner.
     * @return The entity.
     */
    private Parking mapToEntity(final PoitiersParkingDTO.ParkingDetails parkingDTO) {
        return Parking.builder()
                .externalId(String.valueOf(parkingDTO.getId()))
                .source(getType())
                .location(toGeoJsonPoint(parkingDTO.getGeoPoint()))
                .availableSpots(parkingDTO.getAvailableSpots())
                .build();
    }
}