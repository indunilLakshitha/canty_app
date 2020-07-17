package com.af.canty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DetailUpload extends AppCompatActivity {
    private StorageReference Folder;
    private Button getLoc, submit,uploadpic;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ImageView imageView;
    TextView lat,lon,addres,imageurl,imagename;
    private ImageButton filemanager,camera;
    EditText nic,txtdata;
    FirebaseDatabase database;
    FloatingActionButton view_location;
    DatabaseReference myRef;
    Uri fileUri;
    Uri fileimageback;
    private Dialog choosedialog;


    private static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorageRef;
    private  ResultReceiver resultReceiver;
    private static final int REQUEST_CODE_LOCATION_PERMISSION =1;
    private static final int MY_CAMERA_REQUEST_CODE = 1000 ;
    ProgressBar progressBar;
    private static final int ImageBack=1;
    int angle=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_upload);
        getLoc = findViewById(R.id.btn_getloc);



        storageReference=FirebaseStorage.getInstance().getReference("Images");
        databaseReference=FirebaseDatabase.getInstance().getReference("Images");

        txtdata=findViewById(R.id.editText2);

        nic = findViewById(R.id.txt_nic);
        progressBar=findViewById(R.id.progressBar);
        lat=findViewById(R.id.txtLang);
        lon=findViewById(R.id.txtlong);
        addres=findViewById(R.id.txt_address);
        view_location=findViewById(R.id.btn_viw_loc);
        submit=findViewById(R.id.submit);
        uploadpic=findViewById(R.id.upload);

        imageView=findViewById(R.id.imgview);


        mStorageRef = FirebaseStorage.getInstance().getReference();

        Folder=FirebaseStorage.getInstance().getReference().child("imagefolder");

        resultReceiver=new AddressResultReceiver(new Handler());



        view_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadMap();
            }
        });

        choosedialog=new Dialog(DetailUpload.this);
        choosedialog.setContentView(R.layout.choose_option);
        choosedialog.setCancelable(true);
        choosedialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        choosedialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        camera=choosedialog.findViewById(R.id.cam_btn);



        getLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION
                )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            DetailUpload.this,
                            new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                            }else {
                    getCurrentLocation();

                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                fileUri = getOutputMediaFileUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
                startActivityForResult(intent,MY_CAMERA_REQUEST_CODE);

            }
        });
        uploadpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitDetails();


            }
        });


    }
    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "canty");
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdir())
                return null;
        }
        String time_stamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath()+File.separator+"IMG_"
                +time_stamp+"_"+new Random().nextInt()+".jpg");
        return mediaFile;
    }
    public String GetFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
        }else {
            Toast.makeText(DetailUpload.this,"access denied",Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {

        progressBar.setVisibility(View.VISIBLE);
        final LocationRequest locationRequest=new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(DetailUpload.this)
                .requestLocationUpdates(locationRequest , new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(DetailUpload.this)
                                .removeLocationUpdates(this);
                        if(locationRequest!=null && locationResult.getLocations().size()>0){
                            int latestLocationIndex =locationResult.getLocations().size()-1;
                            double lattude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            Toast.makeText(DetailUpload.this,String.valueOf(lattude),Toast.LENGTH_SHORT).show();
                            lon.setText(String.valueOf(longitude));
                            lat.setText(String.valueOf(lattude));
                            activeSubmit();
                            Location location=new Location("providerNA");
                            location.setLatitude(lattude);
                            location.setLongitude(longitude);
                            fetctAddressFromLatLong(location);
                        }



                    }
                }, Looper.getMainLooper());

    }
    private  void fetctAddressFromLatLong(Location location){
        Intent intent =new Intent(this,FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        startService(intent);

    }
    private  class AddressResultReceiver extends ResultReceiver{


        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode ==Constants.SUCCESS_RESULT){
                Toast.makeText(DetailUpload.this,resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();
                addres.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                progressBar.setVisibility(View.GONE);

            }else {
                Toast.makeText(DetailUpload.this,resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();

            }
        }
    }

    protected void activeSubmit(){
        if(lon.length()>0 && nic.length()>=9){
            submit.setEnabled(true);
            view_location.setVisibility(View.VISIBLE);
        }
    }




    public void submitDetails(){


        if (fileUri !=null) {
            progressBar.setVisibility(View.VISIBLE);
            StorageReference storageReference1 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(fileUri));
            storageReference1.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String TempImageName = txtdata.getText().toString().trim();

                            progressBar.setVisibility(View.GONE);
                            @SuppressWarnings("VisibleForTests")
                            Details Imageuploadinfo = new Details(TempImageName, taskSnapshot.getUploadSessionUri().toString());


                            String ImageuploadId = databaseReference.push().getKey();
                            databaseReference.child(ImageuploadId).setValue(Imageuploadinfo);

                            String nicmo = nic.getText().toString();
                            String la = lat.getText().toString();
                            String l = lon.getText().toString();
                            String ad = addres.getText().toString();
                            String name = nic.getText().toString();
                            String imagurl =taskSnapshot.getUploadSessionUri().toString();
                            Details details = new Details(l, la, nicmo, ad, name, imagurl);
                            database = FirebaseDatabase.getInstance();
                            myRef = database.getReference("details");
                            myRef.child(nicmo).setValue(details);

                            Toast.makeText(DetailUpload.this, "upload successful", Toast.LENGTH_SHORT).show();


                        }
                    });



        }


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

if (!(fileUri ==null)) {
    if (requestCode == MY_CAMERA_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            ExifInterface ei = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
                ei = new ExifInterface(getContentResolver().openInputStream(fileUri));

                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotateBitmap = null;
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotateBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotateBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotateBitmap = rotateImage(bitmap, 270);
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotateBitmap = bitmap;
                        break;


                }
                imageView.setImageBitmap(bitmap);
                choosedialog.dismiss();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}





    }

    private Bitmap rotateImage(Bitmap bitmap, int i) {
        Matrix matrix = new Matrix();
        matrix.postRotate(i);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

    public void loadMap(){
          String lang= lat.getText().toString();
          String lonti=lon.getText().toString();
        Uri gmmIntentUri = Uri.parse("geo:"+lang+","+lonti);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }
}
