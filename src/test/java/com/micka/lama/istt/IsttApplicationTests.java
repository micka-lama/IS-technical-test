package com.micka.lama.istt;

import com.micka.lama.istt.commons.AbstractIT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ISTT application test.
 */
class IsttApplicationTests extends AbstractIT {

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    @DisplayName("Verify that the Spring context is correctly initialized")
    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

}
