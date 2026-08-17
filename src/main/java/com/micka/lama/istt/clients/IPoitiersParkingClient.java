package com.micka.lama.istt.clients;

import com.micka.lama.istt.clients.dtos.PoitiersParkingDTO;
import com.micka.lama.istt.scheduler.tasks.partners.DataSourceType;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.net.URI;

/**
 * HTTP client for the source {@link DataSourceType#POITIERS_PARKING}.
 *
 * @see <a href="https://data.grandpoitiers.fr/datasets/mobilites-stationnement-des-parkings-en-temps-reel/api-doc?operation=readLines">API documentation</a>
 */
@HttpExchange
public interface IPoitiersParkingClient {

    /**
     * Retrieve the parkings data.
     *
     * @param uri The URI.
     * @return The data.
     */
    @GetExchange
    PoitiersParkingDTO fetchParkings(URI uri);
}
