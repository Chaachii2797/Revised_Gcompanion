package cabiso.daphny.com.g_companion.MainDIYS;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 9/3/2018.
 */

public class ViewMoreDiysCommunityActivity extends AppCompatActivity {

    private RecyclerView moreRecyclerViewCommunity;
    private ArrayList<DIYnames> diyMoreCommunityList = new ArrayList<>();
    private DatabaseReference diysReference;
    RecyclerView.LayoutManager gridLayoutManager, linearLayoutManager;
    private ViewMoreDiysAdapter mGridAdapterCommunity;


    final int GRID = 0;
    final int LIST = 1;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_more_diys_activity);

        diysReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");

         /* Toolbar Configurations */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("All Free DIYS");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_btn);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        moreRecyclerViewCommunity = (RecyclerView) findViewById(R.id.moreDIYSrecyclerView);

        // Defining Linear Layout Manager
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        // Defining Linear Layout Manager (here, 3 column span count)
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        type = GRID;

        mGridAdapterCommunity = new ViewMoreDiysAdapter(getApplicationContext(),diyMoreCommunityList, GRID);

        moreRecyclerViewCommunity.setLayoutManager(gridLayoutManager);

        moreRecyclerViewCommunity.setAdapter(mGridAdapterCommunity);


        diysReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final DIYnames diysMoreSelling = dataSnapshot.getValue(DIYnames.class);
                Log.e("more_sell_diys", String.valueOf(diysMoreSelling.getDiyName()));


               if(diysMoreSelling.getIdentity().equalsIgnoreCase("community")){
                    diyMoreCommunityList.add(diysMoreSelling);
                    Log.e("comm_MoreDiys", String.valueOf(diysMoreSelling.getDiyName()) + " = " + diysMoreSelling.getUser_id());

                    mGridAdapterCommunity.notifyDataSetChanged();
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
