package cabiso.daphny.com.g_companion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Recommend.Bottle_Recommend;
import cabiso.daphny.com.g_companion.Recommend.Cup_Recommend;
import cabiso.daphny.com.g_companion.Recommend.Glass_Recommend;
import cabiso.daphny.com.g_companion.Recommend.Paper_Recommend;
import cabiso.daphny.com.g_companion.Recommend.Rubber_Recommend;
import cabiso.daphny.com.g_companion.Recommend.Utensils_Recommend;
import cabiso.daphny.com.g_companion.Recommend.Wood_Recommend;
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

    private List<String> tags = new ArrayList<>();

    final ClarifaiClient client;

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


        diyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String results = " ";
                for(int i = 0; i < 5; i++) {
                    results += " " + tags.get(i);
                    tvTag.setText(results);
//                    Bundle b=new Bundle();
//                    String result_tag = null;
//                    b.putStringArray(result_tag, new String[]{tags.get(i)});
//                    Intent intent =new Intent(getApplicationContext(), Bottle_Recommend.class);
//                    intent.putExtras(b);
//                    startActivity(intent);
                    Intent intent = new Intent(ImageRecognitionTags.this,Bottle_Recommend.class);
                    intent.putExtra("result_tag",results);
                    startActivity(intent);

//
//                    if(tags.get(i).equals("bottle") || tags.get(i).equals("container")
//                            || tags.get(i).equals("plastic")|| tags.get(i).equals("gallon")|| tags.get(i).equals("jug")) {
//                        //Toast.makeText(getApplication(), "Result:" + results, Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(ImageRecognitionTags.this, Bottle_Recommend.class);
//                        Toast.makeText(ImageRecognitionTags.this, "bottle", Toast.LENGTH_SHORT).show();
//                        startActivity(intent);
//
//                    }else if(tags.get(i).equals("paper") || tags.get(i).equals("document") || tags.get(i).equals("newspaper")
//                            || tags.get(i).equals("sheet")|| tags.get(i).equals("form")|| tags.get(i).equals("magazines")
//                            || tags.get(i).equals("notebook") || tags.get(i).equals("book") || tags.get(i).equals("book bindings")
//                            || tags.get(i).equals("page")) {
//                        //Toast.makeText(getApplication(), "Result:" + results, Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(ImageRecognitionTags.this, Paper_Recommend.class);
//                        Toast.makeText(ImageRecognitionTags.this, "paper", Toast.LENGTH_SHORT).show();
//                        startActivity(intent);
//
//                    }else if(tags.get(i).equals("wood")) {
//                        //Toast.makeText(getApplication(), "Result:" + results, Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(ImageRecognitionTags.this, Wood_Recommend.class);
//                        Toast.makeText(ImageRecognitionTags.this, "wood", Toast.LENGTH_SHORT).show();
//                        startActivity(intent);
//
//                    }else if(tags.get(i).equals("cup") || tags.get(i).equals("coffee")) {
//                        //Toast.makeText(getApplication(), "Result:" + results, Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(ImageRecognitionTags.this, Cup_Recommend.class);
//                        Toast.makeText(ImageRecognitionTags.this, "cup", Toast.LENGTH_SHORT).show();
//                        startActivity(intent);
//
//                    }else if(tags.get(i).equals("fork") || tags.get(i).equals("spoon") || tags.get(i).equals("plate")
//                            || tags.get(i).equals("equipment") || tags.get(i).equals("cooking") || tags.get(i).equals("kitchenware")
//                            || tags.get(i).equals("pan")) {
//                        //Toast.makeText(getApplication(), "Result:" + results, Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(ImageRecognitionTags.this, Utensils_Recommend.class);
//                        Toast.makeText(ImageRecognitionTags.this, "utensils", Toast.LENGTH_SHORT).show();
//                        startActivity(intent);
//
//                    }else if(tags.get(i).equals("glass") || tags.get(i).equals("drink") || tags.get(i).equals("wine")) {
//                        //Toast.makeText(getApplication(), "Result:" + results, Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(ImageRecognitionTags.this, Glass_Recommend.class);
//                        Toast.makeText(ImageRecognitionTags.this, "glass", Toast.LENGTH_SHORT).show();
//                        startActivity(intent);
//
//                    }else if(results.equals("tire")){
//                        clearFields();
//                        Intent intent = new Intent(ImageRecognitionTags.this, Rubber_Recommend.class);
//                        Toast.makeText(ImageRecognitionTags.this,"tire",Toast.LENGTH_SHORT).show();
//                        startActivity(intent);
//                    }else if(tags.get(i).equals("no person") || tags.get(i).equals("abstract")){
//                        Toast.makeText(getApplication(), "No available DIY for this.", Toast.LENGTH_SHORT).show();
//
//                    }else{
//                        tvTag.setText(results);
//                        Toast.makeText(getApplication(), "No available DIY for this.", Toast.LENGTH_SHORT).show();
//
//                    }
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
        String results = "First tag: ";
        for(int i = 0; i < 5; i++) {
            results += "\n" + tags.get(i);

            tvTag.setText(results);
//            if (tags.get(i).equals("no person") || tags.get(i).equals("abstract") || tags.get(i).equals("indoors")){
//                tvTag.setText("Try Again!");
//                Toast.makeText(getApplication(), "Capture the best angle. Try Again!", Toast.LENGTH_SHORT).show();
//            }else if(tags.get(i).equals("abstract") || tags.get(i).equals("document") || tags.get(i).equals("form") ||
//                    tags.get(i).equals("sheet") || tags.get(i).equals("page") || tags.get(i).equals("bookbindings")){
//                tvTag.setText("paper");
//            }else if(tags.get(i).equals("coffee") || tags.get(i).equals("milk")){
//                tvTag.setText("cup");
//            }else if(tags.get(i).equals("drink") || tags.get(i).equals("wine")) {
//                tvTag.setText("glass");
//            }else{
//
//            }
        }
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
