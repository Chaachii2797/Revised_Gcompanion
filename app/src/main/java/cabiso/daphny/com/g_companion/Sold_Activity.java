package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Adapter.Items_Adapter;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.ForCounter_Rating;
import cabiso.daphny.com.g_companion.Recommend.DIYrecommend;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class Sold_Activity extends AppCompatActivity {

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
    public Sold_Activity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_);

        recyclerView = (RecyclerView) findViewById(R.id.list);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        lv = (ListView) findViewById(R.id.lvView);
        if(lv!=null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please Wait loading DIYs.....");
            progressDialog.show();

            database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Sold_Items").child(userID);

            myRef.orderByChild("title").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();

                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.e(String.valueOf(snapshot.getRef()), snapshot.getChildrenCount() + "");
                        DIYSell img = snapshot.getValue(DIYSell.class);
                        int count=0;
                        if(img.getUser_id().equals(userID)) {
//                            img.setProductID(snapshot.getKey());
                            diyList.add(img);
//                          count = String.valueOf(String.valueOf(snapshot.getChildrenCount()).equals(userID));
                            count = diyList.size();
                            Toast.makeText(Sold_Activity.this, "count: " + count, Toast.LENGTH_SHORT).show();

                            //DatabaseReference reference = database.getReference("to_recommend").child("sold_items").child(userID);
                            final DatabaseReference ref = database.getReference("DIYs_By_Users").child("bottle").child(userID);
                            final DatabaseReference ref1 = database.getReference("DIYs_By_Users").child("glass").child(userID);
                            final DatabaseReference ref2 = database.getReference("DIYs_By_Users").child("paper").child(userID);
                            final DatabaseReference ref3 = database.getReference("DIYs_By_Users").child("cup").child(userID);
                            final DatabaseReference ref4 = database.getReference("DIYs_By_Users").child("tire").child(userID);
                            final DatabaseReference ref5 = database.getReference("DIYs_By_Users").child("wood").child(userID);



//                            DatabaseReference sold = database.getReference("DIYs_By_Users").child("bottle")
//                                    .child(userID).child("sold_items");
                            DIYrecommend recommend = new DIYrecommend();

                            final ForCounter_Rating counter_rating = new ForCounter_Rating();
                            counter_rating.setSold(count);
//                            counter_rating.setTransac_rating(count);

                              //  counter_rating.setOwnerID(userID);
                            Query get_sold = ref.orderByChild("sold_items").equalTo(0);
                            get_sold.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
                                        snapshot1.getRef().child("sold_items").setValue(counter_rating.getSold());
                                        snapshot1.getRef().child("transac_rating").setValue((counter_rating.getSold() * 0.4)
                                                 + counter_rating.getTransac_rating());
//                                        snapshot1.getRef().child("user_ratings").setValue(counter_rating.getTransac_rating());

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Query get_sold1 = ref1.orderByChild("sold_items").equalTo(0);
                            get_sold1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
                                        snapshot1.getRef().child("sold_items").setValue(counter_rating.getSold());
                                        snapshot1.getRef().child("transac_rating").setValue((counter_rating.getSold() * 0.4)
                                                + counter_rating.getTransac_rating());
//                                        snapshot1.getRef().child("user_ratings").setValue(counter_rating.getTransac_rating());

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Query get_sold2 = ref2.orderByChild("sold_items").equalTo(0);
                            get_sold2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
                                        snapshot1.getRef().child("sold_items").setValue(counter_rating.getSold());
                                        snapshot1.getRef().child("transac_rating").setValue((counter_rating.getSold() * 0.4)
                                                + counter_rating.getTransac_rating());
//                                        snapshot1.getRef().child("user_ratings").setValue(counter_rating.getTransac_rating());

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Query get_sold3 = ref3.orderByChild("sold_items").equalTo(0);
                            get_sold3.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
                                        snapshot1.getRef().child("sold_items").setValue(counter_rating.getSold());
                                        snapshot1.getRef().child("transac_rating").setValue((counter_rating.getSold() * 0.4)
                                                + counter_rating.getTransac_rating());
//                                        snapshot1.getRef().child("user_ratings").setValue(counter_rating.getTransac_rating());

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Query get_sold4 = ref4.orderByChild("sold_items").equalTo(0);
                            get_sold4.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
                                        snapshot1.getRef().child("sold_items").setValue(counter_rating.getSold());
                                        snapshot1.getRef().child("transac_rating").setValue((counter_rating.getSold() * 0.4)
                                                + counter_rating.getTransac_rating());
//                                        snapshot1.getRef().child("user_ratings").setValue(counter_rating.getTransac_rating());

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Query get_sold5 = ref5.orderByChild("sold_items").equalTo(0);
                            get_sold5.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
                                        snapshot1.getRef().child("sold_items").setValue(counter_rating.getSold());
                                        snapshot1.getRef().child("transac_rating").setValue((counter_rating.getSold() * 0.4)
                                                + counter_rating.getTransac_rating());
//                                        snapshot1.getRef().child("user_ratings").setValue(counter_rating.getTransac_rating());

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
//                            String upload  = sold.getKey();
//                            //reference.child(upload).setValue(counter_rating);
//
//                            sold.setValue(counter_rating);
                        }

//                      String upload = reference.push().getKey();
//                        String upload = reference.push().getKey();
//                        reference.child(upload).setValue(counter_rating);
                    }
                    //init adapter
                    adapter = new Items_Adapter(Sold_Activity.this, R.layout.recommend_ui, diyList);
                    //set adapter for listview
                    lv.setAdapter(adapter);
                    final int count =lv.getAdapter().getCount();
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            DIYSell itemRef = adapter.getItem(position);
                            Toast toast = Toast.makeText(Sold_Activity.this, itemRef.diyName
                                    + "\n" + itemRef.user_id + "\n" + itemRef.getDiyUrl().toString() + "\n" + count, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else{
            Toast.makeText(Sold_Activity.this, "Wala pa kay tinda na add!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Sold_Activity.this,MainActivity.class);
            startActivity(intent);
        }
    }
}