package com.monster.ideakey.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.monster.ideakey.R;
import com.monster.ideakey.dao.KeyDao;
import com.monster.ideakey.database.AppDatabase;
import com.monster.ideakey.entity.IdeaKey;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class KeyDetailActivity extends AppCompatActivity {

    Button editBtn;
    ImageView deleteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_detail);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editBtn = (Button) findViewById(R.id.editBtn);
        deleteView = findViewById(R.id.delete_write);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("password");
        final Long id = intent.getLongExtra("id",-1);
        final String keyName = intent.getStringExtra("keyName");
        final String userName = intent.getStringExtra("userName");
        final String userPasswordOne = intent.getStringExtra("userPasswordOne");
        final String userPasswrodTwo = intent.getStringExtra("userPasswordTwo");
        final String url = intent.getStringExtra("url");
        final String detail = intent.getStringExtra("detail");

        TextView inputKeyName = findViewById(R.id.showKeyNameDetail);
        inputKeyName.setText(keyName);
        TextView inputAccount = findViewById(R.id.showAccountDetail);
        inputAccount.setText(userName);
        TextView inputPassword1 = findViewById(R.id.showPassword1Detail);
        inputPassword1.setText(userPasswordOne);
        TextView inputPassword2 = findViewById(R.id.showPassword2Detail);
        inputPassword2.setText(userPasswrodTwo);
        TextView inputURL = findViewById(R.id.showURLDetail);
        inputURL.setText(url);
        TextView inputDetail = findViewById(R.id.showDetail);
        inputDetail.setText(detail);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(KeyDetailActivity.this, KeyInfoActivity.class);
                intent.putExtra("password",name);
                intent.putExtra("id",id);
                intent.putExtra("keyName",keyName);
                intent.putExtra("userName",userName);
                intent.putExtra("userPasswordOne",userPasswordOne);
                intent.putExtra("userPasswordTwo",userPasswrodTwo);
                intent.putExtra("url",url);
                intent.putExtra("detail",detail);
                startActivity(intent);
            }
        });

        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(KeyDetailActivity.this);
                builder.setIcon(R.drawable.delete_icon);//设置图标, 这里设为空值
                builder.setTitle("IdeaKey");
                builder.setMessage("确定删除此密码记录？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface arg0, int arg1){
                        try {
                            DESKeySpec dks = new DESKeySpec(name.getBytes());
                            SecretKey sk = SecretKeyFactory.getInstance("DES").generateSecret(dks);
                        } catch (Exception e) {
                            System.out.println(e);
                        }

                        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "IdeaKey").allowMainThreadQueries().build();
                        KeyDao keydao = db.keyDao();
                        keydao.deleteById(id);
                        Toast.makeText(KeyDetailActivity.this,"删除成功！",Toast.LENGTH_SHORT).show();

                        Intent intent2 = new Intent();
                        intent2.putExtra("password", name);
                        intent2.setClass(KeyDetailActivity.this, MainActivity.class);
                        startActivity(intent2);
                        finish();

                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0,int arg1){
                    }
                });
                AlertDialog b = builder.create();
                b.show();//显示对话框
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
