package hervalicious.coinstream.actors

import java.time.Instant
import java.util.Date

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.CircuitBreaker
import hervalicious.coinstream.{Book, FetchUpdate, OrderBookUpdate}
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.service.marketdata.MarketDataService

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by herval on 7/2/17.
  */
class FetchOrderBook(pair: CurrencyPair,
                     marketDataService: MarketDataService,
                     exchange: String,
                     bookListener: ActorRef) extends Actor with ActorLogging {

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
        val book = marketDataService.getOrderBook(pair)
        if (book.getTimeStamp != null && !latest.equals(book.getTimeStamp)) {
          latest = book.getTimeStamp
          log.info(book.toString)

          bookListener ! OrderBookUpdate(
            Book(
              pair.toString,
              book.getAsks.size(),
              book.getBids.size(),
              book.getTimeStamp,
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
