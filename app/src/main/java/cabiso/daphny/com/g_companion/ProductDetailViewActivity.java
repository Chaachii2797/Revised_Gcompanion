package cabiso.daphny.com.g_companion;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static cabiso.daphny.com.g_companion.R.id.btn_contact_seller;

/**
 * Created by Lenovo on 8/22/2017.
 */

public class ProductDetailViewActivity extends AppCompatActivity{
    private DatabaseReference productReference;
    private ProductImagesViewPagerAdapter productImagesViewPagerAdapter;
    private ViewPager productImagesViewPager;
    private String sellerID;
    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_view);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        String productReferenceString = getIntent().getStringExtra("Product reference");
        productReference = FirebaseDatabase.getInstance().getReferenceFromUrl(productReferenceString);
        //Log.d("ProductRefrence url", productReference.toString());
        final TextView productTitleTextView = (TextView) findViewById(R.id.productTitle);
        final TextView productPriceTextView = (TextView) findViewById(R.id.productPrice);
        final TextView productDescriptionTextView = (TextView) findViewById(R.id.productDescription);
        final TextView productPriceStyleTextView = (TextView) findViewById(R.id.productPriceStyle);
        final TextView productOwnerNameTextView = (TextView) findViewById(R.id.productOwnerName);
        final TextView productOwnerAddressTextView = (TextView) findViewById(R.id.productOwnerAddress);
        final TextView productOwnerPhoneTextView = (TextView) findViewById(R.id.productOwnerPhone);
        final Button contact_seller = (Button) findViewById(btn_contact_seller);

        contact_seller.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
                        //Do your code here
                    }
                });
                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = productOwnerPhoneTextView.getText().toString();
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                                "tel", phone, null));
                        startActivity(phoneIntent);
                    }
                });
                sms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = productOwnerPhoneTextView.getText().toString();
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

        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProductInfo productInfo =  dataSnapshot.getValue(ProductInfo.class);
                collapsingToolbarLayout.setTitle(productInfo.title);
                productTitleTextView.setText(productInfo.title);
                productPriceTextView.setText(productInfo.price);
                productDescriptionTextView.setText(productInfo.desc);
                if(productInfo.negotiable.equalsIgnoreCase("yes")){
                    productPriceStyleTextView.setText("Price Negotiable");
                }else{
                    productPriceStyleTextView.setText("Fixed Price");
                }
                String ownerUserID = productInfo.ownerUserID;
                sellerID = ownerUserID;
                DatabaseReference userDataReference = FirebaseDatabase.getInstance().getReference().child("userdata").child(ownerUserID);
                userDataReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot userDataSnapshot) {
                        UserProfileInfo userProfileInfo = userDataSnapshot.getValue(UserProfileInfo.class);
                        productOwnerNameTextView.setText(userProfileInfo.username);
                        productOwnerAddressTextView.setText(userProfileInfo.address);
                        productOwnerPhoneTextView.setText(userProfileInfo.phone);

//                        productOwnerNameTextView.setOnClickListener(new View.OnClickListener(){
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(ProductDetailViewActivity.this, MyProfileActivity.class);
//                                startActivity(intent);
//                            }
//                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                if(productInfo.productPictureURLs!=null){
                    productImagesViewPager = (ViewPager) findViewById(R.id.productImagesViewPager);
                    productImagesViewPagerAdapter = new ProductImagesViewPagerAdapter(getBaseContext(), productInfo.productPictureURLs);
                    productImagesViewPager.setAdapter(productImagesViewPagerAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public class ProductImagesViewPagerAdapter extends PagerAdapter {

        private Context context;
        private List<String> productPictureURLs;

        public ProductImagesViewPagerAdapter(Context context, List<String> productPictureURLs){
            this.context = context;
            this.productPictureURLs = productPictureURLs;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position){
            View currentView = LayoutInflater.from(context).inflate(R.layout.viewpager_product_images, container, false);
            final ImageView productImageView = (ImageView) currentView.findViewById(R.id.viewpager_productImage);
            try{
                StorageReference productImageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(productPictureURLs.get(position));
                Log.d("Image Storage Reference", productImageStorageReference.toString());
                productImageStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("Image Download URI", uri.toString());
                        Picasso.with(context).load(uri).resize(350,350).into(productImageView);
                    }
                });

            }
            catch(Exception e){
                Log.d("Exception", "Getting product image");
            }
            container.addView(currentView);
            return currentView;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object){
            return view.equals(object);
        }


        @Override
        public int getCount(){
            Log.d("Number of images is", new Integer(productPictureURLs.size()).toString());
            return productPictureURLs.size();
        }
    }

}
