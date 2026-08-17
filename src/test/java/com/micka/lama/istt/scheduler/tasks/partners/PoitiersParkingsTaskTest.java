package com.micka.lama.istt.scheduler.tasks.partners;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.micka.lama.istt.clients.IPoitiersParkingClient;
import com.micka.lama.istt.commons.AbstractIT;
import com.micka.lama.istt.entities.Parking;
import com.micka.lama.istt.repositories.IParkingRepository;
import com.micka.lama.istt.scheduler.tasks.partners.configuration.SchedulerProperties;
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

import java.net.URI;
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
class PoitiersParkingsTaskTest extends AbstractIT {

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
     * Key of the property for the URL of the Poitiers partner.
     */
    private static final String POITIERS_PARKING_URL_PROPERTY = "app.scheduler.tasks.POITIERS_PARKING.url";

    /**
     * Scheduler properties.
     */
    @Autowired
    private SchedulerProperties properties;

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
     * Clean up.
     */
    @AfterEach
    void tearDown() {
        parkingRepository.deleteAll();
    }

    @DisplayName("Given a parking partner, " +
            "When the task fetches parkings from this partner, " +
            "Then it saves the data.")
    @Test
    void shouldReturnFakedResponseFromTheConfiguredPoitiersParkingUrl() {
        final String url = properties.getTasks().get(DataSourceType.POITIERS_PARKING).getUrl();
        assertThat(url).isNotBlank();

        WIRE_MOCK.stubFor(get(urlEqualTo(PARKINGS_PATH)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBodyFile("poitiers-parkings-response.json")));

        verify(poitiersParkingClientSpy, timeout(5_000).atLeast(1)).fetchParkings(URI.create(url));

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            final List<Parking> entities = parkingRepository.findAll();
            assertThat(entities).hasSize(7);

            // All the entities got an internal ID and are linked to the data source "POITIERS_PARKING".
            assertThat(entities).allMatch(entity -> entity.getId() != null);
            assertThat(entities).allMatch(entity -> entity.getSource() == DataSourceType.POITIERS_PARKING);

            // First parking entity.
            final Parking parking1 = entities.getFirst();
            assertThat(parking1.getExternalId()).isEqualTo("9");
            assertThat(parking1.getLocation()).isEqualTo(new GeoJsonPoint(0.3348348830917244, 46.58358353103216));
            assertThat(parking1.getAvailableSpots()).isEqualTo(458);

            // Second parking entity.
            final Parking parking2 = entities.get(1);
            assertThat(parking2.getExternalId()).isEqualTo("12");
            assertThat(parking2.getLocation()).isEqualTo(new GeoJsonPoint(0.3512954265806957, 46.58595804860371));
            assertThat(parking2.getAvailableSpots()).isEqualTo(164);

            // Third parking entity.
            final Parking parking3 = entities.get(2);
            assertThat(parking3.getExternalId()).isEqualTo("3");
            assertThat(parking3.getLocation()).isEqualTo(new GeoJsonPoint(0.33779491061805567, 46.58383455409422));
            assertThat(parking3.getAvailableSpots()).isEqualTo(111);

            // Fourth parking entity.
            final Parking parking4 = entities.get(3);
            assertThat(parking4.getExternalId()).isEqualTo("2");
            assertThat(parking4.getLocation()).isEqualTo(new GeoJsonPoint(0.3385507838016221, 46.5793235337795));
            assertThat(parking4.getAvailableSpots()).isEqualTo(366);

            // Fifth parking entity.
            final Parking parking5 = entities.get(4);
            assertThat(parking5.getExternalId()).isEqualTo("11");
            assertThat(parking5.getLocation()).isEqualTo(new GeoJsonPoint(0.3349825350533068, 46.583793004495156));
            assertThat(parking5.getAvailableSpots()).isEqualTo(71);

            // Sixth parking entity.
            final Parking parking6 = entities.get(5);
            assertThat(parking6.getExternalId()).isEqualTo("1");
            assertThat(parking6.getLocation()).isEqualTo(new GeoJsonPoint(0.3450022616476489, 46.58349874703973));
            assertThat(parking6.getAvailableSpots()).isEqualTo(85);

            // Seventh parking entity.
            final Parking parking7 = entities.get(6);
            assertThat(parking7.getExternalId()).isEqualTo("0");
            assertThat(parking7.getLocation()).isEqualTo(new GeoJsonPoint(0.337126307915689, 46.57505317559496));
            assertThat(parking7.getAvailableSpots()).isEqualTo(444);
        });
    }
}
