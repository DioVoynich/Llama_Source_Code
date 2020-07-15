package com.fortune.llama;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    EditText username, email, password, dob, country;
    RadioGroup gender;
    Button register;
    TextView backToLogin;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        dob = findViewById(R.id.dob);
        country = findViewById(R.id.country);
        gender = findViewById(R.id.gender);
        register = findViewById(R.id.register_button);
        backToLogin = findViewById(R.id.cancel);
        dbHelper = new DatabaseHelper(this);

        // For registration button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // All the user information here
                String usernameValue = username.getText().toString();
                String passwordValue = password.getText().toString();
                String emailValue = email.getText().toString();
                String dobValue = dob.getText().toString();
                String countryValue = country.getText().toString();
                RadioButton whichGender = findViewById(gender.getCheckedRadioButtonId());
                String genderValue = whichGender.getText().toString();

                // if user already registered or not
                boolean exiting = dbHelper.checkRegister(usernameValue, emailValue);

                // check the entries.
                if (usernameValue.isEmpty() || passwordValue.isEmpty() || emailValue.isEmpty()
                        || dobValue.isEmpty() || countryValue.isEmpty() || genderValue.isEmpty()) {

                    // empty message
                    Toast.makeText(RegistrationActivity.this, "Please Fill Out All!", Toast.LENGTH_SHORT).show();

                } else {
                    if (!exiting) {
                        ContentValues content_value = new ContentValues();
                        content_value.put("username", usernameValue);
                        content_value.put("password", passwordValue);
                        content_value.put("email", emailValue);
                        content_value.put("country", countryValue);
                        content_value.put("dob", dobValue);
                        content_value.put("gender", genderValue);
                        dbHelper.insertUser(content_value);
                        Toast.makeText(RegistrationActivity.this, "User Registered!", Toast.LENGTH_SHORT).show();
                        Intent moveToLogin = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(moveToLogin);
                    } else {
                        Toast.makeText(RegistrationActivity.this, "This User Already Exists!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Going back to the login page
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToLogin = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(moveToLogin);
            }
        });

    }
}