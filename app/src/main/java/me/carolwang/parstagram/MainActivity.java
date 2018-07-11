package me.carolwang.parstagram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

    EditText tvUsername;
    EditText tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        }

        tvUsername = findViewById(R.id.tvUsername);
        tvPassword = findViewById(R.id.tvPassword);

        tvUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        tvPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void login(View v) {
        final String username = tvUsername.getText().toString();
        final String password = tvPassword.getText().toString();
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // transition to home screen
                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(i);
                    tvUsername.setText("");
                    tvPassword.setText("");
                    Toast.makeText(MainActivity.this, "Welcome, "+username+"!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Sorry, login failed. Try again or sign up for new user.", Toast.LENGTH_LONG).show();
                    Log.i("Parstagram", e.toString());
                }
            }
        });
    }

    public void signUp(View v) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        final String username = tvUsername.getText().toString();
        final String password = tvPassword.getText().toString();
        user.setUsername(username);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // transition to home screen
                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(i);
                    tvUsername.setText("");
                    tvPassword.setText("");
                    Toast.makeText(MainActivity.this, "Welcome, "+username+"!", Toast.LENGTH_LONG).show();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Toast.makeText(MainActivity.this, "Sorry, sign up failed. Check your Internet connection.", Toast.LENGTH_LONG).show();
                    Log.i("Parstagram", e.toString());
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
