package com.example.streams.demo.services;

import com.example.streams.demo.model.StockOrder;
import com.example.streams.demo.model.TradeConfirmation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Value("${application.configs.stockorder.topic.name}")
    private String STOCKORDER_TOPIC_NAME;

    @Autowired
    private KafkaTemplate<String, StockOrder> kafkaStockOrderTemplate;

    @Autowired
    private KafkaTemplate<String, TradeConfirmation> kafkaTradeTemplate;


    public void generateStockOrderEvent(StockOrder stockOrder) {
        logger.info(String.format("Producing Stock Order No: %s, %s", stockOrder.getStockSymbol(), stockOrder.getOrderId()));
        kafkaStockOrderTemplate.send(STOCKORDER_TOPIC_NAME, stockOrder.getOrderId(), stockOrder);
    }


}
