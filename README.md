Game of whales Android Native SDK

[ ![Download](https://api.bintray.com/packages/gameofwhales/maven/sdk/images/download.svg) ](https://github.com/Game-of-whales/GOW-SDK-ANDROID/releases/download/v2.0.13.2/com.gameofwhales.sdk-2.0.13.2.aar)


# Changelog

### 2.0.13.2 (May 15,2018)
FIXED
* Usage of *Store* parameter was fixed.

### 2.0.13.0 (May 15,2018)
ADDED
* Custom data is supported for special offers.

FIXED
* Sometimes events from apps could not be sent to **Game of Whales** server.


### 2.0.12 (May 14, 2018)
ADDED
* The new *Google Play Billing* is supported.
* The information about a device's locale is sent to **Game of Whales**.


### 2.0.11 (Dec 20, 2017)
FIXED
* _pushDelivered_ event could be sent twice.

### 2.0.10 (Dec 20, 2017)
MODIFIED
* Push notification about special offer comes at the same time with the special offer (new parameter _offer_ was added):
_void onPushDelivered(SpecialOffer offer, String campID, String title, String message)_;

* _setPushNotificationsEnable_ method was added to allow user to turn off the push notifications.

### 2.0.9 (Nov 21, 2017)

MODIFIED
* _store_ parameter was added to initializing.



# Implementation Guide

Implement the SDK to your project.

### Step 1
Add the following dependencies to _build.gradle_:
```java
dependencies {
	...
       compile 'com.gameofwhales:sdk:2.0.13.2@aar'
```

### Step 2
Add the following row to your _AndroidManifest.xml_ and change _GAME_ID_ to your game key. You can find your game key on [Game Settings](http://gameofwhales.com/documentation/game) page of **Game of Whales**.

```java
...
<meta-data android:name="gameOfWhales.gameId" android:value="GAME_ID" />
</application>
```

### Step 3
Init _Game of Whales SDK_ in your _Activity Start_ class. 

```java
// GoW_import
import com.gameofwhales.sdk.GameOfWhales;
import com.gameofwhales.sdk.GameOfWhalesListener;
import com.gameofwhales.sdk.SpecialOffer;

...
@Override
    protected void onCreate(Bundle savedInstanceState) {
    ...
    // GameOfWhakes SDK initialization
    final String store = GameOfWhales.STORE_GOOGLEPLAY;
    //final String store = GameOfWhales.STORE_SAMSUNG;
    GameOfWhales.Init(this, store, gowListener);
```

### Step 4
Create new listener and add your functionality to it.

For example:
```java

private GameOfWhalesListener gowListener = new GameOfWhalesListener() {

        @Override
        public void onSpecialOfferAppeared(SpecialOffer specialOffer) {
            Log.i(TAG, "onSpecialOfferAppeared: " + specialOffer.toString());
        }

        @Override
        public void onSpecialOfferDisappeared(SpecialOffer specialOffer) {
            Log.i(TAG, "onSpecialOfferDisappeared: " + specialOffer.toString());
        }


        @Override
        public void onPushDelivered(SpecialOffer offer, String campID, String title, String message) 
        {
           //It's called to show notification in opened game.
        }

        @Override
        public void onPurchaseVerified(final String transactionID, final String state) {

            
            if (state.equals(GameOfWhales.VERIFY_STATE_ILLEGAL))
            {
	    	//TODO: Refund money if state is illegal
            }
        }
    };
```
	
### Purchases

>Check that *Android Bundle Identifier* and *Android Public License Key* have been filled on [*Game Settings*](https://www.gameofwhales.com/documentation/game#game-settings) page before you will make a purchase.
	
### Step 5 (only if you use in-app purchases) 
Add the following line to the code when you get in-app details:
```java
Bundle details = null;
try {
    details = service.getSkuDetails(3, pck, "inapp", query);
} catch (RemoteException e)
{
    Log.e(TAG, "RemoteException while getting details");
}

int response = details.getInt("RESPONSE_CODE");
if (response == 0)
{
     	// GoW_call DetailsReceived()
        GameOfWhales.DetailsReceived(data);
}
```


And add the following line to _onActivityResult_ for successful purchase:
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) 
{
	if (requestCode == BILLING_REQUEST_CODE && resultCode == RESULT_OK) 
	{
	...
        	// GoW_call InAppPurchased()
        	GameOfWhales.InAppPurchased(data);
```


### Step 6 (only if you use Samsung purchases)

```java
	String purchaseID = _purchaseVo.getPurchaseId();
        String paymentID = _purchaseVo.getPaymentId();
        String currency = _purchaseVo.getCurrencyCode();
        double price = _purchaseVo.getItemPrice().doubleValue();
        String sku = _purchaseVo.getItemId();
        String receipt = GameOfWhales.BuildSamsungReceipt(paymentID, purchaseID);

        GameOfWhales.InAppPurchased(sku, price, currency, paymentID, receipt);
```

> You can find an example of using the SDK for Samsung [here](https://github.com/Game-of-whales/GOW-SDK-ANDROID/tree/master/SamsungExample).


### Step 7 (Special Offers)

Before any product can be used in a special offer it has to be bought by someone after SDK has been implemented into the game. Please make sure your game has at least one purchase of the product that is going to be used in the special offer.
If you want to create a special offer for in game resource, please, make sure your game has at least one _converting_ event with the appropriate resource.

In order to receive special offer call the following method:
```java
	SpecialOffer so = GameOfWhales.GetSpecialOffer(itemID);
	if (so!= null)
	{...
```
Special offer can influence a product's price:
```java
	if (so.hasPriceFactor())
	{
		cost *= so.priceFactor;
	}
```
Special offer can also influence count (count of coins, for example) which a player receive by purchase:
```java
	if (so.hasCountFactor())
	{
	 	coins *= so.countFactor;
	}
```

It's possible to pass [custom data](https://www.gameofwhales.com/documentation/custom-data) to special offers. In order to get the data in game's side, use _customValues_ parameter of  _SpecialOffer_ class.
```java
       String str = specialOffer.customValues.get("your_str").toString();
       Integer number = Integer.valueOf(specialOffer.customValues.get("your_int").toString());
       Boolean bool = Boolean.valueOf(specialOffer.customValues.get("your_bool").toString());
```

### Step 8 (push notifications)
Add a receiver to send  information about notifications to your manifest and specify your [_Android Bundle Identifier_](http://www.gameofwhales.com/documentation/android-settings) instead _APP_BUNDLE_.

```cs
     <receiver
        android:name="com.gameofwhales.sdk.util.GOWBroadcastReceiver"
        android:permission="com.google.android.c2dm.permission.SEND">
            <intent-filter>
               <action android:name="com.google.android.c2dm.intent.RECEIVE" />
               <category android:name="APP_BUNDLE"/>
            </intent-filter>
        </receiver>
     
     ...
     </application>
```

>It is necessary to replace APP_BUNDLE in all places of the manifest where it's specified.

### Step 9 (only if push notifications are shown inside your app by using the game's code)

In order to send the information to **Game of Whales** regarding a player's reaction on a push notification (to increase push campaign's [_Reacted_](http://www.gameofwhales.com/documentation/processing-pushes) field) of an already started app call the following method: 
```java
      
      @Override
      public void onPushDelivered(SpecialOffer offer, String campID, String title, String message) 
      {
      		//Show the notification to a player and then call the following method
        	GameOfWhales.PushReacted(campID);
      }
```


### Step 10 (only if you use Google Cloud Messaging)
Call the following method: 

```java
	GameOfWhales.SetAndroidProjectID(PROJECT_NUMBER);//or SenderID
```
Check that the following library have been added to your Android project in gradle file:
```java
	compile 'com.google.android.gms:play-services-gcm:xxx'
```
### Step 11

In order to enable or disable push notifications, use the following method:

```java
	GameOfWhales.SetPushNotificationsEnable(false);
```

In order to check notifications implementation send [a test notification](http://www.gameofwhales.com/documentation/how-send-test-push-notification).

### Step 12 (profiles) 

You can send additional data about your players by using the ``Profile`` method. ``Profile`` method should be called if key parameters of your app or a player have changed.

>If you send more than 3000 properties, **Game of Whales** will sort all properties alphabetically and will save only the first 3000.

>If the length of a string-type property is more than 64 characters, **Game of Whales** will save only the first 64 characters.

For example:

```java
	HashMap<String, Object> changes = new HashMap<>();
        changes.put("class", getUserClass());
        changes.put("gender", Boolean.valueOf(getGender()));
        changes.put("location", getLocation());
        changes.put("level", getLevel());
        GameOfWhales.Profile(changes);
```

### Step 13 (converting)

``Converting`` method should be called when you buy or get some in-game objects, coins, currency, etc.

For example:
Someone bought one _bike_1_ for _1000_ coins and _50_ gas. You should call the following method for this purchase:

```java
	Map<String, Long> resources = new HashMap<>();
        resources.put("bike_1", 1);
	resources.put("gas", -50);
        resources.put("coins", -1000);
        GameOfWhales.Converting(resources, place);
```

You can also use the following methods:

``Consume`` - to buy items for game currency. For example:

```java
	GameOfWhales.Consume("coins", 1000, "gas", 50, "shop");
```
It means that someone spent 1000 "coins" for 50 "gas" in "shop".


``Acquire`` - for in-app purchases. It's important to call ``acquire`` method after ``InAppPurchased``. For example:

```java
	GameOfWhales.Acquire("coins", 1000, sku, 1, "bank");
```
It means that someone has acquired 10000 "coins" for 1 "sku" in "bank".



> You can find an example of using the SDK [here](https://github.com/Game-of-whales/GOW-SDK-ANDROID/tree/master/AndroidExample).

Run your game. The information about it began to be collected and displayed on the [dashboard](http://gameofwhales.com/documentation/dashboard). In a few days, you will get data for analyzing.

This article includes the documentation for _Game of Whales Android Native SDK_. You can find information about another SDK in [documentation about Game of Whales](http://www.gameofwhales.com/documentation/download-setup).
