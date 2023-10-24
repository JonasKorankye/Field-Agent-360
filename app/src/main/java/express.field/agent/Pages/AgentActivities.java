package express.field.agent.Pages;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import express.field.agent.Adapters.AgentActivityAdapter;
import express.field.agent.FragmentPages;
import express.field.agent.Model.AgentModel;
import express.field.agent.ProcessDialog;
import express.field.agent.R;
import express.field.agent.Request.AgentRequest;

/**
 * Created by jonas korankye  on 17/10/23.
 */
public class AgentActivities extends FragmentPages {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.agent_activity, container, false);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.agent_activity_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        processDialog.showDialog(getContext(), "Retrieving Transaction details");
        new AgentRequest().new AgentTransactions() {
            @Override
            public void onRequestComplete(ArrayList<AgentModel> agentModels) {
                processDialog.dismiss();
                recyclerView.setAdapter(new AgentActivityAdapter(getContext(), agentModels) {
                    @Override
                    public void onItemSelected(View view, int position) {

                    }
                });
            }
        }.getAllTransactionDetails(getContext());

        return rootView;
    }

    ProcessDialog processDialog = new ProcessDialog();
}
