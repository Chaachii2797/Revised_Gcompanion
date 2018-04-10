package cabiso.daphny.com.g_companion;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.SellingDIY;
import cabiso.daphny.com.g_companion.Model.User_Profile;

/**
 * Created by Lenovo on 11/2/2017.
 */

public class DIYDetailViewActivity extends AppCompatActivity{

    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    private TextView diy_name, diy_materials, diy_procedures, diy_sell, php, owner_add, owner_cn;
    private Button button_sell, contact_seller, create_promo;


    final Context context = this;
    List<String> item = new ArrayList<>();

    private DIYImagesViewPagerAdapter diyImagesViewPagerAdapter;
    private ViewPager diyImagesViewPager;
    private DatabaseReference pending_reference;
    private DatabaseReference productReference;
    private FirebaseUser mFirebaseUser;
    private String userID;
    private SimpleDateFormat dateFormatter;
    private CardView selling_price, seller_info;

    private int start_year, start_month, start_day;
    private int end_year, end_month, end_day;
    static final int DATE_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy_data);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        String diyReferenceString = getIntent().getStringExtra("Community Ref");
        Toast.makeText(this, "COMMUNITY_REF"+diyReferenceString, Toast.LENGTH_SHORT).show();

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(diyReferenceString);

        pending_reference = FirebaseDatabase.getInstance().getReference("DIY Pending Items").child(userID);

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

        progressDialog = new ProgressDialog(this);
        diy_name = (TextView) findViewById(R.id.diy_name);
        diy_materials = (TextView) findViewById(R.id.diy_material);
        diy_procedures = (TextView) findViewById(R.id.diy_procedure);
        diy_sell = (TextView) findViewById(R.id.sell_details);
        button_sell = (Button) findViewById(R.id.btn_sell_diy);
        php = (TextView) findViewById(R.id.textView33);
        contact_seller = (Button) findViewById(R.id.btn_contact_diy_owner);
        create_promo = (Button) findViewById(R.id.btn_create_promo);
        owner_add = (TextView) findViewById(R.id.diy_owner_add);
        owner_cn = (TextView) findViewById(R.id.diy_owner_cn);
        selling_price = (CardView) findViewById(R.id.cardView3);
        seller_info = (CardView) findViewById(R.id.cardView4);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DIYnames diyInfo = dataSnapshot.getValue(DIYnames.class);
                DIYSell info = dataSnapshot.getValue(DIYSell.class);
                SellingDIY info_selling = dataSnapshot.getValue(SellingDIY.class);
                if(diyInfo.getIdentity().equals("community")){
                    diy_sell.setVisibility(View.INVISIBLE);
                    button_sell.setVisibility(View.INVISIBLE);
                    contact_seller.setVisibility(View.INVISIBLE);
                    create_promo.setVisibility(View.INVISIBLE);
                    php.setVisibility(View.INVISIBLE);
                    owner_add.setVisibility(View.INVISIBLE);
                    owner_cn.setVisibility(View.INVISIBLE);
                    selling_price.setVisibility(View.INVISIBLE);
                    seller_info.setVisibility(View.INVISIBLE);
                    diy_name.setText(diyInfo.diyName);

                        String messageMat = "";
                        List<String> messageMaterials = new ArrayList<String>();
                        int count = 1;
                        for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                            String material_name = count + ". " + postSnapshot.child("name").getValue(String.class).toUpperCase();
                            Long material_qty = postSnapshot.child("quantity").getValue(Long.class);
                            String material_unit = postSnapshot.child("unit").getValue(String.class);
                            Log.e("message", "" + material_name);
                            messageMat += "\n" + material_name + " = " + material_qty + " " + material_unit;
                            messageMaterials.add(material_name);
                            count++;
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

                        Log.d("MessageProcedure", messageProcedure.toString());

                        diy_materials.setText(messageMat);
                        diy_procedures.setText(messageProd);

                        Log.d("SnapItem", "not null");
                        Log.d("SnapMaterial", "" + item);
                        Log.d("SnapDataSnap", "" + dataSnapshot.child("materials").getValue());

                    if (diyInfo.diyUrl != null) {
                        diyImagesViewPager = (ViewPager) findViewById(R.id.diyImagesViewPagers_sell);
                        diyImagesViewPagerAdapter = new DIYImagesViewPagerAdapter(getBaseContext(), diyInfo.diyUrl);
                        diyImagesViewPager.setAdapter(diyImagesViewPagerAdapter);

                        Toast.makeText(DIYDetailViewActivity.this, diyInfo.diyUrl, Toast.LENGTH_SHORT).show();
                    }
                }else if(diyInfo.getIdentity().equals("selling")){
                    diy_sell.setVisibility(View.VISIBLE);
                    button_sell.setVisibility(View.VISIBLE);
                    contact_seller.setVisibility(View.VISIBLE);
                    create_promo.setVisibility(View.VISIBLE);
                    php.setVisibility(View.VISIBLE);
                    owner_add.setVisibility(View.VISIBLE);
                    owner_cn.setVisibility(View.VISIBLE);
                    selling_price.setVisibility(View.VISIBLE);
                    seller_info.setVisibility(View.VISIBLE);
                    diy_name.setText(info.diyName);

                    String message_price="";
                    List<String> message_Price = new ArrayList<String>();
                    for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                        int price= postSnapshot.child("selling_price").getValue(int.class);
                        message_price += price;
                        message_Price.add(message_price);

                    }

                    final DatabaseReference seller_reference = FirebaseDatabase.getInstance().getReference("userdata");
                    seller_reference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            User_Profile seller_profile = dataSnapshot.getValue(User_Profile.class);
                            owner_cn.setText(seller_profile.getContact_no());
                            owner_add.setText(seller_profile.getAddress());
                            Log.e("USERS", seller_profile.getAddress()+ "\n" +seller_profile.getContact_no());
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

                    final String finalMessage_price = message_price;
                    button_sell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Toast.makeText(DIYDetailViewActivity.this, "Buy button clicked!", Toast.LENGTH_SHORT).show();
                                    Float float_this = Float.valueOf(0);

                                    DIYSell productInfo =  dataSnapshot.getValue(DIYSell.class);

                                    String diyName = productInfo.getDiyName();
                                    String diyUrl = productInfo.getDiyUrl();
                                    String user_id = productInfo.getUser_id();
                                    String productID = productInfo.getProductID();
                                    String status = productInfo.getIdentity();

                                    DIYSell info = new DIYSell(diyName, diyUrl, user_id, productID, status, float_this, float_this);
                                    String upload_info = pending_reference.push().getKey();
                                    pending_reference.child(upload_info).setValue(info);
                                    pending_reference.child(upload_info).child("DIY Price").setValue(finalMessage_price);

                                    final Dialog dialog = new Dialog(DIYDetailViewActivity.this);
                                    dialog.setContentView(R.layout.done_dialog);
                                    TextView text = (TextView) dialog.findViewById(R.id.text);
                                    text.setText("DIY added to pending list!");
                                    ImageView image = (ImageView) dialog.findViewById(R.id.dialog_imageview);
                                    image.setImageResource(R.drawable.done);

                                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                    // if button is clicked, close the custom dialog
                                    dialogButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(DIYDetailViewActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                    dialog.show();

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
                            Toast.makeText(DIYDetailViewActivity.this, "Contact Seller button clicked!", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(DIYDetailViewActivity.this, "Chat button clicked!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(DIYDetailViewActivity.this, "Call button clicked!", Toast.LENGTH_SHORT).show();

                                    String phone = owner_cn.getText().toString();
                                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                                            "tel", phone, null));
                                    startActivity(phoneIntent);
                                }
                            });
                            sms.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(DIYDetailViewActivity.this, "SMS button clicked!", Toast.LENGTH_SHORT).show();

                                    String phone = owner_cn.getText().toString();
                                    Intent smsMsgAppVar = new Intent(Intent.ACTION_VIEW);
                                    smsMsgAppVar.setData(Uri.parse("sms:" +  phone));
                                    smsMsgAppVar.putExtra("sms_body", "Hi, Good Day! ");
                                    startActivity(smsMsgAppVar);;
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

                    create_promo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog myDialog = new Dialog(context);
                            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            myDialog.setContentView(R.layout.dialog_promo);
                            myDialog.show();

                            final EditText startDate = (EditText) myDialog.findViewById(R.id.et_start_date);
                            startDate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Initialize a new date picker dialog fragment
                                    Bundle bundle = new Bundle();
                                    DatePickerFragment newFragment = new DatePickerFragment();
                                    newFragment.setArguments(bundle);
                                    newFragment.show(getSupportFragmentManager(), "datePicker");

                                }
                            });

                            EditText endDate = (EditText) myDialog.findViewById(R.id.et_end_date);
                            Button ok = (Button) myDialog.findViewById(R.id.btn_ok);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialog.cancel();
                                }
                            });
