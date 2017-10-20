package cabiso.daphny.com.g_companion;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Adapter.CommunityAdapter;
import cabiso.daphny.com.g_companion.Model.DIYMethods;

/**
 * Created by Lenovo on 10/18/2017.
 */

public class CommunityFragment extends Fragment {

    private ArrayList<DIYMethods> diyList = new ArrayList<>();

    private DatabaseReference databaseReference;
    private DatabaseReference userDatabaseReference;
    private FirebaseDatabase database;
    private StorageReference storageReference;
    private FirebaseUser mFirebaseUser;
    private String userID;

    private ListView lv;
    private RecyclerView recyclerView;

    public static final String TITLE = " DIY Community";
    private FloatingActionButton fab2, fab3;
    private FloatingActionMenu fam;
    private Animation fab_open, fab_close;

    private CommunityAdapter adapter;


    public static CommunityFragment newInstance() {

        return new CommunityFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.community_fragment, container, false);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();



        fam = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        fam.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                } else {
                }
            }
        });
        fab2 = (FloatingActionButton) view.findViewById(R.id.community_fab);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), CaptureDIY.class);
                startActivity(in);
            }
        });

        fab3 = (FloatingActionButton) view.findViewById(R.id.image_recog_fab);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), ImageRecognitionTags.class);
                Snackbar.make(view, "Please wait.......", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(in);
            }
        });


        return view;
    }



}
