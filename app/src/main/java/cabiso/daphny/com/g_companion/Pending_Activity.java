package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cabiso.daphny.com.g_companion.Adapter.Items_Adapter;

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
    private DatabaseReference pendingReference;
    private DatabaseReference userDatabaseReference;

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
                        if (img.getOwnerUserID().toString().equals(userID)) {
                            pendingList.add(img);
                        }else if (userProfileInfo != null){
                            userProfileInfo.getUserRating();
                         //   rate.setText(Integer.toString(userProfileInfo.userRating));
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
                            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("DIYs_By_Users").child(userID).child("user_rating");

                       //     pendingReference = FirebaseDatabase.getInstance().getReference("to_recommend").child("user_rating");
                            ProductInfo itemRef = adapter.getItem(position);
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
        //pendingReference = FirebaseDatabase.getInstance().getReference("to_recommend").child("user_rating").setValue(curRate+count);
//
     //   String upload = databaseReference.push().getKey();
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        curRate = Float.valueOf(decimalFormat.format((curRate * count + rate)/ ++count));

        userDatabaseReference.child("ratings").setValue(curRate + count);

        Toast.makeText(Pending_Activity.this,"Rate : " + rate + "\n Comment : " + comment,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNegativeButtonClicked() {

    }
}