package hervalicious.coinstream.actors

import java.time.Instant
import java.util.Date

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.CircuitBreaker
import hervalicious.coinstream.{FetchUpdate, Ticker, TickerUpdate}
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.service.marketdata.MarketDataService

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by herval on 7/2/17.
  */
class FetchTicker(pair: CurrencyPair,
                  marketDataService: MarketDataService,
                  exchange: String,
                  tickerListener: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher

  var latest: Date = Date.from(Instant.now())

  val breaker = new CircuitBreaker(
    context.system.scheduler,
    maxFailures = 5,
    callTimeout = 10 seconds,
    resetTimeout = 1 minute).onOpen(notifyMeOnOpen())

  override def receive: Receive = {
    case FetchUpdate => {
      breaker.withCircuitBreaker(Future {
        val ticker = marketDataService.getTicker(pair)
        val timestamp = ticker.getTimestamp
        if (ticker.getTimestamp != null && !latest.equals(timestamp)) {
          latest = ticker.getTimestamp
          log.info(ticker.toString)

          tickerListener ! TickerUpdate(
            Ticker(
              ticker.getCurrencyPair.toString,
              ticker.getLast,
              ticker.getBid,
              ticker.getAsk,
              ticker.getHigh,
              ticker.getLow,
              ticker.getVolume,
              timestamp,
              exchange
            )
          )
        }
      })
    }
  }

  def notifyMeOnOpen() = {
    log.warning(s"Open circuit on ${exchange} / ${pair}")
  }

}
