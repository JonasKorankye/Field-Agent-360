package express.field.agent.Pages;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.ArrayList;

import express.field.agent.Utils.Constants;
import express.field.agent.Dialogs.RequestObjSelectorDialog;
import express.field.agent.FragmentPages;
import express.field.agent.Model.RequestObject;
import express.field.agent.ProcessDialog;
import express.field.agent.R;
import express.field.agent.Request.AgentRequest;
import express.field.agent.Utils.FunUtils;

/**
 * Created by myron echenim  on 8/14/16.
 */
public class AirtimeVending extends FragmentPages {


    AppCompatTextView telcoSelector;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.airtime_vending, container, false);
        telcoSelector = (AppCompatTextView) rootView.findViewById(R.id.telco_selector);
        telcoSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processDialog.showDialog(getContext(), "Fetching Telco List");
                new AgentRequest().new RequestObjects() {
                    @Override
                    protected void onRequestComplete(ArrayList<RequestObject> requestObjects) {
                        processDialog.dismiss();
                        new RequestObjSelectorDialog() {
                            @Override
                            public void onListItemSelected(RequestObject requestObject) {
                                selectedOption = requestObject;
                                telcoSelector.setText(requestObject.getName());
                            }
                        }.fragmentInstanceDialogFragment(requestObjects, "Select Telco").show(getFragmentManager(), "");
                    }
                }.getRequestObject(getContext(), Constants.UrlConstant.telco, "Name");
            }
        });

        rootView.findViewById(R.id.vend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedOption == null) {
                    FunUtils.showMessage(getContext(), "Telco Type has not been selected");
                } else {
                    String phoneNumber = ((AppCompatEditText) rootView.findViewById(R.id.phoneField)).getText().toString().trim();
                    String amount = ((AppCompatEditText) rootView.findViewById(R.id.amountField)).getText().toString().trim();
                    if (phoneNumber.length() >= 11) {
                        if (amount.length() != 0) {
                            int amountValue = Integer.parseInt(amount);
                            if (amountValue > 0) {
                                processDialog.showDialog(getContext(), "transaction in progress");
                                new AgentRequest().new AgentTrasaction() {
                                    @Override
                                    protected void onRequestComplete(boolean status, String message) {
                                        processDialog.dismiss();
                                        if (status) {
                                            FunUtils.loadPage(getFragmentManager(), new TransactionCompletePage(), false);
                                        } else {
                                            FunUtils.showMessage(getContext(), message);
                                        }
                                    }
                                }.vendAirtime(getContext(), selectedOption, phoneNumber, amountValue);
                            } else {
                                FunUtils.showMessage(getContext(), "invalid amount");
                            }
                        } else {
                            FunUtils.showMessage(getContext(), "amount field can not be empty");
                        }
                    } else {
                        FunUtils.showMessage(getContext(), "invalid phone number");
                    }
                }
            }
        });
        return rootView;
    }

    ProcessDialog processDialog = new ProcessDialog();
}
