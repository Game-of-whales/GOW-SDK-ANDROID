package com.gameofwhales.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.gameofwhales.sdk.async.AdverstingIDAsyncTask;
import com.gameofwhales.sdk.async.AdverstingIDAsyncTaskListener;
import com.gameofwhales.sdk.protocol.Product;
import com.gameofwhales.sdk.protocol.SpecialOffer;
import com.gameofwhales.sdk.protocol.commands.Command;
import com.gameofwhales.sdk.protocol.commands.LoginCommand;
import com.gameofwhales.sdk.protocol.commands.PushDeliveredCommand;
import com.gameofwhales.sdk.protocol.commands.PushReactedCommand;
import com.gameofwhales.sdk.protocol.commands.ReceiptCommand;
import com.gameofwhales.sdk.protocol.commands.TokenCommand;
import com.gameofwhales.sdk.util.ActivityLyfecycleListener;
import com.gameofwhales.sdk.util.CommandListener;
import com.gameofwhales.sdk.util.DataStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class GameOfWhales {

    public static final String          PUSH_ID = "pushID";
    public static final String          PUSH_GOW = "gowpush";
    private static final String         TAG = "GOW.GameOfWhales";

    private static GameOfWhales         INSTANCE;
    private final Activity              activity;
    private CommandQueue                queue;
    private Set<String>                 segments = new HashSet<>();//TODO: DO NOT USE CONTEINSKEY METHOD
    private Set<GameOfWhalesListener>   listeners = new HashSet<>();
    private Map<String, Product>        products = new HashMap<>();
    private List<SpecialOffer>          specialOffers = new ArrayList<>();
    private Map<String, Replacement>    activeOffers = new HashMap<>();
    private DataStorage                 data;
    private ActivityLyfecycleListener   cycleListener;


    private GameOfWhales(final Activity activity, String gameID, GameOfWhalesListener listener)
    {
        L.d(TAG, "GameOfWhales.init");
        this.activity = activity;

        data = new DataStorage(activity, gameID);
        queue = new CommandQueue(activity, data, commandListener);

        if (listener != null)
            addListener(listener);

        if (data.getAdvID().isEmpty())
        {
            AdverstingIDAsyncTask advTask = new AdverstingIDAsyncTask(activity, new AdverstingIDAsyncTaskListener() {
                @Override
                public void onSuccess(String id) {
                    GameOfWhales.this.data.setAdvID(id);
                    GameOfWhales.this.queue.tryToSendNext();
                    firstlogin();
                }

                @Override
                public void onFailure() {
                    String id = "gen-android-"+ UUID.randomUUID().toString();
                    GameOfWhales.this.data.setAdvID(id);
                    GameOfWhales.this.queue.tryToSendNext();
                    firstlogin();
                }
            });
            advTask.execute();
        }
        else
        {
            firstlogin();
        }



        cycleListener = new ActivityLyfecycleListener();
        activity.getApplication().registerActivityLifecycleCallbacks(cycleListener);
    }

    private CommandListener commandListener = new CommandListener() {
        @Override
        public void onCommandResponse(Command command, JSONObject response) {
            GameOfWhales.this.onCommandResponse(command, response);
        }
    };

    public static GameOfWhales getInstance()
    {
        return GameOfWhales.INSTANCE;
    }

    public static void OnDestroy()
    {
        GameOfWhales.INSTANCE = null;
    }

    void firstlogin()
    {
        Bundle extras = activity.getIntent().getExtras();
        if (extras != null && extras.containsKey(PUSH_ID))
        {
            String pushID = extras.getString(PUSH_ID);
            L.i(TAG, "PushID : " + pushID);

            login();
            pushReacted(pushID);
        }
        else
        {
            login();
        }
    }

    private void login()
    {
        queue.add(new LoginCommand());
    }


    private Product getProduct(final String sku)
    {
        if (!products.containsKey(sku))
        {
            products.put(sku, new Product(sku));
        }

        return products.get(sku);
    }

    private static boolean instanceCheck(String from)
    {
        if (INSTANCE == null)
        {
            L.e(TAG, from + ": GameOfWhales not initialized");
            return false;
        }

        return true;
    }

    public void pushDelivered(final String pushID)
    {
        if (pushID == null || pushID.isEmpty())
            return;

        PushDeliveredCommand command = new PushDeliveredCommand(pushID);
        queue.add(command);
    }

    public static void PushDelivered(final String pushID)
    {
        if (instanceCheck("PushDelivered"))
        {
            INSTANCE.pushDelivered(pushID);
        }
    }


    public void pushReacted(final String pushID)
    {
        if (pushID == null || pushID.isEmpty())
            return;

        PushReactedCommand command = new PushReactedCommand(pushID);
        queue.add(command);
    }

    public static void PushReacted(final String pushID)
    {
        if (instanceCheck("PushReacted"))
        {
            INSTANCE.pushReacted(pushID);
        }
    }

    public static void UpdateToken(final String token)
    {
        if (instanceCheck("UpdateToken"))
        {
            INSTANCE.updateToken(token);
        }
    }

    public static void DetailsReceived(Bundle data)
    {
        if (instanceCheck("DetailsReceived"))
        {
            INSTANCE.detailsReceived(data);
        }
    }

    public static boolean IsAppForeground()
    {
        if (INSTANCE == null)
        {
            return false;
        }
        else
        {
            return GameOfWhales.INSTANCE.cycleListener.isForeground();
        }
    }

    public static GameOfWhales Init(Activity activity, String gameID, GameOfWhalesListener listener)
    {
        INSTANCE = new GameOfWhales(activity, gameID, listener);
        return INSTANCE;
    }

    public static GameOfWhales Init(Activity activity, GameOfWhalesListener listener)
    {
        INSTANCE = new GameOfWhales(activity, null, listener);
        return INSTANCE;
    }


    public void addListener(GameOfWhalesListener listener){
        listeners.add(listener);
    }

    public void removeListener(GameOfWhalesListener listener){
        listeners.remove(listener);
    }


    private void readSpecialOffers(JSONObject object) throws JSONException
    {
        final String id = "specialOffers";

        if (!object.has(id))
            return;

        JSONArray array = object.getJSONArray(id);

        if (array == null)
            return;

        L.i(TAG, "Reading spectial offers");

        specialOffers.clear();
        ArrayList<String> needRequestDetailsList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonSO = array.getJSONObject(i);

            SpecialOffer so = new SpecialOffer(jsonSO);
            specialOffers.add(so);

            Map<String, String> skus = so.getSkus();
            for(Map.Entry<String, String> entry : skus.entrySet())
            {
                Product originalProduct = getProduct(entry.getKey());
                if (!originalProduct.hasDetails() && !needRequestDetailsList.contains(originalProduct.getSku()))
                {
                    needRequestDetailsList.add(originalProduct.getSku());
                }


                Product offerProduct = getProduct(entry.getValue());
                if (!offerProduct.hasDetails() && !needRequestDetailsList.contains(offerProduct.getSku()))
                {
                    needRequestDetailsList.add(offerProduct.getSku());
                }
            }
        }

        onNeedRequestDetails(needRequestDetailsList);

        updateSpecialOffers();
    }



    void activateOffer(Replacement offer)
    {
        if (activeOffers.containsKey(offer.originalSku))
        {
            activeOffers.remove(offer.originalSku);
            activeOffers.put(offer.originalSku, offer);
        }
        else
        {
            activeOffers.put(offer.originalSku, offer);
        }
    }

    Replacement getActiveOfferBySO(String soSku)
    {
        for(Map.Entry<String, Replacement> entry : activeOffers.entrySet())
        {
            if (soSku.equals(entry.getValue().offerProduct.getSku()))
            {
                return entry.getValue();
            }
        }

        return null;
    }


    Replacement getActiveOfferFor(String sku)
    {
        if (activeOffers.containsKey(sku))
        {
            return activeOffers.get(sku);
        }

        return null;
    }

    void updateSpecialOffers()
    {
        List<String> changed = new ArrayList<>();
        List<String> removed = new ArrayList<>();


        for(SpecialOffer so : specialOffers)
        {
            Map<String, String> skus = so.getSkus();
            for(Map.Entry<String, String> entry : skus.entrySet()) {
                String originalSKU = entry.getKey();
                String offerSKU = entry.getValue();

                Replacement currentActiveOffer = getActiveOfferFor(originalSKU);
                Product activeOfferProduct = null;

                if (currentActiveOffer != null)
                    activeOfferProduct = currentActiveOffer.offerProduct;

                Product offerProduct = getProduct(offerSKU);

                if (!offerProduct.hasDetails())
                {
                    continue;
                }

                if (activeOfferProduct == null || activeOfferProduct.getPriceAmountMicros() > offerProduct.getPriceAmountMicros())
                {
                    Replacement newAO = new Replacement();
                    newAO.originalSku = originalSKU;
                    newAO.offerData = so;
                    newAO.offerProduct = offerProduct;
                    activateOffer(newAO);
                    changed.add(originalSKU);
                }
            }
        }


        for(Map.Entry<String, Replacement> entry : activeOffers.entrySet())
        {
            String originalSKU = entry.getValue().originalSku;
            boolean found = false;

            for(SpecialOffer so : specialOffers)
            {
                Map<String, String> skus = so.getSkus();
                for(Map.Entry<String, String> skuEntry : skus.entrySet()) {
                    String originalSOSKU = skuEntry.getKey();
                    if (originalSKU.equals(originalSOSKU))
                    {
                        found = true;
                        break;
                    }
                }

                if (found)
                    break;
            }

            if (!found)
            {
                removed.add(originalSKU);
            }
        }

        for(String sku : removed)
        {
            Replacement replacement = getActiveOfferFor(sku);
            activeOffers.remove(sku);
            onDisappeared(replacement);

        }

        for(String sku : changed)
        {
            Replacement replacement = getActiveOfferFor(sku);
            onAppeared(replacement);
        }

    }


    private void printReport(){
        if (L.isDebug)
        {
            StringBuilder b = new StringBuilder();
            b.append("\nReport{");
            b.append(String.format("\n  Game id: %s", data.getGameID()));
            b.append(String.format("\n  Player id: %s", data.getAdvID()));
            b.append("\n  Segments:{");
            for (String seg:segments) {
                b.append("\n    "+seg);
            }
            b.append("\n  }");
            b.append("\n  Replacements:{");
            for (Replacement rep:getReplacements()) {
                b.append("\n    "+rep.toString());
            }
            b.append("\n  }");

            b.append("\n}");
            L.d(TAG, b.toString());
        }
    }

    private void readSegments(JSONObject object) throws JSONException {

        final String id = "segments";

        if (!object.has(id))
            return;

        JSONArray array = object.getJSONArray(id);

        if (array == null)
            return;

        L.i(TAG, "Reading segments");

        segments.clear();

        for (int i = 0; i < array.length(); i++) {
            String segment = array.getString(i);
            segments.add(segment);
        }
    }


    public void detailsReceived(Bundle data)
    {
        int response = data.getInt("RESPONSE_CODE");
        if (response != 0)
        {
            L.e(TAG, "Query product error with response code " + response);
            return;
        }

        ArrayList<String> responseList = data.getStringArrayList("DETAILS_LIST");

        try {
            for(String item : responseList)
            {
                JSONObject json = new JSONObject(item);
                String sku = json.getString("productId");
                Product p = getProduct(sku);
                p.setDetails(json);
            }

        } catch (JSONException e)
        {
            L.e(TAG, "detailsReceived JSONException");
            e.printStackTrace();
            return;
        }

        updateSpecialOffers();
    }

    public static void InAppPurchased(final Intent data)
    {
        if (instanceCheck("InAppPurchased"))
        {
            INSTANCE.inAppPurchased(data);
        }
    }

    public void inAppPurchased(final Intent data) {
        try {

            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            if (responseCode != 0)
                return;

            final String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            final String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            final JSONObject jo = new JSONObject(purchaseData);
            final String sku = jo.getString("productId");

            final Product product = getProduct(sku);
            if (product == null || !product.hasDetails())
            {
                ArrayList<String> needRequestDetailsList = new ArrayList<>();
                onNeedRequestDetails(needRequestDetailsList);
            }

            try {
                JSONObject payload = new JSONObject();
                payload.put("json", purchaseData);
                payload.put("signature", dataSignature);

                JSONObject receipt = new JSONObject(purchaseData);
                String transactionId = receipt.getString("purchaseToken");
                receipt.put("Store", "GooglePlay");
                receipt.put("TransactionID", transactionId);
                receipt.put("Payload", payload.toString());

                ReceiptCommand receiptCommand = new ReceiptCommand(transactionId, receipt.toString());

                Replacement activeOffer = getActiveOfferBySO(sku);
                if (activeOffer != null)
                {
                    receiptCommand
                            .setSoID(activeOffer.offerData.getId())
                            .setOriginalSku(activeOffer.originalSku)
                            .setSoPayload(activeOffer.offerData.getSoPayload());
                }

                if (product != null && product.hasDetails())
                {
                    receiptCommand
                            .setCurrency(product.getPriceCurrencyCode())
                            .setPrice(product.getFloatPrice());
                }

                queue.add(receiptCommand);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateToken(final String token)
    {
        if (token == null || token.isEmpty())
            return;

        TokenCommand command = new TokenCommand(token);
        queue.add(command);
    }

    public boolean hasReplacements() {
        return activeOffers.size() > 0;
    }

    public Iterable<Replacement> getReplacements() {
        return activeOffers.values();
    }

    public Replacement getReplacement(String sku) {
        return getActiveOfferFor(sku);
    }

    public boolean inSegment(String segmentName)
    {
        return segments.contains(segmentName);
    }

    private void onAppeared(final Replacement replacement) {

        for (GameOfWhalesListener listener : listeners)
        {
            listener.onSpecialOfferAppeared(replacement);
        }
    }

    private void onDisappeared(final Replacement replacement) {

        for (GameOfWhalesListener listener : listeners)
        {
            listener.onSpecialOfferDisappeared(replacement);
        }
    }

    private void onPurchased(final Replacement replacement) {

        for (GameOfWhalesListener listener : listeners)
        {
            listener.onSpecialOfferPurchased(replacement);
        }
    }

    private void onNeedRequestDetails(final ArrayList<String> list)
    {
        if (list == null || list.isEmpty())
            return;


        for (GameOfWhalesListener listener : listeners)
        {
            listener.onNeedRequestDetails(list);
        }
    }

    private void onCommandResponse(Command command, JSONObject response) {

        if (response == null)
            return;

        L.i(TAG, "onCommandResponse: " + response.toString());

        try
        {
            readSegments(response);
            readSpecialOffers(response);

        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }




}
