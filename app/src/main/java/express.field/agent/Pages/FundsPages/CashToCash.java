package express.field.agent.Pages.FundsPages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import express.field.agent.FragmentPages;
import express.field.agent.R;

/**
 * Created by myron echenim  on 8/25/16.
 */
public class CashToCash extends FragmentPages {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cash2cash,container,false);
        return rootView;
    }
}
