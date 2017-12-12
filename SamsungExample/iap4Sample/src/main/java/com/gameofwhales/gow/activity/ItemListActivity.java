package com.gameofwhales.gow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gameofwhales.sdk.GameOfWhales;
import com.samsung.android.sdk.iap.lib.helper.SamsungIapHelper;
import com.samsung.android.sdk.iap.lib.listener.OnGetItemListener;
import com.samsung.android.sdk.iap.lib.listener.OnPaymentListener;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;
import com.samsung.android.sdk.iap.lib.vo.ItemVo;
import com.samsung.android.sdk.iap.lib.vo.PurchaseVo;
import com.gameofwhales.gow.R;
import com.gameofwhales.gow.adapter.ItemListAdapter;

import java.util.ArrayList;

public class ItemListActivity extends Activity 
                                implements OnGetItemListener, OnPaymentListener
{
    private final String TAG = ItemListActivity.class.getSimpleName();
    
    private int       mStartNum    = 1;
    private int       mEndNum      = 15;
    private String    mItemType    = "";
    private int       mIapMode     = 1;
    
    private ListView  mItemListView      = null;
    
    private TextView  mNoDataTextView    = null,
                      mSelectedItemType  = null;
    
    /** ArrayList for Item */
    private ArrayList<ItemVo> mProductList      = new ArrayList<ItemVo>();
    private ItemVo mSelectedProduct = new ItemVo();
    
    /** AdapterView for ItemList */
    private ItemListAdapter   mProductAdapter   = null;
    private SamsungIapHelper  mIapHelper = null;
    
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        
        setContentView( R.layout.item_list_layout );
        
        // 1. save ItemGroupId, StartNum, EndNum, ItemType and IapMode
        //    passed by Intent
        // ====================================================================
        Intent intent = getIntent();
        
        if( intent != null && intent.getExtras() != null 
                && intent.getExtras().containsKey( "StartNum" )
                && intent.getExtras().containsKey( "EndNum" )
                && intent.getExtras().containsKey( "ItemType" )
                && intent.getExtras().containsKey( "IapMode" ) )
        {
            Bundle extras = intent.getExtras();

            mStartNum    = extras.getInt( "StartNum" );
            mEndNum      = extras.getInt( "EndNum" );
            mItemType    = extras.getString( "ItemType" );
            mIapMode     = extras.getInt( "IapMode" );
        }
        else
        {
            Toast.makeText( this, 
                            R.string.IDS_SAPPS_POP_AN_INVALID_VALUE_HAS_BEEN_PROVIDED_FOR_SAMSUNG_IN_APP_PURCHASE,
                            Toast.LENGTH_LONG ).show();
            finish();
        }
        // ====================================================================
        
        ///////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////
        mIapHelper = SamsungIapHelper.getInstance( this, mIapMode );

        mIapHelper.getItemList( mStartNum,
                                mEndNum,
                                mItemType,
                                mIapMode,
                                this );
        ///////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////
        
        initView();
    }

    
    /**
     * initialize views
     */
    public void initView()
    {
        // 1. set views of Item List
        // ====================================================================
        mItemListView   = (ListView)findViewById( R.id.itemList );
        mNoDataTextView = (TextView)findViewById( R.id.noDataText );
        
        mSelectedItemType = (TextView)findViewById(
                                                 R.id.txt_selected_item_type );
        mSelectedItemType.setText( mItemType );
        
        mProductAdapter = new ItemListAdapter( this, 
                                                R.layout.item_row, 
                                                mProductList );
        
        mItemListView.setAdapter( mProductAdapter );
        mItemListView.setEmptyView( mNoDataTextView );
        mItemListView.setVisibility( View.GONE );
        // ====================================================================
        
        // 2. When a item of ListView has been clicked,
        //    start payment process of IAP. 
        // ====================================================================
        mItemListView.setOnItemClickListener(
                                          new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick
            (   
                AdapterView<?>  _parent,
                View            _view,
                int             _position,
                long            _id
            )
            {
                mSelectedProduct = mProductList.get( _position );

                if( mSelectedProduct != null )
                {
                    ///////////////////////////////////////////////////////////
                    ///////////////////////////////////////////////////////////
                    if( mIapHelper != null )
                    {
                        mIapHelper.startPayment( mSelectedProduct.getItemId(),
                                                 true,
                                                 ItemListActivity.this );
                    }
                    ///////////////////////////////////////////////////////////
                    ///////////////////////////////////////////////////////////
                }
                else
                {
                    Log.d( "", "Selected item is null." );
                }
            }
        } );
        // ====================================================================
    }
    
    @Override
    public void onGetItem( ErrorVo _errorVo, ArrayList<ItemVo> _itemList )
    {
        if( _errorVo != null &&
            _errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE )
        {
            // TODO When item list has been loaded successfully,
            //      processes here.
            
            if( _itemList != null && _itemList.size() > 0 )
            {
                mProductList.addAll( _itemList );
                mProductAdapter.notifyDataSetChanged();                
            }
        }
    }


    @Override
    public void onPayment( ErrorVo _errorVo, PurchaseVo _purchaseVo )
    {
        if( _errorVo != null &&
            _errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE )
        {
            // TODO When purchase is finished successfully, processes here.
            String purchaseID = _purchaseVo.getPurchaseId();
            String paymentID = _purchaseVo.getPaymentId();
            String currency = _purchaseVo.getCurrencyCode();
            float price = (float)_purchaseVo.getItemPrice().doubleValue();
            String sku = _purchaseVo.getItemId();
            String receipt = GameOfWhales.BuildSamsungReceipt(paymentID, purchaseID);

            GameOfWhales.InAppPurchased(sku, price, currency, paymentID, receipt);
                /*
                    {
                        "Store":"SamsungApps",
                        "TransactionID":"TPMTID20170815RUA3097361",
                        "Payload":"{\\"purchaseId\\":\\"75b0bcf6010ed8077983918b582cd1a0f7e9b0056b50db054c149a0a56a5c926\\"}"
                    }
                */
        }
        
        // Test Code
        // ====================================================================
        if( _errorVo != null )
        {
            Log.e( TAG, _errorVo.dump() );
        }
        
        if( _purchaseVo != null )
        {
            Log.e( TAG, _purchaseVo.dump() );
        }
        // ====================================================================
    }
}
