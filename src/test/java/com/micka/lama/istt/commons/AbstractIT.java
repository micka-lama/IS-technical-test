package com.micka.lama.istt.commons;

import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.mongodb.MongoDBContainer;

/**
 * Abstraction to configure integration tests.
 */
@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"it"})
public abstract class AbstractIT {

    /**
     * MongoDB container.
     */
    @ServiceConnection
    static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer("mongo:8.3.8");

    static {
        MONGO_DB_CONTAINER.start();
    }
}
