package com.micka.lama.istt.controllers;

import com.micka.lama.istt.commons.AbstractIT;
import com.micka.lama.istt.dtos.ParkingDTO;
import com.micka.lama.istt.entities.Parking;
import com.micka.lama.istt.repositories.IParkingRepository;
import com.micka.lama.istt.scheduler.tasks.partners.DataSourceType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.micka.lama.istt.definitions.ParkingControllerApiPath.NEAREST_PATH;
import static com.micka.lama.istt.definitions.ParkingControllerApiPath.PARKING_CONTROLLER_ROOT_PATH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Parking controller test.
 */
class ParkingControllerTest extends AbstractIT {

    /**
     * Type for a list of {@link ParkingDTO}.
     */
    private static final ParameterizedTypeReference<List<ParkingDTO>> PARKING_DTO_LIST = new ParameterizedTypeReference<>() {
    };

    /**
     * Rest template.
     */
    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Parking repository.
     */
    @Autowired
    private IParkingRepository parkingRepository;

    /**
     * Initialize parkings objects.
     *
     * @return Created parkings (not saved in the database).
     * @see <a href="https://www.opentimeclock.com/latitude-longitude-gps-distance-calculator.html">Compute the distance between two points</a>
     */
    private static List<Parking> initParkings() {
        return List.of(Parking.builder()
                        .externalId("id-1")
                        .source(DataSourceType.POITIERS_PARKING)
                        .location(new GeoJsonPoint(0, 0))
                        .availableSpots(100)
                        .build(),
                Parking.builder()
                        .externalId("id-2")
                        .source(DataSourceType.POITIERS_PARKING)
                        // Around 157m from [0;0]
                        .location(new GeoJsonPoint(0.001, -0.001))
                        .availableSpots(123)
                        .build(),
                Parking.builder()
                        .externalId("id-3")
                        .source(DataSourceType.POITIERS_PARKING)
                        // Around 47m from [0;0]
                        .location(new GeoJsonPoint(0.0003, -0.0003))
                        .availableSpots(99)
                        .build(),
                Parking.builder()
                        .externalId("id-4")
                        .source(DataSourceType.POITIERS_PARKING)
                        // Around 15m from [0;0]
                        .location(new GeoJsonPoint(0.0001, -0.0001))
                        .availableSpots(99)
                        .build());
    }

    /**
     * Build the URI to retrieve the nearest parkings.
     *
     * @param longitude   Longitude parameter.
     * @param latitude    Latitude parameter.
     * @param minDistance Minimum distance parameter.
     * @param maxDistance Maximum distance parameter.
     * @param limit       Limit parameter.
     * @return The built URI.
     */
    private static String getParkingsBuildURI(final Double longitude, final Double latitude, final Double minDistance,
                                              final Double maxDistance, final Integer limit) {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(PARKING_CONTROLLER_ROOT_PATH + NEAREST_PATH);

        if (longitude != null) {
            uriBuilder.queryParam("longitude", longitude);
        }
        if (latitude != null) {
            uriBuilder.queryParam("latitude", latitude);
        }
        if (minDistance != null) {
            uriBuilder.queryParam("minDistance", minDistance);
        }
        if (maxDistance != null) {
            uriBuilder.queryParam("maxDistance", maxDistance);
        }
        if (limit != null) {
            uriBuilder.queryParam("limit", limit);
        }

        return uriBuilder.toUriString();
    }

    /**
     * Clean up.
     */
    @AfterEach
    void tearDown() {
        parkingRepository.deleteAll();
    }

    /**
     * Retrieve the nearest parkings.
     *
     * @param longitude   Longitude of the user.
     * @param latitude    Latitude of the user.
     * @param minDistance Minimum distance.
     * @param maxDistance Maximum distance.
     * @param limit       Limit.
     * @return Response entity containing a list of parkings.
     */
    private ResponseEntity<List<ParkingDTO>> getParkings(final Double longitude, final Double latitude,
                                                         final Double minDistance, final Double maxDistance,
                                                         final Integer limit) {
        return restTemplate.exchange(getParkingsBuildURI(longitude, latitude, minDistance, maxDistance, limit),
                HttpMethod.GET, HttpEntity.EMPTY, PARKING_DTO_LIST);
    }

