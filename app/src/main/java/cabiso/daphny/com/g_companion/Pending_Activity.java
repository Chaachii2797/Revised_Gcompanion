package cabiso.daphny.com.g_companion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import cabiso.daphny.com.g_companion.Adapter.Items_Adapter;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.Review;
import cabiso.daphny.com.g_companion.Recommend.PendingView_Activity;

/**
 * Created by cicctuser on 10/03/2017.
 */

public class Pending_Activity extends AppCompatActivity implements RatingDialogListener{

    private ArrayList<DIYnames> pendingList = new ArrayList<>();
    private ArrayList<DIYnames> ratingList = new ArrayList<>();
    private ListView lv;
    private Items_Adapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;
    private UserProfileInfo userProfileInfo;
    private DatabaseReference pendingReference;
    private DatabaseReference userdataReference;

    private ArrayList<DBMaterial> dbMaterials;

    int count;
    float curRate;

    private DatabaseReference itemReference;
    public Pending_Activity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_);

        recyclerView = (RecyclerView) findViewById(R.id.lvView);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        lv = (ListView) findViewById(R.id.lvView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        database = FirebaseDatabase.getInstance();
        userdataReference = FirebaseDatabase.getInstance().getReference().child("userdata");
//        userdataReference = FirebaseDatabase.getInstance().getReference().child("userdata");
        pendingReference = FirebaseDatabase.getInstance().getReference().child("DIY Pending Items").child(userID);

        pendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                userProfileInfo = dataSnapshot.getValue(UserProfileInfo.class);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DIYnames img = snapshot.getValue(DIYnames.class);
                    int rate=0;
                    pendingList.add(img);
                    if (userProfileInfo != null){
                        userProfileInfo.getUserRating();
                    }else{
                        pendingList.add(img);
                    }
                }
                //init adapter
                adapter = new Items_Adapter(Pending_Activity.this, R.layout.pending_layout, pendingList);
                //set adapter for listview
                lv.setAdapter(adapter);
                final int count =lv.getAdapter().getCount();
                registerForContextMenu(lv);
//                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                       DIYnames selected_diy = (DIYnames) parent.getAdapter().getItem(position);
//                        String name = selected_diy.getDiyName();
//
//                        Intent intent = new Intent(Pending_Activity.this,PendingView_Activity.class);
//                        intent.putExtra("name",name);
//
//                        Bundle extra = new Bundle();
//                        extra.putSerializable("dbmaterials", dbMaterials);
//                        intent.putExtra("dbmaterials", extra);
//                        startActivity(intent);
//                    }
//                });

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final DIYnames itemRef = adapter.getItem(position);
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
                                        .setTitleTextColor(R.color.gen_scheme4)
                                        .setDescriptionTextColor(R.color.contentTextColor)
                                        .setHint("Please write your comment here ...")
                                        .setHintTextColor(R.color.hintTextColor)
                                        .setCommentTextColor(R.color.commentTextColor)
                                        .setCommentBackgroundColor(R.color.gen_scheme1)
                                        .setWindowAnimation(R.style.MyDialogFadeAnimation)
                                        .create(Pending_Activity.this)
                                        .show();

                                dialog.dismiss();
                            }
                        });
                        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        alert.show();

                        Toast toast = Toast.makeText(Pending_Activity.this, itemRef.diyName
                                + "\n" + itemRef.user_id + "\n" + itemRef.getDiyUrl() + "\n" + count, Toast.LENGTH_SHORT);
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
                Float float_this = Float.valueOf(0);
                final int count =lv.getAdapter().getCount();
                itemReference = FirebaseDatabase.getInstance().getReference().child("Sold_Items").child(userID);

                String pending_diyName = pendingList.get(listPosition).getDiyName();
                String pending_diyUrl = pendingList.get(listPosition).getDiyUrl();
                String pending_user_id = pendingList.get(listPosition).getUser_id();
                String pending_productID = pendingList.get(listPosition).getProductID();
                String pending_status = pendingList.get(listPosition).getIdentity();

                DIYnames product = new DIYnames(pending_diyName, pending_diyUrl, pending_user_id,
                        pending_productID, pending_status, float_this, float_this);

                String upload = itemReference.push().getKey();
                itemReference.child(upload).setValue(product);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onPositiveButtonClicked(final int rate, @NotNull final String comment) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        // int rates = (5*252 + 4*124 + 3*40 + 2*29 + 1*33) / (252+124+40+29+33);
        curRate = Float.valueOf(decimalFormat.format((curRate * count + rate)/ ++count));
        final float curRatee = (int) (curRate * count);
//        curRate = Float.valueOf(decimalFormat.format((curRate + rate)));
        Toast.makeText(Pending_Activity.this,"Rate : " + rate + "\n Comment : " + comment,Toast.LENGTH_SHORT).show();
        pendingReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final DIYnames diYs = dataSnapshot.getValue(DIYnames.class);
//                Review review = new Review().setComment(comment).setRatings(rate).setReviewer(userID).setDate_submitted("");
                final Review review = new Review().setRatings(rate).setComment(comment).setReviewer(userID).setDate_submitted("");
                if(diYs.getUser_id()!=null){
                    userdataReference.child(diYs.getUser_id()).child("ratings").push().setValue(review);
                }else{
                    Toast.makeText(Pending_Activity.this, "USERIDIMG: "+diYs.user_id, Toast.LENGTH_SHORT).show();
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
    public void onNegativeButtonClicked() {

    }
}