package org.musaabshirgar1

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * A simple resource showing the last price.
 */
@Path("/prices")
class PriceResource {
    @Inject
    var consumer: PriceConsumer? = null
    @GET
    @Path("last")
    @Produces(MediaType.TEXT_PLAIN)
    fun last(): String? {
        return consumer?.lastPrice
    }
}