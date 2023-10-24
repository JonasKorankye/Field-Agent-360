package express.field.agent.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import express.field.agent.Adapters.ViewHolders.ItemListHolder;
import express.field.agent.R;

/**
 * Created by jonas korankye  on 17/10/23.
 */
abstract public class ItemListAdapter extends RecyclerView.Adapter<ItemListHolder> {

    private Context mContext;

    ArrayList<String> requestObjects;

    public ItemListAdapter(Context context, ArrayList<String> requestObjects) {
        mContext = context;
        this.requestObjects = requestObjects;
    }

    @Override
    public ItemListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selector_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemListHolder holder, final int position) {
        holder.itemName.setText(requestObjects.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemSelected(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestObjects.size();
    }

    public abstract void onItemSelected(View view, int position);
}
