package edu.sdsmt.team4.MobileProject2;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {
    private final Pattern email_regex = Pattern.compile(".*@.*\\..*");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    // handle submit button click
    public void create_account(final View view) {
        final String email = ((EditText)findViewById(R.id.input_email)).getText().toString();
        final String pass1 = ((EditText)findViewById(R.id.input_pass1)).getText().toString();
        final String pass2 = ((EditText)findViewById(R.id.input_pass2)).getText().toString();

        if (email.length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(R.string.Error);
            builder.setMessage(R.string.no_name_entered);
            builder.create().show();
            return;
        }

        if (pass1.length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(R.string.Error);
            builder.setMessage(R.string.no_password_entered);
            builder.create().show();
            return;
        }

        if (!this.email_regex.matcher(email).matches()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(R.string.Error);
            builder.setMessage(R.string.error_username_format);
            builder.create().show();
            return;
        }

        if (!pass1.equals(pass2)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(R.string.Error);
            builder.setMessage(R.string.passwords_do_not_match);
            builder.create().show();
            return;
        }

        MonitorCloud.createUser(email, pass1, this);
    }

    public void CreateUserSuccess() {
        Toast.makeText(getApplicationContext(), R.string.create_account_toast, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void CreateUserFail(String message) {
        // show error message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.Error);
        builder.setMessage(message);
        builder.create().show();
    }
}
