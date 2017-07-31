package cabiso.daphny.com.g_companion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageButton home, diyCom, promo, notif;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistener;
    private String userid;
    private String loggedIn;

    ImageView imageView;
    private TextView textViewTag;


    // private final ClarifaiClient clarifaiClient = new ClarifaiBuilder("{b7aa33dc206c40a4b9cffc09a2e72a9d}").buildSync();

    private final ClarifaiClient clarifaiClient = new ClarifaiBuilder(API_Credentials.CLIENT_ID,
            API_Credentials.CLIENT_SECRET).buildSync();

    private TextView tagText;
    private ArrayList<String> tags = new ArrayList<>();

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    static final int CAPTURE_IMAGE_CODE = 2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewTag = (TextView) findViewById(R.id.tvTag);

        //   mAuth = FirebaseAuth.getInstance();
        // final FirebaseUser user = mAuth.getCurrentUser();
        //userid = user.getUid();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        home = (ImageButton) findViewById(R.id.ibHome);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        home.setOnClickListener(this);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,HomePageActivity.class);
                startActivity(intent);
            }
        });



        fab1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override

            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ImageRecognitionTags.class);
                startActivity(intent);

                //   TODO: Snack bar for camera permission
                Snackbar.make(view, "Waiting.......", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override

            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CaptureDIY.class);
                startActivity(intent);

                //   TODO: Snack bar for camera permission
                Snackbar.make(view, "Waiting.......", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UploadImage.class);
                startActivity(intent);

                //   TODO: Snack bar for camera permission
                Snackbar.make(v, "Waiting.......", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:
                animateFAB();
                break;
            case R.id.fab1:

                Log.d("CaptureDIY", "Fab 1");
                break;
            case R.id.fab2:

                Log.d("CaptureDIY", "Fab 2");
                break;
            case  R.id.fab3:
                Log.d("UploadImage", "Fab 3");
                break;
        }
    }

    public void animateFAB(){
        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
            Log.d("CaptureDIY", "close");
        } else {
            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
            Log.d("CaptureDIY","open");

        }
    }

    public void clearFields() {
        tags.clear();
        textViewTag.setText("");
        ((ImageView)findViewById(R.id.imgPhotoSaver)).setImageResource(android.R.color.transparent);
    }

    public void printTags() {
        String results = "First 3 tags: ";
        for(int i = 0; i < 3; i++) {
            results += "\n" + tags.get(i);
        }
        textViewTag.setText(results);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputStream inStream = null;


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
            }
        }
        if (requestCode == CAPTURE_IMAGE_CODE) {
            if (resultCode == MainActivity.RESULT_OK){
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        display(item.getItemId());
        return true;
    }
    public void display(int itemID){
        android.support.v4.app.FragmentTransaction ft;
        switch (itemID){
            case R.id.nav_profile:
                Intent prof = new Intent(MainActivity.this,Profile.class);
                startActivity(prof);
                break;
            case R.id.nav_chat:
                Intent chat = new Intent(MainActivity.this,Messaging.class);
                startActivity(chat);
                break;
            case R.id.nav_calendar:
                Intent calendar = new Intent(MainActivity.this,Calendar.class);
                startActivity(calendar);
                break;
            case R.id.nav_wishlist:
                Intent wishlist = new Intent(MainActivity.this,Wishlists.class);
                startActivity(wishlist);
                break;
            case R.id.nav_report:
                Intent sales = new Intent(MainActivity.this,SalesReport.class);
                startActivity(sales);
                break;
            case R.id.nav_about:
                Intent about = new Intent(MainActivity.this,About.class);
                startActivity(about);
                break;
            case R.id.nav_logout:
                Intent logout = new Intent(MainActivity.this,WelcomeActivity.class);
                startActivity(logout);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

}
