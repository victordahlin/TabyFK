package se.tabyfkappen.tabyfk.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import se.tabyfkappen.tabyfk.R;

public class CompanyActivity extends AppCompatActivity {
    private Button mAllOffers;
    private Intent companyIntent;
    private ImageButton mWebsite;
    private ImageButton mEmail;
    private ImageButton mMobile;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_company);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        mAllOffers = (Button) findViewById(R.id.bCompanyAllOffers);
        mWebsite = (ImageButton) findViewById(R.id.ibWebsite);
        mEmail = (ImageButton) findViewById(R.id.ibEmail);
        mMobile = (ImageButton) findViewById(R.id.ibMobile);

        // Add toggle switch in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTextFields();
        setAllOffersOnClick();
    }

    private void websiteOnClick(final String website) {
        mWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                startActivity(intent);
            }
        });
    }

    private void emailOnClick(final String email, final String name) {
        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Hej " + name + "!");
                startActivity(intent);
            }
        });
    }

    private void mobileOnClick(final String mobile) {
        mMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(mobile));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.getStackTrace();
                }
            }
        });
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
        companyIntent = getIntent();
        String name = companyIntent.getStringExtra("name");
        String address = companyIntent.getStringExtra("address");
        String mobile = "tel:" + companyIntent.getStringExtra("mobile");
        String openingHours = companyIntent.getStringExtra("opening_hours");
        String longTermDeal = companyIntent.getStringExtra("long_term_deal");
        String email = companyIntent.getStringExtra("email");
        String imageFilePath = companyIntent.getStringExtra("imageFilePath");
        String website = companyIntent.getStringExtra("url");

        if(website == null || website.isEmpty()) {
            website = "https://www.google.se";
        }

        if(!website.startsWith("http://") && !website.startsWith("https://")) {
            website = "http://" + website;
        }

        SimpleDraweeView ivCompany = (SimpleDraweeView) findViewById(R.id.ivCompany);
        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
        TextView tvOpeningHours = (TextView) findViewById(R.id.tvOpeningHours);
        TextView tvLongTermDeal = (TextView) findViewById(R.id.tvLongTermDeal);

        // Apply URL for current offer
        String url = "https://www.tabyfkappen.se/api/v1/image/" + imageFilePath;
        Uri imageUri = Uri.parse(url);
        ivCompany.setImageURI(imageUri);

        tvAddress.setText("Besöksadress:\n" + address);
        tvOpeningHours.setText("Öppettider:\n" + openingHours);
        tvLongTermDeal.setText(longTermDeal);

        mobileOnClick(mobile);
        emailOnClick(email, name);
        websiteOnClick(website);

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
