package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
    private EditText diyName;
    private EditText diyMaterials;
    private EditText diyProcedures;
    private  Button btnShow;
    private Uri downloadUrl;

    private ProgressDialog mProgressDialog;
    // private String mUsername;

    private DatabaseReference databaseReference;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private Uri mImageUrl ;
    private Bitmap bitmap;
    private FirebaseDatabase database;

    private static final int RESULT_LOAD_IMAGE=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_diy);

        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("DIY_Methods").child("category").child("bottle");
        mStorageRef = FirebaseStorage.getInstance().getReference("add_DIY");

        //mAuth = FirebaseAuth.getInstance();
        // final FirebaseUser user = mAuth.getCurrentUser();
        //userid = user.getUid();

        mProgressDialog = new ProgressDialog(this);
        imgViewPhoto = (ImageView) findViewById(R.id.photoSaver);
        btnSave = (Button) findViewById(R.id.btnSave);
        diyName = (EditText) findViewById(R.id.etName);
        diyMaterials = (EditText) findViewById(R.id.etMaterials);
        diyProcedures = (EditText) findViewById(R.id.etProcedures);

        btnSave.setOnClickListener(this);

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
        String dMaterials = diyMaterials.getText().toString();
        String dProcedures = diyProcedures.getText().toString();

        Intent intent = new Intent(CaptureDIY.this,HomePageActivity.class);
        startActivity(intent);

        DIYDetails details = new DIYDetails(dName, dMaterials, dProcedures);
        databaseReference.push().setValue(details);

        diyName.setText("");
        diyMaterials.setText("");
        diyProcedures.setText("");
//        if (dName.isEmpty()) {
//            Toast.makeText(getApplicationContext(), "DIY Name?", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (dMaterials.isEmpty()){
//            Toast.makeText(getApplicationContext(), "DIY Materials?", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (dProcedures.isEmpty()){
//            Toast.makeText(getApplicationContext(), "DIY Procedures?", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if(TextUtils.isEmpty(dName) || TextUtils.isEmpty(dMaterials) || TextUtils.isEmpty(dProcedures)){
            Toast.makeText(getApplicationContext(), "Please fill out necessary details", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), "Updated Info", Toast.LENGTH_SHORT).show();
    }


    //request to get image!
    private void dispatchTakePictureIntent(){
        Intent ImageIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //implicit intent
        CaptureDIY.this.startActivityForResult(ImageIntent,RESULT_LOAD_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode ==RESULT_OK){
            mImageUrl = data.getData();
            imgViewPhoto.setImageURI(mImageUrl);
            StorageReference filePath = mStorageRef.child(mImageUrl.getLastPathSegment());

            mProgressDialog.setMessage("Uploading image....");
            mProgressDialog.show();

            filePath.putFile(mImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl=taskSnapshot.getDownloadUrl();

                    //  DIYDetails details = new DIYDetails(null, mUsername, downloadUrl.toString());
                    databaseReference.getRef().child("Image_URL").setValue(downloadUrl.toString());

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

