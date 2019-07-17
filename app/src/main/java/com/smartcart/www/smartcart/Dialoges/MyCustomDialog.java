package com.smartcart.www.smartcart.Dialoges;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.smartcart.www.smartcart.Classes.ItemsList;
import com.smartcart.www.smartcart.R;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static android.view.View.VISIBLE;


/**
 * Created by shyattoun on 15.11.2018.
 */

public class MyCustomDialog extends DialogFragment {
    private static final String TAG = "myTag";
    private static final int CAMERA_REQUEST = 1888;
    public static final int MY_CAMERA_PERMISSION_CODE = 100;



    private DatabaseReference ItemsRef;
    private FirebaseStorage PostsImagesRefrence;

    private String current_user_id;
    private ProgressBar progressBar;

    private EditText desInput, prcInput, qtyInput;
    private Button updatePost, cancelPost, btnOpenCamera;
    private ImageView ItemPhoto;
    private Uri mImageUri;
    private Context mContext;
    private String itemId, region, chain;
    private StorageTask mUploadTask;
    private SharedPreferences sharedPreferences;
    private ContentResolver contentResolver;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_item, container, false);
        mContext = getActivity();
        sharedPreferences = mContext.getApplicationContext().getSharedPreferences("db", MODE_PRIVATE);
        region = sharedPreferences.getString("region", "random");
        chain = sharedPreferences.getString("chain", "random");

        PostsImagesRefrence = FirebaseStorage.getInstance();
        ItemsRef = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = user.getUid();

        findViewById(view);

        progressBar = ( ProgressBar ) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        cancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onclick: closing Dialog");
                getDialog().dismiss();

            }
        });

        updatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onclick: Submit pressed");
                updatePostFunc();
            }
        });

    startCamera();


        return view;
        }

    private void findViewById(View view) {
        updatePost = ( Button ) view.findViewById(R.id.btnAddItemToList);
        cancelPost = ( Button ) view.findViewById(R.id.btnCancel);
        desInput = ( EditText ) view.findViewById(R.id.etItemDescribtion);
        prcInput = ( EditText ) view.findViewById(R.id.etItemPrice);
        qtyInput = ( EditText ) view.findViewById(R.id.etItemQuantity);
        btnOpenCamera = ( Button ) view.findViewById(R.id.btnCamera);
        ItemPhoto = ( ImageView ) view.findViewById(R.id.ivItemPic);
    }


    @Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        toastMessage("camera permission approved");
        Intent cameraIntent = new
        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
        toastMessage("camera permission denied");
        }

        }


        }


@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        ItemPhoto.setImageBitmap(photo);
        }
        }


    private void toastMessage(String message){
        Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
    }

    public void startCamera() {
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(mContext, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

    }

    public void updatePostFunc (){
        final String description = desInput.getText().toString();
        final String price = prcInput.getText().toString();
        final String quantity = qtyInput.getText().toString();


        Log.d(TAG, "onClick: Attempting to submit to database: \n" +
                "description: " + description + "\n" +
                "price: " + price + "\n" +
                "quantity: " + quantity + "\n"

        );

        String name = desInput.getText().toString();

        if (!description.equals("") && !price.equals("") && !quantity.equals("")) {

            double pricePer = Double.valueOf(price);
            double quantityPer = Double.valueOf(quantity);
            double totalPricePerItem = pricePer * quantityPer;
            final Float totalpriceAndQuantityPerItem = ( float ) (totalPricePerItem * 1000.0 / 1000.0);


            itemId = ItemsRef.push().getKey();
            ItemPhoto.setDrawingCacheEnabled(true);
            ItemPhoto.buildDrawingCache();
            Bitmap bitmap = ItemPhoto.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            ItemPhoto.setDrawingCacheEnabled(false);
            byte[] data = baos.toByteArray();





            String path = "/images/users/" + current_user_id + "/" + name + ".jpg";
            StorageReference storageReference = PostsImagesRefrence.getReference(path);

            progressBar.setVisibility(VISIBLE);
            updatePost.setEnabled(false);

            UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String imageRef = taskSnapshot.getUploadSessionUri().toString();
                    final ItemsList itemsList = new ItemsList(description, price, quantity, totalpriceAndQuantityPerItem, imageRef, itemId);

                    ItemsRef.child("Supers").child(current_user_id)
                            .child(region)
                            .child(chain)
                            .child(itemId).setValue(itemsList)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.GONE);
                                    updatePost.setEnabled(true);
                                    toastMessage("הנתונים נשמרו בהצלחה!");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            updatePost.setEnabled(true);
                            toastMessage("העלאה נכשלה :-(");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d("ErrorAttack", "The error is: " + e.getMessage());
                            toastMessage("העלאה נכשלה");
                        }
                    });
                }

            });

            dismiss();
        } else {
            toastMessage("אנא מלא את כל השדות");
        }
    }

}








