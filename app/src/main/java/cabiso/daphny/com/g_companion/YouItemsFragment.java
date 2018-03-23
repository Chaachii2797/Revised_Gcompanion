package cabiso.daphny.com.g_companion;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Lenovo on 3/23/2018.
 */

public class YouItemsFragment extends Fragment {


    public YouItemsFragment() {

    }

    public static YouItemsFragment newInstance() {

        YouItemsFragment fragment = new YouItemsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return new YouItemsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.your_items_layout, container, false);


        return view;
    }

}
