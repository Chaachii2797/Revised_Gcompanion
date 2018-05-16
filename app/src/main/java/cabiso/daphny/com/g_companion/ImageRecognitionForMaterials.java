package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Adapter.NewAddedMaterial;
import cabiso.daphny.com.g_companion.Model.Constants;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.ImgRecogSetQty;
import cabiso.daphny.com.g_companion.Model.TagClass;
import cabiso.daphny.com.g_companion.Recommend.Bottle_Recommend;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import clarifai2.exception.ClarifaiException;

/**
 * Created by Lenovo on 4/27/2018.
 */

public class ImageRecognitionForMaterials extends AppCompatActivity {

    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 10;
    final ClarifaiClient client;

    private EditText etAddMaterial;
    private Spinner qtySpinner, umSpinner;
    private ImageView imageMaterial, addedMat_image;
    private ListView materialsAddedLv;
    private Button btnAddMaterial, btnForDIY;
    private ProgressDialog progressDialog;
    private TagView tagGroup;
    private ArrayList<TagClass> tagList;

    private List<String> tags = new ArrayList<>();
    private List<String> extras = new ArrayList<>();
    private List<String> validWords = new ArrayList<>();

    private ArrayList<ImgRecogSetQty> anotherMaterialSetQties;
    private ArrayList<DBMaterial> dbMaterials;
    private NewAddedMaterial newAddMatAdapter;
    private static Bitmap bitmap_transfer;

    String[] unitOfMeasurement;
    String[] quantity;

    public ImageRecognitionForMaterials() {
        client = new ClarifaiBuilder("cb169e9d3f9e4ec5a7769cc0422f3162").buildSync();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_recog_each_material);

        getWordBank();

        etAddMaterial = (EditText) findViewById(R.id.et_add_another_material);
        qtySpinner = (Spinner) findViewById(R.id.qtySpinner);
        umSpinner = (Spinner) findViewById(R.id.unitSpinner);
        imageMaterial = (ImageView) findViewById(R.id.add_material_image);
        materialsAddedLv = (ListView) findViewById(R.id.lvMaterialList);
        btnAddMaterial = (Button) findViewById(R.id.btn_add_another_material);
        btnForDIY = (Button) findViewById(R.id.btnForDIY);
        tagGroup = (TagView) findViewById(R.id.tag_group_imageRecog);

        anotherMaterialSetQties  = new ArrayList<>();
        ImgRecogSetQty item1 = new ImgRecogSetQty().setName("test11");
        ImgRecogSetQty item2 = new ImgRecogSetQty().setName("test22");
        anotherMaterialSetQties.add(item1);
        anotherMaterialSetQties.add(item2);

        quantity = getResources().getStringArray(R.array.qty);
        unitOfMeasurement = getResources().getStringArray(R.array.UM);

