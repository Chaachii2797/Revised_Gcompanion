package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.SellingDIY;
import cabiso.daphny.com.g_companion.Recommend.RecommendDIYAdapter;

/**
 * Created by Lenovo on 11/2/2017.
 */

public class DIYDetailViewActivity extends AppCompatActivity{

    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    private TextView diy_name, diy_materials, diy_procedures, diy_sell;
    private Button button_sell;


    final Context context = this;
    List<String> item = new ArrayList<>();

    private DIYImagesViewPagerAdapter diyImagesViewPagerAdapter;
    private ViewPager diyImagesViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy_data);

        String diyReferenceString = getIntent().getStringExtra("Community Ref");
        Toast.makeText(this, "COMMUNITY_REF"+diyReferenceString, Toast.LENGTH_SHORT).show();

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(diyReferenceString);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.back_btn);
        toolbar.setNavigationOnClickListener(   new View.OnClickListener() {
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

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DIYnames diyInfo = dataSnapshot.getValue(DIYnames.class);
                DIYSell info = dataSnapshot.getValue(DIYSell.class);
                SellingDIY info_selling = dataSnapshot.getValue(SellingDIY.class);
                if(diyInfo.getStatus().equals("community")){
                    diy_sell.setVisibility(View.INVISIBLE);
                    button_sell.setVisibility(View.INVISIBLE);
                    diy_name.setText(diyInfo.diyName);

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
    //                    for(int i = 0; i<splitsMat.length; i++){
    //                        Log.d("splitVal", splitsMat[i].substring(5,splitsMat[i].length()-1));
    //                        String message = i+1 +".) "+ splitsMat[i].substring(5,splitsMat[i].length()-1).replaceAll("\\}", "")
    //                                .replaceAll("=", "");
    //                        messageMat+="\n"+message;
    //                        messageMaterials.add(message);
    //
    //                        Log.d("messageProd", messageMat);
    //                    }

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
                    } else {
                        Log.d("SnapItem", "null");
                    }

                    if (diyInfo.diyUrl != null) {
                        diyImagesViewPager = (ViewPager) findViewById(R.id.diyImagesViewPagers_sell);
                        diyImagesViewPagerAdapter = new DIYImagesViewPagerAdapter(getBaseContext(), diyInfo.diyUrl);
                        diyImagesViewPager.setAdapter(diyImagesViewPagerAdapter);

                        Toast.makeText(DIYDetailViewActivity.this, diyInfo.diyUrl, Toast.LENGTH_SHORT).show();
                    }
                }else if(diyInfo.getStatus().equals("selling")){
                    diy_sell.setVisibility(View.VISIBLE);
                    button_sell.setVisibility(View.VISIBLE);
                    diy_name.setText(info.diyName);

                    String message_price="";
                    List<String> message_Price = new ArrayList<String>();
                    for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                        int price= postSnapshot.child("selling_price").getValue(int.class);
                        message_price += "\n" + price;
                        message_Price.add(message_price);

                    }

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
                        diy_procedures.setText(messageProd);
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

}
