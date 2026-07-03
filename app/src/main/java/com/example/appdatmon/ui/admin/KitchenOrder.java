package com.example.appdatmon.ui.admin;

public class KitchenOrder {
    private int id;
    private String tableName;
    private String foodName;
    private int quantity;
    private String status;

    public KitchenOrder(int id, String tableName, String foodName, int quantity, String status) {
        this.id = id;
        this.tableName = tableName;
        this.foodName = foodName;
        this.quantity = quantity;
        this.status = status;
    }

    public int getId() { return id; }
    public String getTableName() { return tableName; }
    public String getFoodName() { return foodName; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
