package com.valapay.test

import com.valapay.test.codec.JsonCodec
import com.valapay.test.codec.JsonListCodec
import com.valapay.test.entity.Book
import com.valapay.test.eventbus.EntitiesCodecsRegistrator
import com.valapay.test.router.CrudRouter
import com.valapay.test.router.SearchRouter
import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HttpVerticle : AbstractVerticle() {

    @Throws(Exception::class)
    override fun start(startFuture: Future<Void>) {
        registerCodecs()

        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create().setUploadsDirectory(System.getProperty("java.io.tmpdir")))
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080) { result ->
                if (result.succeeded()) {
                    startFuture.complete()
                } else {
                    startFuture.fail(result.cause())
                }
            }

        val crudRouter = CrudRouter(vertx)
        router.mountSubRouter(crudRouter.getUrl(), crudRouter.createRouter())
        val searchRouter = SearchRouter(vertx)
        router.mountSubRouter(searchRouter.getUrl(), searchRouter.createRouter())
    }

    private fun registerCodecs() {

        EntitiesCodecsRegistrator.register(vertx)
        vertx.eventBus().registerDefaultCodec(EmptyMessage::class.java, JsonCodec(EmptyMessage::class.java))

        val v = ArrayList<String>()
        vertx.eventBus().registerDefaultCodec(v.javaClass, JsonListCodec())
    }



    private val handlerRoot = Handler<RoutingContext> { rc ->
        vertx.eventBus().send("my.addr", rc.request().getParam("id") ?: "") { resp: AsyncResult<Message<String>> ->
            if (resp.succeeded()) {
                rc.response().end(resp.result().body())
            } else {
                rc.fail(500)
            }
        }
    }


}