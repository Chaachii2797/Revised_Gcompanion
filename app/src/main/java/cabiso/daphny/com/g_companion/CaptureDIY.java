package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cabiso.daphny.com.g_companion.Adapter.CommunityAdapter;
import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

/**
 * Created by Lenovo on 8/22/2017.
 */


public class CaptureDIY extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference databaseReference;
    private Task<Void> materialsReference;
    private Task<Void> proceduresReference;
    private FirebaseDatabase database;
    private FirebaseAuth mFirebaseAuth;

    private FirebaseStorage mStorage;
    private StorageReference storageReference, imageRef;

    private FirebaseUser mFirebaseUser;
    private String userID;
    private String imageFileName;

    private Uri diyPictureUri;

    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 11;
    private List<String> tags = new ArrayList<>();
    private List<String> extras = new ArrayList<>();
    private List<String> validWords = new ArrayList<>();

    final ClarifaiClient client;

    public CaptureDIY() {
        client = new ClarifaiBuilder("cb169e9d3f9e4ec5a7769cc0422f3162").buildSync();
    }

    private static final int SELECT_PHOTO = 100;
    private static final int MAX_LENGTH = 100;
    private Button submitButton;
    private ImageButton btnAddMaterial, btnAddProcedure;
    private EditText name, material, procedure;
    private ImageView imgView;
    private TextView diyTags;
    private ListView materialsList, proceduresList;
    private CommunityAdapter pAdapter;
    private CommunityAdapter mAdapter;
    ArrayList<CommunityItem> itemMaterial;
    ArrayList<CommunityItem> itemProcedure;
    private List<String> diys = new ArrayList<String>();

    private ProgressDialog progressDialog;
    UploadTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_diy);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://g-companion.appspot.com/").child("diy_by_tags");

        diyTags = (TextView) findViewById(R.id.tvTag);
        name = (EditText) findViewById(R.id.add_diy_name);
        material = (EditText) findViewById(R.id.etMaterials);
        procedure = (EditText) findViewById(R.id.etProcedures);
        imgView = (ImageView) findViewById(R.id.add_product_image_plus_icon);
        materialsList = (ListView) findViewById(R.id.materialsList);
        proceduresList = (ListView) findViewById(R.id.proceduresList);
        btnAddMaterial = (ImageButton) findViewById(R.id.btnMaterial);
        btnAddProcedure = (ImageButton) findViewById(R.id.btnProcedure);
        itemMaterial = new ArrayList<>();
        itemProcedure = new ArrayList<>();
//
        mAdapter = new CommunityAdapter(getApplicationContext(), itemMaterial);
        pAdapter = new CommunityAdapter(getApplicationContext(), itemProcedure);

