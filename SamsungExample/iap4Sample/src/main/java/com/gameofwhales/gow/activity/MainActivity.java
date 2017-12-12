package com.gameofwhales.gow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import com.gameofwhales.gow.R;
import com.gameofwhales.sdk.GameOfWhales;
import com.samsung.android.sdk.iap.lib.helper.SamsungIapHelper;
import com.samsung.android.sdk.iap.lib.listener.OnPaymentListener;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;
import com.samsung.android.sdk.iap.lib.vo.PurchaseVo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity
{
    private Spinner mItemTypeSpinner  = null;
    
    // ※ 주의
    // SamsungIapHelper.IAP_MODE_TEST_SUCCESS 모드는 실제 과금이 발생하지 않는
    // 테스트 모드입니다. 릴리즈할 때는 반드시 SamsungIapHelper.IAP_MODE_COMMERCIAL
    // 모드로 설정해야 합니다.SamsungIapHelper.IAP_MODE_COMMERCIAL 모드에서만
    // 실제 결제가 발생합니다.
    //
    // ※ CAUTION
    // SamsungIapHelper.IAP_MODE_TEST_SUCCESS mode is test mode that does not
    // occur actual billing.
    // When you release the SamsungIapHelper.IAP_MODE_COMMERCIAL mode
    // must be set.
    // In SamsungIapHelper.IAP_MODE_COMMERCIAL mode only
    // actual billing is occurred.
    // ========================================================================
    private static final int IAP_MODE = SamsungIapHelper.IAP_MODE_TEST_SUCCESS;
    // ========================================================================

    // Item ID for test button of purchase one item
    // ========================================================================
    private static final String ITEM_ID          = "product_5";
    // ========================================================================

    // Item ID for test button of cached items inbox list
    // ========================================================================
    private static final String ITEM_IDS         = "Nuclear, Claymore, " +
                                                   "Blockbuster,"+ "SpeedUP";
    // ========================================================================
    
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main_layout );
        
        mItemTypeSpinner = (Spinner)findViewById( R.id.spinner_item_type );


        /* * * * * * * * * * * * * * * * * * * * * * * * * */
        GameOfWhales.Init(this, "58ff3e2e30bb6d070389ef0f", null);
    }
    
    public void onClick( View _view )
    {
        if( null == _view )
        {
            return;
        }
        
        switch( _view.getId() )
        {
            // Call ItemListActivity
            // ================================================================
            case R.id.btn_get_item_list :
            {
                String itemType      = null;
                int    itemTypeIndex = mItemTypeSpinner.getSelectedItemPosition();
                
                switch( itemTypeIndex )
                {
                    case 0 :
                    {
                        itemType = SamsungIapHelper.ITEM_TYPE_CONSUMABLE;
                        break;
                    }
                    case 1 :
                    {
                        itemType = SamsungIapHelper.ITEM_TYPE_NON_CONSUMABLE;
                        break;
                    }
                    case 2 :
                    {
                        itemType = SamsungIapHelper.ITEM_TYPE_SUBSCRIPTION;
                        break;
                    }
                    case 3 :
                    {
                        itemType = SamsungIapHelper.ITEM_TYPE_AUTO_RECURRING_SUBSCRIPTIONS;
                        break;
                    }
                    case 4 :
                    {
                        itemType = SamsungIapHelper.ITEM_TYPE_ALL;
                        break;
                    }
                }
                
                Intent intent = new Intent( MainActivity.this,
                                            ItemListActivity.class );

                intent.putExtra( "StartNum", 1 );
                intent.putExtra( "EndNum", 15 );
                intent.putExtra( "ItemType", itemType );
                intent.putExtra( "IapMode", IAP_MODE );
                
                startActivity( intent );

                break;
            }
            // ================================================================
            // Call InboxListActivity
            // ================================================================
            case R.id.btn_get_inbox_list :
            {
                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyyMMdd",
                        Locale.getDefault() );
                String today = sdf.format( d );

                Intent intent = new Intent( MainActivity.this,
                        InboxListActivity.class );

                intent.putExtra( "IapMode", IAP_MODE );
                intent.putExtra( "StartNum", 1 );
                intent.putExtra( "EndNum", 15 );
                intent.putExtra( "StartDate", "20130101" );
                intent.putExtra( "EndDate", today );

                startActivity( intent );

                break;
            }
            // ================================================================
            // Call CachedInboxListActivity
            // ================================================================
            case R.id.btn_get_cached_inbox_list :
            {
                Intent intent = new Intent( MainActivity.this, CachedInboxListActivity.class ) ;

                intent.putExtra( "IapMode", IAP_MODE ) ;
                intent.putExtra( "ItemIds", ITEM_IDS ) ;

                startActivity( intent );

                break;
            }
            // ================================================================
            // Call PurchaseActivity
            // ================================================================
            case R.id.btn_purchase_one_item :
            {
                SamsungIapHelper iapHelper = 
                                SamsungIapHelper.getInstance( this, IAP_MODE );
                
                iapHelper.startPayment( ITEM_ID,
                                        true, 
                                        mOnPaymentListener );
                break;
            }
            // ================================================================
        }
    }
    
    OnPaymentListener mOnPaymentListener = new OnPaymentListener()
    {
        @Override
        public void onPayment( ErrorVo _errorVo, PurchaseVo _purchaseVo )
        {
            if( _errorVo != null &&
                _errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE )
            {
                // TODO When purchase has been finished successfully,
                //      processes here.
                //_purchaseVo.
            }
        
            // Test code : result log
            // ================================================================
            String paymentResult = "";
            
            if( _errorVo != null )
            {
                paymentResult = _errorVo.dump() + "\n\n";
            }
            
            if( _purchaseVo != null )
            {
                paymentResult += _purchaseVo.dump();
            }
        
            Log.e( "Purchase", paymentResult );
            // ================================================================
        }
    };
}