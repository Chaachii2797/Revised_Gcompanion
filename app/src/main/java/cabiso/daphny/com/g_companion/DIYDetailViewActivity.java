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
import android.widget.ImageButton;
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

import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Recommend.RecommendDIYAdapter;

/**
 * Created by Lenovo on 11/2/2017.
 */

public class DIYDetailViewActivity extends AppCompatActivity{

    private ArrayList<CommunityItem> infoList = new ArrayList<>();
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
        //diy_image = (ImageView) findViewById(R.id.diy_image);
        diy_materials = (TextView) findViewById(R.id.diy_materials);
        diy_procedures = (TextView) findViewById(R.id.diy_procedures);

//        final ImageButton star = (ImageButton) findViewById(R.id.staru);
//        ImageButton heart = (ImageButton) findViewById(R.id.heartu);

//        star.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                count++;
//                if(count==1) {
//                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("diy_by_tags").child(userID);
//                    reference.addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                String key = snapshot.getKey();
//                                String path = "/" + dataSnapshot.getKey() + "/" + key;
//                                HashMap<String, Object> result = new HashMap<>();
//                                result.put("bookmarks", count);
//                                reference.child(path).updateChildren(result);
//                                star.setColorFilter(ContextCompat.getColor(context, R.color.star_yello));
//                                count++;
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
//                }else if(count==2){
//                    count=0;
//                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
//                    reference.addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
//                                String key = snapshot.getKey();
//                                String path = "/" + dataSnapshot.getKey() + "/" + key;
//                                HashMap<String, Object> result = new HashMap<>();
//                                result.put("bookmarks",count);
//                                reference.child(path).updateChildren(result);
//                                star.setColorFilter(ContextCompat.getColor(context, R.color.for_star));
//                                count--;
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
//                }
//            }
//        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DIYnames diyInfo = dataSnapshot.getValue(DIYnames.class);
                diy_name.setText(diyInfo.diyName);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        CommunityItem communityItem = dataSnapshot.getValue(CommunityItem.class);
//                        diy_materials.setText(communityItem.getVal());
//                        diy_procedures.setText(communityItem.getVal());

                        ArrayList<CommunityItem> itemMaterial = (ArrayList<CommunityItem>) getIntent().getSerializableExtra("materials");
                        diy_materials.setText((CharSequence) itemMaterial);
                        Toast.makeText(DIYDetailViewActivity.this, communityItem.getVal() , Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


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
     * @param v The view that was clicked.
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
