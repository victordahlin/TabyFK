package se.tabyfkappen.tabyfk.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
import se.tabyfkappen.tabyfk.models.User;

public class LoginActivity extends AppCompatActivity {
    Button mPasswordRemind, mLogin, mCreateAccount;
    private UserDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initButtons();
        initDatabase();

        checkUserLogin();
        setCreateAccountOnClick();
        setLoginOnClick();
        setPasswordRemindOnClick();
    }

    private void initButtons() {
        mPasswordRemind = (Button) findViewById(R.id.bPasswordRemind);
        mLogin = (Button) findViewById(R.id.bLogin);
        mCreateAccount = (Button) findViewById(R.id.bCreateAccount);
    }

    private void initDatabase() {
        dataSource = new UserDataSource(LoginActivity.this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkUserLogin() {
        if(dataSource.size() > 0) {
            User user = dataSource.getUser();
            String email = user.getEmail();
            String token = user.getToken();

            if((email != null && !email.isEmpty()) && (token != null && !token.isEmpty())) {
                Intent deals = new Intent(LoginActivity.this, SuperDealsActivity.class);
                startActivity(deals);
            }
        }
    }

    private void setPasswordRemindOnClick() {
        mPasswordRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passwordRemind = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(passwordRemind);
            }
        });
    }

    private void setLoginOnClick() {
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText etEmail = (EditText) findViewById(R.id.etEmail);
                EditText etPassword = (EditText) findViewById(R.id.etPassword);

                String mEmail = etEmail.getText().toString();
                String mPassword = etPassword.getText().toString();

                if(mPassword.isEmpty() || mEmail.isEmpty() || !mEmail.contains("@")) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(R.string.button_login_error)
                            .setMessage(R.string.email_and_password_message)
                            .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            }).show();
                } else {
                    new UserLoginTask(mEmail, mPassword).execute();
                }
            }
        });
    }

    private void setCreateAccountOnClick() {
        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createAccount = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(createAccount);
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

    private class UserLoginTask extends AsyncTask<Void, Void, String> {
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection connection;
            int responseCode;
            String response = "";

            try {
                URL url = new URL("https://www.tabyfkappen.se/api/v1/login");
                connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder uriParams = new Uri.Builder()
                        .appendQueryParameter("email", mEmail)
                        .appendQueryParameter("password", mPassword);
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
                } else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
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
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(R.string.button_login_error)
                        .setMessage(R.string.error_email_password)
                        .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        }).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    String mToken = jsonObject.getString("token");

                    // Save new credentials (assume user logout)
                    dataSource.update(mEmail, mPassword, mToken);

                    final Intent offers = new Intent(LoginActivity.this, SuperDealsActivity.class);
                    startActivity(offers);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
