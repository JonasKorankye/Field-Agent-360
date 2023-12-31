package express.field.agent.Pages;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import express.field.agent.Adapters.BankOperationAdapter;
import express.field.agent.Utils.Constants;
import express.field.agent.FragmentPages;
import express.field.agent.Pages.FundsPages.Acct2Acct;
import express.field.agent.Pages.FundsPages.Acct2Cash;
import express.field.agent.Pages.FundsPages.CashToAcct;
import express.field.agent.Pages.FundsPages.CashToCash;
import express.field.agent.R;
import express.field.agent.Utils.FunUtils;

/**
 * Created by myron echenim  on 8/14/16.
 */
public class Funds extends FragmentPages {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bank_categories, container, false);
        ((AppCompatTextView) rootView.findViewById(R.id.title)).setText("Fund Transfer");
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.agent_activity_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new BankOperationAdapter(new ArrayList(Arrays.asList(getResources().getStringArray(R.array.funds)))) {
            @Override
            protected void onItemSelected(View view, int position) {
                if (position == Constants.FUNDS_A2A) {
                    FunUtils.loadPage(getFragmentManager(), new Acct2Acct(), true);
                } else if (position == Constants.FUNDS_A2C) {
                    FunUtils.loadPage(getFragmentManager(), new Acct2Cash(), true);
                } else if (position == Constants.FUNDS_C2C) {
                    FunUtils.loadPage(getFragmentManager(), new CashToCash(), true);
                } else if (position == Constants.FUNDS_C2A) {
                    FunUtils.loadPage(getFragmentManager(), new CashToAcct(), true);
                }
            }
        });
        return rootView;
    }
}
