package edu.sdsmt.team4.MobileProject2;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.regex.*;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * Override for creating an options menu
     * @param menu menu to display
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_help, menu);
        return true;
    }

    /**
     * Override for selecting a menu item
     * @param item Item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Parameterize the builder
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.help_msg);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();
        return true;
    }

    // handle login button click
    public void login(final View view) {
        Pattern email_regex = Pattern.compile(".*@.*\\..*");
        final String email = ((EditText)findViewById(R.id.email)).getText().toString();
        final String password = ((EditText)findViewById(R.id.password)).getText().toString();

        if (email.length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(R.string.Error);
            builder.setMessage(R.string.no_name_entered);
            builder.create().show();
            return;
        }
        if (!email_regex.matcher(email).matches()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(R.string.Error);
            builder.setMessage(R.string.error_username_format);
            builder.create().show();
            return;
        }
        if (password.length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(R.string.Error);
            builder.setMessage(R.string.no_password_entered);
            builder.create().show();
            return;
        }

        // try to login
        MonitorCloud.signIn(email, password, this);
    }

    /**
     * Handles clicks for the account creation button.
     * @param view The current game view
     */
    public void onCreateAccountButtonClick(View view) {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void SignInSuccess() {
        final Intent intent = new Intent(this, WaitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void SignInFail(String message) {
        // show error message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.Error);
        builder.setMessage(message);
        builder.create().show();
    }
}
