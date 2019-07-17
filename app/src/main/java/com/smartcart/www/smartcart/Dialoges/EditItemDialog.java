package com.smartcart.www.smartcart.Dialoges;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.storage.UploadTask;
import com.smartcart.www.smartcart.Classes.ItemsList;
import com.smartcart.www.smartcart.R;

import java.io.ByteArrayOutputStream;

import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static android.view.View.VISIBLE;

/**
 * Created by shyattoun on 12.12.2018.
 */

public class EditItemDialog extends DialogFragment {


        private static final String TAG = "myTag";
        private static final int CAMERA_REQUEST = 1888;

        public static final int MY_CAMERA_PERMISSION_CODE = 100;


        private DatabaseReference ItemsRef;
        private FirebaseStorage PostsImagesRefrence;

        private String  current_user_id;
        private ProgressBar progressBar;
        private AlertDialog.Builder alertDialog;


        private EditText desInput, prcInput, qtyInput;
        private Button updatePost, cancelPost, btnOpenCamera;
        private ImageView ItemPhoto;
        private Context mContext;
        private String itemId;

    public static EditItemDialog newInstance(String itemID,String region,String snif) {

        Bundle args = new Bundle();
        args.putString("itemID",itemID);
        args.putString("region",region);
        args.putString("snif",snif);
        EditItemDialog fragment = new EditItemDialog();
        fragment.setArguments(args);
        return fragment;
    }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_edit_layout, container, false);

            getDialog().requestWindowFeature(STYLE_NO_TITLE);
            getDialog().setCanceledOnTouchOutside(true);
            setCancelable(false);

            mContext=getActivity();

            PostsImagesRefrence = FirebaseStorage.getInstance();

            ItemsRef = FirebaseDatabase.getInstance().getReference();
            final FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
            current_user_id =user.getUid();

            findViewById(view);

            progressBar = (ProgressBar) view.findViewById(R.id.EditprogressBar);
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

            btnOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    OpenCamera();
                }
            });

            return view;
        }

        private void OpenCamera() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(mContext, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        }


        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == MY_CAMERA_PERMISSION_CODE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent cameraIntent = new
                            Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }

        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK
                    ) {

                Bitmap photo = ( Bitmap ) data.getExtras().get("data");

                try{
                    ItemPhoto.setImageBitmap(photo);

                }catch (NullPointerException e){
                    Toast.makeText(mContext,"problem " + e.getMessage(),Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onActivityResult: NullPointerExecption" + e.getMessage());

                }
            }
        }

        /**
         * customizable toast
         * @param message
         */
        private void toastMessage(String message){
            Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
        }

    public void updatePostFunc () {
        final String editDescription = desInput.getText().toString();
        final String editPrice = prcInput.getText().toString();
        final String editQuantity = qtyInput.getText().toString();

        double pricePer = Double.valueOf(editPrice);
        double quantityPer = Double.valueOf(editQuantity);
        double totalPricePerItem = pricePer* quantityPer;
        final Float totalpriceAndQuantityPerItem = Float.valueOf(String.valueOf(totalPricePerItem));

        Log.d(TAG, "onClick: Attempting to submit to database: \n" +
                "description: " + editDescription + "\n" +
                "price: " + editPrice + "\n" +
                "quantity: " + editQuantity + "\n"

        );

        String name = desInput.getText().toString();

        if (!editDescription.equals("") && !editPrice.equals("") && !editQuantity.equals("") ) {

            itemId = getArguments().getString("itemID");
            ItemPhoto.setDrawingCacheEnabled(true);
            ItemPhoto.buildDrawingCache();
            Bitmap bitmap = ItemPhoto.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            ItemPhoto.setDrawingCacheEnabled(false);
            byte [] data = baos.toByteArray();

            String path = "/images/users/" + current_user_id + "/" +name+ ".jpeg";
            StorageReference storageReference = PostsImagesRefrence.getReference(path);

            progressBar.setVisibility(VISIBLE);
            updatePost.setEnabled(false);

            UploadTask uploadTask = storageReference.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getUploadSessionUri().toString();
                    final ItemsList itemsList = new ItemsList(editDescription,editPrice,editQuantity,totalpriceAndQuantityPerItem,url,itemId);
                    ItemsRef.child("Supers").child(current_user_id)
                            .child(getArguments().getString("region"))
                            .child(getArguments().getString("snif"))
                            .child(itemId).setValue(itemsList)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.GONE);
                                    updatePost.setEnabled(true);
                                    toastMessage("The information has been saved");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    toastMessage("upload failed");
                }
            });

            dismiss();
        }

        else{
            toastMessage("fill out all the fields");
        }
    }

    private void findViewById (View view){
        updatePost = ( Button ) view.findViewById(R.id.EditbtnAddItemToList);
        cancelPost = ( Button ) view.findViewById(R.id.EditbtnCancel);
        desInput = ( EditText ) view.findViewById(R.id.etEditItemDescribtion);
        prcInput = ( EditText ) view.findViewById(R.id.etEditItemPrice);
        qtyInput = ( EditText ) view.findViewById(R.id.etEditItemQuantity);
        btnOpenCamera = ( Button ) view.findViewById(R.id.EditbtnCamera);
        ItemPhoto = ( ImageView ) view.findViewById(R.id.ivEditItemPic);
    }

    }


