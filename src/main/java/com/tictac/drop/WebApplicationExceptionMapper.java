package com.tictac.drop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

    @Override
    public Response toResponse(WebApplicationException e) {
        LOGGER.error("Handled expected scenarios", e);
        return Response.status(e.getResponse().getStatus()).entity(e.getMessage()).build();
    }
}