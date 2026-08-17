package com.micka.lama.istt.clients.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.micka.lama.istt.scheduler.tasks.partners.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * DTO retrieved from the source {@link DataSourceType#POITIERS_PARKING}.
 *
 * @see <a href="https://data.grandpoitiers.fr/datasets/mobilites-stationnement-des-parkings-en-temps-reel/api-doc?operation=readLines">API documentation</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoitiersParkingDTO {

    /**
     * Total result (ignoring the pagination).
     */
    private int total;

    /**
     * The results (contains the parking details).
     */
    private List<ParkingDetails> results;

    /**
     * The URL to the next page (by keeping the same request parameters).
     */
    private String next;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParkingDetails {

        /**
         * Identifier of the parking.
         */
        @JsonProperty("Id")
        private int id;

        /**
         * Name of the parking.
         */
        @JsonProperty("Nom")
        private String name;

        /**
         * Available parking spots.
         */
        @JsonProperty("Places")
        private int availableSpots;

        /**
         * Total number of parking spots for the parking.
         */
        @JsonProperty("Capacite")
        private int capacity;

        /**
         * Last time the data has been updated.
         */
        @JsonProperty("Dernière_mise_à_jour_Base")
        private Instant lastUpdateDate;

        /*
        TODO: handle _infos_parkings._error
         _infos_parkings._error:
          type: string
          x-originalName: _error
          x-extension: >-
            dataset:ylbhhddjbpgwvm03r86sy7ol/masterData_bulkSearch_infos-parkings
          title: Erreur de récupération de données de référence
          description: >-
            Une erreur lors de la récupération des informations depuis un
            service distant
          x-calculated: true
          readOnly: true
         */

        /**
         * Percentage of the occupied parking spots.
         * <p>
         * ({@link #capacity} - {@link #availableSpots}) / {@link #capacity}
         * <p>
         * Format: 28.0701754385965
         */
        @JsonProperty("taux_doccupation")
        private double occupancyRate;

        /**
         * Geo point [latitude ; longitude].
         * <p>
         * Example: "46.58595804860371, 0.3512954265806957"
         */
        @JsonProperty("infos_parkingsgeo_point")
        private String geoPointInfo;

        /**
         * Centroid, format "lat, lon".
         */
        @JsonProperty("_geopoint")
        private String geoPoint;

        /**
         * Last time the dataset has been updated.
         */
        @JsonProperty("_updatedAt")
        private Instant updatedAt;

        /**
         * Identifier (unique) of the dataset.
         */
        @JsonProperty("_id")
        private String idDataset;

        /**
         * Index of the line in the original file.
         */
        @JsonProperty("_i")
        private long index;

        /**
         * A random number linked to the index to retrieve a random number.
         */
        @JsonProperty("_rand")
        private long random;

    }
}
