package express.field.agent;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

import express.field.agent.Helpers.ContextProvider;
import express.field.agent.Pref.AppPref;
import express.field.agent.Pref.GlobalPref;
import express.field.agent.Request.UserLogin;
import express.field.agent.Utils.DialogUtils;
import express.field.agent.Utils.FunUtils;


/**
 * Created by jonas korankye  on 17/10/23.
 */
public class Login extends AppCompatActivity {

    private static GlobalPref mGlobalPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (GlobalPref.getInstance().getBoolean( AppPref.HAS_LOGIN, true)) {

            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        } else {
            setContentView(R.layout.agent_login_a);
            final AppCompatButton loginButton = (AppCompatButton) findViewById(R.id.buttonLogin);

            final EditText agentIdEditText = (EditText) findViewById(R.id.agent_id);
            final EditText passwordEditText = (EditText) findViewById(R.id.password);
            final CheckBox checkBox = (CheckBox) findViewById(R.id.remember_me);
            agentIdEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String password = passwordEditText.getText().toString();
                    loginButton.setEnabled( password.trim().length() >= 1);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(editable.length() >4){
                        agentIdEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.name, 0, R.drawable.done_icon, 0);
                    }

                }
            });
            passwordEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    loginButton.setEnabled(charSequence.toString().trim().length() >= 1);

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            if (checkBox.isChecked()) {
                 GlobalPref.getInstance().put(GlobalPref.AGENT_ID, agentIdEditText.getText().toString());
                GlobalPref.getInstance().put( GlobalPref.AGENT_PASSWORD, agentIdEditText.getText().toString());
            }


                loginButton.setOnClickListener(view -> {
                    String password = passwordEditText.getText().toString();
                    processDialog.showDialog(Login.this, "Signing in Agent...");
                    new UserLogin() {
                        @Override
                        protected void onLoginRequestComplete(boolean status, String message) {
                            processDialog.dismiss();
                            if (status) {
                                GlobalPref.getInstance().put(AppPref.HAS_LOGIN, true);
                                GlobalPref.getInstance().put(GlobalPref.AGENT_ID, agentIdEditText.getText().toString());
                                GlobalPref.getInstance().put( GlobalPref.AGENT_PASSWORD, agentIdEditText.getText().toString());

                                new AppPref().setStringValue(Login.this, AppPref.AGENT_EMAIL, agentIdEditText.getText().toString());
                                startActivity(new Intent(Login.this, MainActivity.class));
                                finish();
                            } else {
                                if(TextUtils.isEmpty(message) || message.equalsIgnoreCase("null")){
                                    message = "Unknown Error";
                                }
                                new DialogUtils().basicDialog(Login.this);
                                FunUtils.showMessage(Login.this, message);
                                FunUtils.showMessage(Login.this, message);
                            }
                        }
                    }.loginRequestNew(password, agentIdEditText.getText().toString());
                });


        }

    }

    ProcessDialog processDialog = new ProcessDialog();



}
