package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Adapter.CommunityAdapter;
import cabiso.daphny.com.g_companion.Model.CommunityItem;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class MyDiys extends AppCompatActivity {

    private ArrayList<CommunityItem> diyList = new ArrayList<>();
    private ListView lv;
    private ImageView loadview;
    private CommunityAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference databaseReference;
    private DatabaseReference categoryReference;

    public MyDiys() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_diys);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.list);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        lv = (ListView) findViewById(R.id.lvView);
//        if(lv!=null) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("diy_by_tags").child(userID).child("business");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CommunityItem img = snapshot.getValue(CommunityItem.class);
                    diyList.add(img);
                }
//                    DatabaseReference myRef = database.getReference("DIYs_By_Users").child("cup").child(userID);
//                    myRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
//                                DIYrecommend img = snapshot1.getValue(DIYrecommend.class);
//                                diyList.add(img);
//                            }
//                            DatabaseReference myRef = database.getReference("DIYs_By_Users").child("glass").child(userID);
//                            myRef.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
//                                        DIYrecommend img = snapshot1.getValue(DIYrecommend.class);
//                                        diyList.add(img);
//                                    }
//                                    DatabaseReference myRef = database.getReference("DIYs_By_Users").child("paper").child(userID);
//                                    myRef.addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
//                                                DIYrecommend img = snapshot1.getValue(DIYrecommend.class);
//                                                diyList.add(img);
//                                            }
//                                            DatabaseReference myRef = database.getReference("DIYs_By_Users").child("tire").child(userID);
//                                            myRef.addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                    for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
//                                                        DIYrecommend img = snapshot1.getValue(DIYrecommend.class);
//                                                        diyList.add(img);
//                                                    }
//                                                    DatabaseReference myRef = database.getReference("DIYs_By_Users").child("wood").child(userID);
//                                                    myRef.addValueEventListener(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                                            for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
//                                                                DIYrecommend img = snapshot1.getValue(DIYrecommend.class);
//                                                                diyList.add(img);
//                                                            }
//                                                        }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //init adapter
        adapter = new CommunityAdapter(getApplicationContext(),  diyList);

        //set adapter for listview
        lv.setAdapter(adapter);
        registerForContextMenu(lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CommunityItem itemRef = adapter.getItem(position);
//                            adapter.remove(itemRef);
//                            adapter.notifyDataSetChanged();
//                Toast toast = Toast.makeText(MyDiys.this, itemRef.getDiyName()
//                                + "\n" + itemRef.getDiymaterial() + "\n" + itemRef.diyImageUrl + "\n" + itemRef.getDiyprocedure(),
//                        Toast.LENGTH_SHORT);
//                toast.show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // use this method when query submitted
//                diyList.get(Integer.parseInt(query));
                Toast.makeText(getApplicationContext(),"Search pls", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // use this method for auto complete search process
//                diyList.get(Integer.parseInt(newText));
                return false;
            }
        });
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }




