# GOW-SDK-ANDROID
Game Of Whales Android Native SDK.

<p align=center>
<img src=http://gameofwhales.com/static/images/landing/logo-right.png>
</p>

[ ![Download](https://api.bintray.com/packages/gameofwhales/maven/sdk/images/download.svg) ](https://bintray.com/gameofwhales/maven/sdk/_latestVersion)


## Changelog

**1.0.1**
* SDK for Android Native



## Implementation guide

### Step 1
Add the following dependencies to _build.gradle_:
```java
dependencies {
	...
        compile project('com.gameofwhales.sdk:1.0.1')
```

### Step 2
Add the following row to your _AndroidManifest.xml_ and change _GAME_ID_ to your game key. You can find your game key on [Game Settings](http://gameofwhales.com/#/documentation/game) page of **Game of Whales**.

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
import com.gameofwhales.sdk.Replacement;

...
@Override
    protected void onCreate(Bundle savedInstanceState) {
    ...
    // GoW_Init
    GameOfWhales.Init(this, gowListener);
```

### Step 4
Create new listner and add your functionality to it.

For example:
```java

private GameOfWhalesListener gowListener = new GameOfWhalesListener() {

        @Override
        public void onSpecialOfferAppeared(Replacement replacement) {
            //TODO: replace original product to replacement.offerProduct.getSku()
        }

        @Override
        public void onSpecialOfferDisappeared(Replacement replacement) {
            //TODO: return original product replacement.originalSku
        }

        @Override
        public void onSpecialOfferPurchased(Replacement replacement) {
        }

        @Override
        public void onNeedRequestDetails(ArrayList<String> skus) {
            //TODO: request details for sku from massive 
        }

    };
```


### Step 5 (only if you use in-app purchases) 
Add ```GameOfWhales.DetailsReceived``` line to the code when you get in-app details:
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


And add ```GameOfWhales.InAppPurchased``` line to _onActivityResult_ for successful purchase:
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



### Step 6 (only if push notifications are shown inside your app by using the game's code)

In order to send the information to **Game of Whales** regarding a player's reaction on a push notification (to increase push campaign's [_Reacted_](http://www.gameofwhales.com/#/documentation/push_analyze) field) of an already started app call the following method: 
```java
      GameOfWhales.PushReacted(pushID);
```

If you use **Firebase Messaging Service**, you can use the following code to get _PushID_:
```java
@Override
public void onMessageReceived(RemoteMessage remoteMessage) {
       super.onMessageReceived(remoteMessage);

       // GoW_Get PushID
       final String pushId = remoteMessage.getData().get(GameOfWhales.PUSH_ID);
```



> You can find an example of using the SDK [here](https://github.com/Game-of-whales/GOW-SDK-ANDROID/tree/master/AndroidExample).


Run your game. The information about it began to be collected and displayed on the [dashboard](http://gameofwhales.com/#/documentation/dashboard). In a few days, you will get data for analyzing.

This article includes the documentation for _Game of Whales Android Native SDK_. You can find information about another SDK in [documentation about Game of Whales](http://www.gameofwhales.com/#/documentation).




