package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import cabiso.daphny.com.g_companion.Model.DIYDetails;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class CaptureDIY extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgViewPhoto;
    private Button btnSave;
    private EditText price;
    private EditText diyName;
    private EditText category;
    private  Button btnShow;

    private ProgressDialog mProgressDialog;
    // private String mUsername;

    private DatabaseReference databaseReference;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private Uri mImageUrl ;
    private Bitmap bitmap;
    private FirebaseDatabase database;

    private static final int CAMERA_REQUEST_CODE=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_diy);

        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("DIY_Details").push();
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://g-companion.appspot.com/").child("add_DIY");

        //mAuth = FirebaseAuth.getInstance();
        // final FirebaseUser user = mAuth.getCurrentUser();
        //userid = user.getUid();

        mProgressDialog = new ProgressDialog(this);
        imgViewPhoto = (ImageView) findViewById(R.id.photoSaver);
        btnSave = (Button) findViewById(R.id.btnSave);
        price = (EditText) findViewById(R.id.etMaterials);
        diyName = (EditText) findViewById(R.id.etName);
        category = (EditText) findViewById(R.id.etCategory);
        //  btnShow = (Button) findViewById(R.id.btnShow);

        btnSave.setOnClickListener(this);
        //  btnShow.setOnClickListener(this);

        dispatchTakePictureIntent();
     /*  btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CaptureDIY.this,HomePageActivity.class);
                startActivity(intent);
            }
        });*/
    }



    @Override
    public void onClick(View v) {

        String dName = diyName.getText().toString();
        String dPrice = price.getText().toString();
        String dCategory = category.getText().toString();

        Intent intent = new Intent(CaptureDIY.this,HomePageActivity.class);
        startActivity(intent);

        DIYDetails details = new DIYDetails(dName, dPrice, dCategory);
        databaseReference.push().setValue(details);

        price.setText("");
        diyName.setText("");
        category.setText("");


        if (dName.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Price?", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dPrice.isEmpty()){
            Toast.makeText(getApplicationContext(), "DIY name?", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dCategory.isEmpty()){
            Toast.makeText(getApplicationContext(), "Category?", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getApplicationContext(), "Updated Info", Toast.LENGTH_SHORT).show();

    }


    //request to capture image!
    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode ==RESULT_OK){
            mImageUrl = data.getData();
            imgViewPhoto.setImageURI(mImageUrl);
            StorageReference filePath = mStorageRef.child(mImageUrl.getLastPathSegment());

            mProgressDialog.setMessage("Uploading image....");
            mProgressDialog.show();

            filePath.putFile(mImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl =taskSnapshot.getDownloadUrl();

                    //  DIYDetails details = new DIYDetails(null, mUsername, downloadUrl.toString());
                    databaseReference.child("Image_URL").setValue(downloadUrl.toString());

                    Glide.with(getApplicationContext())
                            .load(downloadUrl)
                            .crossFade()
                            .placeholder(R.drawable.add)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(imgViewPhoto);
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });

        }
    }


}

