package uk.co.coryalexander.pedalpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.coryalexander.pedalpay.uk.co.coryalexander.pedalpay.network.GetBookingsResponse;
import uk.co.coryalexander.pedalpay.uk.co.coryalexander.pedalpay.network.RetrofitClient;

public class ProfileActivity extends AppCompatActivity {

    private String userName = "";
    private String userEmail = "";
    private String userDob = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView navBottom = (BottomNavigationView) findViewById(R.id.navBottom);
        navBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.action_book:
                        Intent bookIntent = new Intent(getApplicationContext(), AfterLogin.class);
                        startActivity(bookIntent);

                        break;
                    case R.id.action_checkOut:
                        finish();
                        Intent checkOutIntent = new Intent(getApplicationContext(), CheckOutActivity.class);
                        startActivity(checkOutIntent);

                        break;
                    case R.id.action_profile:
                        break;
                }
                return false;
            }
        });

        TextView lblName = (TextView) findViewById(R.id.lblName);
        TextView lblEmail = (TextView) findViewById(R.id.lblEmail);
        TextView lblDob = (TextView) findViewById(R.id.lblDob);

        TextView[] finalLabels = {lblName, lblEmail, lblDob};

        for(TextView lbl : finalLabels) {
            lbl.setTextColor(R.color.colorPrimary);
        }
       // Toast.makeText(getApplicationContext(), User.getInstance().getDetails().get("email"), Toast.LENGTH_LONG).show();

        TextView lblUserEmail = (TextView) findViewById(R.id.lblUserEmail);
        lblUserEmail.setText(User.getInstance().getDetails().get("email"));

        TextView lblUserName = (TextView) findViewById(R.id.lblUserName);
        lblUserName.setText(User.getInstance().getDetails().get("name"));

        TextView lblUserDob = (TextView) findViewById(R.id.lblUserDob);
        //lblUserDob.setText(User.getInstance().getDetails().get("dob"));



        getBookings();


    }

    private void getBookings() {
        final ArrayList<String> dataSet = new ArrayList<String>();


        Call<GetBookingsResponse> call = RetrofitClient.getInstance().getDataService().getBookings(readData().split(":")[0], readData().split(":")[1]);
        call.enqueue(new Callback<GetBookingsResponse>() {
            @Override
            public void onResponse(Call<GetBookingsResponse> call, Response<GetBookingsResponse> response) {

                for(int  i = 0; i < 4; i++) {
                    int len = Integer.parseInt(response.body().getMessage().get(i).getBookend().split(" ")[1].split(":")[0]) - Integer.parseInt(response.body().getMessage().get(i).getBookstart().split(" ")[1].split(":")[0]);
                    if(response.body().getMessage().get(i).getDistance() <= 0) {
                        dataSet.add(String.format("FUTURE BOOKING \n Date/Time: %s, Length: %s hour(s)", response.body().getMessage().get(i).getBookstart(), len));

                    } else {
                        dataSet.add(String.format("Date/Time: %s, Length: %s hour(s), \n Calories Burnt: %.2f", response.body().getMessage().get(i).getBookstart(), len, response.body().getMessage().get(i).getDistance() * 52D));
                    }
                }
                TextView booking1 = (TextView) findViewById(R.id.booking1);
                booking1.setText(dataSet.get(0));

                TextView booking2 = (TextView) findViewById(R.id.booking2);
                booking2.setText(dataSet.get(1));

                TextView booking3 = (TextView) findViewById(R.id.booking3);
                booking3.setText(dataSet.get(2));

                TextView booking4 = (TextView) findViewById(R.id.booking4);
                booking4.setText(dataSet.get(3));
            }

            @Override
            public void onFailure(Call<GetBookingsResponse> call, Throwable t) {

            }
        });
    }

    private String readData() {
        String line, readData = "";
        try{
            InputStream inStream = openFileInput("logindetails");
            if(inStream != null) {
                InputStreamReader inputReader = new InputStreamReader(inStream);
                BufferedReader bufferedReader = new BufferedReader(inputReader);

                try{
                    while((line = bufferedReader.readLine()) != null) {
                        readData += line;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        //String res = SessionStorage.decrypt(readData, SessionStorage.secretKey);
        return readData;
    }

}
