package cabiso.daphny.com.g_companion;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import cabiso.daphny.com.g_companion.Model.User_Profile;
import de.hdodenhof.circleimageview.CircleImageView;

import static cabiso.daphny.com.g_companion.R.id.user_ratings;

/**
 * Created by Lenovo on 9/12/2018.
 */

public class OtherUserProfileActivity extends AppCompatActivity implements RatingDialogListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference reportUserReference, userDBReference;
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
    private String profileUserId, profileUserFName, profileUserLName, profileEmailAdd, profileNumber, profileAddress,
            profilePicture, profileRatings, reportedBy, customerID;

    final String[] reports = {"It's a scam.", "I haven't received my order.", "The seller doesn't respond to my chat/text/call.",
            "I want refund.", "I want to return the item."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_user_activity);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userID = mFirebaseUser.getUid();

        //get intent from ViewRelatedDIY
        Intent intent = getIntent();
        profileUserId = intent.getExtras().getString("profileUserId");
        profileUserFName = intent.getExtras().getString("profileUserFName");
        profileUserLName = intent.getExtras().getString("profileUserLName");
        profileEmailAdd = intent.getExtras().getString("profileEmailAdd");
        profileNumber = intent.getExtras().getString("profileNumber");
        profileAddress = intent.getExtras().getString("profileAddress");
        profilePicture = String.valueOf(intent.getStringExtra("profilePicture"));
        profileRatings = intent.getExtras().getString("profileRatings");
        reportedBy = intent.getExtras().getString("reportedBy");
        customerID = intent.getExtras().getString("customerID");

          /* Toolbar Configurations */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(profileUserId);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_btn);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        reportUserReference = FirebaseDatabase.getInstance().getReference().child("reportedSeller").child(profileUserId);

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://g-companion-v2.appspot.com/" +
                "file_upload UPLOAD FILE");
        userStorageReference = storageReference.child("UserStorage"+"/"+userID);

        userDBReference = FirebaseDatabase.getInstance().getReference().child("userdata").child(userID);

        final TextView profile_username = (TextView) findViewById(R.id.other_user_profile_name);
        final TextView profile_email = (TextView) findViewById(R.id.other_user_profile_email);
        final TextView profile_password = (TextView) findViewById(R.id.other_user_profile_password);
        final TextView profile_phone = (TextView)findViewById(R.id.other_user_profile_phone);
        final TextView profile_address = (TextView) findViewById(R.id.other_user_profile_address);
        profile_picture = (CircleImageView) findViewById(R.id.other_user_profile_picture);
        final TextView rate = (TextView) findViewById(user_ratings);
        ImageView block_user = (ImageView) findViewById(R.id.blockUser);


        block_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(OtherUserProfileActivity.this);
                builder.setTitle((Html.fromHtml("<font color='#FF7F27'>Choose a reason for reporting this user: </font>")));
                builder.setItems(reports, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int reason) {
                        Toast.makeText(OtherUserProfileActivity.this, "Report: " + " " + reports[reason], Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(OtherUserProfileActivity.this);
                        builder.setTitle("Confirm action: ");
                        builder.setMessage("Are you sure? ");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes, I'm sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Seller reported!", Toast.LENGTH_SHORT).show();

                                float ratingss = Float.parseFloat(profileRatings);

                                User_Profile sellerReport = new User_Profile(profileAddress, profileNumber, profileUserFName,
                                        profileUserLName, profileEmailAdd, "*******", profileUserId, " ", profilePicture,
                                        ratingss, "user");

                                final String upload = reportUserReference.push().getKey();
                                reportUserReference.child(upload).setValue(sellerReport);
                                reportUserReference.child(upload).child("complaint").setValue(reports[reason]);
                                reportUserReference.child(upload).child("reportedBy").setValue(reportedBy);
                                reportUserReference.child(upload).child("customerID").setValue(customerID);

                                Intent intent = new Intent(OtherUserProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.show();




                    }
                });
                builder.show();
            }
        });


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
                        .create(OtherUserProfileActivity.this)
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

        //set seller details
        profile_username.setText(profileUserFName + " " + profileUserLName);
        profile_email.setText(profileEmailAdd);
        profile_phone.setText(profileNumber);
        profile_address.setText(profileAddress);
        profile_password.setText("*******");
        rate.setText(profileRatings);
        Glide.with(this)
                .load(profilePicture)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter().crossFade()
                .into(profile_picture);


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
        Toast.makeText(OtherUserProfileActivity.this,"Rate : " + rate + "\n Comment : " + comment,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNegativeButtonClicked() {
    }

}
