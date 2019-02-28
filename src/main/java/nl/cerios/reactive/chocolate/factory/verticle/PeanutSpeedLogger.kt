package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit.MILLISECONDS

class PeanutSpeedLogger : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun start() {
    vertx.eventBus()
        .consumer<JsonObject>("peanut.speed.set")
        .toObservable()
        .debounce(200, MILLISECONDS)
        .map { json -> Math.floor(json.body().getDouble("value") * 100.0) }
        .subscribe { percentage -> log.debug("Peanut production speed: $percentage%") }
  }
}