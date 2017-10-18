package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cabiso.daphny.com.g_companion.Adapter.Items_Adapter;
import cabiso.daphny.com.g_companion.Model.ForCounter_Rating;
import cabiso.daphny.com.g_companion.Recommend.DIYrecommend;

/**
 * Created by cicctuser on 10/03/2017.
 */

public class Pending_Activity extends AppCompatActivity implements RatingDialogListener{

    private ArrayList<ProductInfo> pendingList = new ArrayList<>();
    private ListView lv;
    private Items_Adapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;
    private UserProfileInfo userProfileInfo;
    private DatabaseReference get_ref;

    int count;
    float curRate;



    private DatabaseReference itemReference;
    public Pending_Activity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_);

        recyclerView = (RecyclerView) findViewById(R.id.list);


        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        lv = (ListView) findViewById(R.id.lvView);

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please Wait loading DIYs.....");
            progressDialog.show();

            database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Pending_Items").child(userID);

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    userProfileInfo = dataSnapshot.getValue(UserProfileInfo.class);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductInfo img = snapshot.getValue(ProductInfo.class);
                        int rate=0;

                        pendingList.add(img);
                        if (userProfileInfo != null){
                            userProfileInfo.getUserRating();
                         //   rate.setText(Integer.toString(userProfileInfo.userRating));
                            DIYrecommend rating = new DIYrecommend();

                        }else{
                            pendingList.add(img);
                        }
                    }
                    //init adapter
                    adapter = new Items_Adapter(Pending_Activity.this, R.layout.recommend_ui, pendingList);
                    //set adapter for listview
                    lv.setAdapter(adapter);
                    final int count =lv.getAdapter().getCount();
                    registerForContextMenu(lv);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            userDatabaseReference = FirebaseDatabase.getInstance().getReference("DIYs_By_Users").child("bottle")
//                                    .child(userID).child("user_ratings");

                       //     pendingReference = FirebaseDatabase.getInstance().getReference("to_recommend").child("user_rating");
                            final ProductInfo itemRef = adapter.getItem(position);
