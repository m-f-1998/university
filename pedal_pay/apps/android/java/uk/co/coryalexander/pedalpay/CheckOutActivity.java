package uk.co.coryalexander.pedalpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

public class CheckOutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        BottomNavigationView navBottom = (BottomNavigationView) findViewById(R.id.navBottom);
        navBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.action_book:
                        finish();
                        Intent bookIntent = new Intent(getApplicationContext(), AfterLogin.class);
                        startActivity(bookIntent);

                        break;
                    case R.id.action_checkOut:
                        break;
                    case R.id.action_profile:
                        finish();
                        Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(profileIntent);

                        break;
                }
                return false;
            }
        });


        ProgressBar progress = (ProgressBar) findViewById(R.id.prgCollect);
        progress.setVisibility(View.VISIBLE);
    }
}