//                                            //init adapter
//                                            adapter = new RecommendDIYAdapter(MyDiys.this, R.layout.recommend_ui, diyList);
//
//                                            //set adapter for listview
//                                            lv.setAdapter(adapter);
//                                            registerForContextMenu(lv);
//                                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                                @Override
//                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                                    DIYrecommend itemRef = adapter.getItem(position);
////                            adapter.remove(itemRef);
////                            adapter.notifyDataSetChanged();
//                                                    Toast toast = Toast.makeText(MyDiys.this, itemRef.getDiyName()
//                                                                    + "\n" + itemRef.getDiymaterial() + "\n" + itemRef.diyImageUrl + "\n" + itemRef.getDiyprocedure(),
//                                                            Toast.LENGTH_SHORT);
//                                                    toast.show();
//                                                }
//                                            });
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                    //init adapter
//                                    adapter = new RecommendDIYAdapter(MyDiys.this, R.layout.recommend_ui, diyList);
//
//                                    //set adapter for listview
//                                    lv.setAdapter(adapter);
//                                    registerForContextMenu(lv);
//                                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                        @Override
//                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                            DIYrecommend itemRef = adapter.getItem(position);
////                            adapter.remove(itemRef);
////                            adapter.notifyDataSetChanged();
//                                            Toast toast = Toast.makeText(MyDiys.this, itemRef.getDiyName()
//                                                            + "\n" + itemRef.getDiymaterial() + "\n" + itemRef.diyImageUrl + "\n" + itemRef.getDiyprocedure(),
//                                                    Toast.LENGTH_SHORT);
//                                            toast.show();
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                            //init adapter
//                            adapter = new RecommendDIYAdapter(MyDiys.this, R.layout.recommend_ui, diyList);
//
//                            //set adapter for listview
//                            lv.setAdapter(adapter);
//                            registerForContextMenu(lv);
//                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                    DIYrecommend itemRef = adapter.getItem(position);
////                            adapter.remove(itemRef);
////                            adapter.notifyDataSetChanged();
//                                    Toast toast = Toast.makeText(MyDiys.this, itemRef.getDiyName()
//                                                    + "\n" + itemRef.getDiymaterial() + "\n" + itemRef.diyImageUrl + "\n" + itemRef.getDiyprocedure(),
//                                            Toast.LENGTH_SHORT);
//                                    toast.show();
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//
//                    //init adapter
//                    adapter = new RecommendDIYAdapter(MyDiys.this, R.layout.recommend_ui, diyList);
//
//                    //set adapter for listview
//                    lv.setAdapter(adapter);
//                    registerForContextMenu(lv);
//                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            DIYrecommend itemRef = adapter.getItem(position);
////                            adapter.remove(itemRef);
////                            adapter.notifyDataSetChanged();
//                            Toast toast = Toast.makeText(MyDiys.this, itemRef.getDiyName()
//                                            + "\n" + itemRef.getDiymaterial() + "\n" + itemRef.diyImageUrl + "\n" + itemRef.getDiyprocedure(),
//                                    Toast.LENGTH_SHORT);
//                            toast.show();
//                        }
//                    });
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }else{
//            Toast.makeText(MyDiys.this, "Wala pay DIY na add!",Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(MyDiys.this,MainActivity.class);
//            startActivity(intent);
//        }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.lvView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_from_mydiys, menu);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.addToCommunity:
                int listPosition = info.position;
                databaseReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child("materials");
                String name = diyList.get(listPosition).getVal();
//                String material = diyList.get(listPosition).getDiymaterial();
//                String procedure = diyList.get(listPosition).getDiyprocedure();
//                String imageURL = diyList.get(listPosition).getDiyImageUrl();


//                ForCounter_Rating counter_rating = new ForCounter_Rating();
//                int sold = (counter_rating.getSold());

//                if(category.equals("bottle")){
//                    categoryReference = FirebaseDatabase.getInstance().getReference().child("DIY_Methods")
//                            .child("category").child("bottle");
//                    DIYrecommend diYrecommend = new DIYrecommend(name, material, procedure, imageURL, userID,
//                            category, soldItems, transac_rate);
//                    String upload = categoryReference.push().getKey();
//                    categoryReference.child(upload).setValue(diYrecommend);
//                }
              //  DIYrecommend diYrecommend = new DIYrecommend(name, material, procedure, imageURL, userID, diyTags);
//                String upload = databaseReference.push().getKey();
//                databaseReference.child(upload).setValue(diYrecommend);
//                Toast.makeText(MyDiys.this, "CLicked! COMMUNITY" + diyList.get(listPosition).getDiyName(), Toast.LENGTH_SHORT).show();

                return true;
            case R.id.addToMarket:
                Intent intent = new Intent(MyDiys.this, SellMyDIYs.class);
                startActivity(intent);
                Toast.makeText(MyDiys.this, "CLicked! MY DIYS", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

}