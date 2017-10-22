package cabiso.daphny.com.g_companion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cabiso.daphny.com.g_companion.Adapter.CommunityAdapter;
import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Recommend.DIYrecommend;
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
    private DatabaseReference tagReference;
    private Task<Void> materialsReference;
    private Task<Void> proceduresReference;
    private FirebaseDatabase database;
    private FirebaseAuth mFirebaseAuth;

    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private DIYrecommend diyRecommend;

    private FirebaseUser mFirebaseUser;
    private String userID;
    private String imageFileName;

    private Uri diyPictureUri;

    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 11;
    private List<String> tags = new ArrayList<>();

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_diy);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(userID);
       // tagReference = databaseReference;


        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://g-companion.appspot.com/" + "diy_by_tags_imgs");

        diyTags = (TextView) findViewById(R.id.tvTag);
        name = (EditText) findViewById(R.id.add_diy_name);
        material = (EditText) findViewById(R.id.etMaterials);
        procedure = (EditText) findViewById(R.id.etProcedures);
        imgView = (ImageView) findViewById(R.id.add_product_image_plus_icon);
        materialsList = (ListView) findViewById(R.id.materialsList);
        proceduresList = (ListView) findViewById(R.id.proceduresList);
        btnAddMaterial = (ImageButton) findViewById(R.id.btnMaterial);
        btnAddProcedure = (ImageButton) findViewById(R.id.btnProcedure);
        itemMaterial = new ArrayList<CommunityItem>();
        itemProcedure = new ArrayList<CommunityItem>();

        mAdapter = new CommunityAdapter(getApplicationContext(), itemMaterial);
        pAdapter = new CommunityAdapter(getApplicationContext(), itemProcedure);

        materialsList.setAdapter(mAdapter);
        proceduresList.setAdapter(pAdapter);


//        tagReference.child(userID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                diyRecommend = dataSnapshot.getValue(DIYrecommend.class);
//                if (diyRecommend != null) {
//
//                    //  Log.d("username", userProfileInfo.username);
//                    name.setText(diyRecommend.diyName);
//                    // Log.d("username", userProfileInfo.username);
//                    material.setText(diyRecommend.diymaterial);
//                    //Log.d("email", userProfileInfo.email);
//                    procedure.setText(diyRecommend.diyprocedure);
//                    //Log.d("profile", userProfileInfo.phone);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Log.w("Failure to read", "Failed to read value.", error.toException());
//            }
//        });

        btnAddMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputMaterials = material.getText().toString();
                if (inputMaterials.isEmpty()) {
                    Toast.makeText(CaptureDIY.this, "Please enter materials", Toast.LENGTH_SHORT).show();
                } else {
                    CommunityItem md = new CommunityItem(inputMaterials);
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

        databaseReference.child(userID).child("diy_by_tags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    CommunityItem com = postSnapshot.getValue(CommunityItem.class);
                    itemMaterial.add(com);

                    ArrayAdapter arrayAdapter = new ArrayAdapter(CaptureDIY.this, android.R.layout.simple_list_item_1,
                            itemMaterial);
                    materialsList.setAdapter(arrayAdapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        submitButton = (Button) findViewById(R.id.submit_diy);
        database = FirebaseDatabase.getInstance();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userID != null) {
                    String result = " ";
                    for (int i=0; i < 5; i++) {
                        result = tags.get(i);

//                        if(tags.get(i).equals(" ") || tags.get(i) != "no person"){
//                            result += " ";

                        databaseReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(userID);
                        databaseReference.child(tags.get(i)).push().child("diy_name").setValue(name.getText().toString());

                        databaseReference.child(tags.get(i)).child("diy_process").child("materials").setValue(itemMaterial);

                        databaseReference.child(tags.get(i)).child("diy_process").child("procedures").setValue(itemProcedure);


                    }
                    Intent intent = new Intent(CaptureDIY.this, MyDiys.class);
                    startActivity(intent);
                }
            }
        });
    }


    public void printTags() {
        String results = "Tags: ";
        for(int i = 0; i < 5; i++) {
            results += "\n" + tags.get(i);
                diyTags.setText(results);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(CaptureDIY.this,"Capture DIY!",Toast.LENGTH_SHORT).show();
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == MainActivity.RESULT_OK){
//                    diyPictureUri = data.getData();
//                    imgView.setImageURI(diyPictureUri);
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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir );
        diyPictureUri = Uri.fromFile(image);
        return image;
    }

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
