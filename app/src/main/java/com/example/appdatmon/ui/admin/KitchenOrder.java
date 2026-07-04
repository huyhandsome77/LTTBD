package com.example.appdatmon.ui.admin;

import com.example.appdatmon.data.model.Order;

public class KitchenOrder {
    private long id;
    private String tableName;
    private String foodName;
    private int quantity;
    private String status;
    private Order rawOrder; // Dùng để lưu trữ object đơn hàng gốc

    public KitchenOrder(long id, String tableName, String foodName, int quantity, String status, Order rawOrder) {
        this.id = id;
        this.tableName = tableName;
        this.foodName = foodName;
        this.quantity = quantity;
        this.status = status;
        this.rawOrder = rawOrder;
    }

    public long getId() { return id; }
    public String getTableName() { return tableName; }
    public String getFoodName() { return foodName; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public Order getRawOrder() { return rawOrder; }

    public void setStatus(String status) { this.status = status; }
}
