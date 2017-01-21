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
import android.widget.ListView;
import java.sql.SQLException;
import java.util.ArrayList;
import se.tabyfkappen.tabyfk.helpers.RestClient;
import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.adapters.CompanyAdapter;
import se.tabyfkappen.tabyfk.dao.UserDataSource;
import se.tabyfkappen.tabyfk.models.Company;
import se.tabyfkappen.tabyfk.models.User;

/**
 * Created by Victor on 2016-01-21.
 * Updated: 2017-01-21
 */
public class CategoryActivity extends AppCompatActivity {
    private ListView mListView;
    private UserDataSource dataSource;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.lvItems);

        initDatabase();

        setCompanyAdapter();
        setCompanyListOnClick();

        // Add toggle switch in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
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

    private void setCompanyAdapter() {
        int categoryId = getIntent().getIntExtra("categoryId", 0);
        ArrayList<Company> companies = RestClient.getInstance(
                user.getToken()).getSelectedCompanies(categoryId);

        if(companies.isEmpty() || companies.size() < 1) {
            new AlertDialog.Builder(CategoryActivity.this)
                    .setMessage(R.string.message_no_offers)
                    .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }

        // Create the adapter to convert the array to views
        CompanyAdapter companyAdapter = new CompanyAdapter(this, companies);
        // Attach the adapter to a ListView
        mListView.setAdapter(companyAdapter);
    }

    private void setCompanyListOnClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent companyIntent = new Intent(CategoryActivity.this, CompanyActivity.class);
                Company offer = (Company) mListView.getItemAtPosition(position);
                companyIntent.putExtra("name", offer.getName());
                companyIntent.putExtra("category_id", offer.getCategoryId());
                companyIntent.putExtra("address", offer.getAddress());
                companyIntent.putExtra("mobile", offer.getMobile());
                companyIntent.putExtra("opening_hours", offer.getOpeningHours());
                companyIntent.putExtra("long_term_deal", offer.getLongTermDeal());
                companyIntent.putExtra("email", offer.getEmail());
                companyIntent.putExtra("imageFilePath", offer.getImageFilePath());
                startActivity(companyIntent);
            }
        });
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
            dataSource.open();
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
