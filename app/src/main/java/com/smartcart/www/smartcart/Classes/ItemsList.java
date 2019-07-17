package com.smartcart.www.smartcart.Classes;

/**
 * Created by shyattoun on 12.5.2018.
 */

public class ItemsList {

    private String tableDescription;
    private String tablePrice;
    private String tableQuantity;
    private Float tableTotalPricePerItem;
    private String productImage;
    private String ItemID;


    public ItemsList() {
    }

    public ItemsList(String tableDescription, String tablePrice, String tableQuantity, Float tableTotalPricePerItem, String productImage, String itemID) {
        this.tableDescription = tableDescription;
        this.tablePrice = tablePrice;
        this.tableQuantity = tableQuantity;
        this.tableTotalPricePerItem = tableTotalPricePerItem;
        this.productImage = productImage;
        ItemID = itemID;
    }



    public String getTableDescription() {
        return tableDescription;
    }

    public String getTablePrice() {
        return tablePrice;
    }

    public String getTableQuantity() {
        return tableQuantity;
    }

    public Float getTableTotalPricePerItem() {
        return tableTotalPricePerItem;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getItemID() {
        return ItemID;
    }

    public void setTableDescription(String tableDescription) {
        this.tableDescription = tableDescription;
    }

    public void setTablePrice(String tablePrice) {
        this.tablePrice = tablePrice;
    }

    public void setTableQuantity(String tableQuantity) {
        this.tableQuantity = tableQuantity;
    }

    public void setTableTotalPricePerItem(Float tableTotalPricePerItem) {
        this.tableTotalPricePerItem = tableTotalPricePerItem;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    @Override
    public String toString() {
        return "ItemsList{" +
                "tableDescription='" + tableDescription + '\'' +
                ", tablePrice='" + tablePrice + '\'' +
                ", tableQuantity='" + tableQuantity + '\'' +
                ", tableTotalPricePerItem='" + tableTotalPricePerItem + '\'' +
                ", productImage='" + productImage + '\'' +
                ", ItemID='" + ItemID + '\'' +
                '}';
    }
}
