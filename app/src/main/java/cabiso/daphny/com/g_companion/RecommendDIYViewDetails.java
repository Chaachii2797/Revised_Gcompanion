package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;

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

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Recommend.Bottle_Recommend;

/**
 * Created by Lenovo on 3/31/2018.
 */

public class RecommendDIYViewDetails extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private TextView recommend_diy_name, recommend_diy_materials, recommend_diy_procedures;
    private ImageView recommend_diy_image;
    private DatabaseReference recommend_databaseReference;
    private RecommendDIYViewDetails.DIYImagesViewPagerAdapter diyImagesViewPagerAdapter;
    private ViewPager diyImagesViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_view_diy_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.back_btn);
        toolbar.setNavigationOnClickListener(   new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Bottle_Recommend.class));
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(this);
        recommend_diy_name = (TextView) findViewById(R.id.recommend_diy_name);
        recommend_diy_materials = (TextView) findViewById(R.id.recommend_diy_material);
        recommend_diy_procedures = (TextView) findViewById(R.id.recommend_diy_procedure);
        recommend_diy_image = (ImageView) findViewById(R.id.diyImagesViewPagers);

        Intent intent = getIntent();
        if (null != intent) {
            String nameData= intent.getStringExtra("name");

            Bundle extras = getIntent().getExtras();
            byte[] byteArray = extras.getByteArray("image");

            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            recommend_diy_name.setText(nameData);
            recommend_diy_image.setImageBitmap(bmp);
            Log.e("imageeeeeee", String.valueOf(bmp));
        }

        recommend_databaseReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
        recommend_databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DIYnames recom_diy_info = dataSnapshot.getValue(DIYnames.class);

                if (recom_diy_info.diyUrl != null) {
                    diyImagesViewPager = (ViewPager) findViewById(R.id.diyImagesViewPagers);
                    diyImagesViewPagerAdapter = new DIYImagesViewPagerAdapter(getBaseContext(), recom_diy_info.diyUrl);
                    diyImagesViewPager.setAdapter(diyImagesViewPagerAdapter);
                    Log.e("imagePls", recom_diy_info.diyUrl);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

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
