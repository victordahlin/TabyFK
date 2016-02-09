package se.tabyfkappen.tabyfk.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import se.tabyfkappen.tabyfk.R;

public class ResetPasswordActivity extends AppCompatActivity {
    private Button mPasswordRemindButton, mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find buttons
        mPasswordRemindButton = (Button) findViewById(R.id.bGetPassword);
        mCancelButton = (Button) findViewById(R.id.bPasswordCancel);

        // Activate event for buttons
        setPasswordRemindOnClick();
        setCancelOnClick();
    }

    private void setPasswordRemindOnClick() {
        mPasswordRemindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etEmail = (EditText) findViewById(R.id.etEmail);
                String mEmail = etEmail.getText().toString();

                if(mEmail.isEmpty() || !mEmail.contains("@")) {
                    new AlertDialog.Builder(ResetPasswordActivity.this)
                            .setTitle("Kunde inte skicka ut nytt lösenord")
                            .setMessage("Ange en e-mail eller e-mail är felaktig")
                            .setPositiveButton("Stäng", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            }).show();
                } else {
                    new ResetPasswordTask(mEmail).execute();
                }
            }
        });
    }

    private void setCancelOnClick() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setAlertBox(String title, String message) {
        new AlertDialog.Builder(ResetPasswordActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Stäng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private class ResetPasswordTask extends AsyncTask<Void, Void, Integer> {
        private final String mEmail;

        ResetPasswordTask(String email) {
            this.mEmail = email;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            HttpURLConnection connection;
            int responseCode = 404;

            try {
                URL url = new URL("https://www.tabyfkappen.se/api/v1/reset");
                connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder uriParams = new Uri.Builder()
                        .appendQueryParameter("email", mEmail);
                String query = uriParams.build().getEncodedQuery();

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                responseCode = connection.getResponseCode();
                connection.connect();

            } catch(IOException e) {
                e.printStackTrace();
            }
            return responseCode;
        }

        @Override
        protected void onPostExecute(final Integer msg) {
            if(msg == 404) {
                setAlertBox("Ogiltig email", "Den givna e-mail existerar ej");
            } else {
                setAlertBox("Mail utskickat", "Tack, vi har skickat ut en länk till din e-mail");
            }
        }
    }


}
