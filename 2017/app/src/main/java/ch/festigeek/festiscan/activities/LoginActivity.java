package ch.festigeek.festiscan.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import ch.festigeek.festiscan.R;
import ch.festigeek.festiscan.communications.RequestPOST;
import ch.festigeek.festiscan.interfaces.ICallback;
import ch.festigeek.festiscan.interfaces.IConstant;
import ch.festigeek.festiscan.utils.Utilities;
import org.json.JSONObject;

import ch.festigeek.festiscan.interfaces.IURL;

public class LoginActivity extends AppCompatActivity implements IConstant, IURL {

    private TextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = (TextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        String email = Utilities.getFromSharedPreferences(this, "email");
        if (email != null) {
            mEmailView.setText(email);
        }
        String password = Utilities.getFromSharedPreferences(this, "password");
        if (password != null) {
            mPasswordView.setText(password);
        }
    }

    private void attemptLogin() {
        String email = mEmailView.getText().toString();
        Utilities.putToSharedPreferences(this, "email", email);
        String password = mPasswordView.getText().toString();
        Utilities.putToSharedPreferences(this, "password", password);
        Map<String, String> content = new HashMap<>();
        content.put("email", email);
        content.put("password", password);
        showProgress(true);
        new RequestPOST(new ICallback<String>() {
            @Override
            public void success(String result) {
                showProgress(false);
                try {
                    JSONObject obj = new JSONObject(result);
                    String token = obj.getString("token");
                    Utilities.putToSharedPreferences(LoginActivity.this, "token", token);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(intent);
                } catch (Exception e) {
                    Log.e(LOG_NAME, e.getMessage());
                }
            }

            @Override
            public void failure(Exception ex) {
                showProgress(false);
                Log.e(LOG_NAME, "-> " + ex.getMessage());
            }
        }, "", BASE_URL + LOGIN, content).execute();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

