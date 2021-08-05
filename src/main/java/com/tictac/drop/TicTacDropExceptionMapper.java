package com.tictac.drop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 *
 */
public class TicTacDropExceptionMapper implements ExceptionMapper<RuntimeException>  {
    private static final Logger logger = LoggerFactory.getLogger(TicTacDropExceptionMapper.class);
    public Response toResponse(RuntimeException e) {
        logger.error("Unhandled exception.", e);
        return Response.status(500).build();
    }
}
