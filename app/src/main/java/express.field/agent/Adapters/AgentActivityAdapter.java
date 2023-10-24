package express.field.agent.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import express.field.agent.Adapters.ViewHolders.AgentActivityAdapterHolder;
import express.field.agent.Model.AgentModel;
import express.field.agent.R;


/**
 * Created by jonas korankye  on 17/10/23.
 */
abstract public class AgentActivityAdapter extends RecyclerView.Adapter<AgentActivityAdapterHolder> {

    private Context mContext;

    ArrayList<AgentModel> agentModels;

    public AgentActivityAdapter(Context context, ArrayList<AgentModel> agentModels) {
        mContext = context;
        this.agentModels = agentModels;
    }

    @Override
    public AgentActivityAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AgentActivityAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.agent_activity_item, parent, false));
    }

    @Override
    public void onBindViewHolder(AgentActivityAdapterHolder holder, final int position) {
        holder.transactionDate.setText(agentModels.get(position).getTrasactionTime());
        holder.transactionAmount.setText(String.valueOf(agentModels.get(position).getAmount()));
        holder.transactionName.setText(agentModels.get(position).getTrasaction());
    }

    @Override
    public int getItemCount() {
        return agentModels.size();
    }

    public abstract void onItemSelected(View view, int position);
}
