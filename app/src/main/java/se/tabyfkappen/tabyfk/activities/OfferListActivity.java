package se.tabyfkappen.tabyfk.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.sql.SQLException;
import java.util.ArrayList;
import se.tabyfkappen.tabyfk.helpers.RestClient;
import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.adapters.OfferAdapter;
import se.tabyfkappen.tabyfk.dao.UserDataSource;
import se.tabyfkappen.tabyfk.models.Offer;
import se.tabyfkappen.tabyfk.models.User;

/**
 * Created by Victor on 2016-01-21.
 * Updated: 2017-01-21
 */
public class OfferListActivity extends AppCompatActivity {
    private ListView mListView;
    private UserDataSource mDataSource;
    private User mUser;
    private Button mShowOffers;
    private TextView mTvOfferFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add toggle switch in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        initDatabase();
        init();

        String name = getIntent().getStringExtra("offerName");

        mTvOfferFor.setText(getResources().getString(R.string.button_offers) + " för " + name);
        getSupportActionBar().setTitle(name);

        setCompanyAdapter();
        setListOnClick();
        setShowOffersOnClick();
    }

    private void init() {
        mListView = (ListView) findViewById(R.id.lvItems);
        mShowOffers = (Button) findViewById(R.id.bShowOffers);
        mTvOfferFor = (TextView) findViewById(R.id.tvOffersFor);
    }

    private void initDatabase() {
        mDataSource = new UserDataSource(this);
        try {
            mDataSource.open();
            mUser = mDataSource.getUser();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change to Super Deals
     */
    private void setShowOffersOnClick() {
        mShowOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offers = new Intent(OfferListActivity.this, SuperDealsActivity.class);
                startActivity(offers);
            }
        });
    }

    /**
     * Add name, desc and file path to next activity
     * if user click on selected item
     */
    private void setListOnClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent offerIntent = new Intent(OfferListActivity.this, OfferActivity.class);

                Offer offer = (Offer) mListView.getItemAtPosition(position);
                offerIntent.putExtra("name", offer.getName());
                offerIntent.putExtra("description", offer.getDescription());
                offerIntent.putExtra("imageFilePath", offer.getImageFilePath());

                startActivity(offerIntent);
            }
        });
    }

    /**
     * If offer empty show error and close activity.
     * Otherwise add items to the list and display.
     */
    private void setCompanyAdapter() {
        int offerId = getIntent().getIntExtra("offerId", 0);
        ArrayList<Offer> offers = RestClient.getInstance(mUser.getToken()).getSelectedOffers(offerId);

        if(offers.isEmpty() || offers.size() < 1) {
            new AlertDialog.Builder(OfferListActivity.this)
                    .setMessage(R.string.message_no_offers)
                    .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }

        OfferAdapter mOfferAdapter = new OfferAdapter(this,offers);
        mListView.setAdapter(mOfferAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        try {
            mDataSource.open();
            mUser = mDataSource.getUser();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        mDataSource.close();
        super.onPause();
    }

}
