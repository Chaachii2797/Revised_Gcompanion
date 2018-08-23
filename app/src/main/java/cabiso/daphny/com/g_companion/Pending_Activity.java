package cabiso.daphny.com.g_companion;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import cabiso.daphny.com.g_companion.InstantMessaging.ui.activities.ChatSplashActivity;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;

/**
 * Created by cicctuser on 10/03/2017.
 */

public class Pending_Activity extends AppCompatActivity{

    private ArrayList<DIYnames> pendingList = new ArrayList<>();
    private ArrayList<DIYSell> sellList = new ArrayList<>();

    private ListView lv;
    private Items_Adapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;
    private UserProfileInfo userProfileInfo;
    private DatabaseReference pendingReference, forMeetupReference;
    private DatabaseReference userdataReference;
//    String snapId;
    private ArrayList<DBMaterial> dbMaterials;
    int count;

    private DatabaseReference soldReference;
    final ArrayList<String> keyList = new ArrayList<>();
    final ArrayList<String> keyListss = new ArrayList<>();

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
        pendingReference = FirebaseDatabase.getInstance().getReference().child("DIY Pending Items").child(userID);

        pendingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                userProfileInfo = dataSnapshot.getValue(UserProfileInfo.class);
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("snapshot", snapshot.getKey());

                    keyList.add(snapshot.getKey());
                    keyListss.add(snapshot.getKey());

                    Log.e("keyList", String.valueOf(keyList));
                    Log.e("keyListss", String.valueOf(keyListss));

                    final DIYSell img = snapshot.getValue(DIYSell.class);
                    int rate=0;
                    sellList.add(img);

                    if (userProfileInfo != null){
                        userProfileInfo.getUserRating();
                    }else{
                        sellList.add(img);
                    }
                    //init adapter
                    adapter = new Items_Adapter(Pending_Activity.this, R.layout.pending_item, sellList);
                    //set adapter for listview
                    lv.setAdapter(adapter);
                    final int count =lv.getAdapter().getCount();
                    registerForContextMenu(lv);


                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            final DIYSell itemRef = adapter.getItem(position);

                            Toast.makeText(Pending_Activity.this, "Pos: " + itemRef, Toast.LENGTH_SHORT).show();


                            if(itemRef.getIdentity().equals("For Confirmation")){
                                Toast.makeText(Pending_Activity.this, "This is your item. Contact buyer.", Toast.LENGTH_SHORT).show();

                                Log.e("itemref", itemRef.getBuyerID());

                                final Dialog myDialog = new Dialog(Pending_Activity.this);
                                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                myDialog.setContentView(R.layout.dialog_pending_approval);
                                final Button chat = (Button) myDialog.findViewById(R.id.dialogBtbChatSeller);
                                final Button forMeetup = (Button) myDialog.findViewById(R.id.dialogBtnForMeetUp);
                                final Button sold = (Button) myDialog.findViewById(R.id.dialogBtnSold);
                                TextView textTitle = (TextView) myDialog.findViewById(R.id.textTitle);
                                TextView textDone = (TextView) myDialog.findViewById(R.id.textDone);

                                chat.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Pending_Activity.this, ChatSplashActivity.class);
                                        startActivity(intent);
                                    }
                                });

                                forMeetup.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Float float_this = Float.valueOf(0);

                                        forMeetupReference = FirebaseDatabase.getInstance().getReference()
                                                .child("Items_ForMeetUp").child(userID);

                                        String meetup_diyName = sellList.get(position).getDiyName();
                                        String meetup_diyUrl = sellList.get(position).getDiyUrl();
                                        String meetup_user_id = sellList.get(position).getUser_id();
                                        String meetup_productID = sellList.get(position).getProductID();
                                        String meetup_status = sellList.get(position).getIdentity();
                                        String meetup_buyer = sellList.get(position).getBuyerID();
                                        double meetup_price = sellList.get(position).getSelling_price();
                                        int meetup_qty = sellList.get(position).getSelling_qty();

                                        DIYSell product = new DIYSell(meetup_diyName, meetup_diyUrl, meetup_user_id,
                                                meetup_productID, "For Buyer Meet-up" , float_this, float_this, meetup_buyer);

                                        String upload = forMeetupReference.push().getKey();
                                        forMeetupReference.child(upload).setValue(product);
                                        forMeetupReference.child(upload).child("userStatus").setValue("seller");
                                        forMeetupReference.child(upload).child("selling_price").setValue(meetup_price);
                                        forMeetupReference.child(upload).child("selling_qty").setValue(meetup_qty);


                                        DatabaseReference meetReference = FirebaseDatabase.getInstance().getReference()
                                                .child("Items_ForMeetUp").child(meetup_buyer);

                                        //DBref for buyer
                                        DIYSell buyProduct = new DIYSell(meetup_diyName, meetup_diyUrl, meetup_user_id,
                                                meetup_productID, "For Seller Meet-up" , float_this, float_this, meetup_buyer);

                                        String buyUpload = meetReference.child(upload).getKey();
                                        meetReference.child(buyUpload).setValue(buyProduct);
                                        meetReference.child(buyUpload).child("userStatus").setValue("buyer");
                                        meetReference.child(buyUpload).child("selling_price").setValue(meetup_price);

                                        sellList.remove(position);
                                        adapter.notifyDataSetChanged();

                                        //remove my item and send to For_MeetUp
                                        DatabaseReference pendReference = FirebaseDatabase.getInstance().getReference()
                                                .child("DIY Pending Items").child(meetup_buyer);

                                        String penkey = keyList.get(position);
                                        String penkeys = keyListss.get(position);

                                        pendingReference.child(penkey).removeValue();
                                        pendReference.child(penkeys).removeValue();


