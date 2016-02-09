package se.tabyfkappen.tabyfk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import se.tabyfkappen.tabyfk.R;

public class CompanyActivity extends AppCompatActivity {
    private Button mAllOffers;
    private Intent companyIntent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        mAllOffers = (Button) findViewById(R.id.bCompanyAllOffers);

        // Add toggle switch in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTextFields();
        setAllOffersOnClick();
    }

    private void setAllOffersOnClick() {
        mAllOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int offerId = companyIntent.getIntExtra("id",0);
                String offerName = companyIntent.getStringExtra("name");

                Intent offerList = new Intent(CompanyActivity.this, OfferListActivity.class);
                offerList.putExtra("offerId", offerId);
                offerList.putExtra("offerName", offerName);
                startActivity(offerList);
            }
        });
    }

    private void setTextFields() {
        // Get content for Company list
        companyIntent = getIntent();
        String name = companyIntent.getStringExtra("name");
        String address = companyIntent.getStringExtra("address");
        String mMobile = companyIntent.getStringExtra("mobile");
        String openingHours = companyIntent.getStringExtra("opening_hours");
        String longTermDeal = companyIntent.getStringExtra("long_term_deal");
        String email = companyIntent.getStringExtra("email");
        String imageFilePath = companyIntent.getStringExtra("imageFilePath");
        String website = companyIntent.getStringExtra("url");

        ImageView ivCompany = (ImageView) findViewById(R.id.ivCompany);
        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
        TextView tvMobile = (TextView) findViewById(R.id.tvMobile);
        TextView tvOpeningHours = (TextView) findViewById(R.id.tvOpeningHours);
        TextView tvLongTermDeal = (TextView) findViewById(R.id.tvLongTermDeal);
        TextView tvEmail = (TextView) findViewById(R.id.tvEmail);
        TextView tvWebsite = (TextView) findViewById(R.id.tvWebsite);

        // Apply URL for current offer
        String url = "https://www.tabyfkappen.se/api/v1/image/" + imageFilePath;
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.ic_texture_black_24dp)
                .showImageOnFail(R.drawable.ic_texture_black_24dp)
                .showImageOnLoading(R.drawable.ic_texture_black_24dp).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(url, ivCompany, options);

        tvAddress.setText("Besöksadress:\n" + address);
        tvMobile.setText("Telefon:\n" + mMobile);
        tvOpeningHours.setText("Öppettider:\n" + openingHours);
        tvLongTermDeal.setText(longTermDeal);
        tvEmail.setText("E-mail:\n" + email);
        tvWebsite.setText("Hemsida:\n" + website);

        getSupportActionBar().setTitle(name);
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

}
