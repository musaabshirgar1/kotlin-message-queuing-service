package org.musaabshirgar1

import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import java.util.concurrent.Executors
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.inject.Inject
import javax.jms.ConnectionFactory
import javax.jms.JMSException
import javax.jms.Session

/**
 * A bean consuming prices from the JMS queue.
 */
@ApplicationScoped
class PriceConsumer : Runnable {
    @Inject
    var connectionFactory: ConnectionFactory? = null
    private val scheduler = Executors.newSingleThreadExecutor()

    @Volatile
    var lastPrice: String? = null

    fun onStart(@Observes ev: StartupEvent?) {
        scheduler.submit(this)
    }

    fun onStop(@Observes ev: ShutdownEvent?) {
        scheduler.shutdown()
    }

    override fun run() {
        try {
            connectionFactory?.createContext(Session.AUTO_ACKNOWLEDGE).use { context ->
                val consumer = context?.createConsumer(context.createQueue("prices"))
                while (true) {
                    val message = consumer
                            ?.receive() ?: return // receive returns `null` if the JMSConsumer is closed
                    lastPrice = message.getBody(String::class.java)
                }
            }
        } catch (e: JMSException) {
            throw RuntimeException(e)
        }
    }
}