package com.example.streams.demo.services;

import com.example.streams.demo.model.StockOrder;
import com.example.streams.demo.model.StockOrdersActivityAlert;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class StockOrderListenerService {

    Logger logger = LoggerFactory.getLogger(StockOrderListenerService.class);


    /*
      // Example for function input stream, output stream
      // 1. Takes Stock Orders and validates if a customer has more than 5 orders for a given stock in 5 minutes window
      // if yes, generates an alert
      //
      //
    */
    @Bean
    public Function<KStream<String, StockOrder>, KStream<String, StockOrdersActivityAlert>> monitorStockOrdersActivity() {

        /*
           High level steps
           1. rekey events by customerId
           2. groupBy Customer Id
           3. windowBy x mins
           4. compute count of orders
           5. filter count of orders greater than x
           6. publish alert event
         */

        return input ->
                input.peek((k, v) -> logger.info("monitorStockOrdersActivity Received Stock Order = " + k + " Value = " + v + " Time"))
                        .groupBy(
                                (k, v) -> v.getCustomerId()
                        )
                        .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
                        .count(Materialized.as("dhq-ks-orders-alert-store"))
                        .filter((k, v) -> v >= 5)
                        .toStream()
                        .map((k, v) -> new KeyValue<>(k.key(),
                                            new StockOrdersActivityAlert(k.key(),
                                                    k.window().start(),
                                                    k.window().end(),
                                                    v)
                                                    )
                                );

    }



    /*
      Test method just for printing messages from output stream
      No value other than seeing events from output stream on IDE console
     */
    @Bean
    public Consumer<KStream<String, StockOrdersActivityAlert>> printAlerts() {
        return input -> input.foreach((k, v) -> logger.info("--->>>> Alert Stock Orders Exceeded Threshold for Key = " + k + " Value = " + v));
    }



}
