package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
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

import cabiso.daphny.com.g_companion.Recommend.DIYrecommend;
import cabiso.daphny.com.g_companion.Recommend.RecommendDIYAdapter;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class MyDiys extends AppCompatActivity {

    private ArrayList<DIYrecommend> diyList = new ArrayList<>();
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
    public MyDiys() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_diys);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        lv = (ListView) findViewById(R.id.lvView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DIYs_By_Users").child(userID);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DIYrecommend img = snapshot.getValue(DIYrecommend.class);
                    diyList.add(img);
                }
                //init adapter
                adapter = new RecommendDIYAdapter(MyDiys.this, R.layout.recommend_ui, diyList);

                //set adapter for listview
                lv.setAdapter(adapter);
                registerForContextMenu(lv);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                        Intent intent = new Intent(Bottle_Recommend.this,ViewDIY.class);
//                        startActivity(intent);
                        DIYrecommend itemRef = adapter.getItem(position);

                        Toast toast = Toast.makeText(MyDiys.this, itemRef.getDiyName()
                                        +"\n"+itemRef.getDiymaterial()+"\n"+itemRef.diyImageUrl+"\n"+itemRef.getDiyprocedure(),
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
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
                databaseReference = FirebaseDatabase.getInstance().getReference().child("DIY_Methods").child("all_DIYS");
                String name = diyList.get(listPosition).getDiyName();
                String material = diyList.get(listPosition).getDiymaterial();
                String procedure = diyList.get(listPosition).getDiyprocedure();
                String imageURL = diyList.get(listPosition).getDiyImageUrl();
                DIYrecommend diYrecommend = new DIYrecommend(name, material, procedure, imageURL);
                String upload = databaseReference.push().getKey();
                databaseReference.child(upload).setValue(diYrecommend);
                Toast.makeText(MyDiys.this, "CLicked! COMMUNITY" + diyList.get(listPosition).getDiyName(), Toast.LENGTH_SHORT).show();
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