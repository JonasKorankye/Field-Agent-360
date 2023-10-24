package express.field.agent.Adapters.ViewHolders;


import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import express.field.agent.R;

/**
 * Created by jonas korankye  on 17/10/23.
 */
public class BillAdapterHolder extends RecyclerView.ViewHolder {

    public LinearLayoutCompat itemGrp;
    public AppCompatImageButton itemBtn;
    public AppCompatTextView itemName;

    public BillAdapterHolder(View itemView) {
        super(itemView);
        itemGrp = (LinearLayoutCompat) itemView.findViewById(R.id.item_grp);
        itemName = (AppCompatTextView) itemView.findViewById(R.id.bill_label);
        itemBtn = (AppCompatImageButton) itemView.findViewById(R.id.bill_img);
    }
}
