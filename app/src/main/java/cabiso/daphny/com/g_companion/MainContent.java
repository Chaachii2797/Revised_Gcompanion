package cabiso.daphny.com.g_companion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class MainContent extends AppCompatActivity {

    private ImageButton home;
    private ImageButton diyCom;
    private ImageButton promo;
    private ImageButton notif;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        home = (ImageButton) findViewById(R.id.ibHome);
        diyCom = (ImageButton) findViewById(R.id.ibDIY);
        promo = (ImageButton) findViewById(R.id.ibPromo);
        notif = (ImageButton) findViewById(R.id.ibNotif);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainContent.this, HomePageActivity.class);
                startActivity(intent);
            }
        });




    }
}
