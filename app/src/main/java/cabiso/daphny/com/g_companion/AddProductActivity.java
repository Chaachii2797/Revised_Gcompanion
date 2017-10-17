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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import static cabiso.daphny.com.g_companion.MyProfileActivity.REQUEST_IMAGE_CAPTURE;

/**
 * Created by Lenovo on 8/22/2017.
 */

public class AddProductActivity extends AppCompatActivity{

    private DatabaseReference marketplaceReference;
    private DatabaseReference currentProductReference;
    private DatabaseReference productImageURLsReference;


    private StorageReference storageReference;
    private StorageReference productImagesStorageReference;

    private FirebaseUser mFirebaseUser;
    private String userID;

    private Uri productPictureUri;
    private static final int SELECT_PHOTO = 100;

    private String currentKey;
    private String imageFileName;
    private List<String> productPictureURLList = new ArrayList<String>();
    private List<Uri> productImageLocalURIs = new ArrayList<Uri>();

    private ProductImagesRecyclerViewAdapter productImagesRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        marketplaceReference = FirebaseDatabase.getInstance().getReference().child("marketplace");

        currentProductReference=marketplaceReference.push();
        currentKey=currentProductReference.getKey();

        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://g-companion.appspot.com/");
        productImagesStorageReference = storageReference.child("ProductImages"+"/"+currentKey);

        final EditText title = (EditText)findViewById(R.id.add_product_title);
        final EditText desc = (EditText)findViewById(R.id.add_diy_material);
        final EditText price = (EditText) findViewById(R.id.add_diy_procedure);

        productImagesRecyclerViewAdapter = new ProductImagesRecyclerViewAdapter();
        final RecyclerView productImagesRecyclerView = (RecyclerView) findViewById(R.id.add_product_images_recycler_view);
        productImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        productImagesRecyclerView.setAdapter(productImagesRecyclerViewAdapter);

        final ImageView addProductImagePlusIcon = (ImageView) findViewById(R.id.add_product_image_plus_icon);
        registerForContextMenu(findViewById(R.id.add_product_image_plus_icon));
        addProductImagePlusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dispatchTakePictureIntent();
                openContextMenu(v);
                v.showContextMenu();
            }
        });

        Button submitButton = (Button) findViewById(R.id.add_submit_diy);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentProductReference.setValue(new ProductInfo(title.getText().toString(), desc.getText().toString(),
                        price.getText().toString(), "yes", productPictureURLList, userID));

                imageFromUri();

                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId()==R.id.add_product_image_plus_icon){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_for_add_product,menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.open_camera:
                dispatchTakePictureIntent();
                break;
            case R.id.open_gallery:
                get_image();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            productImageLocalURIs.add(productPictureUri);
            //productImagesRecyclerViewAdapter.notifyItemInserted(productImageLocalURIs.size()-1);
        }else if(requestCode==SELECT_PHOTO && resultCode == RESULT_OK){
            productImageLocalURIs.add(productPictureUri);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void get_image(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir );
        productPictureUri = Uri.fromFile(image);
        return image;
    }

    public void imageFromUri(){
        for(Uri proPicUri:productImageLocalURIs){
            this.getContentResolver().notifyChange(proPicUri, null);
            ContentResolver cr = this.getContentResolver();
            StorageReference pictureRef = productImagesStorageReference.child(proPicUri.getLastPathSegment());
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

            productPictureURLList.add(pictureRef.toString());
            currentProductReference.child("productPictureURLs").setValue(productPictureURLList);
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

    public class ProductImagesRecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder>{

        @Override
        public int getItemCount(){
            return productImageLocalURIs.size();
        }

        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_addproductimage, parent, false);
            ItemViewHolder vh = new ItemViewHolder(view) ;
            return vh;
        }

        public void onBindViewHolder(ItemViewHolder vh, int position){
            Uri imageUri = productImageLocalURIs.get(position);
            vh.bindImage(imageUri);
        }
    }
}