//                                        Intent item = new Intent(Pending_Activity.this, ForMeetUpActivity.class);
//                                        item.putExtra("meetup_qty", meetup_qty);
//
//                                        startActivity(item);

                                    }
                                });

                                sold.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        Float float_this = Float.valueOf(0);

                                        soldReference = FirebaseDatabase.getInstance().getReference().child("Sold_Items").child(userID);

                                        String pending_diyName = sellList.get(position).getDiyName();
                                        String pending_diyUrl = sellList.get(position).getDiyUrl();
                                        String pending_user_id = sellList.get(position).getUser_id();
                                        String pending_productID = sellList.get(position).getProductID();
                                        String pending_status = sellList.get(position).getIdentity();
                                        String pending_buyer = sellList.get(position).getBuyerID();
                                        double pending_price = sellList.get(position).getSelling_price();
                                        int pendng_qty = sellList.get(position).getSelling_qty();

                                        DIYSell product = new DIYSell(pending_diyName, pending_diyUrl, pending_user_id,
                                                pending_productID, "PURCHASED" , float_this, float_this, pending_buyer);

                                        String upload = soldReference.push().getKey();
                                        soldReference.child(upload).setValue(product);
                                        soldReference.child(upload).child("userStatus").setValue("seller");
                                        soldReference.child(upload).child("selling_price").setValue(pending_price);
                                        soldReference.child(upload).child("selling_qty").setValue(pendng_qty);


                                        DatabaseReference buyerReference = FirebaseDatabase.getInstance().getReference()
                                                .child("Sold_Items").child(pending_buyer);

                                        DIYSell buyProduct = new DIYSell(pending_diyName, pending_diyUrl, pending_user_id,
                                                pending_productID, "PURCHASED" , float_this, float_this, pending_buyer);

                                        String buyUpload = buyerReference.child(upload).getKey();
                                        buyerReference.child(buyUpload).setValue(buyProduct);
                                        buyerReference.child(buyUpload).child("userStatus").setValue("buyer");
                                        buyerReference.child(buyUpload).child("selling_price").setValue(pending_price);


                                    }
                                });

                                textDone.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Pending_Activity.this, ForMeetUpActivity.class);
                                        startActivity(intent);
                                        myDialog.dismiss();
                                    }
                                });

                                myDialog.show();

                            }

                            else {

                                Log.e("itemrefff", itemRef.getBuyerID());
                                // Alert Dialog for pending item state
                                AlertDialog.Builder ab = new AlertDialog.Builder(Pending_Activity.this, R.style.MyAlertDialogStyle);
                                ab.setTitle("Approval Pending");
                                ab.setMessage("Your DIY item with product ID " + "[" + itemRef.getProductID() + "]" + " is in Pending state");
                                ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                ab.create().show();

                            }
                        }
                    });

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}