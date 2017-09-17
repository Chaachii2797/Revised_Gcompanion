package cabiso.daphny.com.g_companion;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Lenovo on 8/22/2017.
 */

import cabiso.daphny.com.g_companion.Recommend.DIYrecommend;

import static cabiso.daphny.com.g_companion.MyProfileActivity.REQUEST_IMAGE_CAPTURE;

public class CaptureDIY extends AppCompatActivity{

    private DatabaseReference marketplaceReference;
    private DatabaseReference currentProductReference;
    private DatabaseReference productImageURLsReference;

    private StorageReference storageReference;
    private StorageReference diyImagesStorageReference;

    private FirebaseUser mFirebaseUser;
    private String userID;

    private Uri diyPictureUri;

    private static final int RESULT_LOAD_IMAGE=1;
    private String currentKey;
    private String imageFileName;
    private List<String> diyPictureURLList = new ArrayList<String>();
    private List<Uri> diyImageLocalURIs = new ArrayList<Uri>();

    private DIYImagesRecyclerViewAdapter diyImagesRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_diy);

        marketplaceReference = FirebaseDatabase.getInstance().getReference().child("DIY_Methods").child("category");

        currentProductReference=marketplaceReference.push();
        currentKey=currentProductReference.getKey();

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://g-companion.appspot.com/");
        diyImagesStorageReference = storageReference.child("add_DIY"+"/"+currentKey);

        final EditText name = (EditText)findViewById(R.id.add_diy_name);
        final EditText material = (EditText)findViewById(R.id.add_diy_material);
        final EditText procedure = (EditText) findViewById(R.id.add_diy_procedure);

        diyImagesRecyclerViewAdapter = new DIYImagesRecyclerViewAdapter();
        final ImageView addProductImagePlusIcon = (ImageView) findViewById(R.id.add_product_image_plus_icon);
        addProductImagePlusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CaptureDIY.this,"IMAGE URL: "+diyPictureURLList,Toast.LENGTH_SHORT).show();
                dispatchTakePictureIntent();
            }
        });

        Button submitButton = (Button) findViewById(R.id.add_submit_diy);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CaptureDIY.this,"IMAGE URL: "+diyPictureURLList,Toast.LENGTH_SHORT).show();
                currentProductReference.setValue(new DIYrecommend(name.getText().toString(), material.getText().toString(),
                        procedure.getText().toString()),diyPictureURLList.get(0));

                imageFromUri();

                Intent intent = new Intent(CaptureDIY.this,HomePageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK){

            if(requestCode == RESULT_LOAD_IMAGE && resultCode ==RESULT_OK){
                diyImageLocalURIs.add(diyPictureUri);
            }
            //productImagesRecyclerViewAdapter.notifyItemInserted(productImageLocalURIs.size()-1);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    private void dispatchTakePictureIntent() {
        Intent ImageIntent  = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        CaptureDIY.this.startActivityForResult(ImageIntent,RESULT_LOAD_IMAGE);

        if (ImageIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }

            catch(IOException io){
                Log.d("Exception caught", io.toString());
            }
            if(photoFile!=null){
                Uri photoUri = FileProvider.getUriForFile(this,"cabiso.daphny.com.g_companion", photoFile);
                Log.d("URI is", photoUri.toString());
                ImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(ImageIntent, RESULT_LOAD_IMAGE);
            }
        }
//        if (ImageIntent.resolveActivity(getPackageManager()) != null) {
//            File photoFile = null;
//            try{
//                photoFile = createImageFile();
//            }
//            catch(IOException io){
//                Log.d("Exception caught", io.toString());
//            }
//            if(photoFile!=null){
//                Uri photoUri = FileProvider.getUriForFile(this,"cabiso.daphny.com.g_companion", photoFile);
//                Log.d("URI is", photoUri.toString());
//                ImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                startActivityForResult(ImageIntent, RESULT_LOAD_IMAGE);
//            }
        }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir );
        diyPictureUri = Uri.fromFile(image);
        return image;
    }

    public void imageFromUri(){
        for(Uri proPicUri:diyImageLocalURIs){
            this.getContentResolver().notifyChange(proPicUri, null);
            ContentResolver cr = this.getContentResolver();
            StorageReference pictureRef = diyImagesStorageReference.child(proPicUri.getLastPathSegment());
            UploadTask uploadTask = pictureRef.putFile(proPicUri);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });
            diyPictureURLList.add(pictureRef.toString());
            currentProductReference.child("diyPictureURLs").setValue(diyPictureURLList);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public ImageView productImage;

        public ItemViewHolder(View v){
            super(v);
            view=v;
            productImage = (ImageView) v.findViewById(R.id.product_image_in_card);
        }

        public void bindImage(Uri imageUri){
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageUri.getPath(), options);

            options.inSampleSize = calculateInSampleSize(options, 90, 90);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(), options);
            productImage.setImageBitmap(bitmap);
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }
    }

    public class DIYImagesRecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder>{

        @Override
        public int getItemCount(){
            return diyImageLocalURIs.size();
        }

        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_addproductimage, parent, false);
            ItemViewHolder vh = new ItemViewHolder(view) ;
            return vh;
        }

        public void onBindViewHolder(ItemViewHolder vh, int position){
            Uri imageUri = diyImageLocalURIs.get(position);
            vh.bindImage(imageUri);
        }
    }
}
