package hervalicious.coinstream.actors

import akka.actor.Actor
import hervalicious.coinstream.{Book, OrderBookUpdate, Ticker, TickerUpdate}
import io.getquill.{PostgresJdbcContext, SnakeCase}

/**
  * Created by herval on 7/2/17.
  */
class SinkToDatabase extends Actor {
  lazy val ctx = new PostgresJdbcContext[SnakeCase]("ctx")

  import ctx._

  private def save(t: Ticker) = {
    ctx.run(quote(
      query.insert(lift(t))
    ))
  }

  private def save(t: Book) = {
    ctx.run(quote(
      query.insert(lift(t))
    ))
  }

  override def receive: Receive = {
    case TickerUpdate(ticker) => save(ticker)
    case OrderBookUpdate(book) => save(book)
  }
}
