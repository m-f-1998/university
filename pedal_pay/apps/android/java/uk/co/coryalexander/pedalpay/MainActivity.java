package uk.co.coryalexander.pedalpay;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.coryalexander.pedalpay.uk.co.coryalexander.pedalpay.network.LoginResponse;
import uk.co.coryalexander.pedalpay.uk.co.coryalexander.pedalpay.network.RetrofitClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create videoview object and give it the URI to the aerial shot of edinburgh, inside the res/raw folder
        VideoView vw = (VideoView) findViewById(R.id.vidLoginBg);
        Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg);
        vw.setVideoURI(path);
        vw.start();

        Button btnLogin = (Button) findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
                EditText txtPass = (EditText) findViewById(R.id.txtPassword);
                String emailAddress = txtEmail.getText().toString();
                String password = txtPass.getText().toString();
                if(validateEmail(emailAddress)) {
                    if(validatePassword(password)) {
                        loginConfirmed(emailAddress, password);
                    } else {
                        txtPass.setError("Password must be 6 or more characters");
                        txtPass.requestFocus();
                    }

                } else {
                    txtEmail.setError("Invalid E-mail Address");
                    txtEmail.requestFocus();
                }
            }
        });

        TextView lblForgotPass = (TextView) findViewById(R.id.lblForgotPassword);
        TextView lblCreateAccount = (TextView) findViewById(R.id.lblCreateAccount);

        lblForgotPass.setMovementMethod(LinkMovementMethod.getInstance());
        lblCreateAccount.setMovementMethod(LinkMovementMethod.getInstance());

        String email = readData().split(":")[0];
        String password = "";
        if(readData().split(":").length > 1) password = readData().split(":")[1];
        String autoLog = "false";
        if(readData().split(":").length == 3) {
            autoLog = readData().split(":")[2];
        }
        if(autoLog == "true") {
           // Toast.makeText(getApplicationContext(), "AutoLog", Toast.LENGTH_LONG);
            loginConfirmed(email, password);
        }

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

    private boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        Matcher match = pattern.matcher(email);
        return match.find();
    }

    private boolean validatePassword(String pass) {
        return pass.length() >= 6;
    }

    private boolean loginConfirmed(String email, final String password) {

        Call<LoginResponse> call = RetrofitClient.getInstance().getDataService().userLogin(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                LoginResponse loginResponse = response.body();
                if(!loginResponse.isError()) {
                    CheckBox chkStayLogged = (CheckBox) findViewById(R.id.chkStayLogged);
                    if(chkStayLogged.isChecked()) {
                        saveData(String.format("%s:%s:%s", loginResponse.getMessage(), password, "true"));
                        //Toast.makeText(getApplicationContext(), String.format("%s:%s:%s", loginResponse.getMessage(), password, "true"), Toast.LENGTH_LONG).show();
                    } else {
                        saveData(String.format("%s:%s:%s", loginResponse.getMessage(), password, "false"));

                    }
                    //Toast.makeText(getApplicationContext(), loginResponse.getUserDetails().get(0).getAddressone(), Toast.LENGTH_LONG).show();

                    User user = User.getInstance();

                    user.getDetails().put("email", loginResponse.getUserDetails().get(0).getEmail());
                    user.getDetails().put("name", loginResponse.getUserDetails().get(0).getName());
                    user.getDetails().put("dob", loginResponse.getUserDetails().get(0).getDob());
                    user.getDetails().put("mobile", loginResponse.getUserDetails().get(0).getMobile());
                    user.getDetails().put("addressone", loginResponse.getUserDetails().get(0).getAddressone());
                    user.getDetails().put("addresstwo", loginResponse.getUserDetails().get(0).getAddresstwo());
                    user.getDetails().put("city", loginResponse.getUserDetails().get(0).getCity());
                    user.getDetails().put("zip", loginResponse.getUserDetails().get(0).getZip());
                    user.getDetails().put("gender", loginResponse.getUserDetails().get(0).getGender());
                    user.getDetails().put("id", "" + loginResponse.getUserDetails().get(0).getId());

                    Intent loginIntent = new Intent(getApplicationContext(), AfterLogin.class);
                    startActivity(loginIntent);
                }else{
                    EditText txtPass = (EditText) findViewById(R.id.txtPassword);
                    txtPass.setError(loginResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), "onFailure", Toast.LENGTH_LONG).show();

            }
            public void saveData(String data) {
                String fileName = "logindetails";
                FileOutputStream outputStream;

                //Encrpyt login data
                // String encryptedData = SessionStorage.encrypt(data, SessionStorage.secretKey);

                try {
                    outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                    outputStream.write(data.getBytes());
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        return false;
    }



    public static void removeUnderline(TextView textView) {
        Spannable s = (Spannable)textView.getText();
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




    private static class URLSpanNoUnderline extends URLSpan {

        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            super.updateDrawState(tp);
            tp.setUnderlineText(false);
        }
    }
}
