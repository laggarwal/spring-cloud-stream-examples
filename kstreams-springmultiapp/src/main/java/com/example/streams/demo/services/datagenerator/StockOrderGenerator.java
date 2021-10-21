package com.example.streams.demo.services.datagenerator;

import com.example.streams.demo.model.StockOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Random;

@Service
public class StockOrderGenerator {
    private final Random random;
    private final Random qty;
    private final StockOrder[] stocks;
    private int index;

    public StockOrderGenerator() {
        String testDataFILE = "src/main/resources/data/stockorder.json";
        ObjectMapper mapper = new ObjectMapper();
        random = new Random();
        qty = new Random();
        index=0;
        try {
            stocks = mapper.readValue(new File(testDataFILE), StockOrder[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getIndex() {
        return index++;
        //return random.nextInt(4);
    }

    private int getQuantity() {
        return qty.nextInt(2) + 1;
    }

    public StockOrder getNextProduct() {
        StockOrder stockOrder = stocks[getIndex()];
        return stockOrder;
    }

    public StockOrder getProduct(int i) {
        StockOrder stockOrder = stocks[i];
        return stockOrder;
    }

}
