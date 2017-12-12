package com.gameofwhales.gow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gameofwhales.gow.adapter.InboxListAdapter;
import com.samsung.android.sdk.iap.lib.helper.SamsungIapHelper;
import com.samsung.android.sdk.iap.lib.listener.OnGetInboxListener;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;
import com.samsung.android.sdk.iap.lib.vo.InboxVo;
import com.gameofwhales.gow.R;

import java.util.ArrayList;

public class InboxListActivity extends Activity implements OnGetInboxListener
{
    private int     mIapMode     = 1;
    private int     mStartNum    = 0;
    private int     mEndNum      = 0;
    private String  mStartDate   = "";
    private String  mEndDate     = "";
    
    private ListView              mInboxListView    = null;
    private TextView              mNoDataTextView       = null;
    
    /** ArrayList for list of purchased item */
    private ArrayList<InboxVo>    mInboxList = new ArrayList<InboxVo>();
    
    /** AdapterView for list of purchased item */
    private InboxListAdapter mInboxListAdapter = null;
    
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.inbox_list_layout );
        
        Intent intent = getIntent();
        
        if( intent != null && intent.getExtras() != null 
                && intent.getExtras().containsKey( "IapMode" )
                && intent.getExtras().containsKey( "StartNum" )
                && intent.getExtras().containsKey( "EndNum" )
                && intent.getExtras().containsKey( "StartDate" )
                && intent.getExtras().containsKey( "EndDate" ) )
        {
            Bundle extras = intent.getExtras();

            mIapMode     = extras.getInt( "IapMode" );
            mStartNum    = extras.getInt( "StartNum" );        
            mEndNum      = extras.getInt( "EndNum" );
            mStartDate   = extras.getString( "StartDate" );         
            mEndDate     = extras.getString( "EndDate" );
        }
        else
        {
            Toast.makeText( this, 
                            R.string.IDS_SAPPS_POP_AN_INVALID_VALUE_HAS_BEEN_PROVIDED_FOR_SAMSUNG_IN_APP_PURCHASE,
                            Toast.LENGTH_LONG ).show();
            finish();
        }
        
        initView();
        
        ///////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////
        SamsungIapHelper iapHelper = SamsungIapHelper.getInstance( this,
                                                                   mIapMode );
        iapHelper.getItemInboxList( mStartNum,
                                    mEndNum,
                                    mStartDate,
                                    mEndDate,
                                    this );
        ///////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////
    }

    
    /**
     * initialize views
     */
    public void initView()
    {
        mInboxListView = (ListView)findViewById( R.id.itemInboxList );
        mNoDataTextView    = (TextView)findViewById( R.id.noDataText );
        mNoDataTextView.setVisibility( View.GONE );
        
        mInboxListView.setEmptyView( mNoDataTextView );
        
        mInboxListAdapter = new InboxListAdapter( this, 
                                                  R.layout.inbox_row, 
                                                  mInboxList );
        
        mInboxListView.setAdapter( mInboxListAdapter );
    }
    
    @Override
    public void onGetItemInbox
    (   
        ErrorVo             _errorVo,
        ArrayList<InboxVo>  _inboxList
    )
    {
        if( _errorVo != null &&
            _errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE )
        {
            // TODO When inbox list has been loaded successfully,
            //      processes here.
            
            if( _inboxList != null && _inboxList.size() > 0 )
            {
                mInboxList.addAll( _inboxList );
                mInboxListAdapter.notifyDataSetChanged();
            }
        }
    }
}
