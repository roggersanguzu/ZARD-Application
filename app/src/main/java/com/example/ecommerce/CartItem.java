package com.example.ecommerce;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private String productName;
    private int productPrice;
    private int productImageResId;
    private int quantity;

    public CartItem(String productName, int productPrice, int productImageResId) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageResId = productImageResId;
        this.quantity = 1; // Default quantity
    }

    protected CartItem(Parcel in) {
        productName = in.readString();
        productPrice = in.readInt();
        productImageResId = in.readInt();
        quantity = in.readInt();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    public String getProductName() {
        return productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public int getProductImageResId() {
        return productImageResId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity() {
        quantity++;
    }

    public void decreaseQuantity() {
        if (quantity > 1) quantity--;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeInt(productPrice);
        dest.writeInt(productImageResId);
        dest.writeInt(quantity);
    }
}