//                            showDatePickerDialog(v);
                            Toast.makeText(context, "CREATE PROMO BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                        }
                    });


                    if (item != null) {
                        String[] splitsMat = dataSnapshot.child("materials").getValue().toString().split(",");
                        Log.e("messageProd", "" + splitsMat);

                        String messageMat = "";
                        List<String> messageMaterials = new ArrayList<String>();
                        int count = 1;
                        for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                            String material_name = count + ". " + postSnapshot.child("name").getValue(String.class).toUpperCase();
                            Long material_qty = postSnapshot.child("quantity").getValue(Long.class);
                            String material_unit = postSnapshot.child("unit").getValue(String.class);
                            Log.e("message", "" + material_name);
                            messageMat += "\n" + material_name + " = " + material_qty + " " + material_unit;
                            messageMaterials.add(material_name);
                            count++;
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

                        Log.d("MessageProcedure", messageProcedure.toString());

                        diy_materials.setText(messageMat);
//                        diy_procedures.setText(messageProd);
                        diy_procedures.setText("ASK PERMISSION TO THE OWNER OR BUY THE ITEM!");
                        diy_sell.setText(message_price);

                        Log.d("SnapItem", "not null");
                        Log.d("SnapMaterial", "" + item);
                        Log.d("SnapDataSnap", "" + dataSnapshot.child("materials").getValue());
                    } else {
                        Log.d("SnapItem", "null");
                    }

                    if (diyInfo.diyUrl != null) {
                        diyImagesViewPager = (ViewPager) findViewById(R.id.diyImagesViewPagers_sell);
                        diyImagesViewPagerAdapter = new DIYImagesViewPagerAdapter(getBaseContext(), diyInfo.diyUrl);
                        diyImagesViewPager.setAdapter(diyImagesViewPagerAdapter);

                        Toast.makeText(DIYDetailViewActivity.this, diyInfo.diyUrl, Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Called when a view has been clicked.
     *
     */



    private class DIYImagesViewPagerAdapter extends PagerAdapter {

        private Context context;
        private String diyUrl;

        public DIYImagesViewPagerAdapter(Context context, String diyUrl) {
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
