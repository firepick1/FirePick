package com.firepick.webpnp.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Date;

@Path("/helloworld")
public class HelloResource {

    @GET
    @Produces("text/plain")
    public String getClichedMessage() {
        Date date = new Date();
        return "Hello World" + date;
    }
}