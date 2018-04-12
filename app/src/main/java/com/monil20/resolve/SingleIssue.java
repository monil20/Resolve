package com.monil20.resolve;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.monil20.resolve.adapters.CommentsListAdapter;
import com.monil20.resolve.services.SAddComment;
import com.monil20.resolve.services.SComments;
import com.monil20.resolve.services.SVotes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SingleIssue extends AppCompatActivity {

    int issueId,userId;
    int can_up = 1,can_down = 1;
    ImageView issueImg;
    TextView issueType, issueSubmittedBy, issueDsc, votes, textView5;
    EditText commenttext;
    Button add;
    ListView commentList;
    ImageView up,down;
    ArrayList<String> comments;
    ArrayList<Integer> userIds;
    SVotes voteservice;
    SAddComment commentservice;
    SComments service;

    CommentsListAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_issue);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent intent = getIntent();
        issueId = Integer.parseInt(intent.getStringExtra("issueId"));
        userId = Integer.parseInt(intent.getStringExtra("userId"));
        Toast.makeText(this,issueId+"",Toast.LENGTH_SHORT).show();

        initialize();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.16/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        service = retrofit.create(SComments.class);
        voteservice = retrofit.create(SVotes.class);
        commentservice = retrofit.create(SAddComment.class);
        Call<String> data = service.getSingleIssue();
        Call<String> votedata = voteservice.getVotes();
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    setIssue(data.getJSONArray("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        votedata.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    setVoteCount(data.getJSONArray("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
        initComments();

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(can_up>0) {
                    int votec = (Integer.parseInt((votes.getText().toString()))+1);
                    votes.setText((Integer.parseInt(votes.getText().toString()) + 1) + "");
                    Call<String> data = voteservice.sendVotes(issueId,votec);
                    data.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                    if(can_up==2)
                        up.setImageResource(R.drawable.thumbs_up);
                    else
                        up.setImageResource(R.drawable.thumbs_up_green);
                    can_up--;
                    can_down++;
                }
                if(can_down > 0) {
                    down.setEnabled(true);
                    down.setImageResource(R.drawable.thumbs_down);
                }
                else
                    down.setEnabled(false);
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(can_down>0) {
                    int votec = (Integer.parseInt((votes.getText().toString()))-1);
                    votes.setText((Integer.parseInt(votes.getText().toString()) - 1) + "");
                    Call<String> data = voteservice.sendVotes(issueId,votec);
                    data.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                    if(can_down==2)
                    {
                        down.setImageResource(R.drawable.thumbs_down);
                    }
                    else
                        down.setImageResource(R.drawable.thumbs_down_red);
                    can_down--;
                    can_up++;
                }
                if(can_up > 0) {
                    up.setEnabled(true);
                    up.setImageResource(R.drawable.thumbs_up);
                }
                else
                    up.setEnabled(false);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment = commenttext.getText().toString();
                Call<String> data = commentservice.sendComment(comment,issueId,userId);
                data.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("XXX",response.body());
                        initComments();
                        View view = getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        commenttext.setText("");
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("XXX",call.toString());
                    }
                });
            }
        });
    }

    private void initComments() {
        Call<String> data = service.getComments();
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    setComments(data.getJSONArray("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void initIssue() {
        Call<String> data = service.getSingleIssue();
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    setIssue(data.getJSONArray("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void setComments(JSONArray listItems) {
        try {
            userIds = new ArrayList<>();
            comments = new ArrayList<>();
            for(int i = 0; i < listItems.length(); i++){
                if(listItems.getJSONObject(i).getInt("issueId") == issueId){
                    comments.add( listItems.getJSONObject(i).getString("comment"));
                    userIds.add(listItems.getJSONObject(i).getInt("userId"));
                }
            }
            adapter = new CommentsListAdapter(this,comments,userIds);
            commentList.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setVoteCount(JSONArray data) {
        try {
            for(int i = 0; i < data.length(); i++) {
                if (data.getJSONObject(i).getInt("issueId") == issueId) {
                    int votec = data.getJSONObject(i).getInt("vcount");
                    votes.setText(votec + "");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setIssue(JSONArray listItems) {
        try {
//            Toast.makeText(getApplicationContext(),String.valueOf(listItems.length()),Toast.LENGTH_SHORT).show();
            for(int i = 0; i < listItems.length(); i++){
                if(listItems.getJSONObject(i).getInt("issueId") == issueId){
                    issueType.setText(listItems.getJSONObject(i).getString("type")+"");
                    issueSubmittedBy.setText("Submitted by: "+listItems.getJSONObject(i).getString("userId")+"");
                    issueDsc.setText(listItems.getJSONObject(i).getString("dsc")+"");
                    String img = listItems.getJSONObject(i).getString("img");
                    byte[] decodedBytes = Base64.decode(img, 0);
                    Bitmap image = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    issueImg.setImageBitmap(image);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        issueImg = findViewById(R.id.img);
        issueType = findViewById(R.id.title);
        issueSubmittedBy = findViewById(R.id.submittedBy);
        issueDsc = findViewById(R.id.dsc);
        commentList = findViewById(R.id.commentsList);
        up = findViewById(R.id.up);
        down = findViewById(R.id.down);
        votes = findViewById(R.id.votecount);
        add = findViewById(R.id.add);
        commenttext = findViewById(R.id.newcomment);
        textView5 = findViewById(R.id.textView5);

        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Raleway-Regular.ttf");
        issueType.setTypeface(typeface);
        issueSubmittedBy.setTypeface(typeface);
        issueDsc.setTypeface(typeface);
        votes.setTypeface(typeface);
        add.setTypeface(typeface);
        commenttext.setTypeface(typeface);
        textView5.setTypeface(typeface);

    }

    @Override
    public void onBackPressed() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, 0);
    }
}