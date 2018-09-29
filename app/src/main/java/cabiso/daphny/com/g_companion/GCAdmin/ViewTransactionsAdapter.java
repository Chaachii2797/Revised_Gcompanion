package cabiso.daphny.com.g_companion.GCAdmin;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 9/18/2018.
 */

public class ViewTransactionsAdapter extends ArrayAdapter<DIYSell> {

    private Activity context;
    private int resource;
    private List<DIYSell> list_diys_logs;
    private DatabaseReference dbReference, userReference;
    private ArrayList<String> userStatusList = new ArrayList<>();
    private String sellerIDs, buyerID;

    public ViewTransactionsAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<DIYSell> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        list_diys_logs = objects;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();

        final View v = inflater.inflate(resource, null);

        dbReference = FirebaseDatabase.getInstance().getReference().child("Sold_Items");
        userReference = FirebaseDatabase.getInstance().getReference().child("userdata");

        final TextView get_transac_date = (TextView) v.findViewById(R.id.get_transac_date);
        TextView get_transac_diy_name = (TextView) v.findViewById(R.id.get_transac_diy_name);
        final TextView get_transac_buyer_name = (TextView) v.findViewById(R.id.get_transac_buyer_name);
        final TextView get_who = (TextView) v.findViewById(R.id.tv_reported_by);
        final TextView get_qty = (TextView) v.findViewById(R.id.tvQTY);

        get_transac_diy_name.setText(list_diys_logs.get(position).getDiyName());
        get_qty.setText("Buy " + list_diys_logs.get(position).getBuyingQuantity() + " piece/s"
                + " with the price of " + list_diys_logs.get(position).getSelling_price() + " pesos.");

        Log.e("buyerID", list_diys_logs.get(position).getBuyerID());
        Log.e("sellerID", list_diys_logs.get(position).getUser_id());

        dbReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("datSnap", dataSnapshot.getKey());

                if(list_diys_logs.get(position).getUser_id().equals(dataSnapshot.getKey())){
                    Log.e("IDsss", list_diys_logs.get(position).getUser_id() + " = " + dataSnapshot.getKey());

                    for(DataSnapshot viewLogSnap : dataSnapshot.getChildren()){
                        Log.e("viewLog", viewLogSnap.getKey());


                        Object dateAdded = viewLogSnap.child("dateAdded").getValue();
                        Log.e("dateAdded", String.valueOf(dateAdded));

                        String transacDate = String.valueOf(dateAdded);
                        get_transac_date.setText(transacDate);

                        Object userStatus = viewLogSnap.child("userStatus").getValue();
                        Log.e("userStatus", String.valueOf(userStatus));
                        userStatusList.add(userStatus.toString());
                        Log.e("sellerList", userStatus.toString());


                        userReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for(DataSnapshot userSnaps : dataSnapshot.getChildren()){
                                    User_Profile userNames = userSnaps.getValue(User_Profile.class);
                                    Log.e("userNames", userNames.getF_name());

                                        if(userStatusList.get(position).equals("buyer")) {
                                        Log.e("userStatusList", "BUYER KO " + list_diys_logs.get(position).getUser_id()); //if buyer, kuhaon ang userID (sa seller na)
                                        buyerID = list_diys_logs.get(position).getUser_id();
                                        Log.e("buyerID", buyerID);

                                        if(buyerID.equals(userNames.getUserID())){
                                            get_who.setText("Seller: ");
                                            get_transac_buyer_name.setText(userNames.getF_name() + " " + userNames.getL_name());

                                        }

                                    }


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                }
                else if (list_diys_logs.get(position).getBuyerID().equals(dataSnapshot.getKey())){
                    for(DataSnapshot viewLogSnap : dataSnapshot.getChildren()){
                        Log.e("viewLog", viewLogSnap.getKey());


                        Object dateAdded = viewLogSnap.child("dateAdded").getValue();
                        Log.e("dateAdded", String.valueOf(dateAdded));

                        String transacDate = String.valueOf(dateAdded);
                        get_transac_date.setText(transacDate);

                        Object userStatus = viewLogSnap.child("userStatus").getValue();
                        Log.e("userStatus", String.valueOf(userStatus));
                        userStatusList.add(userStatus.toString());
                        Log.e("sellerList", userStatus.toString());


                        userReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for(DataSnapshot userSnaps : dataSnapshot.getChildren()){
                                    User_Profile userNames = userSnaps.getValue(User_Profile.class);
                                    Log.e("userNames", userNames.getF_name());

                                    if(userStatusList.get(position).equals("seller")){
                                        Log.e("userStatusList", "SELLER KO " +list_diys_logs.get(position).getBuyerID()); //if seller, kuhaon ang buyerID (sa buyer)
                                        sellerIDs = list_diys_logs.get(position).getBuyerID();
                                        Log.e("sellerIDs", sellerIDs);

                                        if(sellerIDs.equals(userNames.getUserID())){
                                            get_who.setText("Buyer: ");
                                            get_transac_buyer_name.setText(userNames.getF_name() + " " + userNames.getL_name());

                                        }

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

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




        return v;
    }
}
