package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

/**
 * Created by Lenovo on 1/6/2018.
 */

public class SellDIYDetail extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private DatabaseReference dbRef;
    private TextView name_diys, materials_diys, procedures_diys, diy_sell;
    private Button btnSell;
    private ImageView diy_image;
    private String userID;
    private int count=0;

    final Context context = this;
    List<String> item = new ArrayList<>();

    private DIYImagesPagerAdapter diyImagesPagerAdapter;
    private ViewPager diyImagesPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy_sell_data);

        String diyReferenceStrings = getIntent().getStringExtra("Market Ref");
        Toast.makeText(this, "MARKET_REF"+ diyReferenceStrings, Toast.LENGTH_SHORT).show();

        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(diyReferenceStrings);

        Toolbar toolbarr = (Toolbar) findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbarr);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarr.setNavigationIcon(R.drawable.back_btn);
        toolbarr.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(this);
        name_diys = (TextView) findViewById(R.id.name_diys);
        materials_diys = (TextView) findViewById(R.id.material_diys);
        procedures_diys = (TextView) findViewById(R.id.procedure_diys);
        diy_sell = (TextView) findViewById(R.id.sell_details);
        btnSell = (Button) findViewById(R.id.buyBtn);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DIYSell info = dataSnapshot.getValue(DIYSell.class);
                Log.e("SELLinfo", ""+info);
                CommunityItem item = dataSnapshot.getValue(CommunityItem.class);
                Log.e("SELLitem", ""+item);
                if(dataSnapshot.getValue(DIYSell.class) != null){
                    String sname = info.diyName;
                    name_diys.setText(sname);
                }

                if(item!=null){
                    Log.e("SELLitemINSIDE", ""+item);
                    Log.e("SELLinfoINSIDE", ""+info);
                    String[] splitsMat = dataSnapshot.child("materials").getValue().toString().split(",");
                    Log.e("SELLmessageProd", ""+splitsMat);

                    String sell_messageMat = "";
                    List<String> sell_messageMaterials = new ArrayList<String>();
                    for(int i = 0; i<splitsMat.length; i++){
                        Log.d("SELLsplitVal", splitsMat[i].substring(5,splitsMat[i].length()-1));
                        String message = i+1 +". "+ splitsMat[i].substring(5,splitsMat[i].length()-1).replaceAll("\\}", "").replaceAll("=", "");
                        sell_messageMat+="\n"+message;
                        sell_messageMaterials.add(message);

                        Log.d("SELLmessageProd", sell_messageMat);
                    }

                    String[] splits = dataSnapshot.child("procedures").getValue().toString().split(",");
                    Log.e("SELLsplits", ""+splits);

                    String messageProd = "";
                    List<String> messageProcedure = new ArrayList<String>();
                    for(int i = 0; i<splits.length; i++){
                        Log.d("splitVal", splits[i].substring(5,splits[i].length()-1));
                        String message = i+1 +". "+ splits[i].substring(5,splits[i].length()-1).replaceAll("\\}", "").replaceAll("=", "");
                        messageProd+="\n"+message;
                        messageProcedure.add(message);

                        Log.d("SELLmessageProd", messageProd);
                    }

                    materials_diys.setText(sell_messageMat);
                    procedures_diys.setText(messageProd);

                }

                //returns NULL BWESIT SA PIKAS DLI NULL
//                DIYSell sell = dataSnapshot.getValue(DIYSell.class);
//                if(dataSnapshot.getValue(DIYSell.class) != null) {
//                    String sname = sell.diyName;
//                    Log.e("SELL_SHT", "" + sell.getDiyName());
//                    Log.e("SELL_SHTs", "" + sell.diyUrl);
//                    name_diys.setText("" + sname);
//                }
//                else{
//                    Log.e("HAYS", "" + sell.diyName);
//
//                }

//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    DIYSell info = snapshot.getValue(DIYSell.class);
//
//                    name_diys.setText("" + info.getDiyName());
//                    Log.e("SELL_SHT", "" + info.diyName);
//
//                    //for temporary price
//                    String sellItem = snapshot.child("DIY Price").getValue().toString();
//                    diy_sell = (TextView) findViewById(R.id.sellDetails);
//                    diy_sell.setText(sellItem);
//
//
//                    btnSell.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Toast.makeText(SellDIYDetail.this, "BUY ITEM!", Toast.LENGTH_SHORT).show();
//                        }
//
//                    });
//
//                if(item!=null){
//                    String[] splitsMat = snapshot.child("materials").getValue().toString().split(",");
//                    Log.e("messageProd", ""+splitsMat);
//
//                    String messageMat = "";
//                    List<String> messageMaterials = new ArrayList<String>();
//                    for(int i = 0; i<splitsMat.length; i++){
//                        Log.d("splitVal", splitsMat[i].substring(5,splitsMat[i].length()-1));
//                        String message = i+1 +".) "+ splitsMat[i].substring(5,splitsMat[i].length()-1).replaceAll("\\}", "")
//                                .replaceAll("=", "");
//                        messageMat+="\n"+message;
//                        messageMaterials.add(message);
//
//                        Log.d("messageProd", messageMat);
//                    }
//
//                    String[] splits = snapshot.child("procedures").getValue().toString().split(",");
//                    Log.e("splits", ""+splits);
//
//                    String messageProd = "";
//                    List<String> messageProcedure = new ArrayList<String>();
//                    for(int i = 0; i<splits.length; i++){
//                        Log.d("splitVal", splits[i].substring(5,splits[i].length()-1));
//                        String message = i+1 +".) "+ splits[i].substring(5,splits[i].length()-1).replaceAll("\\}", "").replaceAll("=", "");
//                        messageProd+="\n"+message;
//                        messageProcedure.add(message);
//
//                        Log.d("messageProd", messageProd);
//                    }
//
//                    Log.d("MessageProcedure", messageProcedure.toString());
//
//                    materials_diys.setText(messageMat);
//                    procedures_diys.setText(messageProd);
//
//                    Log.d("SnapItem", "not null");
//                    Log.d("SnapMaterial", ""+item);
//                    Log.d("SnapDataSnap", ""+snapshot.child("materials").getValue());
//                }else{
//                    Log.d("SnapItem", "null");
//                }
//
//                    if (info.diyUrl != null) {
//                        diyImagesPager = (ViewPager) findViewById(R.id.diyImagesViewPagers);
//                        diyImagesPagerAdapter = new DIYImagesPagerAdapter(getBaseContext(), info.diyUrl);
//                        diyImagesPager.setAdapter(diyImagesPagerAdapter);
//
//                        Toast.makeText(SellDIYDetail.this, info.diyUrl, Toast.LENGTH_SHORT).show();
//                    }
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed",databaseError.getDetails());
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     */

    private class DIYImagesPagerAdapter extends PagerAdapter {

        private Context context;
        private String diyUrl;

        public DIYImagesPagerAdapter(Context context, String diyUrl) {
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
