package cabiso.daphny.com.g_companion;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Recommend.Bottle_Recommend;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.api.request.input.SearchClause;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

/**
 * Created by Lenovo on 7/30/2017.
 */

public class ImageRecognitionTags extends AppCompatActivity{

    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;

    private ImageView imageView;
    private Button diyBtn;
    private TextView tvTag, tv_category;

    private List<String> tags = new ArrayList<>();
    private List<String> extras = new ArrayList<>();
    private List<String> validWords = new ArrayList<>();
    final ClarifaiClient client;
    String CURRENT_MODEL;

    public ImageRecognitionTags() {
        client = new ClarifaiBuilder("cb169e9d3f9e4ec5a7769cc0422f3162").buildSync();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recog_tag);


        //  firstFrame = (FrameLayout)findViewById(R.id.FirstFrame);
        diyBtn = (Button)findViewById(R.id.btnDIY);
        imageView = (ImageView)findViewById(R.id.imgPhotoSaver);
        tvTag = (TextView) findViewById(R.id.tvTag);
        //tv_category = (TextView) findViewById(R.id.tvCategory);

        getWordBank();
        diyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String results = " ";
                for(int i = 0; i < 10; i++) {
                    results += " "+tags.get(i);
                    Log.e("RESULT: ",""+results);
                    Intent intent = new Intent(ImageRecognitionTags.this,Bottle_Recommend.class);
                    intent.putExtra("result_tag", results);
                    startActivity(intent);
                }

            }
        });
        dispatchTakePictureIntent();

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public void clearFields() {
        tags.clear();
        tvTag.setText("");
        ((ImageView)findViewById(R.id.imgPhotoSaver)).setImageResource(android.R.color.transparent);
    }

    public void printTags() {
        String results = "Tags: ";

//        if(!tags.get(0).equals("no person")){
//            tv_category.setText(tags.get(0));
//        }else{
//            tv_category.setText(tags.get(1));
//        }

        for(int i = 0; i < 10; i++) {

            for(int c = 0; c < validWords.size(); c++){
                if(tags.get(i).contains(validWords.get(c))){
                    results += "\n" + tags.get(i);
                    Log.e("tags_here", ""+results);
                    Log.e("value_here",extras.get(i));
                    tvTag.setText(results);

                }else{
                    //invalid words
                }
            }
            Log.e("tags", ""+results);
            Log.e("value",extras.get(i));

        }
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == MainActivity.RESULT_OK){
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);
                imageView.setImageBitmap(bitmap);

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

                        return generalModel.predict().withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray))).executeSync();
//                        return client.getDefaultModels().generalModel().predict()
//                                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray)))
//                                .executeSync();
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

}