        etAddMaterial.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTags(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tagGroup.setOnTagLongClickListener(new TagView.OnTagLongClickListener() {
            @Override
            public void onTagLongClick(Tag tag, int position) {
                Toast.makeText(ImageRecognitionForMaterials.this, "Long Click: " + tag.text, Toast.LENGTH_SHORT).show();
            }
        });

        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                etAddMaterial.setText(tag.text);
                etAddMaterial.setSelection(tag.text.length());//to set cursor position

            }
        });
        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {

            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ImageRecognitionForMaterials.this, R.style.AppTheme);
                builder.setMessage("\"" + tag.text + "\" will be delete. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.remove(position);
                        Toast.makeText(ImageRecognitionForMaterials.this, "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();

            }
        });

        prepareTags();

        Toolbar toolbar = (Toolbar) findViewById(R.id.imToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.back_btn);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent im = new Intent(ImageRecognitionForMaterials.this,MainActivity.class);
                startActivity(im);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dbMaterials = new ArrayList<>();
        newAddMatAdapter = new NewAddedMaterial(ImageRecognitionForMaterials.this,
                R.layout.materials_added_adapter, dbMaterials);
        materialsAddedLv.setAdapter(newAddMatAdapter);

        imageMaterial.setClickable(true);
        imageMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                etAddMaterial.setEnabled(true);
                qtySpinner.setEnabled(true);
                umSpinner.setEnabled(true);
            }

        });

        etAddMaterial.setEnabled(false);
        qtySpinner.setEnabled(false);
        umSpinner.setEnabled(false);


        btnAddMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String added_tag = etAddMaterial.getText().toString().toLowerCase();
                String added_qty = qtySpinner.getSelectedItem().toString();
                String added_um = umSpinner.getSelectedItem().toString();
                String mat_image = imageMaterial.getDrawable().toString();

                if(!added_tag.isEmpty()){
                    dbMaterials.add(new DBMaterial().setName(added_tag).setUnit(added_um)
                            .setQuantity(Integer.parseInt(added_qty)));
                    newAddMatAdapter.notifyDataSetChanged();

                    etAddMaterial.setText("");
                    qtySpinner.setSelection(0);
                    umSpinner.setSelection(0);

                    imageMaterial.setImageResource(R.drawable.add);

                }else{
                    Toast.makeText(ImageRecognitionForMaterials.this, "Add material cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnForDIY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ImageRecognitionForMaterials.this, "DIY button clicked", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ImageRecognitionForMaterials.this,Bottle_Recommend.class);
                Bundle extra = new Bundle();
                extra.putSerializable("dbmaterials", dbMaterials);
                intent.putExtra("dbmaterials", extra);

                startActivity(intent);

                progressDialog = new ProgressDialog(ImageRecognitionForMaterials.this);
                progressDialog.setTitle("Processing...");
                progressDialog.setMessage("Please wait loading DIYs...");
                progressDialog.show();
            }
        });



    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

        }
    }

    public void getWordBank() {
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

    public void printTags() {
        String results = "";

        dbMaterials.clear();
        for (int in = 0; in < 2; in++) {
            for (int ci = 0; ci < validWords.size(); ci++) {
                if (tags.get(in).contains(validWords.get(ci))) {
                    results += "\n" + tags.get(in);
                    etAddMaterial.setText(results);
                   // anotherMaterialSetQties.add(new ImgRecogSetQty().setName(results));
//                    addMaterial.setText(results);
                    Log.e("imageRecogItem", results);
                    //dbMaterials.add(new DBMaterial().setName(tags.get(in)));
                } else {
                    //invalid words
                }
            }
        }

        newAddMatAdapter.notifyDataSetChanged();
        Log.e("imageRecogItemCount", anotherMaterialSetQties.size() + " ");
//        imgRecogSetQtyAdapter.notifyDataSetChanged();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == MainActivity.RESULT_OK){
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);

                imageMaterial.setImageBitmap(bitmap);

                //Ika pic ma update tanan naa sa listview sht
//                imageMaterial.buildDrawingCache();
//                setBitmap_transfer(imageMaterial.getDrawingCache());

                constan.photoMap = bitmap;



                //Ma pass deretso ang image, dli pa siya ma set sa image view
//                Intent in = new Intent(this,ImageRecognitionForMaterials.class);
//                Drawable drbl= imageMaterial.getDrawable();
//                Bitmap bit = ((BitmapDrawable)drbl).getBitmap();
//                in.putExtra("Bitmap",bit);
//                Log.e("Bitmap", bit.toString());
//                startActivity(in);



                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
//                Uri tempUri = getImageUri(getApplicationContext(), bmp);
//                Log.e("mat_URL", tempUri.toString());

                Toast.makeText(this, " " + bmp.toString() , Toast.LENGTH_SHORT).show();
                Log.e("mat_URL", bitmap.toString());

                new AsyncTask<Bitmap, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
                    // Model prediction
                    @Override
                    protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Bitmap... bitmaps) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 90, stream);

                        byte[] byteArray = stream.toByteArray();

                        final ConceptModel generalModel = client.getDefaultModels().generalModel();
//                        final Concept diy = client.getConceptByID("trash_DIY").executeSync().get().asConcept();

//                        ClarifaiResponse<List<Concept>> card = client.searchConcepts(String.valueOf(SearchClause.matchConcept(Concept
//                                .forName("cardboard")))).getPage(0).executeSync();

                        return generalModel.predict().withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray)))
                                .executeSync();

//                        return client.getDefaultModels().generalModel().predict()
//                                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray)))
//                                .executeSync();

                    }


                    // Handling API response and then collecting and printing tags
                    @Override
                    protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
                        try{
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
//                                ImgRecogSetQty imgRecogSetQty = new ImgRecogSetQty();
//                                imgRecogSetQty.setName(predictedTags.get(i).name());
//                                anotherMaterialSetQties.add(imgRecogSetQty);
                            }
                            //printTags();

                        }catch (ClarifaiException ex){
                            ex.getMessage();
                        }
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
//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }

    //Materials Tags
    private void prepareTags() {
        tagList = new ArrayList<>();
        JSONArray jsonArray;
        JSONObject temp;
        try {
            jsonArray = new JSONArray(Constants.MATERIALS);
            for (int i = 0; i < jsonArray.length(); i++) {
                temp = jsonArray.getJSONObject(i);
                tagList.add(new TagClass(temp.getString("code"), temp.getString("name")));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setTags(CharSequence cs) {
        /**
         * for empty edittext
         */
        if (cs.toString().equals("")) {
            tagGroup.addTags(new ArrayList<Tag>());
            return;
        }

        String text = cs.toString();
        ArrayList<Tag> tags = new ArrayList<>();
        Tag tag;

        for (int i = 0; i < tagList.size(); i++) {
            if (tagList.get(i).getName().toLowerCase().startsWith(text.toLowerCase())) {
                tag = new Tag(tagList.get(i).getName());
                tag.radius = 10f;
                tag.layoutColor = Color.parseColor(tagList.get(i).getColor());
                if (i % 2 == 0) // you can set deletable or not
                    tag.isDeletable = true;
                tags.add(tag);
            }
        }
        tagGroup.addTags(tags);

    }



//    public static Bitmap getBitmap_transfer() {
//        return bitmap_transfer;
//    }
//
//    public static void setBitmap_transfer(Bitmap bitmap_transfer_param) {
//        bitmap_transfer = bitmap_transfer_param;
//    }

    public static class constan {
        public static Bitmap photoMap = null;
    }

}
