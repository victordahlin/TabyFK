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
import android.widget.ListView;
import android.widget.TextView;
import java.sql.SQLException;
import se.tabyfkappen.tabyfk.helpers.RestClient;
import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.dao.UserDataSource;
import se.tabyfkappen.tabyfk.models.User;

public class AboutAppActivity extends AppCompatActivity {
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private UserDataSource mDataSource;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerList = (ListView) findViewById(R.id.lNavApp);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        TextView mAboutApp = (TextView) findViewById(R.id.tvAboutApp);

        initDatabase();

        // Get offers JSON from RestAPI and convert to string
        mAboutApp.setText(RestClient.getInstance(mUser.getToken()).getAboutApp());

        addDrawerItems();
        setupDrawer();

        // Add toggle switch in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * Initilize from data source to receive from API
     */
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
     * Change activity on selected item
     */
    private void addDrawerItems() {
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Constants.menuItems);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0 :
                        Intent deals = new Intent(AboutAppActivity.this, SuperDealsActivity.class);
                        deals.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(deals);
                    case 1:
                        break;
                    case 2:
                        Intent about = new Intent(AboutAppActivity.this, AboutTabyFKActivity.class);
                        about.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(about);
                        break;
                    case 3:
                        mDataSource.update(mUser.getEmail(), mUser.getEmail(), null);
                        Intent logout = new Intent(AboutAppActivity.this, LoginActivity.class);
                        startActivity(logout);
                        break;

                }
            }
        });
    }

    /**
     * Drawer config
     */
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
