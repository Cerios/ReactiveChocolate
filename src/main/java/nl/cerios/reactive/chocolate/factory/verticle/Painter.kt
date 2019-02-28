package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import nl.cerios.reactive.chocolate.factory.model.ChocoNut
import nl.cerios.reactive.chocolate.factory.model.ColorNut
import nl.cerios.reactive.chocolate.factory.timesHalfToTwo
import org.slf4j.LoggerFactory
import java.security.SecureRandom
import java.util.concurrent.TimeUnit.MILLISECONDS

enum class Color { GREEN, YELLOW, BLUE, RED, ORANGE }

class Painter : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)
  private val processingTime = 1000L

  override fun start() {
    vertx.eventBus()
        .consumer<JsonObject>("chocoNut")
        .toObservable()
        .map { json -> ChocoNut.fromJson(json.body()) }
        .delay(processingTime.timesHalfToTwo(), MILLISECONDS)
        .map { chocoNut -> ColorNut(chocoNut, chooseColor()).toJson() }
        .subscribe(
            { chocoNutJson -> vertx.eventBus().publish("colorNut", chocoNutJson) },
            { throwable -> log.error("Error painting chocoNuts.", throwable) })
  }

  private fun chooseColor() = Color.values()[SecureRandom().nextInt(Color.values().size)]
}