package se.tabyfkappen.tabyfk.activities;

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
import se.tabyfkappen.tabyfk.helpers.RestClient;
import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.adapters.OfferAdapter;
import se.tabyfkappen.tabyfk.dao.UserDataSource;
import se.tabyfkappen.tabyfk.helpers.ImageHelper;
import se.tabyfkappen.tabyfk.models.Offer;
import se.tabyfkappen.tabyfk.models.User;

public class OfferListActivity extends AppCompatActivity {
    private ListView mListView;
    private UserDataSource mDataSource;
    private User mUser;
    private Button mShowOffers;
    private OfferAdapter mOfferAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add toggle switch in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // init database
        mDataSource = new UserDataSource(this);
        try {
            mDataSource.open();
            mUser = mDataSource.getUser();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        new ImageHelper(getApplicationContext());

        String name = getIntent().getStringExtra("offerName");

        mListView = (ListView) findViewById(R.id.lvItems);
        TextView tvOfferFor = (TextView) findViewById(R.id.tvOffersFor);
        mShowOffers = (Button) findViewById(R.id.bShowOffers);

        tvOfferFor.setText("erbjudande för " + name);
        getSupportActionBar().setTitle(name);

        setCompanyAdapter();
        setListOnClick();
        setShowOffersOnClick();
    }

    private void setShowOffersOnClick() {
        mShowOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offers = new Intent(OfferListActivity.this, SuperDealsActivity.class);
                startActivity(offers);
            }
        });
    }

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

    private void setCompanyAdapter() {
        int offerId = getIntent().getIntExtra("offerId", 0);
        mOfferAdapter = new OfferAdapter(this,
                RestClient.getInstance(mUser.getToken()).getSelectedOffers(offerId));
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
