package com.monil20.resolve;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.monil20.resolve.services.SSubmitIssue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SubmitIssue extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener {

    EditText desc, title;
    ImageView img;
    Spinner type;
    Button submit, map, closeMapDialog;
    TextView textView, textView2, textView5;

    Typeface typeface;

    Marker myMarker;
    private GoogleMap mMap;
    private android.location.LocationManager locationManager;

    LatLng issueLoc;

    final Context context = this;

    SSubmitIssue service;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    String imgStr;
    int userId;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_issue);

        userId = Integer.parseInt(getIntent().getStringExtra("userId"));

        initialize();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.16/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        service = retrofit.create(SSubmitIssue.class);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        Call<String> data = service.getData();
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    setSpinner(data.getJSONArray("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.mapsdialog);
                dialog.setTitle("Long press");
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                locinfo = dialog.findViewById(R.id.locinfo);
//                dialog.setTitle("Long press on the maps to add a marker. Long press on the marker to change location");
                initializeMap();
                closeMapDialog = dialog.findViewById(R.id.close);
                closeMapDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        map.setText("Location recorded");
                    }
                });
                dialog.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),imgStr,Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(),type.getSelectedItem().toString(),Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(),issueLoc.latitude+"",Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(),issueLoc.longitude+"",Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(),desc.getText().toString().trim(),Toast.LENGTH_LONG).show();
                Call<String> data = service.sendData(type.getSelectedItem().toString(),issueLoc.latitude,issueLoc.longitude,desc.getText().toString().trim(),userId, imgStr, title.getText().toString().trim());
                data.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
//                        Toast.makeText(getApplicationContext(),response.body().toString(),Toast.LENGTH_LONG).show();
                        Log.d("XXX",response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_LONG).show();

                    }
                });
                Toast.makeText(getApplicationContext(),"Issue submitted succesfully!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SubmitIssue.this,Home.class);
                intent.putExtra("userId",userId+"");
                startActivity(intent);
            }
        });

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

    private void setSpinner(JSONArray listItems) {
        String [] typeArr = new String[listItems.length()];
        try {
            for(int i = 0; i < listItems.length(); i++){
                typeArr[i] = listItems.getJSONObject(i).getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,typeArr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(aa);
    }

    private void initializeMap() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initialize() {
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView5 = findViewById(R.id.textView5);
        title = findViewById(R.id.title);
        desc  = findViewById(R.id.dsc);
        img = findViewById(R.id.imageView_issue);
        type = findViewById(R.id.type);
        submit = findViewById(R.id.btnSubmit);
        map = findViewById(R.id.btnMap);

        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Raleway-Regular.ttf");
        textView.setTypeface(typeface);
        textView2.setTypeface(typeface);
        textView5.setTypeface(typeface);
        title.setTypeface(typeface);
        desc.setTypeface(typeface);
        submit.setTypeface(typeface);
        map.setTypeface(typeface);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (myMarker == null) {

                    // Marker was not set yet. Add marker:
                    myMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Your marker title")
                            .snippet("Your marker snippet"));
//                    locinfo.setText(myMarker.getPosition().latitude+", "+myMarker.getPosition().longitude);
                    issueLoc = new LatLng(myMarker.getPosition().latitude,myMarker.getPosition().longitude);
                    myMarker.setDraggable(true);

                }
                else {

                    // Marker already exists, just update it's position
                    myMarker.setPosition(latLng);
//                    locinfo.setText(myMarker.getPosition().latitude+", "+myMarker.getPosition().longitude);
                    issueLoc = new LatLng(myMarker.getPosition().latitude,myMarker.getPosition().longitude);

                }
            }
        });
        googleMap.setOnMarkerDragListener(this);
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
//        locinfo.setText(marker.getPosition().latitude+", "+marker.getPosition().longitude);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
//        locinfo.setText(marker.getPosition().latitude+", "+marker.getPosition().longitude);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
//        locinfo.setText(marker.getPosition().latitude+", "+marker.getPosition().longitude);
        issueLoc = new LatLng(myMarker.getPosition().latitude,myMarker.getPosition().longitude);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(SubmitIssue.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(SubmitIssue.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        byte[] imgBytes = bytes.toByteArray();
        imgStr = Base64.encodeToString(imgBytes,Base64.DEFAULT);
        /*File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        img.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                byte[] imgBytes = bytes.toByteArray();
                imgStr = Base64.encodeToString(imgBytes,Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        img.setImageBitmap(bm);
    }

}
