package hervalicious.coinstream

import akka.actor.{ActorSystem, Props}
import akka.routing.RoundRobinPool
import hervalicious.coinstream.actors.{SinkToDatabase, UpdateExchange}
import org.knowm.xchange.bitfinex.v1.BitfinexExchange
import org.knowm.xchange.bittrex.v1.BittrexExchange

import scala.concurrent.duration._

/**
  * Created by herval on 7/2/17.
  */
object Monitor {

  // https://github.com/timmolter/XChange
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("mySystem")
    import system.dispatcher

    val db = system.actorOf(
      Props(new SinkToDatabase()).withRouter(
        new RoundRobinPool(5)
      )
    )

    val streamers = Seq(
//      classOf[CoinbaseExchange],
//      classOf[BitstampExchange],
      classOf[BittrexExchange],
      classOf[BitfinexExchange]
//      classOf[MercadoBitcoinExchange]
    ).map { exchange =>
      system.actorOf(Props(new UpdateExchange(exchange, db)))
    }

    streamers.foreach { s =>
      system.scheduler.schedule(10 milliseconds, 10 milliseconds) {
        s ! FetchTickers
      }
      system.scheduler.schedule(1 second, 2 seconds) {
        s ! FetchOrderBooks
      }
    }
  }
}
