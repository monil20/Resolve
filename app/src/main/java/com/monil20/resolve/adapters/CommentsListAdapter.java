package com.monil20.resolve.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.monil20.resolve.R;
import com.monil20.resolve.SingleIssue;

import java.util.ArrayList;

/**
 * Created by monil20 on 2/24/18.
 */

public class CommentsListAdapter extends BaseAdapter {
    ArrayList<String> comments;
    ArrayList<Integer> submittedBy;
    Context context;
    private static LayoutInflater inflater=null;
    public CommentsListAdapter(SingleIssue singleIssue, ArrayList<String> cmnts, ArrayList<Integer> submits){

        context = singleIssue;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        comments = cmnts;
        submittedBy = submits;
    }
    @Override
    public int getCount() {
        return comments.size();
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
        view = inflater.inflate(R.layout.singlelistitem_comment, null);

        holder.tvComment = view.findViewById(R.id.comment);
        holder.tvSubmittedBy = view.findViewById(R.id.username);

        holder.tvComment.setText(comments.get(position)+"");
        holder.tvSubmittedBy.setText(submittedBy.get(position)+"");

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
            }
        });
        return view;
    }

    public class Holder{
        TextView tvComment, tvSubmittedBy;
    }
}
