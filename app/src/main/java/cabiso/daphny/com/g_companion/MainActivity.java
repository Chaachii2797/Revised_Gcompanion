package cabiso.daphny.com.g_companion;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, MarketPlaceFragment.OnListFragmentInteractionListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageButton home, diyCom, promo, notif;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private String mUsername;
    private String mPhotoUrl;

    // private final ClarifaiClient clarifaiClient = new ClarifaiBuilder("{b7aa33dc206c40a4b9cffc09a2e72a9d}").buildSync();

    final ClarifaiClient client = new ClarifaiBuilder("b7aa33dc206c40a4b9cffc09a2e72a9d").buildSync();

//    private final ClarifaiClient clarifaiClient = new ClarifaiBuilder(API_Credentials.CLIENT_ID,
         //   API_Credentials.CLIENT_SECRET).buildSync();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Fragment fragment = new MarketPlaceFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.theFragmentFrame, fragment).addToBackStack("MarketPlaceFragment").commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        home = (ImageButton) findViewById(R.id.ibHome);
        diyCom = (ImageButton) findViewById(R.id.ibDIY);

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
        diyCom.setOnClickListener(this);


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,HomePageActivity.class);
                startActivity(intent);
            }
        });


        diyCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,DIYActivity.class);
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
                Intent intent = new Intent(MainActivity.this,AddProductActivity.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.logoff:
                FirebaseAuth.getInstance().signOut();
                Intent intent1=new Intent(this,Login.class);
                startActivity(intent1);
                return true;
            case R.id.profile:
                Intent intent2=new Intent(this,MyProfileActivity.class);
                startActivity(intent2);
                return true;
        }
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

    @Override
    public void onListFragmentInteraction(DatabaseReference ref) {
        Intent intent = new Intent(this, ProductDetailViewActivity.class);
        intent.putExtra("Product reference", ref.toString());
        startActivity(intent);
    }
}
