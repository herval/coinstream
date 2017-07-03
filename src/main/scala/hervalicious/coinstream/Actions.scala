package hervalicious.coinstream

import java.util.Date

/**
  * Created by herval on 7/2/17.
  */
sealed trait Action

case object FetchTickers
case object FetchOrderBooks
case object FetchUpdate
case class TickerUpdate(t: Ticker)
case class OrderBookUpdate(b: Book)

case class Book(pair: String,
                asks: Long,
                bids: Long,
                ts: Date,
                exchange: String)

case class Ticker(pair: String,
                  last: BigDecimal,
                  bid: BigDecimal,
                  ask: BigDecimal,
                  high: BigDecimal,
                  low: BigDecimal,
                  volume: BigDecimal,
                  ts: Date,
                  exchange: String)
