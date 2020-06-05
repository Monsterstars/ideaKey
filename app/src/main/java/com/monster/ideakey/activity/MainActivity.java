package com.monster.ideakey.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.monster.ideakey.R;
import com.monster.ideakey.adapter.DataItemAdapter;
import com.monster.ideakey.dao.KeyDao;
import com.monster.ideakey.database.AppDatabase;
import com.monster.ideakey.entity.IdeaKey;
import com.monster.ideakey.utils.base64Utils;
import com.monster.ideakey.utils.encryptUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.monster.ideakey.utils.HTTPUtils.post;

public class MainActivity extends AppCompatActivity {

    private static RecyclerView recyclerView;
    private static RecyclerView.Adapter dataItemAdapter;
    private static RecyclerView.LayoutManager layoutManager;
    private static List<IdeaKey> keyDataList = new ArrayList<>();


    static String name;
    static AppDatabase db;
    static byte[] desKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        name = intent.getStringExtra("password");

        try {
            DESKeySpec dks = new DESKeySpec(name.getBytes());
            SecretKey sk = SecretKeyFactory.getInstance("DES").generateSecret(dks);
            desKey = sk.getEncoded();
        } catch (Exception e) {
            System.out.println(e);
        }

        KeyDao keydao;
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "IdeaKey").allowMainThreadQueries().build();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setBackgroundColor(getResources().getColor(R.color.grey));
        setSupportActionBar(myToolbar);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        initDataItems();
        dataItemAdapter = new DataItemAdapter(keyDataList,MainActivity.this, name);
        recyclerView.setAdapter(dataItemAdapter);

                //拖动
        ItemTouchHelper.SimpleCallback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(keyDataList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(keyDataList, i, i - 1);
                    }
                }
                dataItemAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            //拖动删除
            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.drawable.delete_icon);//设置图标, 这里设为空值
                builder.setTitle("IdeaKey");
                builder.setMessage("确定删除此密码记录？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface arg0, int arg1){
                        int position = viewHolder.getAdapterPosition();
                        KeyDao keydao = db.keyDao();
                        keydao.deleteById(keyDataList.get(position).getId());
                        keyDataList.remove(position);
                        dataItemAdapter.notifyItemRemoved(position);
                        Toast.makeText(MainActivity.this,"删除成功！",Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0,int arg1){
                        recyclerView.setAdapter(dataItemAdapter);
                    }
                });
                AlertDialog b = builder.create();
                b.show();//显示对话框



            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private static void initDataItems() {
        KeyDao keydao = db.keyDao();
        keyDataList = keydao.loadAllKeys();
        try {
            for (int i = 0; i < keyDataList.size(); i++) {

                keyDataList.get(i).setKeyName(new String(encryptUtils.decrypt(base64Utils.decode(keyDataList.get(i).getKeyName().toCharArray()), desKey)));
                keyDataList.get(i).setUserName(new String(encryptUtils.decrypt(base64Utils.decode(keyDataList.get(i).getUserName().toCharArray()), desKey)));
                keyDataList.get(i).setUserPasswordOne(new String(encryptUtils.decrypt(base64Utils.decode(keyDataList.get(i).getUserPasswordOne().toCharArray()), desKey)));
                keyDataList.get(i).setUserPasswordTwo(new String(encryptUtils.decrypt(base64Utils.decode(keyDataList.get(i).getUserPasswordTwo().toCharArray()), desKey)));
                keyDataList.get(i).setUrl(new String(encryptUtils.decrypt(base64Utils.decode(keyDataList.get(i).getUrl().toCharArray()), desKey)));
                keyDataList.get(i).setDetail(new String(encryptUtils.decrypt(base64Utils.decode(keyDataList.get(i).getDetail().toCharArray()), desKey)));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.key_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                initDataItems();
                for (int i = 0; i < keyDataList.size(); i++) {
                    if (keyDataList.get(i).getKeyName().contains(query)) {//或者.getId.contains(query);
                        //查出来的滚动到最底部
                        recyclerView.scrollToPosition(i);
                        System.out.println("yes");
                        return false;
                    }
                }
                //没查出来的话
                Toast toast = Toast.makeText(MainActivity.this,
                        query + " 未查询到！", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.add_action:
                //跳转页面
                Intent intent = new Intent();
                intent.putExtra("password", name);
                intent.setClass(MainActivity.this, KeyInfoActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {

        initDataItems();
        dataItemAdapter = new DataItemAdapter(keyDataList,MainActivity.this,name);
        recyclerView.setAdapter(dataItemAdapter);
        super.onNewIntent(intent);
    }
}
