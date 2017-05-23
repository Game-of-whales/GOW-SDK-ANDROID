package com.gameofwhales.sdk.protocol.commands;

import org.json.JSONException;
import org.json.JSONObject;

public class ReceiptCommand extends Command {

    public static String ID = "sdk.receipt";
    private String currency;
    private float price;
    private String transactionId;
    private String receipt;
    private String soId;
    private String originalSku;
    private String soPayload;

    public ReceiptCommand(String transactionId, String receipt)
    {
        this.id = ID;
        this.transactionId = transactionId;
        this.receipt = receipt;
    }

    public ReceiptCommand setPrice(float price)
    {
        this.price = price;
        return this;
    }

    public ReceiptCommand setCurrency(String currency)
    {
        this.currency = currency;
        return this;
    }

    public ReceiptCommand setSoID(String soId)
    {
        this.soId = soId;
        return this;
    }

    public ReceiptCommand setOriginalSku(String originalSku)
    {
        this.originalSku = originalSku;
        return this;
    }

    public ReceiptCommand setSoPayload(String soPayload)
    {
        this.soPayload = soPayload;
        return this;
    }

    @Override
    public boolean canSend()
    {
        return super.canSend() && price > 0.0001f && !currency.isEmpty();
    }

    @Override
    public JSONObject getArgs() {

        try {
            JSONObject args = new JSONObject();
            args.put("currency", currency);
            args.put("price", price * 100);
            args.put("receipt", receipt);
            args.put("transactionId", transactionId);
            if (soId != null && !soId.isEmpty()) {
                args.put("so", soId);
                args.put("originalSku", originalSku);
                args.put("soPayload", soPayload);
            }
            return args;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
