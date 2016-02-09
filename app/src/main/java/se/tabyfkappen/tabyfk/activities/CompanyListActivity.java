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
import java.sql.SQLException;
import se.tabyfkappen.tabyfk.helpers.RestClient;
import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.adapters.CompanyAdapter;
import se.tabyfkappen.tabyfk.dao.UserDataSource;
import se.tabyfkappen.tabyfk.models.Company;
import se.tabyfkappen.tabyfk.models.User;

public class CompanyListActivity extends AppCompatActivity {
    private ListView mDrawerList, mListView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Button mCategoryListButton;
    private Button mShowOffers;
    private UserDataSource mDataSource;
    private User mUser;
    private CompanyAdapter mCompanyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.lvItems);
        mDrawerList = (ListView) findViewById(R.id.lNavDeals);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mCategoryListButton = (Button) findViewById(R.id.bCategoryList);
        mShowOffers = (Button) findViewById(R.id.bShowOffers);

        // Init database handler
        mDataSource = new UserDataSource(this);
        try {
            mDataSource.open();
            mUser = mDataSource.getUser();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setCompanyListOnClick();
        setCompanyAdapter();
        addDrawerItems();
        setupDrawer();
        setCategoryOnClick();
        setShowOffersOnClick();

        // Add toggle switch in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setShowOffersOnClick() {
        mShowOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offers = new Intent(CompanyListActivity.this, SuperDealsActivity.class);
                startActivity(offers);
            }
        });
    }

    public void setCategoryOnClick() {
        mCategoryListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent temporary = new Intent(CompanyListActivity.this, CategoryListActivity.class);
                startActivity(temporary);
            }
        });
    }

    private void setCompanyAdapter() {
        mCompanyAdapter = new CompanyAdapter(this,
                RestClient.getInstance(mUser.getToken()).getCompanies());
        mListView.setAdapter(mCompanyAdapter);
    }

    private void setCompanyListOnClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent companyIntent = new Intent(CompanyListActivity.this, CompanyActivity.class);
                Company company = (Company) mListView.getItemAtPosition(position);
                companyIntent.putExtra("id", company.getId());
                companyIntent.putExtra("name", company.getName());
                companyIntent.putExtra("category_id", company.getCategoryId());
                companyIntent.putExtra("address", company.getAddress());
                companyIntent.putExtra("mobile", company.getMobile());
                companyIntent.putExtra("opening_hours", company.getOpeningHours());
                companyIntent.putExtra("long_term_deal", company.getLongTermDeal());
                companyIntent.putExtra("email", company.getEmail());
                companyIntent.putExtra("imageFilePath", company.getImageFilePath());
                companyIntent.putExtra("url", company.getUrl());
                startActivity(companyIntent);
            }
        });
    }

    private void addDrawerItems() {
        String[] menuItems = { "Erbjudande & Partners", "Om appen", "Om Täby FK", "Logga ut" };
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems);
        mDrawerList.setAdapter(mAdapter);
        // Add listner to the navigation drawer
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent superDeals = new Intent(CompanyListActivity.this, SuperDealsActivity.class);
                        startActivity(superDeals);
                        break;
                    case 1:
                        Intent app = new Intent(CompanyListActivity.this, AboutAppActivity.class);
                        startActivity(app);
                        break;
                    case 2:
                        Intent tabyfk = new Intent(CompanyListActivity.this, AboutTabyFKActivity.class);
                        startActivity(tabyfk);
                        break;
                    case 3:
                        Intent logout = new Intent(CompanyListActivity.this, LoginActivity.class);
                        startActivity(logout);
                        break;
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
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
        // Inflate the menu; this adds items to the action bar if it is present.
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
