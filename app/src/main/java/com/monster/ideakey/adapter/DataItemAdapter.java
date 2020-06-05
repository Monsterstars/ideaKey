package com.monster.ideakey.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.monster.ideakey.activity.IndexActivity;
import com.monster.ideakey.activity.KeyDetailActivity;
import com.monster.ideakey.dao.KeyDao;
import com.monster.ideakey.database.AppDatabase;
import com.monster.ideakey.entity.IdeaKey;
import com.monster.ideakey.R;

import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import static com.monster.ideakey.utils.HTTPUtils.post;

public class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.MyViewHolder> {

    private List<IdeaKey> keyList;
    private Context context;
    private String name;

    public DataItemAdapter(List<IdeaKey> mDataItemList, Context context) {
        this.keyList = mDataItemList;
        this.context = context;
    }

    public DataItemAdapter(List<IdeaKey> mDataItemList, Context context,String name) {
        this.keyList = mDataItemList;
        this.context = context;
        this.name = name;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public ImageView imageView;
        public  MyViewHolder(View v){
            super(v);
            textView = v.findViewById(R.id.textView);
            imageView = v.findViewById(R.id.imageView);
        }
    }

    @Override
    public DataItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.key_item_view, parent, false);
        final MyViewHolder vh = new MyViewHolder(v);
        vh.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

                        AppDatabase db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "IdeaKey").allowMainThreadQueries().build();
                        int position = vh.getAdapterPosition();
                        IdeaKey dataItem = keyList.get(position);
                        KeyDao keydao = db.keyDao();
                        keydao.deleteById(dataItem.getId());
                        keyList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context,"删除成功！",Toast.LENGTH_SHORT).show();
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
        return  vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        final IdeaKey dataItem = keyList.get(position);
        holder.textView.setText(dataItem.getKeyName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 点击事件
                Intent intent;
                intent = new Intent(context, KeyDetailActivity.class);
                intent.putExtra("password",name);
                intent.putExtra("id",dataItem.getId());
                intent.putExtra("keyName",dataItem.getKeyName());
                intent.putExtra("userName",dataItem.getUserName());
                intent.putExtra("userPasswordOne",dataItem.getUserPasswordOne());
                intent.putExtra("userPasswordTwo",dataItem.getUserPasswordTwo());
                intent.putExtra("url",dataItem.getUrl());
                intent.putExtra("detail",dataItem.getDetail());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount(){
        return keyList.size();
    }



}
