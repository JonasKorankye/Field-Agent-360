package express.field.agent.Adapters.ViewHolders;


import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import express.field.agent.R;

/**
 * Created by jonas korankye  on 17/10/23.
 */
public class ItemListHolder extends RecyclerView.ViewHolder {


    public AppCompatTextView itemName;

    public ItemListHolder(View itemView) {
        super(itemView);
        itemName = (AppCompatTextView) itemView.findViewById(R.id.object_name);

    }
}
