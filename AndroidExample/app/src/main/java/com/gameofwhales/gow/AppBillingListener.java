package com.gameofwhales.gow;

import java.util.ArrayList;

public interface AppBillingListener {

    void onServiceConnected();
    void onServiceDisconnected();
    void onDetails(ArrayList<AppBilling.ItemData> newDetails);
}
