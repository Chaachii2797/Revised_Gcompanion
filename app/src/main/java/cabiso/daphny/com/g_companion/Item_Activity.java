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

import cabiso.daphny.com.g_companion.Adapter.Items_Adapter;
import cabiso.daphny.com.g_companion.Model.DIYSell;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class Item_Activity extends AppCompatActivity {

    private ArrayList<DIYSell> diyList = new ArrayList<>();
    private ListView lv;
    private Items_Adapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference itemReference;
    public Item_Activity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_);

            recyclerView = (RecyclerView) findViewById(R.id.list);

            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userID = mFirebaseUser.getUid();

            lv = (ListView) findViewById(R.id.lvView);
            if(lv!=null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please Wait loading DIYs.....");
            progressDialog.show();

            database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("marketplace");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DIYSell img = snapshot.getValue(DIYSell.class);
                        if (img.getUser_id().toString().equals(userID)) {
                            diyList.add(img);
                        }
                    }
                    //init adapter
                    adapter = new Items_Adapter(Item_Activity.this, R.layout.my_item_layout, diyList);
                    //set adapter for listview
                    lv.setAdapter(adapter);
//                    final int count =lv.getAdapter().getCount();
                    registerForContextMenu(lv);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            DIYSell itemRef = adapter.getItem(position);
//                            adapter.remove(adapter.getItem(position));
//                            adapter.notifyDataSetChanged();
                            Toast toast = Toast.makeText(Item_Activity.this, itemRef.diyName
                                    + "\n" + itemRef.user_id + "\n" + itemRef.getDiyUrl() + "\n" , Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(Item_Activity.this, "Wala pa kay tinda na add!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Item_Activity.this,MainActivity.class);
            startActivity(intent);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.lvView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_for_myitems, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.sold_item:
                int listPosition = info.position;
                Float float_this = Float.valueOf(0);

                final int count =lv.getAdapter().getCount();
                itemReference = FirebaseDatabase.getInstance().getReference().child("Sold_Items").child(userID);
                String myItem_diyName = diyList.get(listPosition).getDiyName();
                String myItem_diyUrl = diyList.get(listPosition).getDiyUrl();
                String myItem_user_id = diyList.get(listPosition).getUser_id();
                String myItem_productID = diyList.get(listPosition).getProductID();
                String myItem_status = diyList.get(listPosition).getStatus();

                DIYSell product = new DIYSell(myItem_diyName, myItem_diyUrl, myItem_user_id,
                        myItem_productID, myItem_status, float_this, float_this);
                String upload = itemReference.push().getKey();
                itemReference.child(upload).setValue(product);

        }
        return super.onContextItemSelected(item);
    }
}