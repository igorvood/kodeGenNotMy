package com.valapay.test

import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class VerticlesDeployer {

    @Autowired
    private lateinit var vertx: Vertx

    @Autowired
    private lateinit var context: ApplicationContext

    @PostConstruct
    fun init() {
        val verticles = context.getBeansOfType(CoroutineVerticle::class.java).values
        verticles.forEach { deploy(vertx, it) }
    }

    private fun deploy(vertx: Vertx, verticle: CoroutineVerticle) {
        vertx.deployVerticle(verticle) { res ->
            if (res.succeeded()) {
                println("Deployment id is: " + res.result())
            } else {
                println("Deployment failed!" + res.cause())
            }
        }
    }
}
