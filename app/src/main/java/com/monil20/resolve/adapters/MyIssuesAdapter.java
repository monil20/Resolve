package com.monil20.resolve.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.monil20.resolve.MyCases;
import com.monil20.resolve.R;

import java.util.ArrayList;

/**
 * Created by monil20 on 4/11/18.
 */

public class MyIssuesAdapter extends BaseAdapter {
    ArrayList<String> img, title, status, date;
    Context context;
    private static LayoutInflater inflater=null;
    public  MyIssuesAdapter(MyCases myCases, ArrayList<String> img, ArrayList<String> title, ArrayList<String> status, ArrayList<String> date){

        context = myCases;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.img = img;
        this.title = title;
        this.status = status;
        this.date = date;

    }
    @Override
    public int getCount() {
        return title.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View view;
        view = inflater.inflate(R.layout.singlelistitem_mycases, null);

        holder.tvTitle = view.findViewById(R.id.textView_title);
        holder.tvStatus = view.findViewById(R.id.textView_Status);
        holder.tvDate = view.findViewById(R.id.textView_Date);
        holder.ivImg = view.findViewById(R.id.imageView_issue);

        byte[] decodedBytes = Base64.decode(img.get(position), 0);
        Bitmap image = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        holder.tvTitle.setText(title.get(position)+"");
        holder.tvStatus.setText(status.get(position)+"");
        holder.tvDate.setText(date.get(position)+"");
        holder.ivImg.setImageBitmap(image);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
            }
        });
        return view;
    }

    public class Holder{
        ImageView ivImg;
        TextView tvTitle, tvStatus, tvDate;
    }
}
