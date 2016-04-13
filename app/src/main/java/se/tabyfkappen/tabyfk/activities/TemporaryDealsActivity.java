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
import java.util.ArrayList;

import se.tabyfkappen.tabyfk.Constants;
import se.tabyfkappen.tabyfk.adapters.TemporaryDealsAdapter;
import se.tabyfkappen.tabyfk.helpers.RestClient;
import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.dao.UserDataSource;
import se.tabyfkappen.tabyfk.models.Offer;
import se.tabyfkappen.tabyfk.models.User;

public class TemporaryDealsActivity extends AppCompatActivity {
    private ListView mDrawerList, mListView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Button mSuperDealsButton;
    private UserDataSource dataSource;
    private User user;
    private Button mPartners;
    private TemporaryDealsAdapter mOfferAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_temporary_deals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        initDatabase();

        setListOnClick();
        setOfferAdapter();
        addDrawerItems();
        setupDrawer();
        setSuperDealsOnClick();
        setPartnersOnClick();

        // Add toggle switch in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void init() {
        mListView = (ListView) findViewById(R.id.lvItems);
        mDrawerList = (ListView) findViewById(R.id.lNavDeals);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSuperDealsButton = (Button) findViewById(R.id.bSuperDeals);
        mPartners = (Button) findViewById(R.id.bTFKpartners);
    }

    private void initDatabase() {
        dataSource = new UserDataSource(this);
        try {
            dataSource.open();
            user = dataSource.getUser();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void setSuperDealsOnClick() {
        mSuperDealsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent temporary = new Intent(TemporaryDealsActivity.this, SuperDealsActivity.class);
                startActivity(temporary);
            }
        });
    }

    public void setPartnersOnClick() {
        mPartners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tfkPartners = new Intent(TemporaryDealsActivity.this, CompanyListActivity.class);
                startActivity(tfkPartners);
            }
        });
    }

    private void setOfferAdapter() {
        ArrayList<Offer> temporaryDeals = RestClient.getInstance(user.getToken()).getTemporaryDeals();
        mOfferAdapter = new TemporaryDealsAdapter(this, temporaryDeals);
        mListView.setAdapter(mOfferAdapter);
    }

    private void setListOnClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent offerIntent = new Intent(TemporaryDealsActivity.this, OfferActivity.class);

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
                        Intent app = new Intent(TemporaryDealsActivity.this, AboutAppActivity.class);
                        startActivity(app);
                        break;
                    case 2:
                        Intent tabyfk = new Intent(TemporaryDealsActivity.this, AboutTabyFKActivity.class);
                        startActivity(tabyfk);
                        break;
                    case 3:
                        dataSource.update("", "", "");
                        Intent logout = new Intent(TemporaryDealsActivity.this, LoginActivity.class);
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
        int id = item.getItemId();
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        try {
            dataSource.open();
            mOfferAdapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }
}
