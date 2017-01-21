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
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.dao.UserDataSource;
import se.tabyfkappen.tabyfk.helpers.RestClient;
import se.tabyfkappen.tabyfk.models.User;

/**
 * Created by Victor on 2016-01-21.
 * Updated: 2017-01-21
 */
public class OfferActivity extends AppCompatActivity {
    private Button mOfferUse;
    private TextView tvOffer;
    private TextView tvOfferUsed;
    private int mOfferId;
    private UserDataSource mDataSource;
    private User user;
    private SimpleDraweeView ivOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        // Find toolbar and activate it
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Activate back arrow in the bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        init();
        initDatabase();

        // Get data from other activity
        Intent offerIntent = getIntent();
        mOfferId = offerIntent.getIntExtra("id", 0);
        String name = offerIntent.getStringExtra("name");
        String description = offerIntent.getStringExtra("description");
        String imageFilePath = offerIntent.getStringExtra("imageFilePath");

        // Apply URL for current offer
        String url = "https://www.tabyfkappen.se/api/v1/image/" + imageFilePath;
        Uri imageUri = Uri.parse(url);
        ivOffer.setImageURI(imageUri);
        tvOffer.setText(description);

        getSupportActionBar().setTitle(name);
        setOfferUseOnClick();
    }

    private void init() {
        ivOffer = (SimpleDraweeView) findViewById(R.id.ivOffer);
        tvOffer = (TextView) findViewById(R.id.tvOffer);
        mOfferUse = (Button) findViewById(R.id.bOfferUse);
        tvOfferUsed = (TextView) findViewById(R.id.tvOfferUse);
    }

    private void initDatabase() {
        mDataSource = new UserDataSource(this);
        try {
            mDataSource.open();
            user = mDataSource.getUser();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void setOfferUseOnClick() {
        mOfferUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(OfferActivity.this)
                        .setTitle(R.string.empty)
                        .setMessage(R.string.message_use_offer)
                        .setPositiveButton(R.string.message_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = "https://www.tabyfkappen.se/api/v1/offer?token="
                                        + user.getToken() + "&offer_id=" + mOfferId;
                                new OfferUseTask().execute(url);
                            }
                        })
                        .setNegativeButton(R.string.message_no, new DialogInterface.OnClickListener() {
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
