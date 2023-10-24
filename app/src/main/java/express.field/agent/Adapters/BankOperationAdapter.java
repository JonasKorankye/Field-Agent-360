package express.field.agent.Adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import express.field.agent.Adapters.ViewHolders.BankOperationAdapterHolder;
import express.field.agent.R;

/**
 * Created by jonas korankye  on 17/10/23.
 */
abstract public class BankOperationAdapter extends RecyclerView.Adapter<BankOperationAdapterHolder> {

    ArrayList operationItems;

    public BankOperationAdapter(ArrayList operationItems) {
        this.operationItems = operationItems;

    }

    @Override
    public BankOperationAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BankOperationAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_categories_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BankOperationAdapterHolder holder, final int position) {
        holder.item.setText(operationItems.get(position).toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemSelected(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return operationItems.size();
    }

    protected abstract void onItemSelected(View view, int position);
}
