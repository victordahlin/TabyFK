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
import android.widget.GridView;
import android.widget.ListView;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.sql.SQLException;
import se.tabyfkappen.tabyfk.dao.UserDataSource;
import se.tabyfkappen.tabyfk.helpers.RestClient;
import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.adapters.CategoryAdapter;
import se.tabyfkappen.tabyfk.models.Category;
import se.tabyfkappen.tabyfk.models.User;

public class CategoryListActivity extends AppCompatActivity {
    private ListView mDrawerList;
    private GridView mGridView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Button mCompanyListButton;
    private Button mShowOffers;
    private UserDataSource mDataSource;
    private User mUser;
    private CategoryAdapter mCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_category_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initGUIElements();
        initDatabase();

        setGridViewOnClick();
        setCategoryAdapter();
        addDrawerItems();
        setupDrawer();
        setCompanyOnClick();
        setShowOffersOnClick();

        // Add toggle switch in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initGUIElements() {
        mGridView = (GridView) findViewById(R.id.gList);
        mDrawerList = (ListView) findViewById(R.id.lNavDeals);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mCompanyListButton = (Button) findViewById(R.id.bCompanyList);
        mShowOffers = (Button) findViewById(R.id.bShowOffers);
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

    private void setShowOffersOnClick() {
        mShowOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offers = new Intent(CategoryListActivity.this, SuperDealsActivity.class);
                startActivity(offers);
            }
        });
    }

    private void setCompanyOnClick() {
        mCompanyListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent temporary = new Intent(CategoryListActivity.this, CompanyListActivity.class);
                startActivity(temporary);
            }
        });
    }

    private void setCategoryAdapter() {
        mCategoryAdapter = new CategoryAdapter(this,
                RestClient.getInstance(mUser.getToken()).getCategories());
        mGridView.setAdapter(mCategoryAdapter);
    }

    private void setGridViewOnClick() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent categoryIntent = new Intent(CategoryListActivity.this, CategoryActivity.class);
                Category category = (Category) mGridView.getItemAtPosition(position);
                categoryIntent.putExtra("categoryId", category.getId());
                startActivity(categoryIntent);
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
                        Intent superDeals = new Intent(CategoryListActivity.this, SuperDealsActivity.class);
                        startActivity(superDeals);
                        break;
                    case 1:
                        Intent app = new Intent(CategoryListActivity.this, AboutAppActivity.class);
                        startActivity(app);
                        break;
                    case 2:
                        Intent tabyfk = new Intent(CategoryListActivity.this, AboutTabyFKActivity.class);
                        startActivity(tabyfk);
                        break;
                    case 3:
                        Intent logout = new Intent(CategoryListActivity.this, LoginActivity.class);
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
            mCategoryAdapter.notifyDataSetChanged();
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
