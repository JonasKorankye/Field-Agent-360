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
public class BillPayment extends FragmentPages {

    AppCompatTextView billSelector;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.bill_payment, container, false);
        billSelector = (AppCompatTextView) rootView.findViewById(R.id.bill_selector);
        billSelector.setOnClickListener(new View.OnClickListener() {
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
                                billSelector.setText(requestObject.getName());
                            }
                        }.fragmentInstanceDialogFragment(requestObjects, "Select Bill Type").show(getFragmentManager(), "");
                    }
                }.getRequestObject(getContext(), Constants.UrlConstant.bills, "Name");
            }
        });

        rootView.findViewById(R.id.pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedOption == null) {
                    FunUtils.showMessage(getContext(), "Bill Payment Type has not been selected");
                } else {
                    String customerId = ((AppCompatEditText) rootView.findViewById(R.id.customerId)).getText().toString().trim();
                    String amount = ((AppCompatEditText) rootView.findViewById(R.id.amountField)).getText().toString().trim();
                    if (customerId.length() != 0) {
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
                                }.payBill(getContext(), selectedOption, customerId, amountValue);
                            } else {
                                FunUtils.showMessage(getContext(), "invalid amount");
                            }
                        } else {
                            FunUtils.showMessage(getContext(), "amount field can not be empty");
                        }
                    } else {
                        FunUtils.showMessage(getContext(), "customer id can not be empty");
                    }
                }
            }
        });
        return rootView;
    }

    ProcessDialog processDialog = new ProcessDialog();
}
