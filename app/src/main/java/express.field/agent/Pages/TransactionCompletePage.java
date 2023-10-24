package express.field.agent.Pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import express.field.agent.FragmentPages;
import express.field.agent.MainActivity;
import express.field.agent.R;

/**
 * Created by myron echenim  on 8/14/16.
 */
public class TransactionCompletePage extends FragmentPages {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.isComplete = true;
        View rootView = inflater.inflate(R.layout.transaction_complete, container, false);
        rootView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        return rootView;
    }
}
