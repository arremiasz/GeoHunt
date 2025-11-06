package com.geohunt.backend.WebSockets;

import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CustomSpringConfigurator extends ServerEndpointConfig.Configurator {
    private static volatile ApplicationContext context;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        if (context == null) {
            throw new IllegalStateException("Spring ApplicationContext is not set yet.");
        }
        return context.getBean(endpointClass);
    }
}