    /**
     * Retrieve the nearest parkings.
     *
     * @param longitude   Longitude of the user.
     * @param latitude    Latitude of the user.
     * @param minDistance Minimum distance.
     * @param maxDistance Maximum distance.
     * @param limit       Limit.
     * @return Response entity.
     */
    private ResponseEntity<String> getParkingsString(final Double longitude, final Double latitude,
                                                     final Double minDistance, final Double maxDistance,
                                                     final Integer limit) {
        return restTemplate.exchange(getParkingsBuildURI(longitude, latitude, minDistance, maxDistance, limit),
                HttpMethod.GET, HttpEntity.EMPTY, String.class);
    }

    @DisplayName("Given a user and there is 0 registered parking, " +
            "When he asks to retrieve the nearest parkings, " +
            "Then the response is empty.")
    @Test
    void testGetParkingsEmpty() {
        final ResponseEntity<List<ParkingDTO>> response = getParkings(0d, 0d, null, null, null);

        assertThat(response.getStatusCode()).isSameAs(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @ParameterizedTest(name = "Given a user and there is 0 registered parking, " +
            "When he asks to retrieve the nearest parkings by using the parameters [longitude: {0}, latitude: {1}, minDistance: {2} maxDistance: {3}, limit: {4}, " +
            "Then the response code is {5}.")
    @CsvSource(value = {
            // Longitude, latitude, minDistance, maxDistance, limit, expected HTTP status code

            // The longitude cannot be under -180.
            "-180   | 0     | null | null | null | OK",
            "-180.1 | 0     | null | null | null | BAD_REQUEST",

            // The longitude cannot be higher than 180.
            "180    | 0     | null | null | null | OK",
            "180.1  | 0     | null | null | null | BAD_REQUEST",

            // The latitude cannot be under -90.
            "0      | -90   | null | null | null | OK",
            "0      | -90.1 | null | null | null | BAD_REQUEST",

            // The latitude cannot be higher than 90.
            "0      | 90    | null | null | null | OK",
            "0      | 90.1  | null | null | null | BAD_REQUEST",

            // The minDistance cannot be under 0.
            "0      | 0     | 0    | null | null | OK",
            "0      | 0     | -0.1 | null | null | BAD_REQUEST",

            // The maxDistance canont be under 0.
            "0      | 0     | null | 0    | null | OK",
            "0      | 0     | null | -0.1 | null | BAD_REQUEST",

            // The limit cannot be under 1.
            "0      | 0     | null | null | 1    | OK",
            "0      | 0     | null | null | 0    | BAD_REQUEST",

            // The limit cannot be higher than 50.
            "0      | 0     | null | null | 50   | OK",
            "0      | 0     | null | null | 51   | BAD_REQUEST",

            // Longitude cannot be null.
            "null   | 0     | null | null | null | BAD_REQUEST",

            // Latitude cannot be null.
            "0      | null  | null | null | null | BAD_REQUEST"
    }, nullValues = "null", delimiter = '|')
    void testGetParkingsEmptyWithParametersValidation(final Double longitude, final Double latitude, final Double minDistance,
                                                      final Double maxDistance, final Integer limit, final HttpStatus expectedStatus) {
        final ResponseEntity<String> response = getParkingsString(longitude, latitude, minDistance, maxDistance, limit);
        assertThat(response.getStatusCode()).isSameAs(expectedStatus);
    }

    @DisplayName("Given a user and parkings close to him, " +
            "When he requests with the default parameters (only longitude and latitude), " +
            "Then he gets all the nearest parkings.")
    @Test
    void testGetParkings() {
        final List<Parking> entities = parkingRepository.saveAll(initParkings());
        final ResponseEntity<List<ParkingDTO>> response = getParkings(0d, 0d, null, null, null);
        assertThat(response.getStatusCode()).isSameAs(HttpStatus.OK);

        final List<ParkingDTO> responseBody = response.getBody();

        assertThat(responseBody)
                .hasSize(entities.size())
                .satisfiesOnlyOnce(dto -> {
                    assertThat(dto.getId()).isEqualTo(entities.getFirst().getId().toString());
                    assertThat(dto.getExternalId()).isEqualTo(entities.getFirst().getExternalId());
                    assertThat(dto.getLatitude()).isEqualTo(entities.getFirst().getLocation().getY());
                    assertThat(dto.getLongitude()).isEqualTo(entities.getFirst().getLocation().getX());
                    assertThat(dto.getAvailableSpots()).isEqualTo(entities.getFirst().getAvailableSpots());
                })
                .satisfiesOnlyOnce(dto -> {
                    assertThat(dto.getId()).isEqualTo(entities.get(1).getId().toString());
                    assertThat(dto.getExternalId()).isEqualTo(entities.get(1).getExternalId());
                    assertThat(dto.getLatitude()).isEqualTo(entities.get(1).getLocation().getY());
                    assertThat(dto.getLongitude()).isEqualTo(entities.get(1).getLocation().getX());
                    assertThat(dto.getAvailableSpots()).isEqualTo(entities.get(1).getAvailableSpots());
                })
                .satisfiesOnlyOnce(dto -> {
                    assertThat(dto.getId()).isEqualTo(entities.get(2).getId().toString());
                    assertThat(dto.getExternalId()).isEqualTo(entities.get(2).getExternalId());
                    assertThat(dto.getLatitude()).isEqualTo(entities.get(2).getLocation().getY());
                    assertThat(dto.getLongitude()).isEqualTo(entities.get(2).getLocation().getX());
                    assertThat(dto.getAvailableSpots()).isEqualTo(entities.get(2).getAvailableSpots());
                })
                .satisfiesOnlyOnce(dto -> {
                    assertThat(dto.getId()).isEqualTo(entities.get(3).getId().toString());
                    assertThat(dto.getExternalId()).isEqualTo(entities.get(3).getExternalId());
                    assertThat(dto.getLatitude()).isEqualTo(entities.get(3).getLocation().getY());
                    assertThat(dto.getLongitude()).isEqualTo(entities.get(3).getLocation().getX());
                    assertThat(dto.getAvailableSpots()).isEqualTo(entities.get(3).getAvailableSpots());
                });
    }

    @DisplayName("Given a user at [0;0] and parkings close to him, " +
            "When he requests the nearest parkings, " +
            "Then the result is sorted by the nearest parkings.")
    @Test
    void testGetNearestParkingsDistance() {
        final List<Parking> entities = parkingRepository.saveAll(initParkings());
        final ResponseEntity<List<ParkingDTO>> response = getParkings(0d, 0d, null, null, null);
        assertThat(response.getStatusCode()).isSameAs(HttpStatus.OK);

        final List<ParkingDTO> responseBody = response.getBody();

        // Check that the result is sorted by the nearest parkings.
        assertThat(responseBody).map(ParkingDTO::getId).containsExactly(
                entities.get(0).getId().toString(),
                entities.get(3).getId().toString(),
                entities.get(2).getId().toString(),
                entities.get(1).getId().toString()
        );

        assertThat(responseBody).map(ParkingDTO::getDistance)
                .isSorted()
                // We already asserted that with decimals, the result is sorted.
                // Simplify the assertion by not dealing with the decimals.
                .map(Double::intValue)
                .containsExactly(0, 15, 47, 157);
    }

    @DisplayName("Given a user and parkings at 0, 15, 47 and 157m, " +
            "When he requests the parkings from 20 to 150m, " +
            "Then he retrieves the 2 nearest parkings.")
    @Test
    void testGetNearestParkingsMinMaxDistance() {
        final List<Parking> entities = parkingRepository.saveAll(initParkings());
        final ResponseEntity<List<ParkingDTO>> response = getParkings(0d, 0d, 20.0, 150.0, 10);
        assertThat(response.getStatusCode()).isSameAs(HttpStatus.OK);

        final List<ParkingDTO> responseBody = response.getBody();

        // Check that the result is sorted by the nearest parkings.
        assertThat(responseBody).map(ParkingDTO::getId).containsExactly(
                entities.get(2).getId().toString()
        );

        assertThat(responseBody).map(ParkingDTO::getDistance)
                // Simplify the assertion by not dealing with the decimals.
                .map(Double::intValue)
                .containsExactly(47);
    }

    @DisplayName("Given a user and parkings, " +
            "When he requests the nearest parking (limit: 1), " +
            "Then he retrieves the nearest parking.")
    @Test
    void testGetNearestParkingsLimit() {
        final List<Parking> entities = parkingRepository.saveAll(initParkings());
        final ResponseEntity<List<ParkingDTO>> response = getParkings(0d, 0d, 0.0, 150.0, 1);
        assertThat(response.getStatusCode()).isSameAs(HttpStatus.OK);

        final List<ParkingDTO> responseBody = response.getBody();

        // Check that the result is sorted by the nearest parkings.
        assertThat(responseBody).map(ParkingDTO::getId).containsExactly(
                entities.getFirst().getId().toString()
        );

        assertThat(responseBody).map(ParkingDTO::getDistance)
                // Simplify the assertion by not dealing with the decimals.
                .map(Double::intValue)
                .containsExactly(0);
    }
}