//                            adapter.remove(adapter.getItem(position));
//                            adapter.notifyDataSetChanged();
                            AlertDialog.Builder alert = new AlertDialog.Builder(
                                    Pending_Activity.this);
                            alert.setTitle("HEY!");
                            alert.setMessage("Are you sure you already received the order?");
                            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do your work here
                                    new AppRatingDialog.Builder()
                                            .setPositiveButtonText("Submit")
                                            .setNegativeButtonText("Cancel")
                                            .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                                            .setDefaultRating(2)
                                            .setTitle("Rate the seller!")
                                            .setDescription("Please select some stars and give your feedback to the seller.")
                                            .setTitleTextColor(R.color.titleTextColor)
                                            .setDescriptionTextColor(R.color.contentTextColor)
                                            .setHint("Please write your comment here ...")
                                            .setHintTextColor(R.color.hintTextColor)
                                            .setCommentTextColor(R.color.commentTextColor)
                                            .setCommentBackgroundColor(R.color.bg_screen2)
                                            .setWindowAnimation(R.style.MyDialogFadeAnimation)
                                            .create(Pending_Activity.this)
                                            .show();

                                    dialog.dismiss();

                                    if(itemRef.getOwnerUserID()!=null){
                                        get_ref = FirebaseDatabase.getInstance().getReference("DIYs_By_Users").child("bottle");
                                        get_ref.addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
//                                                DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                                                String key = snapshot1.getKey();
                                                String path = "/" + dataSnapshot.getKey() + "/" + key;
                                                HashMap<String, Object> result = new HashMap<>();
                                                    DIYrecommend recommend = snapshot1.getValue(DIYrecommend.class);
                                                    if(itemRef.getOwnerUserID().equals(recommend.diyownerID)){
                                                        Toast.makeText(getApplicationContext(),"IDs: 1"+recommend.getDiyownerID()+"\n"+"IDs 2: "
                                                                +itemRef.getOwnerUserID(),Toast.LENGTH_SHORT).show();

                                                        if(recommend.getDiyownerID().equals(itemRef.getOwnerUserID())){
//                                                            FirebaseDatabase.getInstance().getReference("DIYs_By_Users").child("bottle").child(itemRef.getOwnerUserID())
//                                                                    .child("user_rating").setValue(5);
                                                            result.put("user_ratings", 4);
                                                            get_ref.child(path).updateChildren(result);
//                                                            recommend.setUser_ratings(5);

                                                        }
//                                                        DatabaseReference ref = database.getReference("DIYs_By_Users").child("bottle").child(itemRef.getOwnerUserID()).getRef();
//                                                        {
//                                                            if(ref!=null){
//                                                                if(ref.child("user_rating")==null){
//                                                                    ref.child("user_ratings").setValue(5);
//                                                                    recommend.setUser_ratings(5);
//                                                                }else{
//                                                                    ref.child("user_ratings").setValue(5);
//                                                                    recommend.setUser_ratings(5);
//                                                                }
////                                                                        ref.child("user_ratings").setValue(5+3);
//                                                            }
//
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

                                }
                            });
                            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            alert.show();

                            Toast toast = Toast.makeText(Pending_Activity.this, itemRef.title
                                    + "\n" + itemRef.ownerUserID + "\n" + itemRef.price + "\n" + itemRef.desc + "\n"
                                    + itemRef.getProductPictureURLs().get(0) + "\n" + count, Toast.LENGTH_SHORT);
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
            inflater.inflate(R.menu.menu_for_myitems, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.sold_item:
                int listPosition = info.position;
                final int count =lv.getAdapter().getCount();
                itemReference = FirebaseDatabase.getInstance().getReference().child("Sold_Items").child(userID);
                String title = pendingList.get(listPosition).title;
                String description = pendingList.get(listPosition).desc;
                String price = pendingList.get(listPosition).price;
                String negotiable = pendingList.get(listPosition).negotiable;
                List productPictureURLs = pendingList.get(listPosition).productPictureURLs;
                ProductInfo product = new ProductInfo(title, description, price, negotiable,productPictureURLs, userID);
                String upload = itemReference.push().getKey();
                itemReference.child(upload).setValue(product);


        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onPositiveButtonClicked(int rate, @NotNull String comment) {

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
       // int rates = (5*252 + 4*124 + 3*40 + 2*29 + 1*33) / (252+124+40+29+33);
        curRate = Float.valueOf(decimalFormat.format((curRate * count + rate)/ ++count));

//        pendingReference.child("ratings").setValue((curRate * count) * 0.4);

        Toast.makeText(Pending_Activity.this,"Rate : " + rate + "\n Comment : " + comment,Toast.LENGTH_SHORT).show();

        DatabaseReference myRef = database.getReference("Sold_Items").child(userID);

        myRef.orderByChild("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e(String.valueOf(snapshot.getRef()), snapshot.getChildrenCount() + "");
                    ProductInfo img = snapshot.getValue(ProductInfo.class);
                    int rate = 0;
                    if (img.getOwnerUserID().equals(userID)) {
//                            img.setProductID(snapshot.getKey());
                      //  pendingList.add(img);
//                          count = String.valueOf(String.valueOf(snapshot.getChildrenCount()).equals(userID));
                        //count = diyList.size();
                        Toast.makeText(Pending_Activity.this, "count: " + rate, Toast.LENGTH_SHORT).show();

                        final DatabaseReference ref = database.getReference("DIYs_By_Users").child("bottle").child(userID);
                        final DatabaseReference ref1 = database.getReference("DIYs_By_Users").child("glass").child(userID);
                        final DatabaseReference ref2 = database.getReference("DIYs_By_Users").child("paper").child(userID);
                        final DatabaseReference ref3 = database.getReference("DIYs_By_Users").child("cup").child(userID);
                        final DatabaseReference ref4 = database.getReference("DIYs_By_Users").child("tire").child(userID);
                        final DatabaseReference ref5 = database.getReference("DIYs_By_Users").child("wood").child(userID);


//                        DatabaseReference ratings = database.getReference("DIYs_By_Users").child("bottle")
//                                .child(userID).child("user_ratings");
                        DIYrecommend recommend = new DIYrecommend();

                        final ForCounter_Rating counter_rating = new ForCounter_Rating();
                        counter_rating.setRating((int) (curRate * count));
                        counter_rating.setTransac_rating(rate);
//                        final ForCounter_Rating counter_rating = new ForCounter_Rating();
//                        counter_rating.setSold(rate);
                        //  counter_rating.setOwnerID(userID);
                        Query get_rate = ref.orderByChild("user_ratings").equalTo(0);
                        get_rate.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    snapshot1.getRef().child("user_ratings").setValue(counter_rating.getRating());
                                    snapshot1.getRef().child("transac_rating").setValue((counter_rating.getSold() * 0.4)
                                    + (counter_rating.getRating() * 0.6));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Query get_rate1 = ref1.orderByChild("user_ratings").equalTo(0);
                        get_rate1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    snapshot1.getRef().child("user_ratings").setValue(counter_rating.getRating());
                                    snapshot1.getRef().child("transac_rating").setValue((counter_rating.getRating() * 0.4)
                                            + (counter_rating.getSold() * 0.6));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Query get_rate2 = ref2.orderByChild("user_ratings").equalTo(0);
                        get_rate2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    snapshot1.getRef().child("user_ratings").setValue(counter_rating.getRating());
                                    snapshot1.getRef().child("transac_rating").setValue((counter_rating.getSold() * 0.4)
                                            + (counter_rating.getRating() * 0.6));
//                                    snapshot1.getRef().child("user_ratings").setValue(counter_rating.getTransac_rating());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Query get_rate3 = ref3.orderByChild("user_ratings").equalTo(0);
                        get_rate3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    snapshot1.getRef().child("user_ratings").setValue(counter_rating.getRating());
                                    snapshot1.getRef().child("transac_rating").setValue((counter_rating.getSold() * 0.4)
                                            + (counter_rating.getRating() * 0.6));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Query get_rate4 = ref4.orderByChild("user_ratings").equalTo(0);
                        get_rate4.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    snapshot1.getRef().child("user_ratings").setValue(counter_rating.getRating());
                                    snapshot1.getRef().child("transac_rating").setValue((counter_rating.getSold() * 0.4)
                                            + (counter_rating.getRating() * 0.6));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Query get_rate5 = ref5.orderByChild("user_ratings").equalTo(0);
                        get_rate5.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    snapshot1.getRef().child("user_ratings").setValue(counter_rating.getRating());
                                    snapshot1.getRef().child("transac_rating").setValue((counter_rating.getSold() * 0.4)
                                            + (counter_rating.getRating() * 0.6));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
//                        String upload = sold.getKey();
//                        reference.child(upload).setValue(counter_rating);

                       // ratings.setValue(recommend);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        lv.removeViewAt(rate);
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}