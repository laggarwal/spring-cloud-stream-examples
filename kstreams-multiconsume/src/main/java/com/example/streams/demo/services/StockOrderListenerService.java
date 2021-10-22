package com.example.streams.demo.services;

import com.example.streams.demo.model.StockAverage;
import com.example.streams.demo.model.StockOrder;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Consumer;

@Service
public class StockOrderListenerService {

    Logger logger = LoggerFactory.getLogger(StockOrderListenerService.class);


    /*
      // Example with Multiple aggregations/compute on same input stream
      // 1. Takes Stock Orders and validates if a customer has more than 5 orders for a given stock in 5 minutes window
      // if yes, generates an alert
      //
      // 2. Computes an Average price for the stock orders
    */
    @Bean
    public Consumer<KStream<String, StockOrder>> consumeStockOrdersMulti() {

        /*
           High level steps
           1. rekey events by customerId
           2. groupBy Customer Id
           3. windowBy 1 mins
           4. compute count of orders
           5. filter events greater than x
           6. publish alert event
         */


        return input -> {
            input.peek((k, v) -> logger.info("validateStockOrders Received Stock Order = " + k + " Value = " + v + " Time"))
                    .groupBy(
                            (k, v) -> v.getCustomerId()
                    )
                    .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
                    .count()
                    .filter((k, v) -> v >= 5)
                    .toStream()
                    .foreach((k, v) -> logger.info("Alert Stock Exceeded Threshold for Key = " + k + " Value = " + v));


            // Compute Average for Stock Orders
            input.peek((k,v) -> logger.info("processStockOrdersAvg Received Stock Order = " + k + " Value = " + v + " Time" ))
                    .groupByKey()
                    .aggregate(new Initializer<StockAverage>() {
                                   @Override
                                   public StockAverage apply() {
                                       return new StockAverage("", 0.0d, 0);
                                   }
                               },
                            new Aggregator<String, StockOrder, StockAverage>() {
                                @Override
                                public StockAverage apply(String key, StockOrder value, StockAverage aggregate) {
                                    double averagePrice = (value.getStockPrice() + aggregate.getAvgPrice()) / (value.getStockQty() + aggregate.getTotalQty());
                                    aggregate.setStockSymbol(key);
                                    aggregate.setAvgPrice(averagePrice);
                                    aggregate.setTotalQty(value.getStockQty() + aggregate.getTotalQty());
                                    return aggregate;
                                }
                            },
                            Materialized.as("stock-avg-state-store")
                    )
                    .toStream(Named.as("dhq-ks-stockorder-avg"))
                    .foreach((k, v) -> logger.info("processStockOrdersAvg Key = " + k + " Stock Average Value = " + v));

        };

    }


}
