package express.field.agent.Adapters.ViewHolders;


import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import express.field.agent.R;

/**
 * Created by jonas korankye  on 17/10/23.
 */
public class BankOperationAdapterHolder extends RecyclerView.ViewHolder {
    public AppCompatTextView item;

    public BankOperationAdapterHolder(View itemView) {
        super(itemView);
        item = (AppCompatTextView) itemView.findViewById(R.id.operationText);
    }
}
