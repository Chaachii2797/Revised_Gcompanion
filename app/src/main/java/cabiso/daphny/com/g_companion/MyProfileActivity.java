package cabiso.daphny.com.g_companion;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import cabiso.daphny.com.g_companion.EditData.EditProfileActivity;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import de.hdodenhof.circleimageview.CircleImageView;

import static cabiso.daphny.com.g_companion.R.id.user_ratings;

/**
 * Created by Lenovo on 8/22/2017.
 */

public class MyProfileActivity extends AppCompatActivity implements RatingDialogListener {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference userdataReference, userDBReference;
    private String userID;
    private User_Profile userProfileInfo;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private StorageReference userStorageReference;
    private FirebaseDatabase database;
    private File profileImage;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    protected static final int GALLERY_PICTURE = 1;

    private CircleImageView profile_picture;
    private Uri profilePictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userID = mFirebaseUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        userdataReference = mDatabaseReference.child("userdata");

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://g-companion-v2.appspot.com/" +
                "file_upload UPLOAD FILE");
        userStorageReference = storageReference.child("UserStorage"+"/"+userID);

        userDBReference = FirebaseDatabase.getInstance().getReference().child("userdata").child(userID);

        setContentView(R.layout.activity_my_profile);

        final TextView profile_username = (TextView) findViewById(R.id.profile_name);
        final TextView profile_email = (TextView) findViewById(R.id.profile_email);
        final TextView profile_password = (TextView) findViewById(R.id.profile_password);
        final TextView profile_phone = (TextView)findViewById(R.id.profile_phone);
        final TextView profile_address = (TextView) findViewById(R.id.profile_address);
        profile_picture = (CircleImageView) findViewById(R.id.profile_picture);
        final Button edit_profile_btn = (Button) findViewById(R.id.edit_submit);
        final TextView rate = (TextView) findViewById(user_ratings);


        rate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new AppRatingDialog.Builder()
                        .setPositiveButtonText("Submit")
                        .setNegativeButtonText("Cancel")
                        .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                        .setDefaultRating(2)
                        .setTitle("Rate this seller")
                        .setDescription("Please select some stars and give your feedback")
                        .setTitleTextColor(R.color.titleTextColor)
                        .setDescriptionTextColor(R.color.contentTextColor)
                        .setHint("Please write your comment here ...")
                        .setHintTextColor(R.color.hintTextColor)
                        .setCommentTextColor(R.color.commentTextColor)
                        .setCommentBackgroundColor(R.color.bg_screen2)
                        .setWindowAnimation(R.style.MyDialogFadeAnimation)
                        .create(MyProfileActivity.this)
                        .show();

            }
        });

        final StorageReference pictureReference = userStorageReference.child("profilePicture.jpg");
        try{
            profileImage = File.createTempFile("images","jpg");
        }
        catch(IOException io){
            Log.d("Exception caught", io.toString());
        }

        pictureReference.getFile(profileImage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                profile_picture.setImageBitmap(BitmapFactory.decodeFile(profileImage.getAbsolutePath()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });


        userdataReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfileInfo = dataSnapshot.getValue(User_Profile.class);
                Log.d("profileUserID", userID);
                if(userProfileInfo!=null){
                    profile_username.setText(userProfileInfo.getF_name() + " " + userProfileInfo.getL_name());
                    profile_email.setText(userProfileInfo.getEmail());
                    profile_phone.setText(userProfileInfo.getContact_no());
                    profile_address.setText(userProfileInfo.getAddress());
                    profile_password.setText(userProfileInfo.getPassword());

                    rate.setText(userProfileInfo.getUserRating().toString());
                    Log.e("userratee", String.valueOf(userProfileInfo.getUserRating()));


                    if(userProfileInfo.getUserProfileUrl() == null) {
                        profile_picture.setImageDrawable(getResources().getDrawable(R.drawable.add));

                    } else{
                        Glide.with(MyProfileActivity.this)
                                .load(userProfileInfo.getUserProfileUrl())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .fitCenter().crossFade()
                                .into(profile_picture);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failure to read", "Failed to read value.", error.toException());
            }
        });

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("profileUserId", userdataReference.child(userID).getKey());

                intent.putExtra("profileUserName", userProfileInfo.getF_name() + " " + userProfileInfo.getL_name());
                intent.putExtra("profileEmailAdd", userProfileInfo.getEmail());
                intent.putExtra("profilePass", userProfileInfo.getPassword());
                intent.putExtra("profileNumber", userProfileInfo.getContact_no());
                intent.putExtra("profileAddress", userProfileInfo.getAddress());

                Log.e("profileUserId", userdataReference.child(userID).getKey());
                startActivity(intent);
            }
        });

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

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            imageFromUri();
        }
        galleryAddPic();
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir );
        profilePictureUri = Uri.fromFile(image);
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        /*File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);*/
        mediaScanIntent.setData(profilePictureUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void imageFromUri(){
        this.getContentResolver().notifyChange(profilePictureUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try{
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, profilePictureUri);
            profile_picture.setImageBitmap(bitmap);
        }
        catch(IOException io){
            Log.d("IO Exception caught", io.toString());
        }
        StorageReference profilePictureRef = userStorageReference.child("profilePicture.jpg");
        UploadTask uploadTask = profilePictureRef.putFile(profilePictureUri);

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

                userDBReference.child("userProfileUrl").setValue(taskSnapshot.getDownloadUrl().toString());

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int rate, @NotNull String comment) {

        TextView rates = (TextView) findViewById(user_ratings);
//        int total = 0;
//        total = rate++;
//        int average = total/rate;
        rates.setText(Integer.toString(rate));
        Toast.makeText(MyProfileActivity.this,"Rate : " + rate + "\n Comment : " + comment,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNegativeButtonClicked() {
    }
}
