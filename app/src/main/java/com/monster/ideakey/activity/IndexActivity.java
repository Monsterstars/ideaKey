package com.monster.ideakey.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.monster.ideakey.R;
import com.monster.ideakey.dao.KeyDao;
import com.monster.ideakey.database.AppDatabase;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.monster.ideakey.utils.HTTPUtils.post;
import static com.monster.ideakey.utils.MD5Utils.MD5;


public class IndexActivity extends AppCompatActivity {

    private final String staticPassword = "e807f1fcf82d132f9bb018ca6738a19f";
    EditText editText;

    AlertDialog.Builder builder;
    static AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        editText = findViewById(R.id.editText10);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER); //输入类型
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance()); //设置为密码输入框


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        handleSSLHandshake();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "IdeaKey").allowMainThreadQueries().build();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setBackgroundColor(getResources().getColor(R.color.grey));
        setSupportActionBar(myToolbar);

        Button button1 = (Button) findViewById(R.id.button10);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.editText10);
                String password = MD5(editText.getText().toString());

                if(password.equals(staticPassword)){
                    Intent intent = new Intent();
                    intent.putExtra("password",editText.getText().toString());
                    intent.setClass(IndexActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else {
                    builder = new AlertDialog.Builder(IndexActivity.this);
                    builder.setTitle("密码错误");
                    builder.show();
                }
            }
        });

        Button button20 = (Button) findViewById(R.id.button20);
        button20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    post("https:192.168.0.111:8899/androidServer/message", db.keyDao().loadAllKeys().toString().replace(" ", ""));
                    builder = new AlertDialog.Builder(IndexActivity.this);
                    builder.setTitle("备份成功");
                    builder.show();
                }
                catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
}

}
