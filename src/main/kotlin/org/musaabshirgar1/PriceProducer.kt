package org.musaabshirgar1

import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.inject.Inject
import javax.jms.ConnectionFactory
import javax.jms.Session

/**
 * A bean producing random prices every 5 seconds and sending them to the prices JMS queue.
 */
@ApplicationScoped
class PriceProducer : Runnable {
    @Inject
    var connectionFactory: ConnectionFactory? = null
    private val random = Random()
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    fun onStart(@Observes ev: StartupEvent?) {
        scheduler.scheduleWithFixedDelay(this, 0L, 5L, TimeUnit.SECONDS)
    }

    fun onStop(@Observes ev: ShutdownEvent?) {
        scheduler.shutdown()
    }

    override fun run() {
        connectionFactory?.createContext(Session.AUTO_ACKNOWLEDGE).use { context ->
            context?.createProducer()?.send(
                    context.createQueue("prices"),
                    random.nextInt(100).toString()
            )
        }
    }
}