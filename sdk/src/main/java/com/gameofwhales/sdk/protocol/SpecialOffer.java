package com.gameofwhales.sdk.protocol;

import com.gameofwhales.sdk.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SpecialOffer {

    private static final String TAG = "GOW.SpecialOffer";

    private String id;
    private String name;
    private String description;

    private Map<String, String> skus = new HashMap<>();

    private Date activatedAt;
    private Date dateTo;
    private double activation;
    private Date finishAt;

    private String soPayload;


    public SpecialOffer(JSONObject json) {
        try {
            id = json.getString("_id");
            name = json.getString("name");
            description = json.getString("description");
            activation = json.getDouble("activation");

            if (json.has("soPayload")) {
                soPayload = json.getString("soPayload");
            }

            activatedAt = new Date(json.getLong("activatedAt") * 1000);
            dateTo = new Date(json.getLong("dateTo") * 1000);

            try
            {
                final JSONObject jskus = json.getJSONObject("skus");
                Iterator<String> it = jskus.keys();
                while(it.hasNext())
                {
                    String original = it.next();
                    String product = jskus.getString(original);
                    skus.put(original, product);
                }

            } catch (JSONException e)
            {
                L.e(TAG, "Json exception on definition create");

            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(activatedAt);
            calendar.add(Calendar.HOUR_OF_DAY, ((Double) activation).intValue());

            Date finishActivation = calendar.getTime();

            if (dateTo.after((finishActivation))) {
                finishAt = finishActivation;
            } else {
                finishAt = dateTo;
            }

        } catch (JSONException e) {
            L.e(TAG, e.getMessage());
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getActivatedAt() {
        return activatedAt;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public double getActivation() {
        return activation;
    }

    public Date getFinishAt() {
        return finishAt;
    }

    public String getSoPayload() {
        return soPayload;
    }

    public Map<String, String> getSkus() {
        return skus;
    }

    public boolean isExpired() {
        Calendar calendar = Calendar.getInstance();
        Calendar calendarAdded = Calendar.getInstance();
        calendarAdded.add(Calendar.HOUR_OF_DAY, Double.valueOf(activation).intValue());
        return calendar.after(dateTo) || calendar.getTime().after(calendarAdded.getTime());
    }

    public boolean IsActive() {
        return skus.size() > 0 && !isExpired();
    }
}
