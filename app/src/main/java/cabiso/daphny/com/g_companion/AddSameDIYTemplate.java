package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.SellingDIY;

/**
 * Created by Lenovo on 6/21/2018.
 */

public class AddSameDIYTemplate extends AppCompatActivity{

    private EditText name, price, quantity;
    private ImageView addImageProduct;
    private Button addDIYProduct;
    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private FirebaseUser mFirebaseUser;
    private String userID;
    private String imageFileName;
    private Uri diyPictureUri;
    private ProgressDialog progressDialog;
    UploadTask uploadTask;

    private DatabaseReference databaseReference, related_diy_by_user;
    private FirebaseStorage mStorage;
    private StorageReference storageReference, imageRef;
    //String mCurrentPhotoPath;
    //Uri photoURI;
    public String photoFileName = "same_prod";
    File photoFile;
    public final String APP_TAG = "same_diy_products";
    ArrayList<SellingDIY> dbSelling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_same_diy_template);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("same_diy_product");
        related_diy_by_user = FirebaseDatabase.getInstance().getReference().child("diy_by_users").child(userID);

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://g-companion-v2.appspot.com/").child("same_diy_product_storage");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.back_btn);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DIYDetailViewActivity.class));
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addImageProduct = (ImageView) findViewById(R.id.add_product_image);
        name = (EditText) findViewById(R.id.add_diy_product_name);
        price = (EditText) findViewById(R.id.add_diy_product_price);
        quantity = (EditText) findViewById(R.id.add_diy_product_quantity);
        addDIYProduct = (Button) findViewById(R.id.addDIYProduct);

        dbSelling = new ArrayList<>();

        addImageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        addDIYProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                imageRef = storageReference.child(photoFile.getAbsolutePath());
                imageRef = storageReference.child(String.valueOf(name.getText()));

//                StorageReference fileRef =
//                        storageReference.child(photoFileName).child(String.valueOf(name.getText()));

                Toast.makeText(AddSameDIYTemplate.this, "Button Clicked" + "\n" +
                        name.getText() + "\n" + price.getText() + "\n" + quantity.getText() + "\n" + photoFile ,
                        Toast.LENGTH_SHORT).show();

                //creating and showing progress dialog
                progressDialog = new ProgressDialog(AddSameDIYTemplate.this);
                progressDialog.setMax(100);
                progressDialog.setMessage("Adding DIY Product...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                progressDialog.setCancelable(false);

                //starting upload
                uploadTask = imageRef.putFile(Uri.fromFile(photoFile));

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        //sets and increments value of progressbar
                        progressDialog.incrementProgressBy((int) progress);
                    }
                });
                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(AddSameDIYTemplate.this, "Error in uploading!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Float float_this = Float.valueOf(0);

                        String upload = databaseReference.push().getKey();
                        String productID_sell = generateString();

                        String for_price = price.getText().toString();
                        String for_qty = quantity.getText().toString();
//                        final String for_descr = etDescription.getText().toString();

                        final double price = Double.parseDouble(for_price);
                        final int qty = Integer.parseInt(for_qty);

                        dbSelling.add(new SellingDIY().setSelling_price(price).setSelling_qty(qty));

                        //push data to Firebase Database - same_diy_product node
                        databaseReference.child(upload).setValue(new DIYSell(name.getText().toString(),
                                taskSnapshot.getDownloadUrl().toString(), userID, productID_sell, "selling",
                                float_this, float_this));
                        databaseReference.child(upload).child("status").setValue("selling");
                        databaseReference.child(upload).child("DIY Price").setValue(price);
                        databaseReference.child(upload).child("Item Quantity").setValue(qty);


                        related_diy_by_user.child(upload).setValue(new DIYSell(name.getText().toString(),
                                taskSnapshot.getDownloadUrl().toString(), userID, productID_sell, "selling",
                                float_this, float_this));
                        related_diy_by_user.child(upload).child("DIY Price").setValue(dbSelling);
                        related_diy_by_user.child(upload).child("status").setValue("selling");
//                        related_diy_by_user.child(upload).child("Item Quantity").setValue(quantity.getText().toString());

                        // Alert Dialog for finished uploaing DIYs
                        AlertDialog.Builder ab = new AlertDialog.Builder(AddSameDIYTemplate.this, R.style.MyAlertDialogStyle);
                        ab.setMessage("Thank you for making this DIY!");
                        ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent in = new Intent(AddSameDIYTemplate.this, MainActivity.class);
                                in.putExtra("a", "neww");
                                startActivity(in);
                            }
                        });

                        ab.create().show();
                        progressDialog.dismiss();
                    }
                });

            }
        });

    }

    public static String generateString() {
        String prod_id = UUID.randomUUID().toString();
        return prod_id;
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(AddSameDIYTemplate.this, "cabiso.daphny.com.g_companion", photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.e(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

//    private File createImageFile() throws IOException {
//// Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new     Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//// Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }
//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            addImageProduct = (ImageView) findViewById(R.id.add_product_image);
            addImageProduct.setImageBitmap(takenImage);

            Log.e("photo_file", "" + takenImage);

            if(takenImage == null){
                Toast.makeText(this, "NULL IMAGE", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this, "NOT NULL IMAGE", Toast.LENGTH_SHORT).show();

            }


        } else { // Result was a failure
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }

    }


}
