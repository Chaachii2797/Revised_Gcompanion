package cabiso.daphny.com.g_companion.Recommend;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;

public class Activity_View_Recommend extends AppCompatActivity {

    private ViewPager imgview;
    private ViewImagesRecommendationPagerAdapter diyImagesViewPagerAdapter;
    private TextView diy_name, diy_materials, diy_procedures, diy_sell, php, user_owner_name,txtBy,
            owner_add, owner_cn;
    private Button button_sell, contact_seller, create_promo;
    private String user_name, userID;
    private CardView selling_price, seller_info;

    private ArrayList<DBMaterial> dbMaterials;
    private DatabaseReference user_data;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference databaseReference;
    private DatabaseReference pending_reference;
    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__view__recommend);

        imgview = (ViewPager) findViewById(R.id.diyImagesViewPagers_sell);
        diy_name = (TextView) findViewById(R.id.diy_name);
        diy_materials = (TextView) findViewById(R.id.diy_material);
        diy_procedures = (TextView) findViewById(R.id.diy_procedure);
        diy_sell = (TextView) findViewById(R.id.sell_details);
        user_owner_name = (TextView) findViewById(R.id.txt_user_owner_name);
        txtBy = (TextView) findViewById(R.id.txt_by);
        button_sell = (Button) findViewById(R.id.btn_sell_diy);
        php = (TextView) findViewById(R.id.textView33);
        contact_seller = (Button) findViewById(R.id.btn_contact_diy_owner);
        //create_promo = (Button) findViewById(R.id.btn_create_promo);
        owner_add = (TextView) findViewById(R.id.diy_owner_add);
        owner_cn = (TextView) findViewById(R.id.diy_owner_cn);
        selling_price = (CardView) findViewById(R.id.cardView3);
        seller_info = (CardView) findViewById(R.id.cardView4);


        final String get_name = getIntent().getStringExtra("name");
        diy_name.setText(get_name);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();
        Bundle extra = getIntent().getBundleExtra("dbmaterials");
        dbMaterials = (ArrayList<DBMaterial>) extra.getSerializable("dbmaterials");
        user_data = FirebaseDatabase.getInstance().getReference().child("userdata");
        //databaseReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
        pending_reference = FirebaseDatabase.getInstance().getReference("DIY Pending Items").child(userID);


        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags");
            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    final DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                    final DIYSell info = dataSnapshot.getValue(DIYSell.class);

                    if(get_name.equals(diYnames.getDiyName())){
                        if(diYnames.getIdentity().equals("selling")) {

                            diy_sell.setVisibility(View.VISIBLE);
                            user_owner_name.setVisibility(View.VISIBLE);
                            button_sell.setVisibility(View.VISIBLE);
                            contact_seller.setVisibility(View.VISIBLE);
                            //create_promo.setVisibility(View.VISIBLE);
                            php.setVisibility(View.VISIBLE);
                            owner_add.setVisibility(View.VISIBLE);
                            owner_cn.setVisibility(View.VISIBLE);
                            selling_price.setVisibility(View.VISIBLE);
                            seller_info.setVisibility(View.VISIBLE);
                            diy_name.setText(info.diyName);

                            user_data.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
//                                    if(diYnames.getUser_id().equals(user_profile.getUserID())){
//                                        user_name = user_profile.getF_name()+" "+user_profile.getL_name();
//                                        user_owner_name.setText(user_name);
//                                        Log.e("user_name", "" + user_name);
//                                    }else if(diYnames.user_id.equals(user_profile.getUserID())){
//                                        user_name = user_profile.getF_name()+" "+user_profile.getL_name();
//                                        user_owner_name.setText(user_name);
//                                    }
                                    if(diYnames.user_id.equals(user_profile.getUserID())){
                                        user_name = user_profile.getF_name()+" "+user_profile.getL_name();
                                        user_owner_name.setText(user_name);
                                        owner_cn.setText(user_profile.getContact_no());
                                        owner_add.setText(user_profile.getAddress());
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


                            String message_price="";
                            List<String> message_Price = new ArrayList<String>();
                            for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                                int price= postSnapshot.child("selling_price").getValue(int.class);
                                message_price += price;
                                message_Price.add(message_price);
                            }

                            final String finalMessage_price = message_price;
                            button_sell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final Float float_this = Float.valueOf(0);

                                            DIYSell productInfo = dataSnapshot.getValue(DIYSell.class);
                                            final String diyName = productInfo.getDiyName();
                                            final String diyUrl = productInfo.getDiyUrl();
                                            final String user_id = productInfo.getUser_id();
                                            final String productID = productInfo.getProductID();
                                            final String status = productInfo.getIdentity();

                                            if (!userID.equals(info.getUser_id())) {
                                                Log.e("pending_not_same", String.valueOf("" + userID != info.getUser_id()));
                                                Log.e("userid", String.valueOf("" + userID));
                                                Log.e("info_userid", String.valueOf("" + info.getUser_id()));

                                                pending_reference.orderByChild("productID").equalTo(info.productID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            Log.e("same_prodID", "" + dataSnapshot.exists());
                                                            Log.e("same_data", "" + dataSnapshot);

                                                            final Dialog dialog = new Dialog(Activity_View_Recommend.this);
                                                            dialog.setContentView(R.layout.exist_dialog);
                                                            TextView text = (TextView) dialog.findViewById(R.id.et_email);
                                                            text.setText("DIY already added to pending list!");
                                                            ImageView image = (ImageView) dialog.findViewById(R.id.exist_dialog_imageview);
                                                            image.setImageResource(R.drawable.exist);

                                                            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOKI);
                                                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(Activity_View_Recommend.this, Bottle_Recommend.class);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                            dialog.show();
                                                        } else {

                                                            DIYSell info = new DIYSell(diyName, diyUrl, user_id, productID, status, float_this, float_this);
                                                            String upload_info = pending_reference.push().getKey();
                                                            pending_reference.child(upload_info).setValue(info);
                                                            pending_reference.child(upload_info).child("DIY Price").setValue(finalMessage_price);

                                                            final Dialog dialog = new Dialog(Activity_View_Recommend.this);
                                                            dialog.setContentView(R.layout.done_dialog);
                                                            TextView text = (TextView) dialog.findViewById(R.id.text);
                                                            text.setText("DIY added to pending list!");
                                                            ImageView image = (ImageView) dialog.findViewById(R.id.dialog_imageview);
                                                            image.setImageResource(R.drawable.done);

                                                            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(Activity_View_Recommend.this, Bottle_Recommend.class);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                            dialog.show();
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }else if(userID.equals(info.getUser_id())) {
                                                Log.e("pending_same", String.valueOf("" + userID.equals(info.getUser_id())));
                                                Toast.makeText(Activity_View_Recommend.this, "It's your own product!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            });

                            contact_seller.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(Activity_View_Recommend.this, "Contact Seller button clicked!", Toast.LENGTH_SHORT).show();

                                    final Dialog myDialog = new Dialog(context);
                                    myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    myDialog.setContentView(R.layout.contact_seller);
                                    myDialog.setCancelable(false);
                                    Button chat = (Button) myDialog.findViewById(R.id.chat);
                                    Button call = (Button) myDialog.findViewById(R.id.call);
                                    Button sms = (Button) myDialog.findViewById(R.id.sms);
                                    TextView cancel = (TextView) myDialog.findViewById(R.id.cancel);

                                    chat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(Activity_View_Recommend.this, "Chat button clicked!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    call.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(Activity_View_Recommend.this, "Call button clicked!", Toast.LENGTH_SHORT).show();

                                            String phone = owner_cn.getText().toString();
                                            Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                                                    "tel", phone, null));
                                            startActivity(phoneIntent);
                                        }
                                    });
                                    sms.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(Activity_View_Recommend.this, "SMS button clicked!", Toast.LENGTH_SHORT).show();

                                            String phone = owner_cn.getText().toString();
                                            Intent smsMsgAppVar = new Intent(Intent.ACTION_VIEW);
                                            smsMsgAppVar.setData(Uri.parse("sms:" +  phone));
                                            smsMsgAppVar.putExtra("sms_body", "Hi, Good Day! ");
                                            startActivity(smsMsgAppVar);
                                        }
                                    });
                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            myDialog.cancel();
                                        }
                                    });

                                    myDialog.show();
                                    myDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                        @Override
                                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                dialog.cancel();
                                                return true;
                                            }
                                            return false;
                                        }
                                    });
                                }
                            });


                            String messageMat = "";
                            List<String> messageMaterials = new ArrayList<String>();
                            //int count = 1;
                            for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                                String dbMaterialName = postSnapshot.child("name").getValue(String.class).toLowerCase();
                                String dbMaterialUnit = postSnapshot.child("unit").getValue(String.class);
                                Long dbMaterialQuantity = postSnapshot.child("quantity").getValue(Long.class);
