package it.gov.pagopa.atmlayer.service.model.configurations;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext cres) throws IOException {
        cres.getHeaders().add("Access-Control-Allow-Origin","*");
        cres.getHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        cres.getHeaders().add("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
    }

}
