package com.monster.ideakey.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.monster.ideakey.R;
import com.monster.ideakey.dao.KeyDao;
import com.monster.ideakey.database.AppDatabase;
import com.monster.ideakey.entity.IdeaKey;
import com.monster.ideakey.utils.base64Utils;
import com.monster.ideakey.utils.encryptUtils;

import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import static com.monster.ideakey.utils.generatePasswordUtils.getRandomString;

public class KeyInfoActivity extends AppCompatActivity {

    String name;
    byte[] desKey = null;

    EditText inputKeyName;
    EditText inputAccount ;
    EditText inputPassword1 ;
    EditText inputPassword2 ;
    EditText inputURL ;
    EditText inputDetail ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_info);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputKeyName = findViewById(R.id.inputKeyName);
        inputAccount = findViewById(R.id.inputAccount);
        inputPassword1 = findViewById(R.id.inputPassword1);
        inputPassword2 = findViewById(R.id.inputPassword2);
        inputURL = findViewById(R.id.inputURL);
        inputDetail = findViewById(R.id.inputDetail);
        inputAccount.setSaveEnabled(false);
        inputPassword1.setSaveEnabled(false);
        inputPassword2.setSaveEnabled(false);
        inputURL.setSaveEnabled(false);
        inputDetail.setSaveEnabled(false);
        inputKeyName.setSaveEnabled(false);

        final Intent intent = getIntent();
        final Long id = intent.getLongExtra("id",-1);
        name = intent.getStringExtra("password");


        try {
            DESKeySpec dks = new DESKeySpec(name.getBytes());
            SecretKey sk = SecretKeyFactory.getInstance("DES").generateSecret(dks);
            desKey = sk.getEncoded();
        }
        catch (Exception e){
            System.out.println(e);
        }

        final AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"IdeaKey").allowMainThreadQueries().build();

        if (id != -1) {

            create2(intent);

        }

        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String KeyName = inputKeyName.getText().toString();
                String Account = inputAccount.getText().toString();
                String Password1 = inputPassword1.getText().toString();
                String Password2 = inputPassword2.getText().toString();
                String URL = inputURL.getText().toString();
                String Detail = inputDetail.getText().toString();

                if (id != -1) {
                    IdeaKey ideaKey;
                    KeyDao keyDao = db.keyDao();
                    ideaKey = keyDao.loadOneIdeaKey(id);
                    ideaKey.setKeyName(KeyName);
                    ideaKey.setUserName(Account);
                    ideaKey.setUrl(URL);
                    ideaKey.setUserPasswordOne(Password1);
                    ideaKey.setUserPasswordTwo(Password2);
                    ideaKey.setDetail(Detail);
                    update(db,desKey,ideaKey);
                }else {
                    encode(db, desKey, new IdeaKey(KeyName, Account, Password1, Password2, URL, Detail));
                }
//                MainActivity.fresh();
                Intent intent2 = new Intent();
                intent2.putExtra("password", name);
                intent2.setClass(KeyInfoActivity.this, MainActivity.class);
                startActivity(intent2);
                finish();
            }
        });

        Button autoProduceBtn1 = (Button)findViewById(R.id.autoProduceBtn1);
        autoProduceBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputPassword1 = findViewById(R.id.inputPassword1);
                inputPassword1.setText(getRandomString());
            }
        });

        Button autoProduceBtn2 = (Button)findViewById(R.id.autoProduceBtn2);
        autoProduceBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputPassword2 = findViewById(R.id.inputPassword2);
                inputPassword2.setText(getRandomString());
            }
        });
    }

    public void create2(Intent intent){

        Long id = intent.getLongExtra("id",-1);
        String keyName = intent.getStringExtra("keyName");
        String userName = intent.getStringExtra("userName");
        String userPasswordOne = intent.getStringExtra("userPasswordOne");
        String userPasswordTwo = intent.getStringExtra("userPasswordTwo");
        String url = intent.getStringExtra("url");
        String detail = intent.getStringExtra("detail");

        inputKeyName.setText(keyName);
        inputAccount.setText(userName);
        inputPassword1.setText(userPasswordOne);
        inputPassword2.setText(userPasswordTwo);
        inputURL.setText(url);
        inputDetail.setText(detail);

    }

    private void update(AppDatabase db, byte[] desKey, IdeaKey ideaKey){
        KeyDao keydao = db.keyDao();
        try {
            System.out.println(Arrays.toString(ideaKey.getKeyName().getBytes()));
            ideaKey.setKeyName(base64Utils.encode(encryptUtils.encrypt(ideaKey.getKeyName().getBytes(), desKey)));
            ideaKey.setUserName(base64Utils.encode(encryptUtils.encrypt(ideaKey.getUserName().getBytes(), desKey)));
            ideaKey.setUserPasswordOne(base64Utils.encode(encryptUtils.encrypt(ideaKey.getUserPasswordOne().getBytes(), desKey)));
            ideaKey.setUserPasswordTwo(base64Utils.encode(encryptUtils.encrypt(ideaKey.getUserPasswordTwo().getBytes(), desKey)));
            ideaKey.setUrl(base64Utils.encode(encryptUtils.encrypt(ideaKey.getUrl().getBytes(), desKey)));
            ideaKey.setDetail(base64Utils.encode(encryptUtils.encrypt(ideaKey.getDetail().getBytes(), desKey)));
            keydao.updateIdeaKey(ideaKey);
        }
        catch (Exception e){
            System.out.println("错误");
            System.out.println(e);
        }
    }

    private void encode(AppDatabase db, byte[] desKey, IdeaKey ideaKey){
        System.out.println(db);
        System.out.println(desKey);
        System.out.println(ideaKey.toString());
        KeyDao keydao = db.keyDao();
        try {
            ideaKey.setKeyName(base64Utils.encode(encryptUtils.encrypt(ideaKey.getKeyName().getBytes(), desKey)));
            ideaKey.setUserName(base64Utils.encode(encryptUtils.encrypt(ideaKey.getUserName().getBytes(), desKey)));
            ideaKey.setUserPasswordOne(base64Utils.encode(encryptUtils.encrypt(ideaKey.getUserPasswordOne().getBytes(), desKey)));
            ideaKey.setUserPasswordTwo(base64Utils.encode(encryptUtils.encrypt(ideaKey.getUserPasswordTwo().getBytes(), desKey)));
            ideaKey.setUrl(base64Utils.encode(encryptUtils.encrypt(ideaKey.getUrl().getBytes(), desKey)));
            ideaKey.setDetail(base64Utils.encode(encryptUtils.encrypt(ideaKey.getDetail().getBytes(), desKey)));
            keydao.insertOneIdeaKey(ideaKey);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
