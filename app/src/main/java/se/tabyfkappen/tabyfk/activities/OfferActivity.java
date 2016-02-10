package se.tabyfkappen.tabyfk.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.dao.UserDataSource;
import se.tabyfkappen.tabyfk.helpers.RestClient;
import se.tabyfkappen.tabyfk.models.User;

public class OfferActivity extends AppCompatActivity {
    private Button mOfferUse;
    private TextView tvOffer;
    private TextView tvOfferUsed;
    private int mOfferId;
    private UserDataSource dataSource;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        // Find toolbar and activate it
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Activate back arrow in the bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SimpleDraweeView ivOffer = (SimpleDraweeView) findViewById(R.id.ivOffer);
        tvOffer = (TextView) findViewById(R.id.tvOffer);
        mOfferUse = (Button) findViewById(R.id.bOfferUse);
        tvOfferUsed = (TextView) findViewById(R.id.tvOfferUse);

        Intent offerIntent = getIntent();
        mOfferId = offerIntent.getIntExtra("id", 0);
        String name = offerIntent.getStringExtra("name");
        String description = offerIntent.getStringExtra("description");
        String imageFilePath = offerIntent.getStringExtra("imageFilePath");

        // Init database handler
        dataSource = new UserDataSource(this);
        try {
            dataSource.open();
            user = dataSource.getUser();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Apply URL for current offer
        String url = "https://www.tabyfkappen.se/api/v1/image/" + imageFilePath;
        Uri imageUri = Uri.parse(url);
        ivOffer.setImageURI(imageUri);
        tvOffer.setText(description);

        getSupportActionBar().setTitle(name);
        setOfferUseOnClick();
    }

    private void setOfferUseOnClick() {
        mOfferUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(OfferActivity.this)
                        .setTitle("")
                        .setMessage("Vill du förbruka detta erbjudande?")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = "https://www.tabyfkappen.se/api/v1/offer?token="
                                        + user.getToken() + "&offer_id=" + mOfferId;
                                new OfferUseTask().execute(url);
                            }
                        })
                        .setNegativeButton("Nej", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
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

    private class OfferUseTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                return urlConnection.getResponseCode();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(final Integer code) {
            if (code == 200) {
                tvOffer.setVisibility(View.INVISIBLE);
                mOfferUse.setVisibility(View.INVISIBLE);
                tvOfferUsed.setVisibility(View.VISIBLE);
                // Remove offer from the list
                RestClient.getInstance(user.getToken()).removeOffer(mOfferId);
            }
        }
    }


}
