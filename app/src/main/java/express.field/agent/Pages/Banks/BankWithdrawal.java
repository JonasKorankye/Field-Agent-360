package express.field.agent.Pages.Banks;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.ArrayList;

import express.field.agent.Constants;
import express.field.agent.Dialogs.RequestObjSelectorDialog;
import express.field.agent.FragmentPages;
import express.field.agent.Model.RequestObject;
import express.field.agent.ProcessDialog;
import express.field.agent.R;
import express.field.agent.Request.AgentRequest;
import express.field.agent.FunUtils;

/**
 * Created by myron echenim  on 8/25/16.
 */
public class BankWithdrawal extends FragmentPages {



    AppCompatTextView bankSelector;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.bank_withdrawal, container, false);


        bankSelector = (AppCompatTextView) rootView.findViewById(R.id.bank_selector);
        bankSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processDialog.showDialog(getContext(), "Fetching Banks");
                new AgentRequest().new RequestObjects() {
                    @Override
                    protected void onRequestComplete(ArrayList<RequestObject> requestObjects) {
                        processDialog.dismiss();
                        new RequestObjSelectorDialog() {
                            @Override
                            public void onListItemSelected(RequestObject requestObject) {
                                selectedOption = requestObject;
                                bankSelector.setText(requestObject.getName());
                            }
                        }.fragmentInstanceDialogFragment(requestObjects, "Select Bank").show(getFragmentManager(), "");
                    }
                }.getRequestObject(getContext(), Constants.UrlConstant.banks, "BankName");
            }
        });


        rootView.findViewById(R.id.makeTransfer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String acctNumber = ((AppCompatEditText) rootView.findViewById(R.id.accountNumber)).getText().toString().trim();
                String customerPin = ((AppCompatEditText) rootView.findViewById(R.id.customerPin)).getText().toString().trim();
                String amount = ((AppCompatEditText) rootView.findViewById(R.id.amountField)).getText().toString().trim();
                String otp = ((AppCompatEditText) rootView.findViewById(R.id.otp)).getText().toString().trim();
                String agentPin = ((AppCompatEditText) rootView.findViewById(R.id.agentPin)).getText().toString().trim();
                if (acctNumber.length() == 10) {
                    if (customerPin.isEmpty()) {
                        FunUtils.showMessage(getContext(), "invalid customer pin");
                    } else {
                        if (amount.isEmpty()) {
                            FunUtils.showMessage(getContext(), "invalid amount");
                        } else {
                            if (Integer.parseInt(amount) == 0) {
                                FunUtils.showMessage(getContext(), "invalid amount");
                            } else {
                                if (otp.length() < 4) {
                                    if (agentPin.isEmpty()) {
                                        FunUtils.showMessage(getContext(), "making withdrawal");
                                    } else {
                                        processDialog.showDialog(getContext(), "opening account");
                                        new AgentRequest().new AgentTrasaction() {
                                            @Override
                                            protected void onRequestComplete(boolean status, String message) {
                                                processDialog.dismiss();
                                                Log.e("update", message);
                                            }
                                        }.bankWithdrawal(getContext(), selectedOption, acctNumber, customerPin, amount, otp, agentPin);
                                    }
                                } else {
                                    FunUtils.showMessage(getContext(), "invalid otp");
                                }
                            }
                        }
                    }
                } else {
                    FunUtils.showMessage(getContext(), "invalid account number");
                }


            }
        });

        return rootView;
    }

    ProcessDialog processDialog = new ProcessDialog();
}
