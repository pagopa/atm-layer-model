package it.gov.pagopa.atmlayer.service.model.configurations;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    @ConfigProperty(name = "app.allowed.origins")
    String allowedOrigins;
    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext cres) throws IOException {
        cres.getHeaders().add("Access-Control-Allow-Origin","http://localhost:3005");
        cres.getHeaders().add("Access-Control-Allow-Headers", "Content-Type,Allowed,x-api-key");
        cres.getHeaders().add("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        cres.getHeaders().add("Access-Control-Allow-Credentials","true");
        cres.getHeaders().add("Content-Type","application/json");
    }

}
