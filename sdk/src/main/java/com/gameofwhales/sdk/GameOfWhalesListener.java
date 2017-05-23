package com.gameofwhales.sdk;

import java.util.ArrayList;


public interface GameOfWhalesListener {

    //void replacementLoaded();
    void onSpecialOfferAppeared(Replacement replacement);
    void onSpecialOfferDisappeared(Replacement replacement);
    void onSpecialOfferPurchased(Replacement replacement);
    void onNeedRequestDetails(ArrayList<String> skus);
}
