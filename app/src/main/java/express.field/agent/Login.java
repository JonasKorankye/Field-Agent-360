package express.field.agent;

import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import express.field.agent.Pref.AppPref;
import express.field.agent.Request.UserLogin;


/**
 * Created by jonas korankye  on 17/10/23.
 */
public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        if (!new AppPref().getBooleanValue(this, AppPref.HAS_LOGIN)) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        } else {
            setContentView(R.layout.agent_login);
            final AppCompatButton loginButton = (AppCompatButton) findViewById(R.id.login);

            final AppCompatEditText emailEditText = (AppCompatEditText) findViewById(R.id.email);
            final AppCompatEditText passwordEditText = (AppCompatEditText) findViewById(R.id.password);
            emailEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String password = passwordEditText.getText().toString();
                    loginButton.setEnabled(FunUtils.isValidEmail(charSequence.toString()) && password.trim().length() >= 6);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            passwordEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    loginButton.setEnabled(FunUtils.isValidEmail(emailEditText.getText().toString()) && charSequence.toString().trim().length() >= 6);

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = passwordEditText.getText().toString();
                        processDialog.showDialog(Login.this, "Signing in Agent...");
                        new UserLogin() {
                            @Override
                            protected void onLoginRequestComplete(boolean status, String message) {
                                processDialog.dismiss();
                                if (status) {
                                    new AppPref().setBooleanValue(Login.this, AppPref.HAS_LOGIN, status);
                                    new AppPref().setStringValue(Login.this, AppPref.AGENT_EMAIL, emailEditText.getText().toString());
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                } else {
                                    FunUtils.showMessage(Login.this, message);
                                }
                            }
                        }.loginRequest(password, emailEditText.getText().toString());
                    }
                });


        }

    }

    ProcessDialog processDialog = new ProcessDialog();


}
