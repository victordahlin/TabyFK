package se.tabyfkappen.tabyfk.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.sql.SQLException;
import se.tabyfkappen.tabyfk.helpers.RestClient;
import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.dao.UserDataSource;
import se.tabyfkappen.tabyfk.models.Offer;
import se.tabyfkappen.tabyfk.adapters.OfferAdapter;
import se.tabyfkappen.tabyfk.models.User;

public class SuperDealsActivity extends AppCompatActivity {
    private ListView mDrawerList, mListView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Button mTemporaryDealsButton, mPartners;
    private UserDataSource mDataSource;
    private User mUser;
    private OfferAdapter mOfferAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_super_deals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.lvItems);
        mDrawerList = (ListView) findViewById(R.id.lNavDeals);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTemporaryDealsButton = (Button) findViewById(R.id.bTemporaryDeals);
        mPartners = (Button) findViewById(R.id.bTFKpartners);

        // Init database handler
        mDataSource = new UserDataSource(this);
        try {
            mDataSource.open();
            mUser = mDataSource.getUser();
            RestClient.getInstance(mUser.getToken());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addDrawerItems();
        setupDrawer();

        setListOnClick();
        setOfferAdapter();
        setTemporaryDealsOnClick();
        setPartnersOnClick();

        // Add toggle switch in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void setTemporaryDealsOnClick() {
        mTemporaryDealsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent temporary = new Intent(SuperDealsActivity.this, TemporaryDealsActivity.class);
                startActivity(temporary);
            }
        });
    }

    public void setPartnersOnClick() {
        mPartners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tfkPartners = new Intent(SuperDealsActivity.this, CompanyListActivity.class);
                startActivity(tfkPartners);
            }
        });
    }

    private void setOfferAdapter() {
        mOfferAdapter = new OfferAdapter(this,
                RestClient.getInstance(mUser.getToken()).getSuperDeals());
        mListView.setAdapter(mOfferAdapter);
    }

    private void setListOnClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent offerIntent = new Intent(SuperDealsActivity.this, OfferActivity.class);

                Offer offer = (Offer) mListView.getItemAtPosition(position);
                offerIntent.putExtra("id", offer.getId());
                offerIntent.putExtra("name", offer.getName());
                offerIntent.putExtra("description", offer.getDescription());
                offerIntent.putExtra("imageFilePath", offer.getImageFilePath());

                startActivity(offerIntent);
            }
        });
    }

    private void addDrawerItems() {
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Constants.menuItems);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        Intent app = new Intent(SuperDealsActivity.this, AboutAppActivity.class);
                        startActivity(app);
                        break;
                    case 2:
                        Intent about = new Intent(SuperDealsActivity.this, AboutTabyFKActivity.class);
                        startActivity(about);
                        break;
                    case 3:
                        mDataSource.update(mUser.getEmail(), mUser.getEmail(), null);
                        Intent logout = new Intent(SuperDealsActivity.this, LoginActivity.class);
                        startActivity(logout);
                        break;
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        try {
            mDataSource.open();
            mUser = mDataSource.getUser();
            mOfferAdapter.notifyDataSetChanged();
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
