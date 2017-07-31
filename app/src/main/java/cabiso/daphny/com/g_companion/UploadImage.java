package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import cabiso.daphny.com.g_companion.Fragments.DIYCommunity;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class UploadImage extends AppCompatActivity implements View.OnClickListener{

    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView imgView;
    private Button button;

    private ProgressDialog mProgressDialog;
    private StorageReference mStorageRef;
    private String userid;
    private FirebaseAuth mAuth;
    private Uri ImagePathAndName;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        mStorageRef = FirebaseStorage.getInstance().getReference("Upload Images");
        //   mAuth = FirebaseAuth.getInstance();
        // final FirebaseUser user = mAuth.getCurrentUser();
        //  userid = user.getUid();

        imgView = (ImageView) findViewById(R.id.imgUpload);
        button = (Button) findViewById(R.id.buttonSave);
        button.setOnClickListener(this);

        Fragment fragment = new DIYCommunity();
        Bundle bundle = new Bundle();
        bundle.putParcelable("bitmap", bitmap);
        fragment.setArguments(bundle);
        fetchImage();

    }

    @Override
    public void onClick(View v) {
        if (v == button) {
            Toast.makeText(UploadImage.this, "YEEEEEEEEs!" + userid, Toast.LENGTH_SHORT).show();
//            uploadImage(ImagePathAndName);
//            StorageReference filePath = mStorageRef.child(userid).child(ImagePathAndName.getLastPathSegment());
            StorageReference filePath = mStorageRef.child(ImagePathAndName.getLastPathSegment());
            showProgressDialog();
            filePath.putFile(ImagePathAndName).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(UploadImage.this,"Successfully uploaded image!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UploadImage.this,MainActivity.class);
                    startActivity(intent);
                    hideProgressDialog();
                }
            });
        }else{Toast.makeText(UploadImage.this,"Failed to upload image!",Toast.LENGTH_SHORT).show();}
    }

    private void fetchImage(){
        Intent ImageIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //implicit intent
        UploadImage.this.startActivityForResult(ImageIntent,RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            ImagePathAndName = data.getData();
            imgView.setImageURI(ImagePathAndName);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}

