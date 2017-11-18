package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Recommend.RecommendDIYAdapter;

/**
 * Created by Lenovo on 11/2/2017.
 */

public class DIYDetailViewActivity extends AppCompatActivity{

    private ArrayList infoList;
    private ListView lv;
    private RecommendDIYAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;

    private TextView diy_name, diy_materials, diy_procedures;
    private ImageView diy_image;
    private String userID;
    private int count=0;

    final Context context = this;
    List<String> item = new ArrayList<>();

    private DIYImagesViewPagerAdapter diyImagesViewPagerAdapter;
    private ViewPager diyImagesViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy_data);

        String diyReferenceString = getIntent().getStringExtra("Community Ref");

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(diyReferenceString);

        progressDialog = new ProgressDialog(this);
        diy_name = (TextView) findViewById(R.id.diy_name);
        diy_materials = (TextView) findViewById(R.id.diy_materials);
        diy_procedures = (TextView) findViewById(R.id.diy_procedures);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DIYnames diyInfo = dataSnapshot.getValue(DIYnames.class);
                diy_name.setText(diyInfo.diyName);
                CommunityItem item = dataSnapshot.getValue(CommunityItem.class);

                if(item!=null){
                    String[] splitsMat = dataSnapshot.child("materials").getValue().toString().split(",");

                    String messageMat = "";
                    List<String> messageMaterials = new ArrayList<String>();
                    for(int i = 0; i<splitsMat.length; i++){
                        Log.d("splitVal", splitsMat[i].substring(5,splitsMat[i].length()-1));
                        String message = i+1 +".) "+ splitsMat[i].substring(5,splitsMat[i].length()-1).replaceAll("\\}", "").replaceAll("=", "");
                        messageMat+="\n"+message;
                        messageMaterials.add(message);

                        Log.d("messageProd", messageMat);
                    }

                    String[] splits = dataSnapshot.child("procedures").getValue().toString().split(",");

                    String messageProd = "";
                    List<String> messageProcedure = new ArrayList<String>();
                    for(int i = 0; i<splits.length; i++){
                        Log.d("splitVal", splits[i].substring(5,splits[i].length()-1));
                        String message = i+1 +".) "+ splits[i].substring(5,splits[i].length()-1).replaceAll("\\}", "").replaceAll("=", "");
                        messageProd+="\n"+message;
                        messageProcedure.add(message);

                        Log.d("messageProd", messageProd);
                    }

                    Log.d("MessageProcedure", messageProcedure.toString());

                    diy_materials.setText(messageMat);
                    diy_procedures.setText(messageProd);

                    Log.d("SnapItem", "not null");
                    Log.d("SnapMaterial", ""+item);
                    Log.d("SnapDataSnap", ""+dataSnapshot.child("materials").getValue());
                }else{
                    Log.d("SnapItem", "null");
                }

                if (diyInfo.diyUrl != null) {
                    diyImagesViewPager = (ViewPager) findViewById(R.id.diyImagesViewPager);
                    diyImagesViewPagerAdapter = new DIYImagesViewPagerAdapter(getBaseContext(), diyInfo.diyUrl);
                    diyImagesViewPager.setAdapter(diyImagesViewPagerAdapter);

                Toast.makeText(DIYDetailViewActivity.this, diyInfo.diyUrl, Toast.LENGTH_SHORT).show();
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
            View currentView = LayoutInflater.from(context).inflate(R.layout.viewpager_product_images, container, false);
            final ImageView diyImageView = (ImageView) currentView.findViewById(R.id.viewpager_productImage);
            try {
                final StorageReference diyImageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(diyUrl);
                diyImageStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("Image Download URI", uri.toString());
                        //Picasso.with(context).load(uri.getLastPathSegment()).resize(350,350).into(productImageView);
                        Glide.with(context).load(uri.toString()).into(diyImageView);

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
