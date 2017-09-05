package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import cabiso.daphny.com.g_companion.Model.DIYMethods;
import cabiso.daphny.com.g_companion.Recommend.DIYrecommend;


public class UploadImage extends AppCompatActivity implements View.OnClickListener{

    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView imgView;
    private EditText etname, etmaterials, etprocedures;
    private Button button;

    private ProgressDialog mProgressDialog;
    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Uri ImagePathAndName, downloadUri;
    String file_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        mStorageRef = FirebaseStorage.getInstance().getReference("Upload Images");
        databaseReference = FirebaseDatabase.getInstance().getReference("DIY_Methods").child("category").child("bottle").push();
//        mAuth = FirebaseAuth.getInstance();
//        final FirebaseUser user = mAuth.getCurrentUser();
//        userid = user.getUid();

        imgView = (ImageView) findViewById(R.id.imgUpload);
        etname = (EditText) findViewById(R.id.etName);
        etmaterials = (EditText) findViewById(R.id.etMaterials);
        etprocedures = (EditText) findViewById(R.id.etProcedures);

        button = (Button) findViewById(R.id.buttonSave);
        button.setOnClickListener(this);

        fetchImage();
    }


    @Override
    public void onClick(View v) {
        String name = etname.getText().toString();
        String materials = etmaterials.getText().toString();
        String procedures = etprocedures.getText().toString();
        String image = imgView.getDrawable().toString();

        Intent intent = new Intent(UploadImage.this,DIYActivity.class);
        startActivity(intent);

        DIYrecommend items = new DIYrecommend(name,materials,procedures,image);
        databaseReference.push().setValue(items);

        if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "DIY Name?", Toast.LENGTH_SHORT).show();
            return;
        }if (materials.isEmpty()){
            Toast.makeText(getApplicationContext(), "Materials?", Toast.LENGTH_SHORT).show();
            return;
        }if (procedures.isEmpty()){
            Toast.makeText(getApplicationContext(), "Procedures?", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getApplicationContext(), "Updated Info", Toast.LENGTH_SHORT).show();

            StorageReference filePath = mStorageRef.child(file_name);
            Log.d("GWAPA", "CHAACHII"+file_name);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            showProgressDialog();
            filePath.putFile(ImagePathAndName).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUri = taskSnapshot.getDownloadUrl();
                    mStorageRef.putFile(downloadUri);
                    databaseReference.child("DIY_Methods").setValue(downloadUri.toString());
                    Glide.with(getApplicationContext())
                            .load(downloadUri)
                            .crossFade()
                            .placeholder(R.drawable.add)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(imgView);
                    Toast.makeText(UploadImage.this,"Successfully uploaded image!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UploadImage.this,DIYActivity.class);
                    startActivity(intent);
                    hideProgressDialog();
                }
            });

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
            if (ImagePathAndName.getScheme().toString().compareTo("content")==0)
            {
                Cursor cursor =getContentResolver().query(ImagePathAndName, null, null, null, null);
                if (cursor.moveToFirst())
                {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                    Uri filePathUri = Uri.parse(cursor.getString(column_index));
                    file_name = filePathUri.getLastPathSegment().toString();
                }
            }
            imgView.setImageURI(ImagePathAndName);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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
