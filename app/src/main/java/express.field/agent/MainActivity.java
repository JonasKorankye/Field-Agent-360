package express.field.agent;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import express.field.agent.Adapters.BillsAdapter;
import express.field.agent.Pages.AgentActivities;
import express.field.agent.Pages.AirtimeVending;
import express.field.agent.Pages.BankOperation;
import express.field.agent.Pages.Banks.BankDeposit;
import express.field.agent.Pages.Banks.BankWithdrawal;
import express.field.agent.Pages.BillPayment;
import express.field.agent.Pages.Funds;
import express.field.agent.Pages.RevenueCollection;
import express.field.agent.Pref.AppPref;
import express.field.agent.Request.AgentRequest;
import express.field.agent.Utils.FunUtils;

/**
 * Created by jonas korankye  on 17/10/23.
 */

public class MainActivity extends AgentLocationActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ImageView menu;
    private NavigationView navigationView;

    ProcessDialog processDialog = new ProcessDialog();

    AppCompatTextView agentName;
    AppCompatTextView agentBalance;
    static AppCompatTextView agentTopBalance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agency_activity);

//        Toolbar toolbar = findViewById(R.id.agent_toolbar);
//        findViewById(R.id.bank_operations).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Utils.loadPage(getSupportFragmentManager(), new BankOperation(), true);
//            }
//        });
//        findViewById(R.id.vending).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Utils.loadPage(getSupportFragmentManager(), new AirtimeVending(), true);
//            }
//        });
//        findViewById(R.id.agent_activity).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Utils.loadPage(getSupportFragmentManager(), new AgentActivities(), true);
//            }
//        });

        drawerLayout = findViewById(R.id.drawer_root);
        menu = findViewById(R.id.menu);
        navigationView = findViewById(R.id.slider);
        navigationView.setNavigationItemSelectedListener(this);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });

        findViewById(R.id.log_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Do you want to sign out of this account ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getSharedPreferences(AppPref.class.getName(), MODE_PRIVATE).edit().clear().commit();
                                startActivity(new Intent(MainActivity.this, Login.class));
                                finish();
                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert))
                        .setTitle("Alert");
                ;

                alertDialog.show();

            }
        });
        RecyclerView bills = (RecyclerView) findViewById(R.id.bills);
        bills.setHasFixedSize(true);
        bills.setLayoutManager(new GridLayoutManager(this, 3));
        bills.setAdapter(new BillsAdapter(this) {
            @Override
            public void onItemSelected(View view, int position) {
                if (position == MenuItems.BANK_DEPOSIT.position) {
                    FunUtils.loadPage(getSupportFragmentManager(), new BankDeposit(), true);
                } else if (position == MenuItems.BANK_WITHDRAWAL.position) {
                    FunUtils.loadPage(getSupportFragmentManager(), new BankWithdrawal(), true);
                } else if (position == MenuItems.BANK_OPERATION.position) {
                    FunUtils.loadPage(getSupportFragmentManager(), new BankOperation(), true);
                } else if (position == MenuItems.AIRTIME_VENDING.position) {
                    FunUtils.loadPage(getSupportFragmentManager(), new AirtimeVending(), true);
                } else if (position == MenuItems.FUNDS.position) {
                    FunUtils.loadPage(getSupportFragmentManager(), new Funds(), true);
                } else if (position == MenuItems.BILL_PAYMENT.position) {
                    FunUtils.loadPage(getSupportFragmentManager(), new BillPayment(), true);
                } else if (position == MenuItems.REVENUE_COLLECTION.position) {
                    FunUtils.loadPage(getSupportFragmentManager(), new RevenueCollection(), true);
                } else {
                    FunUtils.loadPage(getSupportFragmentManager(), new RevenueCollection(), true);
                }
            }
        });

        agentName = (AppCompatTextView) findViewById(R.id.agent_name);
        agentBalance = (AppCompatTextView) findViewById(R.id.agent_balance_value);
        agentTopBalance = (AppCompatTextView) findViewById(R.id.agent_balance);

//        agentName.setText(new AppPref().getStringValue(this, AppPref.AGENT_NAME));
        // agentBalance.setText(String.valueOf(new AppPref().getFloatValue(this, AppPref.CURRENT_BALANCE)));
        agentBalance.setText("6499.00");
        // agentTopBalance.setText(String.valueOf(new AppPref().getFloatValue(this, AppPref.CURRENT_BALANCE)));
        agentTopBalance.setText("6499.00");

        findViewById(R.id.agent_balance_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResume();
            }
        });
    }

    private void makeBalanceRequest() {
        new AgentRequest().new BalanceRequest() {
            @Override
            public void onBalanceRetrieved(boolean status, float currentBalance, String agentNameValue) {
                if (status) {
                    new AppPref().setFloatValue(MainActivity.this, AppPref.CURRENT_BALANCE, currentBalance);
                    new AppPref().setStringValue(MainActivity.this, AppPref.AGENT_NAME, agentNameValue);
                    agentName.setText(agentNameValue);
                    agentBalance.setText(String.valueOf(currentBalance));
                    agentTopBalance.setText(String.valueOf(currentBalance));
                } else {
                    FunUtils.showMessage(MainActivity.this, "could not retrieve balance");
                }
                if (processDialog.getProgressDialog().isShowing()) {
                    processDialog.dismiss();
                }
            }
        }.getAgentBalance(this);

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        makeBalanceRequest();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout dLayout) {
        dLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout dLayout) {
        if (dLayout.isDrawerOpen(GravityCompat.START)) {
            dLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        processDialog.showDialog(this, "Updating Balance");
        makeBalanceRequest();
    }


    public static boolean isComplete;

    @Override
    public void onBackPressed() {
        if (isComplete) {
            isComplete = false;
            onBackPressed();

        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                if (FunUtils.isApplicationBroughtToBackground(this)) {
                    recreate();
                } else {
                    FunUtils.openActivity(this, MainActivity.class);
                }

                break;
            case R.id.nav_settings:
                FunUtils.loadPage(getSupportFragmentManager(), new BankOperation(), true);
                closeDrawer(drawerLayout);
                break;
            case R.id.nav_share:
                FunUtils.loadPage(getSupportFragmentManager(), new AirtimeVending(), true);
                closeDrawer(drawerLayout);
                break;
            case R.id.nav_about:
                FunUtils.loadPage(getSupportFragmentManager(), new AgentActivities(), true);
                closeDrawer(drawerLayout);
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                closeDrawer(drawerLayout);
                break;
        }

        return true;
    }


}
