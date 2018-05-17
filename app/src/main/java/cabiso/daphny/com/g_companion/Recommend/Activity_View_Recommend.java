package cabiso.daphny.com.g_companion.Recommend;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;

public class Activity_View_Recommend extends AppCompatActivity {

    private ViewPager imgview;
    private ViewImagesRecommendationPagerAdapter diyImagesViewPagerAdapter;
    private TextView tv_matrial, tv_procedures, tv_diyname, tv_owner_name;
    private ArrayList<DBMaterial> dbMaterials;
    private DatabaseReference user_data;
    private FirebaseUser mFirebaseUser;
    private String username, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__view__recommend);

        imgview = (ViewPager) findViewById(R.id.diyImagesViewPagers);
        tv_diyname = (TextView) findViewById(R.id.diy_name_sell);
        tv_matrial = (TextView) findViewById(R.id.diy_material_sell);
        tv_procedures = (TextView) findViewById(R.id.diy_procedure_sell);

        tv_owner_name = (TextView) findViewById(R.id.tv_ownername);

        final String get_name = getIntent().getStringExtra("name");
        tv_diyname.setText(get_name);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();
        Bundle extra = getIntent().getBundleExtra("dbmaterials");
        dbMaterials = (ArrayList<DBMaterial>) extra.getSerializable("dbmaterials");
        user_data = FirebaseDatabase.getInstance().getReference().child("userdata");

        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags");
            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    final DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                    if(get_name.equals(diYnames.getDiyName())){
                        if(diYnames.getIdentity().equals("selling")) {

                            user_data.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                    if(diYnames.getUser_id().equals(user_profile.getUserID())){
                                        username = user_profile.getF_name()+" "+user_profile.getL_name();
                                        tv_owner_name.setText(username);
                                        Log.e("user_name", "" + username);
                                    }else if(diYnames.user_id.equals(user_profile.getUserID())){
                                        username = user_profile.getF_name()+" "+user_profile.getL_name();
                                        tv_owner_name.setText(username);
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
                                messageMat += "\n" + dbMaterialName + " = " + dbMaterialQuantity + " " + dbMaterialUnit;
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

                            tv_matrial.setText(messageMat);
//                            tv_procedures.setText("NOT APPLICABLE! BUY THE ITEM FIRST OR ASK PERMISSION TO THE OWNER!");
                            tv_procedures.setText(messageProd);
                            tv_procedures.setTextColor(Color.BLACK);

                            if (diYnames.diyUrl != null) {
                                imgview = (ViewPager) findViewById(R.id.diyImagesViewPagers);
                                diyImagesViewPagerAdapter = new ViewImagesRecommendationPagerAdapter(getBaseContext(), diYnames.diyUrl);
                                imgview.setAdapter(diyImagesViewPagerAdapter);
                            }
                        }
                        else if(diYnames.getIdentity().equals("community")) {
                            user_data.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                    if(diYnames.getUser_id().equals(user_profile.getUserID())){
                                        username = user_profile.getF_name()+" "+user_profile.getL_name();
                                        tv_owner_name.setText(username);
                                        Log.e("user_name", "" + username);
                                    }else if(diYnames.user_id.equals(user_profile.getUserID())){
                                        username = user_profile.getF_name()+" "+user_profile.getL_name();
                                        tv_owner_name.setText(username);
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
                                messageMat += "\n" + dbMaterialName + " = " + dbMaterialQuantity + " " + dbMaterialUnit;
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
                            tv_matrial.setText(messageMat);
                            tv_procedures.setText(messageProd);

                            if (diYnames.diyUrl != null) {
                                imgview = (ViewPager) findViewById(R.id.diyImagesViewPagers);
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
