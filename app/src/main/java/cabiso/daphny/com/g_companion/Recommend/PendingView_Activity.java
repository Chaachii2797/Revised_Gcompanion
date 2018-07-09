package cabiso.daphny.com.g_companion.Recommend;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 11/2/2017.
 */

public class PendingView_Activity extends AppCompatActivity{

    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private DatabaseReference db_proced;

    private TextView view_diy_name, user_owner_name;
    private Button button_sell, contact_seller, create_promo;
    private String user_name;
    private CardView selling_price, seller_info;

    final Context context = this;
    List<String> item = new ArrayList<>();

    private DIYImagesViewPagerAdapterView diyImagesViewPagerAdapterView;
    private ViewPager diyImagesViewPagers_pview;
    private DatabaseReference pending_reference;
    private DatabaseReference userdata_reference;
    private FirebaseUser mFirebaseUser;
    private String userID;
    private ArrayList<DBMaterial> dbMaterials;

    private int start_year, start_month, start_day;
    private int end_year, end_month, end_day;
    static final int DATE_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_view);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        progressDialog = new ProgressDialog(this);
        view_diy_name = (TextView) findViewById(R.id.diy_name_pview);
        user_owner_name = (TextView) findViewById(R.id.txt_user_owner_name_pview);

        final String get_name = getIntent().getStringExtra("name");
        view_diy_name.setText(get_name);

        Bundle extra = getIntent().getBundleExtra("dbmaterials");
        dbMaterials = (ArrayList<DBMaterial>) extra.getSerializable("dbmaterials");

        userdata_reference = FirebaseDatabase.getInstance().getReference().child("userdata");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.back_btn);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                if(get_name.equals(diYnames.getDiyName())){
                    userdata_reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                            if(diYnames.getUser_id().equals(user_profile.getUserID())){
                                user_name = user_profile.getF_name()+" "+user_profile.getL_name();
                                user_owner_name.setText(user_name);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                final DIYnames diyInfo = dataSnapshot.getValue(DIYnames.class);
//                final DIYSell info = dataSnapshot.getValue(DIYSell.class);
//                if(diyInfo.getIdentity().equals("community")){
//                    diy_sell.setVisibility(View.INVISIBLE);
//                    user_owner_name.setVisibility(View.INVISIBLE);
//                    txtBy.setVisibility(View.INVISIBLE);
//                    contact_seller.setVisibility(View.INVISIBLE);
//                    php.setVisibility(View.INVISIBLE);
//                    owner_add.setVisibility(View.INVISIBLE);
//                    owner_cn.setVisibility(View.INVISIBLE);
//                    selling_price.setVisibility(View.INVISIBLE);
//                    seller_info.setVisibility(View.INVISIBLE);
//                    diy_name.setText(diyInfo.diyName);
//
//                    user_data.addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
//                            if(userID.equals(user_profile.getUserID())){
//                                user_name = user_profile.getF_name()+" "+user_profile.getL_name();
//                                user_owner_name.setText(user_name);
//                                Log.e("user_name", "" + user_name);
//                            }else if(diyInfo.user_id.equals(user_profile.getUserID())){
//                                user_name = user_profile.getF_name()+" "+user_profile.getL_name();
//                                user_owner_name.setText(user_name);
//                            }
//                        }
//
//                        @Override
//                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                        }
//
//                        @Override
//                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//                    String messageMat = "";
//                    List<String> messageMaterials = new ArrayList<String>();
//                    int count = 1;
//                    for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
//                        String material_name = count + ". " + postSnapshot.child("name").getValue(String.class).toUpperCase();
//                        Long material_qty = postSnapshot.child("quantity").getValue(Long.class);
//                        String material_unit = postSnapshot.child("unit").getValue(String.class);
//                        Log.e("message", "" + material_name);
//                        messageMat += "\n" + material_name + " = " + material_qty + " " + material_unit;
//                        messageMaterials.add(material_name);
//                        count++;
//                    }
//
//                    String[] splits = dataSnapshot.child("procedures").getValue().toString().split(",");
//                    Log.e("splits", "" + splits);
//
//                    String messageProd = "";
//                    List<String> messageProcedure = new ArrayList<String>();
//                    for (int i = 0; i < splits.length; i++) {
//                        Log.d("splitVal", splits[i].substring(5, splits[i].length() - 1));
//                        String message = i + 1 + ". " + splits[i].substring(5, splits[i].length() - 1).replaceAll("\\}", "").replaceAll("=", "");
//                        messageProd += "\n" + message;
//                        messageProcedure.add(message);
//                        Log.d("messageProd", messageProd);
//                    }
//
//                    Log.d("MessageProcedure", messageProcedure.toString());
//
//                    diy_materials.setText(messageMat);
//                    diy_procedures.setText(messageProd);
//
//                    Log.d("SnapItem", "not null");
//                    Log.d("SnapMaterial", "" + item);
//                    Log.d("SnapDataSnap", "" + dataSnapshot.child("materials").getValue());
//
//                    if (diyInfo.diyUrl != null) {
//                        diyImagesViewPagers_pview = (ViewPager) findViewById(R.id.diyImagesViewPagers);
//                        diyImagesViewPagerAdapterView = new DIYImagesViewPagerAdapterView(getBaseContext(), diyInfo.diyUrl);
//                        diyImagesViewPagers_pview.setAdapter(diyImagesViewPagerAdapterView);
//
//                        Toast.makeText(PendingView_Activity.this, diyInfo.diyUrl, Toast.LENGTH_SHORT).show();
//                    }
//                }else if(diyInfo.getIdentity().equals("selling")){
//                    diy_sell.setVisibility(View.VISIBLE);
//                    user_owner_name.setVisibility(View.VISIBLE);
//                    button_sell.setVisibility(View.VISIBLE);
//                    contact_seller.setVisibility(View.VISIBLE);
//                    create_promo.setVisibility(View.VISIBLE);
//                    php.setVisibility(View.VISIBLE);
//                    owner_add.setVisibility(View.VISIBLE);
//                    owner_cn.setVisibility(View.VISIBLE);
//                    selling_price.setVisibility(View.VISIBLE);
//                    seller_info.setVisibility(View.VISIBLE);
//                    diy_name.setText(info.diyName);
//
//                    user_data.addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
////                            if(userID.equals(user_profile.getUserID())){
////                                user_name = user_profile.getF_name()+" "+user_profile.getL_name();
////                                user_owner_name.setText(user_name);
////                                owner_cn.setText(user_profile.getContact_no());
////                                owner_add.setText(user_profile.getAddress());
////                                Log.e("USER", user_profile.getAddress()+ "\n" +user_profile.getContact_no());
////                                Log.e("user_name", "" + user_name);
////                            }
//                            if(diyInfo.user_id.equals(user_profile.getUserID())){
//                                user_name = user_profile.getF_name()+" "+user_profile.getL_name();
//                                user_owner_name.setText(user_name);
//                                owner_cn.setText(user_profile.getContact_no());
//                                owner_add.setText(user_profile.getAddress());
//                            }
//                        }
//
//                        @Override
//                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                        }
//
//                        @Override
//                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//                    String message_price="";
//                    List<String> message_Price = new ArrayList<String>();
//                    for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
//                        int price= postSnapshot.child("selling_price").getValue(int.class);
//                        message_price += price;
//                        message_Price.add(message_price);
//
//                    }
//                    contact_seller.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Toast.makeText(PendingView_Activity.this, "Contact Seller button clicked!", Toast.LENGTH_SHORT).show();
//
//                            final Dialog myDialog = new Dialog(context);
//                            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                            myDialog.setContentView(R.layout.contact_seller);
//                            myDialog.setCancelable(false);
//                            Button chat = (Button) myDialog.findViewById(R.id.chat);
//                            Button call = (Button) myDialog.findViewById(R.id.call);
//                            Button sms = (Button) myDialog.findViewById(R.id.sms);
//                            TextView cancel = (TextView) myDialog.findViewById(R.id.cancel);
//
//                            chat.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Toast.makeText(PendingView_Activity.this, "Chat button clicked!", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            call.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Toast.makeText(PendingView_Activity.this, "Call button clicked!", Toast.LENGTH_SHORT).show();
//
//                                    String phone = owner_cn.getText().toString();
//                                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
//                                            "tel", phone, null));
//                                    startActivity(phoneIntent);
//                                }
//                            });
//                            sms.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Toast.makeText(PendingView_Activity.this, "SMS button clicked!", Toast.LENGTH_SHORT).show();
//
//                                    String phone = owner_cn.getText().toString();
//                                    Intent smsMsgAppVar = new Intent(Intent.ACTION_VIEW);
//                                    smsMsgAppVar.setData(Uri.parse("sms:" +  phone));
//                                    smsMsgAppVar.putExtra("sms_body", "Hi, Good Day! ");
//                                    startActivity(smsMsgAppVar);
//                                }
//                            });
//                            cancel.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    myDialog.cancel();
//                                }
//                            });
//
//                            myDialog.show();
//                            myDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                                @Override
//                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//                                        dialog.cancel();
//                                        return true;
//                                    }
//                                    return false;
//                                }
//                            });
//                        }
//                    });
//
//                    if (item != null) {
//                        String[] splitsMat = dataSnapshot.child("materials").getValue().toString().split(",");
//                        Log.e("messageProd", "" + splitsMat);
//
//                        String messageMat = "";
//                        List<String> messageMaterials = new ArrayList<String>();
//                        int count = 1;
//                        for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
//                            String material_name = count + ". " + postSnapshot.child("name").getValue(String.class).toUpperCase();
//                            Long material_qty = postSnapshot.child("quantity").getValue(Long.class);
//                            String material_unit = postSnapshot.child("unit").getValue(String.class);
//                            Log.e("message", "" + material_name);
//                            messageMat += "\n" + material_name + " = " + material_qty + " " + material_unit;
//                            messageMaterials.add(material_name);
//                            count++;
//                        }
//
//                        String[] splits = dataSnapshot.child("procedures").getValue().toString().split(",");
//                        Log.e("splits", "" + splits);
//
//                        String messageProd = "";
//                        List<String> messageProcedure = new ArrayList<String>();
//                        for (int i = 0; i < splits.length; i++) {
//                            Log.d("splitVal", splits[i].substring(5, splits[i].length() - 1));
//                            String message = i + 1 + ". " + splits[i].substring(5, splits[i].length() - 1).replaceAll("\\}", "").replaceAll("=", "");
//                            messageProd += "\n" + message;
//                            messageProcedure.add(message);
//
//                            Log.d("messageProd", messageProd);
//                        }
//
//                        Log.d("MessageProcedure", messageProcedure.toString());
//
//                        diy_materials.setText(messageMat);
////                        diy_procedures.setText(messageProd);
//                        if(userID.equals(diyInfo.getUser_id())){
//                            diy_procedures.setText(messageProd);
//                        }else{
//                            diy_procedures.setText("ASK PERMISSION TO THE OWNER OR BUY THE ITEM!");
//                        }
//                        diy_sell.setText(message_price);
//
//                        Log.d("SnapItem", "not null");
//                        Log.d("SnapMaterial", "" + item);
//                        Log.d("SnapDataSnap", "" + dataSnapshot.child("materials").getValue());
//                    } else {
//                        Log.d("SnapItem", "null");
//                    }
//
//                    if (diyInfo.diyUrl != null) {
//                        diyImagesViewPagers_pview = (ViewPager) findViewById(R.id.diyImagesViewPagers);
//                        diyImagesViewPagerAdapterView = new DIYImagesViewPagerAdapterView(getBaseContext(), diyInfo.diyUrl);
//                        diyImagesViewPagers_pview.setAdapter(diyImagesViewPagerAdapterView);
//
//                        Toast.makeText(PendingView_Activity.this, diyInfo.diyUrl, Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }


    /**
     * Called when a view has been clicked.
     *
     */



    private class DIYImagesViewPagerAdapterView extends PagerAdapter {

        private Context context;
        private String diyUrl;

        public DIYImagesViewPagerAdapterView(Context context, String diyUrl) {
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
                        //Picasso.with(context).load(uri.getLastPathSegment()).resize(350,350).into(productImageView);
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

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        private EditText text;

        public DatePickerFragment(){

        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int start_year = calendar.get(Calendar.YEAR);
            int start_month = calendar.get(Calendar.MONTH);
            int start_day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(getActivity(),this,start_year,start_month,start_day);
            return  dpd;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//            EditText startDate = (EditText) view.findViewById(R.id.et_start_date);
            SimpleDateFormat dateFormatter;
            dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, month, dayOfMonth);
            EditText text = (EditText) this.getArguments().getSerializable("editText");
            text.setText(dateFormatter.format(newDate.getTime()));
//            startDate.setText(dateFormatter.format(newDate.getTime()));

        }

    }

}
