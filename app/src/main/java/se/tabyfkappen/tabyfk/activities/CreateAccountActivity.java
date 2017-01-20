package se.tabyfkappen.tabyfk.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import javax.net.ssl.HttpsURLConnection;
import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.dao.UserDataSource;

public class CreateAccountActivity extends AppCompatActivity {
    private Button mCreateAccount, mCancel, mConditionTerms;
    UserDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initButtons();
        initDatabase();

        // Activate on click
        setCancelButtonOnClick();
        setConditionTermsOnClick();
        setCreateAccountOnClick();
    }

    private void initButtons() {
        mCancel = (Button) findViewById(R.id.bCancel);
        mConditionTerms = (Button) findViewById(R.id.bTermsCondition);
        mCreateAccount = (Button) findViewById(R.id.bCreateAccount);
    }

    private void initDatabase() {
        dataSource = new UserDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setCancelButtonOnClick() {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setCreateAccountOnClick() {
        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText etFirstName = (EditText) findViewById(R.id.etFirstName);
                EditText etLastName = (EditText) findViewById(R.id.etLastName);
                EditText etEmail = (EditText) findViewById(R.id.etEmail);
                EditText etPassword = (EditText) findViewById(R.id.etPassword);
                EditText etPasswordVerify = (EditText) findViewById(R.id.etPasswordVerify);
                EditText etCode= (EditText) findViewById(R.id.etCode);

                CheckBox cbTermConditions = (CheckBox) findViewById(R.id.cbTermsCondition);

                String mFirstName = etFirstName.getText().toString();
                String mLastName = etLastName.getText().toString();
                String mEmail = etEmail.getText().toString().toLowerCase().trim();
                String mPassword = etPassword.getText().toString();
                String mPasswordVerify = etPasswordVerify.getText().toString();
                String mCode = etCode.getText().toString().trim();

                if(mFirstName.isEmpty() || mLastName.isEmpty() || mEmail.isEmpty() || mPassword.isEmpty() || mPasswordVerify.isEmpty() || mCode.isEmpty()) {
                    setAlertBox(R.string.empty, R.string.message_all_fields);
                } else if(mPassword.length() < 6) {
                    setAlertBox(R.string.empty, R.string.error_invalid_password);
                } else if (!mPassword.equals(mPasswordVerify)) {
                    setAlertBox(R.string.empty, R.string.error_incorrect_password);
                } else if(!cbTermConditions.isChecked()) {
                    setAlertBox(R.string.error_condition, R.string.error_condition_accept);
                } else if(!mEmail.contains("@")) {
                    setAlertBox(R.string.error_email, R.string.error_email_message);
                } else {
                    new CreateAccountTask(mFirstName, mLastName, mEmail, mPassword, mCode).execute();
                }
            }
        });
    }

    private void setAlertBox(int title, int message) {
        new AlertDialog.Builder(CreateAccountActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }

    private void setConditionTermsOnClick() {
        mConditionTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CreateAccountActivity.this)
                        .setTitle(R.string.button_read_terms_and_conditions)
                        .setMessage(R.string.terms_and_condition)
                        .setPositiveButton("Stäng", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        }).show();
            }
        });
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

    private class CreateAccountTask extends AsyncTask<Void, Void, String> {
        private final String mFirstName;
        private final String mLastName;
        private final String mEmail;
        private final String mPassword;
        private final String mCode;

        CreateAccountTask(String firstName, String lastName, String email,
                      String password, String code) {
            this.mFirstName = firstName;
            this.mLastName = lastName;
            this.mEmail = email;
            this.mPassword = password;
            this.mCode = code;
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection connection;
            int responseCode;
            String response = "";

            try {
                URL url = new URL("https://www.tabyfkappen.se/api/v1/create");
                connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder uriParams = new Uri.Builder()
                        .appendQueryParameter("first_name", mFirstName)
                        .appendQueryParameter("last_name", mLastName)
                        .appendQueryParameter("email", mEmail)
                        .appendQueryParameter("password", mPassword)
                        .appendQueryParameter("activation_code", mCode);
                String query = uriParams.build().getEncodedQuery();

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    response = readFromStream(connection.getInputStream());
                } else {
                    response = responseCode + "";
                }
                connection.connect();

            } catch(IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        private String readFromStream(InputStream in) {
            BufferedReader br = null;
            StringBuilder response = new StringBuilder();

            try {
                br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(final String msg) {
            if(msg.contains("401")) {
                setAlertBox(R.string.activation_code, R.string.activation_code_used);
            } else if(msg.contains("404")) {
                setAlertBox(R.string.activation_code, R.string.activation_code_invalid);
            } else if(msg.contains("403")) {
                setAlertBox(R.string.error_email, R.string.error_email_used);
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    String token = jsonObject.getString("token");

                    // Create new user in database and log in
                    dataSource.create(mEmail, mPassword, token);
                    Intent deals = new Intent(CreateAccountActivity.this, SuperDealsActivity.class);
                    startActivity(deals);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
