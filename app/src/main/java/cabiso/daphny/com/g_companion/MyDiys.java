package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Recommend.RecommendDIYAdapter;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class MyDiys extends AppCompatActivity {

    private ArrayList<DIYnames> diyList = new ArrayList<>();
    private ArrayList<CommunityItem> infoList = new ArrayList<>();
    private ListView lv;
    private ImageView loadview;
    private RecommendDIYAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference databaseReference;
    private DatabaseReference categoryReference;

    private ImageButton heart, star;
    private int count=0;

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

        heart = (ImageButton) findViewById(R.id.heartu);
        star = (ImageButton) findViewById(R.id.staru);

        database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.hasChildren()) {
//
//                        Collections.sort(diyList);
//                        Collections.reverse(diyList);

                        progressDialog.dismiss();

                        Toast.makeText(MyDiys.this, "HEY!", Toast.LENGTH_SHORT).show();

                        DIYnames img = dataSnapshot.getValue(DIYnames.class);
                        diyList.add(img);

                        CommunityItem mp = dataSnapshot.getValue(CommunityItem.class);
                        infoList.add(mp);

                        adapter = new RecommendDIYAdapter(MyDiys.this, R.layout.recommend_ui, diyList);
                        lv.setAdapter(adapter);

//                        if(snapshot.getChildrenCount() == diyList.size()){
//                            for(int i=0; i<diyList.size();i++){
//                                Log.e("get "," "+diyList.get(i).getSold_items());
//                            }f
//                        }



//                        registerForContextMenu(lv);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(getApplicationContext(), diyList.get(position).getDiyName(), Toast.LENGTH_SHORT).show();
                                DIYnames selectedItem = adapter.getItem(position);
                                if(selectedItem.getUser_id()!=null){
//                                    star.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Toast.makeText(getApplicationContext(),"CLIIIIIIIICK!", Toast.LENGTH_SHORT).show();
//                                            if(star.isPressed()){
//                                                count+=1;
//                                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("diy_by_tags").child(userID);
//                                                reference.addChildEventListener(new ChildEventListener() {
//                                                    @Override
//                                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                                                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
//                                                            String key = snapshot.getKey();
//                                                            String path = "/" + dataSnapshot.getKey() + "/" + key;
//                                                            HashMap<String, Object> result = new HashMap<>();
//                                                            result.put("bookmarks",count);
//                                                            reference.child(path).updateChildren(result);
                                    star.setColorFilter(ContextCompat.getColor(MyDiys.this, R.color.star_yello));
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(DatabaseError databaseError) {
//
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    });


                                }
                                //To-DO get you data from the ItemDetails Getter
                                // selectedItem.getImage() or selectedItem.getName() .. etc
                                // the  send the data using intent when opening another activity
                                Intent intent = new Intent(MyDiys.this, DIYDetailViewActivity.class);
//                                String items = infoList.get(position).getVal();

                                CommunityItem mat = (CommunityItem) parent.getItemAtPosition(position);


                                adapter.notifyDataSetChanged();
//                                Toast toast = Toast.makeText(MyDiys.this, items, Toast.LENGTH_SHORT);
//                                toast.show();
                                intent.putExtra("image", selectedItem.getDiyUrl().toString());
                                intent.putExtra("name", selectedItem.getDiyName());
                               // intent.putExtra("procedures", infoList.get(position));
                                // intent.putExtra("materials", selectedItem.getDiymaterial());


                                view.buildDrawingCache();
                                Bitmap image = view.getDrawingCache();
                                Bundle extras = new Bundle();
                                extras.putParcelable("imagebitmap", image);

                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                intent.putExtra("image", byteArray);
                                startActivity(intent);
                            }
                        });
                    }
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
               // String name = diyList.get(listPosition).getVal();
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