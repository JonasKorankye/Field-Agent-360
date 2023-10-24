package express.field.agent;

import android.content.Context;
import android.view.View;


import androidx.fragment.app.Fragment;

import express.field.agent.Model.RequestObject;

/**
 * Created by jonas korankye  on 17/10/23.
 */
public class FragmentPages extends Fragment {

    protected RequestObject selectedOption;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        selectedOption = null;

        MainActivity.agentTopBalance.setVisibility(View.VISIBLE);

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.agentTopBalance.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity.agentTopBalance.setVisibility(View.GONE);
    }
}
