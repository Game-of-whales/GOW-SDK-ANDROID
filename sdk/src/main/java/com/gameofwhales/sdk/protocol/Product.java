package com.gameofwhales.sdk.protocol;

import com.gameofwhales.sdk.L;

import org.json.JSONException;
import org.json.JSONObject;


public class Product {
    private static String TAG = "GOW.Product";

    private String sku;
    private String type;
    private String price;
    private long priceAmountMicros;
    private String priceCurrencyCode;
    private String title;
    private String description;

    public Product setDetails(JSONObject json)
    {
        try
        {
            String detailsSku = json.getString("productId");
            if (!sku.isEmpty() && !sku.equals(detailsSku))
            {
                L.e(TAG, "On SetDetails: skus does not match");
                return this;
            }

            type = json.getString("type");
            price = json.getString("price");
            priceAmountMicros = json.getLong("price_amount_micros");
            priceCurrencyCode = json.getString("price_currency_code");
            title = json.getString("title");
            description = json.getString("description");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return this;
    }



    public Product(String sku)
    {
        this.sku = sku;
    }

    public Product(JSONObject json) {
        setDetails(json);
    }


    public boolean hasDetails()
    {
        return price != null &&
               !price.isEmpty() &&
               priceCurrencyCode != null &&
               !priceCurrencyCode.isEmpty();
    }

    public String getSku() {
        return sku;
    }

    public String getType() {
        return type;
    }

    public String getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getPriceAmountMicros() {
        return priceAmountMicros;
    }

    public String getPriceCurrencyCode() {
        return priceCurrencyCode;
    }

    public float getFloatPrice() {
        return (getPriceAmountMicros() * 1.0f) / 1000000;
    }

}