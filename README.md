# Coinstream

This project monitors trading volume of different pairs of cryptocoins on multiple exchanges.

## DB schema

```
createdb coins
psql coins

create table ticker(
    pair text,
    last numeric(100, 20),
    bid numeric(100, 20),
    ask numeric(100, 20),
    high numeric(100, 20),
    low numeric(100, 20),
    volume numeric(100, 20),
    ts timestamp with time zone,
    exchange text
)

create table book(
    pair text,
    asks bigint,
    bids bigint,
    ts timestamp with time zone,
    exchange text
)
```

## Running

```
sbt assembly
java -jar coinstream.jar
```
