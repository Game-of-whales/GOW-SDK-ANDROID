Game of whales Android Native SDK

[<img src=https://www.gameofwhales.com/sites/default/files/documentation/download.png>](https://github.com/Game-of-whales/GOW-SDK-ANDROID/releases/download/v2.0.33/com.gameofwhales.sdk-2.0.33.aar)


# Changelog

### 2.0.33 (Dec 16, 2019)

ADDED
* The information about the player's device is sent to **Game of Whales** server. 

FIXED
* Minor fixes.

### 2.0.32 (Oct 22, 2019)

ADDED
* [`onConnected` callback](https://www.gameofwhales.com/documentation/android-native-sdk#gowListener) was added. 


### 2.0.29 (Sep 23, 2019)

ADDED
* Supporting of ["A/B Testing"](https://www.gameofwhales.com/documentation/ab-testing) was added. 


### 2.0.26 (Jun 25, 2019)

ADDED
* GDPR support: the non-personal mode was added. 

FIXED

**Android**

* SDK receiver doesn't handle a push notification if it has handled the push notification with the same id before.


### 2.0.24 (Jun 19, 2019)

ADDED

* `onInitialized` callback was added. It should be used to get information that the SDK has been initialized.
* `Purchase` method was added to register purchases without verification.


### 2.0.23 (Feb 15, 2019)

* Minor fixes.



### 2.0.22 (Jan 25, 2019)
ADDED

* The supporting of [future (anti-churn) special offers](https://www.gameofwhales.com/documentation/anti-churn-offers) were added.
* The possibility of getting a profile's properties was added.



### 2.0.21 (Dec 17, 2018)
FIXED

* The handling of errors was improved.



### 2.0.20 (Nov 20, 2018)
ADDED

* The supporting of cross promotion ads was added.


### 2.0.19 (Oct 29, 2018)
FIXED

* Sometimes events (for example, ```pushDelivered```) were not sent to **GOW server**. The issue was fixed.


### 2.0.16 (Aug 06, 2018)
FIXED

* Usage of *Store* parameter was fixed.


### 2.0.15 (Jun 14, 2018)
FIXED

* Push notifications were not supported for Android 8 +.


### 2.0.14 (Jun 14, 2018)
ADDED

* ```redeemable``` parameter was added to ```SpecialOffer``` class.

### 2.0.13.2 (May 15, 2018)
FIXED
* Usage of *Store* parameter was fixed.

### 2.0.13.0 (May 15, 2018)
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

The SDK will ask the following permissions on the user's device:
```java
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
```

> **NOTE about Android X**: if your project uses the libraries for _Android X_, [**download com.gameofwhales.sdkx-&lt;version&gt;.aar**](https://github.com/Game-of-whales/GOW-SDK-ANDROID/releases/download/v2.0.33/com.gameofwhales.sdkx-2.0.33.aar) library, remove **com.gameofwhales.sdk-&lt;version&gt;.aar** from your project and add **com.gameofwhales.sdkx-&lt;version&gt;.aar**. _&lt;version&gt;_ is the number of used SDK version.  



Implement the SDK to your project.

### Step 1
Add the following dependencies to _build.gradle_:
```java
dependencies {
	...
       compile 'com.gameofwhales:sdk:2.0.33@aar'
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

> **GDPR NOTE**: By default, the SDK uses advertisement ID (IDFA) as a user ID to send events to **Game of Whales** server. In order to work in a non-personal mode when a random value will be used as a user ID, the SDK should be initialized as follows:

```java
	boolean nonPersonal = true;
        GameOfWhales.Init(this, GameOfWhales.STORE_GOOGLEPLAY, null, nonPersonal);
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
        public void onFutureSpecialOfferAppeared(SpecialOffer specialOffer) {
        }
	
	@Override
        public void onPushDelivered(SpecialOffer offer, String campID, String title, String message) 
        {
           //It's called to show notification in opened game.
        }
	
	@Override
	public void onInitialized() {
	 //It's needed just if you want to get information that the SDK has been initialized.
	}
	
	@Override
        void onConnected(boolean dataReceived){
           //It's called after the GOW server response with 'dataReceived': true.
           //If there was no response from the GOW server, there was an error during the request to the server 
           //or the game is offline, 'dataReceived' is false.
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

The verify state can be:

* _VERIFY_STATE_LEGAL_ - a purchase is normal.
* _VERIFY_STATE_ILLEGAL_ - a purchase is a cheater's.
* _VERIFY_STATE_UNDEFINED_ - GOW server couldn't define the state of a purchase. 


### Purchases

>Check that *Android Bundle Identifier* and *Android Public License Key* have been filled on [*Game Settings*](https://www.gameofwhales.com/documentation/game#game-settings) page before you will make a purchase.
	
### Step 5.1 (only if you use old Google Billing) 
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

### Step 5.2 (only if you use new version of Google Play Billing)

Add the following line to the code when you get in-app details:
```java
 @Override
 public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
        for (SkuDetails d : skuDetailsList)
        {
              GameOfWhales.DetailsFromString(d.toString());

```
And add the following line to _onPurchasesUpdated_ for successful purchase:
```java
 @Override
 public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
	if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            	for (Purchase purchase : purchases) {
			GameOfWhales.InAppPurchased(purchase.getOriginalJson(), purchase.getSignature());
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

>If you can't use the previous methods for purchases, you can use ``inAppPurchased`` method with the following parameters: 
```java
	String receipt = BuildGooglePlayReceipt(originalJson, signature);
	inAppPurchased(sku, price, currency, transactionID, receipt.toString());
```

### Step 7 (purchases without verification on GOW side)

> The method is available since v.2.0.24 SDK version

In order to send information about purchases without verification on **Game of Whales** side, call `Purchase` method. For example:

```java
	String product = "product_10";
        String currency = "USD";
        double price = 1.99;
        
        GameOfWhales.Purchase(product, currency, price * 100);
```

> Pay attention that all purchases received through `Purchase` method (including refunds, restores, cheater's purchases) will increase the stats. So in order to have correct stats, a game developer should verify purchases on the game side and send the data only about legal purchases to **Game of Whales** system.


### Special Offers
### Step 8

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


### Push notifications
### Step 9
Add a receiver to send information about notifications to your manifest and specify your [_Android Bundle Identifier_](http://www.gameofwhales.com/documentation/android-settings) instead _APP_BUNDLE_.

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

### Step 10
Add the next permissions to your manifest and specify your _Android Bundle Identifier_ instead _APP_BUNDLE_.

```cs
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
<permission android:name="APP_BUNDLE.permission.C2D_MESSAGE" android:protectionLevel="signature" />
<uses-permission android:name="APP_BUNDLE.permission.C2D_MESSAGE" />
```

> If you haven't had an android manifest in your project yet, you can use _GOWAndroidManifest.xml_ from SDK, but you need renaming of it to _AndroidManifest.xml_ and replace _APP_BUNDLE_ to your _Android Bundle Identifier_ wherever it is defined.


### Step 11
Register your project in the [Firebase console](http://www.gameofwhales.com/documentation/firebase-settings).

**If you use _Google Cloud Messaging_**:

Call the following method: 

```java
	GameOfWhales.SetAndroidProjectID(PROJECT_NUMBER);//or SenderID
```
Check that the following library have been added to your Android project in gradle file:
```java
	compile 'com.google.android.gms:play-services-gcm:xxx'
```

**If you use _Firebase Cloud Messaging_**:

[Add Firebase](https://firebase.google.com/docs/cloud-messaging/) to your project.

Call the following method during the starting of your game when _Firebase_ has already been initialized: 

```java
	GameOfWhales.UpdateToken(FirebaseInstanceId.getInstance().getToken(), GameOfWhales.PROVIDER_FCM);
```


### Step 12 (only if push notifications are shown inside your app by using the game's code)

In order to send the information to **Game of Whales** regarding a player's reaction on a push notification (to increase push campaign's [_Reacted_](http://www.gameofwhales.com/documentation/processing-pushes) field) of an already started app call the following method: 
```java
      
      @Override
      public void onPushDelivered(SpecialOffer offer, String campID, String title, String message) 
      {
      		//Show the notification to a player and then call the following method
        	GameOfWhales.PushReacted(campID);
      }
```


### Step 13
In order to enable or disable push notifications, use the following method:

```java
	GameOfWhales.SetPushNotificationsEnable(false);
```

In order to check notifications implementation send [a test notification](http://www.gameofwhales.com/documentation/how-send-test-push-notification).


### Profiles
### Step 14 

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


### Converting
### Step 15

If you are going to use [AI offers](https://www.gameofwhales.com/documentation/ai-offers) functionality you need to send to **Game of Whales** information about players' game activity by using ``Converting`` method. ``Converting`` method should be called to show what exactly the player spent and what he got instead of spent resources. [Read more...](https://www.gameofwhales.com/documentation/ai-offers#aiData)


For example:
Someone bought one _bike_1_ for _1000_ coins and _50_ gas. You should call the following method to reflect this operation in **Game of Whales**:

```java
	Map<String, Long> resources = new HashMap<>();
        resources.put("bike_1", 1);
	resources.put("gas", -50);
        resources.put("coins", -1000);
        GameOfWhales.Converting(resources, place);
```

Another sample: someone bought a main pack for $5. It was in-app purchase with _mainPack_ SKU. The pack included _100 coins_ and _1 bike_. In order to send the data that the player got _100 coins_ and _1 bike_ after the purchase of _mainPack_ to **Game of Whales**, the following ``Converting`` method should be called:

```java
	Map<String, Long> resources = new HashMap<>();
        resources.put("bike", 1);
	resources.put("coin", 100);
        resources.put("mainPack", -1);
        GameOfWhales.Converting(resources, place);
```

There are 2 additional methods that can be used instead of ```Converting``` method:

``Consume`` - to show that a player spends a certain amount of one resource for the purchase of a quantity of another resource.

For example:

```java
	GameOfWhales.Consume("coins", 1000, "gas", 50, "shop");
```
It means that someone spent 1000 "coins" for 50 "gas" in "shop".


``Acquire`` -  to show that a player acquires a certain amount of one resource and spends a quantity of another resource. The method can be used for _in-app_ and _in game_ items. It's important to call ``Acquire`` method after ``InAppPurchased``.

For example:

```java
	GameOfWhales.Acquire("coins", 1000, sku, 1, "bank");
```
It means that someone has acquired 10000 "coins" for 1 "sku" in "bank".

``Consume`` and ``Acquire`` methods can be called when one resource is changed to another resource. In more complicated cases (for example, when one resource is spent for several types of resources) ``Converting`` method should be called.


### Cross promotion ads
> It's supported since version 2.0.20 of SDK for Android.

To handle the ads set in **Game of Whales**, you need to do some actions:

### Step 16

Subscribe to the following events to get the information about the current state of the ads by using ``GameOfWhalesListener`` class:

```java
private GameOfWhalesListener gowListener = new GameOfWhalesListener() {

        ...
	
        @Override
        public void onAdLoaded() {
            
        }

        @Override
        public void onAdLoadFailed() {
           
        }

        @Override
        public void onAdClosed() {
            
        }
	
	...
    };
```

### Step 17

Start to load the ads at any place of your code (for example, during the launch of the game):

```java
	GameOfWhales.LoadAd();    
```

### Step 18

Add the following code to the part of your game where you want to show the ads:

```java
    if (GameOfWhales.IsAdLoaded())
    {
	GameOfWhales.ShowAd();
    }
    else
    {
	GameOfWhales.LoadAd();
    }
```

# Profile's properties

### Step 19

You can get some profile's properties defined on **Game of Whales** side via the SDK.

For example, you can get the profile's property `group` by using the following code:

```java
JSONObject properties = GameOfWhales.GetProperties();
String id = "group";
if (properties.has(id))
	{
		String group = properties.getString(id);
	}
```

You can also receive the profile's group by using the special method:

```java
	String group = GameOfWhales.getUserGroup();
```

# A/B testing (experiments)

>It's supported since version 2.0.29 of SDK for Android.

### Step 20

In order to start working with experiments, it's needed to add some methods by using _GameOfWhalesListener_ class: 


In order to confirm that the experiment payload settings have been applied and the player should take part in the experiment, it's needed to return true:

```java
    @Override
    public boolean CanStartExperiment(Experiment experiment)
    {
       //Read experiment.payload, apply changes for experiment and return True if changes were applied
	     
       return true;
    }
```

In order to check if there is an experiment at the start of the application, subscribe to `OnConnected` callback (see _Step 4_). If the experiment exists, `CanStartExperiment` will be called before `OnConnected`. 

> `OnConnected` callback is supported since 2.0.32 version of SDK.

When the experiment is finished, `OnExperimentEnded` method will be called. You are able to remove all experiment changes or keep them for further work regardless of the experiment:

```java
    @Override
    public void OnExperimentEnded(Experiment experiment)
    {
       //Disable experiment changes or keep them
    }
```


> You can find an example of using the SDK [here](https://github.com/Game-of-whales/GOW-SDK-ANDROID/tree/master/AndroidExample).

Run your game. The information about it began to be collected and displayed on the [dashboard](http://gameofwhales.com/documentation/dashboard). In a few days, you will get data for analyzing.

This article includes the documentation for _Game of Whales Android Native SDK_. You can find information about another SDK in [documentation about Game of Whales](http://www.gameofwhales.com/documentation/download-setup).
