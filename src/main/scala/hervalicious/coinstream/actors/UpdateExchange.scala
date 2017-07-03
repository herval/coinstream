package hervalicious.coinstream.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import hervalicious.coinstream.{FetchOrderBooks, FetchTickers, FetchUpdate}
import org.knowm.xchange.{ExchangeFactory, Exchange => ExchangeClass}


/**
  * Created by herval on 7/2/17.
  */
class UpdateExchange[T <: ExchangeClass](clazz: Class[T], listener: ActorRef) extends Actor with ActorLogging {

  import scala.collection.JavaConversions._

  val xchange = ExchangeFactory.INSTANCE.createExchange(clazz.getName)
  val marketDataService = xchange.getMarketDataService
  val symbols = xchange.getExchangeSymbols.toList

  val tickerFetchers = symbols.map { s =>
    context.actorOf(Props(
      new FetchTicker(s, marketDataService, clazz.getSimpleName, listener)
    ))
  }

  val orderBookFetchers = symbols.map { s =>
    context.actorOf(Props(
      new FetchOrderBook(s, marketDataService, clazz.getSimpleName, listener)
    ))
  }

  override def receive: Receive = {
    case FetchTickers => tickerFetchers.foreach { actor =>
      actor ! FetchUpdate
    }

    case FetchOrderBooks => {
      orderBookFetchers.foreach { actor =>
        actor ! FetchUpdate
      }
    }
  }
}
