package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cabiso.daphny.com.g_companion.Adapter.Items_Adapter;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;

/**
 * Created by Lenovo on 8/20/2018.
 */

public class ForMeetUpActivity extends AppCompatActivity {

    private ArrayList<DIYSell> meetUpList = new ArrayList<>();
    final ArrayList<String> meetupKey = new ArrayList<>();
    final ArrayList<String> meetupKeyyy = new ArrayList<>();

    private ListView lv;
    private Items_Adapter adapter;
    private ProgressDialog progressDialog;

    private String userID;
    private FirebaseUser mFirebaseUser;
    private UserProfileInfo userProfileInfo;
    private DatabaseReference userdataReference, meetUpRef, soldReference, diyReference;
    int count;
    final ArrayList<String> prices = new ArrayList<>();
    final ArrayList<DIYSell> diyNames = new ArrayList<>();
    final ArrayList<String> snapKey = new ArrayList<>();

    private DatabaseReference loggedInName;
    private String loggedInUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_meetup);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        lv = (ListView) findViewById(R.id.lvView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        userdataReference = FirebaseDatabase.getInstance().getReference().child("userdata");
        meetUpRef = FirebaseDatabase.getInstance().getReference().child("Items_ForMeetUp").child(userID);
        diyReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
        loggedInName = FirebaseDatabase.getInstance().getReference().child("userdata");
        Log.e("diyReference", String.valueOf(diyReference));

        loggedInName.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedInUserName = dataSnapshot.child("f_name").getValue(String.class);
                loggedInUserName +=" "+dataSnapshot.child("l_name").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        meetUpRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressDialog.dismiss();
                userProfileInfo = dataSnapshot.getValue(UserProfileInfo.class);
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("snapshot", snapshot.getKey());

                    meetupKey.add(snapshot.getKey());
                    meetupKeyyy.add(snapshot.getKey());

                    Log.e("meetupKey", String.valueOf(meetupKey));
                    Log.e("meetupKeyyy", String.valueOf(meetupKeyyy));

                    final DIYSell img = snapshot.getValue(DIYSell.class);
                    int rate = 0;
                    meetUpList.add(img);

                    if (userProfileInfo != null) {
                        userProfileInfo.getUserRating();
                    } else {
                        meetUpList.add(img);
                    }
                    //init adapter
                    adapter = new Items_Adapter(ForMeetUpActivity.this, R.layout.pending_item, meetUpList);
                    //set adapter for listview
                    lv.setAdapter(adapter);
                    final int count = lv.getAdapter().getCount();
                    registerForContextMenu(lv);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            final DIYSell itemRef = adapter.getItem(position);

                            if(itemRef.getIdentity().equals("For Buyer Meet-up")) {
                                Toast.makeText(ForMeetUpActivity.this, "DIY name: " + itemRef.getDiyName() + " = " + position, Toast.LENGTH_SHORT).show();

                                for (int i=0;i < diyNames.size();i++) {
                                    if (diyNames.get(i).getDiyName().equals(itemRef.getDiyName())) {
                                        Log.e("Match", "YESSS" + " : " + diyNames.get(i) + " = " + itemRef.getDiyName());

                                    } else {
                                        Log.e("Match", "NOOO");
                                    }
                                }

                                AlertDialog.Builder ab = new AlertDialog.Builder(ForMeetUpActivity.this, R.style.MyAlertDialogStyle);
                                ab.setTitle("Approval Meet-up");
                                ab.setMessage("Are you done meeting with the buyer?");

                                ab.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Float float_this = Float.valueOf(0);

                                        //ika done, maadto siya sa sold item, nya ari mabutang ang counter para sa qty
                                        soldReference = FirebaseDatabase.getInstance().getReference().child("Sold_Items").child(userID);

                                        String sold_diyName = meetUpList.get(position).getDiyName();
                                        String sold_diyUrl = meetUpList.get(position).getDiyUrl();
                                        String sold_user_id = meetUpList.get(position).getUser_id();
                                        String sold_productID = meetUpList.get(position).getProductID();
                                        String sold_status = meetUpList.get(position).getIdentity();
                                        String sold_buyer = meetUpList.get(position).getBuyerID();
                                        double sold_price = meetUpList.get(position).getSelling_price();

                                        DIYSell product = new DIYSell(sold_diyName, sold_diyUrl, sold_user_id,
                                                sold_productID, "DELIVERED", float_this, float_this, sold_buyer, loggedInUserName);

                                        String upload = soldReference.push().getKey();
                                        soldReference.child(upload).setValue(product);
                                        soldReference.child(upload).child("userStatus").setValue("seller");
                                        soldReference.child(upload).child("selling_price").setValue(sold_price);

//                                        if (sold_productID.equals(itemRef.getProductID())) {
//                                            HashMap<String, Object> results = new HashMap<>();
//                                            results.put("selling_qty", itemRef.getSelling_qty()-1);
//                                            Log.e("SLLINGQTY", String.valueOf(itemRef.getSelling_qty()-1));
//                                            soldReference.child(upload).updateChildren(results);
//                                        }

                                        //DBref for buyer
                                        DatabaseReference soldReference = FirebaseDatabase.getInstance().getReference()
                                                .child("Sold_Items").child(sold_buyer);

                                        DIYSell buyProduct = new DIYSell(sold_diyName, sold_diyUrl, sold_user_id,
                                                sold_productID, "PURCHASED", float_this, float_this, sold_buyer, loggedInUserName);

                                        String buyUpload = soldReference.child(upload).getKey();
                                        soldReference.child(buyUpload).setValue(buyProduct);
                                        soldReference.child(buyUpload).child("userStatus").setValue("buyer");
                                        soldReference.child(buyUpload).child("selling_price").setValue(sold_price);

                                        DatabaseReference meetReference = FirebaseDatabase.getInstance().getReference()
                                                .child("Items_ForMeetUp").child(sold_buyer);

                                        //remove my item and send to SOLD_Items
                                        String key = meetupKey.get(position);
                                        String keys = meetupKeyyy.get(position);

                                        meetUpRef.child(key).removeValue();
                                        meetReference.child(keys).removeValue();

                                        //get DIY quantity
                                        int meetUpQty = meetUpList.get(position).getSelling_qty();
                                        Log.e("meetUpQty", String.valueOf(meetUpQty));

                                        //decrease quantity
                                        int quantityCount = meetUpQty;
                                        quantityCount--;
                                        Log.e("countt", String.valueOf(quantityCount));

                                        //update quatity count
//                                        HashMap<String, Object> results = new HashMap<>();
//                                        results.put("selling_qty", quantityCount);
//                                        diyReference.child("key sa kani nga diy? huhu").child("DIY Price").updateChildren(results);
//
                                        Intent intent = new Intent(ForMeetUpActivity.this, Sold_Activity.class);
                                        startActivity(intent);
                                    }
                                });

                                ab.setNegativeButton("NOT YET", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                ab.create().show();

                            }

                            //if seller meet-up
                            // i alert dialog ang meet up date?


                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        diyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(final DataSnapshot snaps : dataSnapshot.getChildren()){
                    Log.e("dbsnap", "" + String.valueOf(snaps));
                    DIYnames diyInfo = snaps.getValue(DIYnames.class);
                    final DIYSell diySellinfo = snaps.getValue(DIYSell.class);

                    Log.e("snapsKey", snaps.getKey() + " = " + snaps.child("diyName").getValue());

                    diyNames.add(diySellinfo);
                    Log.e("diyNamess", String.valueOf(diyNames));

                    snapKey.add(snaps.getKey() + " = " + snaps.child("diyName").getValue());

                    final String key = snaps.getKey() + " = " + snaps.child("diyName").getValue();

                    String message_price="";
                    String message_qty = "";
                    String message_dsc = "";

                    List<String> message_Price = new ArrayList<String>();
                    List<String> message_Qty = new ArrayList<String>();
                    final List<String> message_Dsc = new ArrayList<String>();


                    for(DataSnapshot priceSnap : snaps.child("DIY Price").getChildren()){
                        Log.e("priceSnap", String.valueOf(priceSnap));

                        double price = priceSnap.child("selling_price").getValue(double.class);
                        int qty = priceSnap.child("selling_qty").getValue(int.class);
                        String dsc = priceSnap.child("selling_descr").getValue(String.class);

                        message_qty += qty;
                        message_Qty.add(message_qty);

                        message_price += price;
                        message_Price.add(message_price);

                        message_dsc +=dsc;
                        message_Dsc.add(message_dsc);

                        Log.e("message_qty","" +  message_qty);
                        Log.e("message_price", "" + message_price);

                        prices.add(message_qty);
                        Log.e("pricess", String.valueOf(prices));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
