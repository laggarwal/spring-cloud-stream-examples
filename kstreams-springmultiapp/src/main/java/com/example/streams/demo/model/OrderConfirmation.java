package com.example.streams.demo.model;

public class OrderConfirmation {

    private TradeConfirmation tradeConfirmation;
    private StockOrder stockOrder;
    private String result;
    private String orderId;

    public OrderConfirmation(StockOrder stockOrder, TradeConfirmation tradeConfirmation) {
        this.stockOrder = stockOrder;
        this.tradeConfirmation = tradeConfirmation;
        if(null == tradeConfirmation) {
            result = "NO_RESPONSE";
        }
        orderId = stockOrder.getOrderId();
    }

    public TradeConfirmation getTradeConfirmation() {
        return tradeConfirmation;
    }

    public void setTradeConfirmation(TradeConfirmation tradeConfirmation) {
        this.tradeConfirmation = tradeConfirmation;
    }

    public StockOrder getStockOrder() {
        return stockOrder;
    }

    public void setStockOrder(StockOrder stockOrder) {
        this.stockOrder = stockOrder;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
