package express.field.agent.Adapters.ViewHolders;

import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import express.field.agent.R;


/**
 * Created by jonas korankye  on 17/10/23.
 */
public class AgentActivityAdapterHolder extends RecyclerView.ViewHolder {


    public AppCompatTextView transactionDate, transactionAmount, transactionName;

    public AgentActivityAdapterHolder(View itemView) {
        super(itemView);
        transactionDate = (AppCompatTextView) itemView.findViewById(R.id.trasaction_date);
        transactionAmount = (AppCompatTextView) itemView.findViewById(R.id.trasaction_amount);
        transactionName = (AppCompatTextView) itemView.findViewById(R.id.trasaction_name);

    }
}
