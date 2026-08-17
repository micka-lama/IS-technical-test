package com.micka.lama.istt.configurations;

import com.micka.lama.istt.clients.IPoitiersParkingClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * REST client configuration.
 */
@Configuration
public class RestClientConfiguration {

    /**
     * Create the HTTP client for the Poitiers partner.
     *
     * @param factory Factory to create client.
     * @return Client bean.
     */
    @Bean
    public IPoitiersParkingClient poitiersParkingClient(final HttpServiceProxyFactory factory) {
        return factory.createClient(IPoitiersParkingClient.class);
    }

    /**
     * Factory to build the HTTP clients.
     *
     * @param clientBuilder Builder.
     * @return Factory bean.
     */
    @Bean
    public HttpServiceProxyFactory proxyFactory(final RestClient.Builder clientBuilder) {
        final RestClient client = clientBuilder.build();
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build();
    }
}
