package com.micka.lama.istt.scheduler.tasks.partners;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.micka.lama.istt.clients.IPoitiersParkingClient;
import com.micka.lama.istt.commons.AbstractIT;
import com.micka.lama.istt.entities.Parking;
import com.micka.lama.istt.repositories.IParkingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * Tests dedicated to the partner Poitiers.
 * <p>
 * Retrieve the data from this partner.
 */
@ActiveProfiles({"poitiers-partner"})
class PoitiersParkingsTaskPaginationTest extends AbstractIT {

    /**
     * Wiremock setup.
     */
    @RegisterExtension
    static final WireMockExtension WIRE_MOCK = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    /**
     * Wiremock endpoint to retrieve the data from the partner.
     */
    private static final String PARKINGS_PATH = "/parkings-mock";

    /**
     * Wiremock endpoint to retrieve the data from the partner, given there is a new page of results.
     */
    private static final String NEXT_PAGE_PATH = "/parkings-mock/next";

    /**
     * Key of the property for the URL of the Poitiers partner.
     */
    private static final String POITIERS_PARKING_URL_PROPERTY = "app.scheduler.tasks.POITIERS_PARKING.url";

    /**
     * File containing the first page of results.
     */
    private static final String FIRST_PAGE_FILE = "poitiers-parkings-response-first-page.json";

    /**
     * File containing the last page of results.
     */
    private static final String LAST_PAGE_FILE = "poitiers-parkings-response-last-page.json";

    /**
     * URL contained in the first page. To be replaced when using Wiremock.
     */
    private static final String NEXT_URL = "https://data.grandpoitiers.fr/data-fair/api/v1/datasets/5irzo0y4owy4oi5zg88luy4z/lines?size=6&after=1786802634810%2C1856762941056948";

    /**
     * HTTP client spy bean for the Poitiers partner.
     */
    @MockitoSpyBean
    private IPoitiersParkingClient poitiersParkingClientSpy;

    /**
     * Parking repository.
     */
    @Autowired
    private IParkingRepository parkingRepository;

    /**
     * Update the properties of the Spring application.
     *
     * @param registry Property registry.
     */
    @DynamicPropertySource
    static void configurePoitiersParkingUrl(final DynamicPropertyRegistry registry) {
        registry.add(POITIERS_PARKING_URL_PROPERTY, () -> WIRE_MOCK.baseUrl() + PARKINGS_PATH);
    }

    /**
     * Read a file (for Wiremock usage, prefixed by <code>__file/</code>).
     *
     * @param fileName The filename.
     * @return The content of the file.
     * @throws IOException Error while trying to read the given file.
     */
    private static String readBodyFile(final String fileName) throws IOException {
        try (final InputStream inputStream = PoitiersParkingsTaskPaginationTest.class.getClassLoader().getResourceAsStream("__files/" + fileName)) {
            assertThat(inputStream).as("Body file %s must exist on the classpath", fileName).isNotNull();
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Clean up.
     */
    @AfterEach
    void tearDown() {
        parkingRepository.deleteAll();
    }

    @DisplayName("Given the task to retrieve the parkings data, " +
            "When the response contains another page (for more data), " +
            "Then it inserts all parkings from every page.")
    @Test
    void shouldFollowTheNextUrlAndInsertAllParkingsFromEveryPage() throws IOException {
        final String firstPageBody = readBodyFile(FIRST_PAGE_FILE);
        WIRE_MOCK.stubFor(get(urlEqualTo(PARKINGS_PATH)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(firstPageBody.replace(NEXT_URL, WIRE_MOCK.baseUrl() + NEXT_PAGE_PATH))));

        WIRE_MOCK.stubFor(get(urlEqualTo(NEXT_PAGE_PATH)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBodyFile(LAST_PAGE_FILE)));

        verify(poitiersParkingClientSpy, timeout(5_000).atLeast(1)).fetchParkings(URI.create(WIRE_MOCK.baseUrl() + PARKINGS_PATH));
        verify(poitiersParkingClientSpy, timeout(5_000).atLeast(1)).fetchParkings(URI.create(WIRE_MOCK.baseUrl() + NEXT_PAGE_PATH));


        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            final List<Parking> entities = parkingRepository.findAll();
            assertThat(entities).hasSize(7);

            // All the entities got an internal ID and are linked to the data source "POITIERS_PARKING".
            assertThat(entities).allMatch(entity -> entity.getId() != null);
            assertThat(entities).allMatch(entity -> entity.getSource() == DataSourceType.POITIERS_PARKING);

            // First parking entity.
            final Parking parking1 = entities.getFirst();
            assertThat(parking1.getExternalId()).isEqualTo("2");
            assertThat(parking1.getLocation()).isEqualTo(new GeoJsonPoint(0.3385507838016221, 46.5793235337795));
            assertThat(parking1.getAvailableSpots()).isEqualTo(388);

            // Second parking entity.
            final Parking parking2 = entities.get(1);
            assertThat(parking2.getExternalId()).isEqualTo("3");
            assertThat(parking2.getLocation()).isEqualTo(new GeoJsonPoint(0.33779491061805567, 46.58383455409422));
            assertThat(parking2.getAvailableSpots()).isEqualTo(143);

            // Third parking entity.
            final Parking parking3 = entities.get(2);
            assertThat(parking3.getExternalId()).isEqualTo("9");
            assertThat(parking3.getLocation()).isEqualTo(new GeoJsonPoint(0.3348348830917244, 46.58358353103216));
            assertThat(parking3.getAvailableSpots()).isEqualTo(459);

            // Fourth parking entity.
            final Parking parking4 = entities.get(3);
            assertThat(parking4.getExternalId()).isEqualTo("12");
            assertThat(parking4.getLocation()).isEqualTo(new GeoJsonPoint(0.3512954265806957, 46.58595804860371));
            assertThat(parking4.getAvailableSpots()).isEqualTo(218);

            // Fifth parking entity.
            final Parking parking5 = entities.get(4);
            assertThat(parking5.getExternalId()).isEqualTo("1");
            assertThat(parking5.getLocation()).isEqualTo(new GeoJsonPoint(0.3450022616476489, 46.58349874703973));
            assertThat(parking5.getAvailableSpots()).isEqualTo(76);

            // Sixth parking entity.
            final Parking parking6 = entities.get(5);
            assertThat(parking6.getExternalId()).isEqualTo("0");
            assertThat(parking6.getLocation()).isEqualTo(new GeoJsonPoint(0.337126307915689, 46.57505317559496));
            assertThat(parking6.getAvailableSpots()).isEqualTo(481);

            // Seventh parking entity.
            final Parking parking7 = entities.get(6);
            assertThat(parking7.getExternalId()).isEqualTo("11");
            assertThat(parking7.getLocation()).isEqualTo(new GeoJsonPoint(0.3349825350533068, 46.583793004495156));
            assertThat(parking7.getAvailableSpots()).isEqualTo(61);
        });
    }
}
