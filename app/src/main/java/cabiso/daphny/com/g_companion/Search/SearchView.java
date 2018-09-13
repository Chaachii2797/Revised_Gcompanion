package cabiso.daphny.com.g_companion.Search;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

public class SearchView extends Activity {
    private ViewPager imgviewpager;
    private ViewImagesSearchPagerAdapter viewImagesSearchPagerAdapter;
    private TextView sview_diyName, sview_diyMaterials, sview_diyProcedures;

    private DatabaseReference searchViewReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        searchViewReference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        imgviewpager = (ViewPager) findViewById(R.id.sview_view_pager);
        sview_diyName = (TextView) findViewById(R.id.sview_diy_name);
        sview_diyMaterials = (TextView) findViewById(R.id.sview_diy_materials);
        sview_diyProcedures = (TextView) findViewById(R.id.sview_diy_procedures);

        final String get_name = getIntent().getStringExtra("name");
        sview_diyName.setText(get_name);

        searchViewReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                if(get_name.equalsIgnoreCase(diYnames.getDiyName())){
                    if(diYnames.getIdentity().equalsIgnoreCase("selling")){

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

                        sview_diyMaterials.setText(messageMat);
//                            tv_procedures.setText("NOT APPLICABLE! BUY THE ITEM FIRST OR ASK PERMISSION TO THE OWNER!");
                        sview_diyProcedures.setText(messageProd);
                        sview_diyProcedures.setTextColor(Color.BLACK);

                        if (diYnames.diyUrl != null) {
                            imgviewpager = (ViewPager) findViewById(R.id.sview_view_pager);
                            viewImagesSearchPagerAdapter = new ViewImagesSearchPagerAdapter(getBaseContext(), diYnames.diyUrl);
                            imgviewpager.setAdapter(viewImagesSearchPagerAdapter);
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



    private class ViewImagesSearchPagerAdapter extends PagerAdapter {

        private Context context;
        private String diyUrl;

        public ViewImagesSearchPagerAdapter(Context context, String diyUrl) {
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
