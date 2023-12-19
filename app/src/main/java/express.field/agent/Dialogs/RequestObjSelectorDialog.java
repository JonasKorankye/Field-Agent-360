package express.field.agent.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import express.field.agent.Adapters.RequestObjItemListAdapter;
import express.field.agent.Model.RequestObject;
import express.field.agent.R;

/**
 * Created by jonas korankye  on 17/10/23.
 */
abstract public class RequestObjSelectorDialog extends DialogFragment {



    public DialogFragment fragmentInstanceDialogFragment(ArrayList<RequestObject> requestObjects, String title) {

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", requestObjects);
        bundle.putString("title", title);
        this.setArguments(bundle);
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        View rootView = getActivity().getLayoutInflater().inflate(R.layout.item_selector_list, null);
        final ArrayList<RequestObject> requestObjects = getArguments().getParcelableArrayList("list");
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.item_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(new RequestObjItemListAdapter(getContext(), requestObjects) {
            @Override
            public void onItemSelected(View view, int position) {
                dismissAllowingStateLoss();
                onListItemSelected(requestObjects.get(position));
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getArguments().getString("title")).setView(rootView);

        return builder.create();
    }

    abstract public void onListItemSelected(RequestObject requestObject);
}
