package express.field.agent.Pages;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import express.field.agent.Adapters.BankOperationAdapter;
import express.field.agent.FragmentPages;
import express.field.agent.Pages.Banks.AccountOpening;
import express.field.agent.Pages.Banks.BankDeposit;
import express.field.agent.Pages.Banks.BankWithdrawal;
import express.field.agent.R;
import express.field.agent.FunUtils;

/**
 * Created by myron echenim  on 8/23/16.
 */
public class BankOperation extends FragmentPages {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bank_categories, container, false);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.agent_activity_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new BankOperationAdapter(new ArrayList(Arrays.asList(getResources().getStringArray(R.array.bank_operation)))) {
            @Override
            protected void onItemSelected(View view, int position) {
                if (position == 0) {
                    FunUtils.loadPage(getFragmentManager(), new AccountOpening(), true);
                } else if (position == 1) {
                    FunUtils.loadPage(getFragmentManager(), new BankDeposit(), true);
                } else {
                    FunUtils.loadPage(getFragmentManager(), new BankWithdrawal(), true);
                }
            }
        });
        return rootView;
    }
}
