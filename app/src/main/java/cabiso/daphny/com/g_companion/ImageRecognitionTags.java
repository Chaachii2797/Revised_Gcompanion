package cabiso.daphny.com.g_companion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Recommend.RecommendActivity;
import cabiso.daphny.com.g_companion.Recommend.RecommendSaveTag;
import cabiso.daphny.com.g_companion.Recommend.Recommend_Activity;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
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
    private TextView tvTag;

    private ArrayList<String> tags = new ArrayList<>();
    private DatabaseReference databaseReference;
    private FirebaseDatabase mRef;

    final ClarifaiClient client;

    public ImageRecognitionTags() {
        client = new ClarifaiBuilder("b7aa33dc206c40a4b9cffc09a2e72a9d").buildSync();
    }


    // private final ClarifaiClient clarifaiClient = new ClarifaiBuilder(API_Credentials.CLIENT_ID,
            //API_Credentials.CLIENT_SECRET).buildSync();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recog_tag);


        //  firstFrame = (FrameLayout)findViewById(R.id.FirstFrame);
        diyBtn = (Button)findViewById(R.id.btnDIY);
        imageView = (ImageView)findViewById(R.id.imgPhotoSaver);
        tvTag = (TextView) findViewById(R.id.tvTag);


        diyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String results = " ";
                for(int i = 0; i < 1; i++) {
                    results += " " + tags.get(i);
                }


                clearFields();
                Intent intent = new Intent(ImageRecognitionTags.this, Recommend_Activity.class);
                Toast.makeText(ImageRecognitionTags.this,"chaachii",Toast.LENGTH_SHORT).show();
                startActivity(intent);

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
        String results = "First tag: ";
        for(int i = 0; i < 1; i++) {
            results += "\n" + tags.get(i);
        }
        tvTag.setText(results);
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

}