//            adapter = new CommunityAdapter(CaptureDIY.this, R.layout.activity_diy_data, itemMaterial);
//        materialsList.setAdapter(mAdapter);
//        proceduresList.setAdapter(pAdapter);

        getWordBank();


        btnAddMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputMaterials = material.getText().toString();
                if (inputMaterials.isEmpty()) {
                    Toast.makeText(CaptureDIY.this, "Please enter materials", Toast.LENGTH_SHORT).show();
                } else {
                    CommunityItem md = new CommunityItem(inputMaterials);
//                    ArrayList<CommunityItem> materials = new ArrayList<>();
                    itemMaterial.add(md);
                    mAdapter.notifyDataSetChanged();
                    material.setText(" ");
                }
            }
        });

        btnAddProcedure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputProcedure = procedure.getText().toString();
                if (inputProcedure.isEmpty()) {
                    Toast.makeText(CaptureDIY.this, "Please enter procedures", Toast.LENGTH_SHORT).show();
                } else {
                    CommunityItem md = new CommunityItem(inputProcedure);
//                    ArrayList<CommunityItem> procedures = new ArrayList<>();
                    itemProcedure.add(md);
                    pAdapter.notifyDataSetChanged();
                    procedure.setText(" ");
                }
            }
        });
        final ImageView addProductImagePlusIcon = (ImageView) findViewById(R.id.add_product_image_plus_icon);
        addProductImagePlusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
                dispatchTakePictureIntent();
            }
        });

        submitButton = (Button) findViewById(R.id.submit_diy);
        database = FirebaseDatabase.getInstance();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                imageRef = storageReference.child(diyPictureUri.getLastPathSegment());

                //creating and showing progress dialog
                progressDialog = new ProgressDialog(CaptureDIY.this);
                progressDialog.setMax(100);
                progressDialog.setMessage("Uploading...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                progressDialog.setCancelable(false);

                //starting upload
                uploadTask = imageRef.putFile(diyPictureUri);
                // Observe state change events such as progress, pause, and resume
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
                        Toast.makeText(CaptureDIY.this, "Error in uploading!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Float float_this = Float.valueOf(0);
                        if (userID != null) {
                            String results = " ";
//                            for(int i = 0; i < 5; i++) {
//                                for(int c = 0; c < validWords.size(); c++) {
//                                    if (tags.get(i).contains(validWords.get(c))) {
//                                        results = tags.get(i);
//                                        diyTags.setText(results);
//                                    } else {
//                                        //invalid words
//                                    }
//                                }
                            for (int i = 0; i < 10; i++) {
                                for(int c = 0; c < validWords.size(); c++){
                                    if(tags.get(i).contains(validWords.get(c))){
                                        results = tags.get(i);

                                        //results += "\n" + tags.get(i);
                                        diyTags.setText(results);
                                    }else{
                                        //invalid words
                                    }
                                }
//                                if (results.equals("no person")) {
//                                    diyTags.setText("");
//                                } else {
//                                    diyTags.setText(results);
//                                }
                                    String upload = databaseReference.push().getKey();

                                    Random random = new Random();
//                                String key = databaseReference.getKey();
                                    String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
                                    String productID = String.valueOf(candidateChars.charAt(random.nextInt(candidateChars.length())));

                                    // if(name.getText() == name.getText()) {

                                CommunityItem comItem = new CommunityItem();
                                String listMaterial = TextUtils.join(", ", itemMaterial);

                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");

                                    databaseReference.child(upload).setValue(new DIYnames(name.getText().toString(),
                                            taskSnapshot.getDownloadUrl().toString(), userID, results, productID,
                                            float_this, float_this, new CommunityItem(listMaterial)));

//                                String listMaterial = itemMaterial.stream().map(CommunityItem::toString).collect(Collectors.joining(", "));
                                    databaseReference.child(upload).child("materials")
                                            .setValue(new CommunityItem(listMaterial));
//                                Map<String, Object> map = new HashMap<>();
//                                map.put(upload, itemMaterial);
//                                    databaseReference.child(upload).child("materials").updateChildren(map);

                                    databaseReference.child(upload).child("procedures")
                                            .setValue(itemProcedure);
                                    // }

                            }

                            Toast.makeText(CaptureDIY.this, "Upload successful", Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder ab = new AlertDialog.Builder(CaptureDIY.this);
                                ab.setMessage("Thank you for contributing to the DIY Community!");
                                ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent in = new Intent(CaptureDIY.this, MainActivity.class);
//                                        in.putExtra("materials", itemMaterial);
//                                        in.putExtra("procedures", itemProcedure);
                                        startActivity(in);
                                    }
                                });

                            ab.create().show();
                        }
                        progressDialog.dismiss();
                    }
                });



            }
        });
    }


    public void printTags() {
        String results = "Tags: ";
        for(int i = 0; i < 10; i++) {

            for(int c = 0; c < validWords.size(); c++){
                if(tags.get(i).contains(validWords.get(c))){
                    results += "\n" + tags.get(i);
                    diyTags.setText(results);
                }else{
                    //invalid words
                }
            }
            Log.e("tags", ""+results);
            Log.d("value",extras.get(i));
        }
    }


    @Override
    public void onClick(View v) {
        if(v==submitButton){
            registerForContextMenu(submitButton);
            openContextMenu(submitButton);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId()==R.id.add_submit_diy){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public void clearFields() {
        tags.clear();
        diyTags.setText("");
        ((ImageView)findViewById(R.id.add_product_image_plus_icon)).setImageResource(android.R.color.transparent);
    }

    public void getWordBank(){
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("word_bank");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                validWords.add(dataSnapshot.getValue().toString());
                Log.d("fsdfs", dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(CaptureDIY.this,"Capture DIY!",Toast.LENGTH_SHORT).show();
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == MainActivity.RESULT_OK){

                diyPictureUri = data.getData();
                imgView.setImageURI(diyPictureUri);
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);
                imgView.setImageBitmap(bitmap);
                new AsyncTask<Bitmap, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {

                    // Model prediction
                    @Override
                    protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Bitmap... bitmaps) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 90, stream);
                        byte[] byteArray = stream.toByteArray();
                        final ConceptModel general = client.getDefaultModels().generalModel();
                        return client.getDefaultModels().generalModel().predict()
                                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray)))
                                .executeSync();
                    }

                    // Handling API response and then collecting and printing tags
                    @Override
                    protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "API contact error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final List<ClarifaiOutput<Concept>> predictions = response.get();
                        if (predictions.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "No results from API", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final List<Concept> predictedTags = predictions.get(0).data();
                        for(int i = 0; i < predictedTags.size(); i++) {
                            tags.add(predictedTags.get(i).name());
                            extras.add(String.valueOf(predictedTags.get(i).value()));

                        }
                        printTags();
                    }
                }.execute(bitmap);
            }
        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture or selection.
            Toast.makeText(getApplicationContext(), "User Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            // capture failed or did not find file.
            Toast.makeText(getApplicationContext(), "Unknown Failure. Please notify app owner.", Toast.LENGTH_SHORT).show();
        }


    }

//    private File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        imageFileName = "JPEG_"+timeStamp+"_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(imageFileName, ".jpg", storageDir );
//        diyPictureUri = Uri.fromFile(image);
//        return image;
//    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CaptureDIY.this, MainActivity.class);
        startActivity(intent);
    }

}