//                                messageMat += "\n" + dbMaterialName + " = " + dbMaterialQuantity + " " + dbMaterialUnit;
                                messageMat += "\n" + dbMaterialQuantity + " " + dbMaterialUnit+ " " +dbMaterialName;
                                messageMaterials.add(messageMat);
                                //count++;
                            }

                            String[] splits = dataSnapshot.child("procedures").getValue().toString().split(",");
                            Log.e("splits", "" + splits);

                            String messageProd = "";
                            List<String> messageProcedure = new ArrayList<String>();
                            for (int i = 0; i < splits.length; i++) {
                                Log.d("splitVal", splits[i].substring(5, splits[i].length() - 1));
                                String message = i + 1 + ". " + splits[i].substring(5, splits[i].length() - 1).replaceAll("\\}", "").replaceAll("=", "");
                                messageProd += "\n" + message;
                                messageProcedure.add(message);
                                Log.d("messageProd", messageProd);
                            }

                            diy_materials.setText(messageMat);
//                            tv_procedures.setText("NOT APPLICABLE! BUY THE ITEM FIRST OR ASK PERMISSION TO THE OWNER!");
                            diy_procedures.setText(messageProd);
                            diy_procedures.setTextColor(Color.BLACK);
                            diy_sell.setText(message_price);

                            if (diYnames.diyUrl != null) {
                                imgview = (ViewPager) findViewById(R.id.diyImagesViewPagers_sell);
                                diyImagesViewPagerAdapter = new ViewImagesRecommendationPagerAdapter(getBaseContext(), diYnames.diyUrl);
                                imgview.setAdapter(diyImagesViewPagerAdapter);
                            }



                        }
                        else if(diYnames.getIdentity().equals("community")) {

                            diy_sell.setVisibility(View.INVISIBLE);
                            button_sell.setVisibility(View.INVISIBLE);
                            contact_seller.setVisibility(View.INVISIBLE);
                            //create_promo.setVisibility(View.INVISIBLE);
                            php.setVisibility(View.INVISIBLE);
                            owner_add.setVisibility(View.INVISIBLE);
                            owner_cn.setVisibility(View.INVISIBLE);
                            selling_price.setVisibility(View.INVISIBLE);
                            seller_info.setVisibility(View.INVISIBLE);
                            user_owner_name.setVisibility(View.VISIBLE);
                            txtBy.setVisibility(View.VISIBLE);
                            diy_name.setText(diYnames.diyName);

                            user_data.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                    if(diYnames.getUser_id().equals(user_profile.getUserID())){
                                        user_name = user_profile.getF_name()+" "+user_profile.getL_name();
                                        user_owner_name.setText(user_name);
                                        Log.e("user_name", "" + user_name);
                                    }else if(diYnames.user_id.equals(user_profile.getUserID())){
                                        user_name = user_profile.getF_name()+" "+user_profile.getL_name();
                                        user_owner_name.setText(user_name);
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

                            String messageMat = "";
                            List<String> messageMaterials = new ArrayList<String>();
                            //int count = 1;
                            for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                                String dbMaterialName = postSnapshot.child("name").getValue(String.class).toLowerCase();
                                String dbMaterialUnit = postSnapshot.child("unit").getValue(String.class);
                                Long dbMaterialQuantity = postSnapshot.child("quantity").getValue(Long.class);
//                                messageMat += "\n" + dbMaterialName + " = " + dbMaterialQuantity + " " + dbMaterialUnit;
                                messageMat += "\n" + dbMaterialQuantity + " " + dbMaterialUnit+ " "+ dbMaterialName;
                                messageMaterials.add(messageMat);
                                //count++;
                            }

                            String[] splits = dataSnapshot.child("procedures").getValue().toString().split(",");
                            Log.e("splits", "" + splits);

//                            String messageMat = "";
//                            List<String> messageMaterials = new ArrayList<String>();
//                            int count = 1;
//                            for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
//                                DataSnapshot dbMaterialNode = postSnapshot;
//                                String dbMaterialName = dbMaterialNode.child("name").getValue(String.class).toLowerCase();
//                                String dbMaterialUnit = dbMaterialNode.child("unit").getValue(String.class);
//                                long dbMaterialQuantity = dbMaterialNode.child("quantity").getValue(Long.class);
//                                messageMat = dbMaterialQuantity + " " + dbMaterialUnit + " " + dbMaterialName;
//                                messageMaterials.add(messageMat);
//                                count++;
//                            }
//
//                            String[] splits = dataSnapshot.child("procedures").getValue().toString().split(",");
//                            Log.e("splits", "" + splits);

                            String messageProd = "";
                            List<String> messageProcedure = new ArrayList<String>();
                            for (int i = 0; i < splits.length; i++) {
                                Log.d("splitVal", splits[i].substring(5, splits[i].length() - 1));
                                String message = i + 1 + ". " + splits[i].substring(5, splits[i].length() - 1).replaceAll("\\}", "").replaceAll("=", "");
                                messageProd += "\n" + message;
                                messageProcedure.add(message);

                                Log.d("messageProd", messageProd);
                            }
                            diy_materials.setText(messageMat);
                            diy_procedures.setText(messageProd);

                            if (diYnames.diyUrl != null) {
                                imgview = (ViewPager) findViewById(R.id.diyImagesViewPagers_sell);
                                diyImagesViewPagerAdapter = new ViewImagesRecommendationPagerAdapter(getBaseContext(), diYnames.diyUrl);
                                imgview.setAdapter(diyImagesViewPagerAdapter);
                            }
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

    private class ViewImagesRecommendationPagerAdapter extends PagerAdapter {

        private Context context;
        private String diyUrl;

        public ViewImagesRecommendationPagerAdapter(Context context, String diyUrl) {
            this.context = context;
            this.diyUrl = diyUrl;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View currentView = LayoutInflater.from(context).inflate(R.layout.viewpager_product_images_commu, container, false);
            final ImageView diyImageView = (ImageView) currentView.findViewById(R.id.viewpager_productImage_community);
            try {
                final StorageReference diyImageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(diyUrl);
                diyImageStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("Image Download URI", uri.toString());
                        Glide.with(context).load(uri.toString())
                                .fitCenter().centerCrop().crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .into(diyImageView);

                    }
                });
            } catch (Exception e) {
                Log.d("Exception", "Getting diy image");
            }
            container.addView(currentView);
            return currentView;
        }


        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return diyUrl.length();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }
}
