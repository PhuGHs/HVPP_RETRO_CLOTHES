package com.example.ecommerce_hvpp.fragments.customer_fragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.ecommerce_hvpp.R;
import com.example.ecommerce_hvpp.model.User;
import com.example.ecommerce_hvpp.util.CustomComponent.CustomToast;
import com.example.ecommerce_hvpp.viewmodel.Customer.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFrament#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFrament extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditProfileFrament() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFrament.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFrament newInstance(String param1, String param2) {
        EditProfileFrament fragment = new EditProfileFrament();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private NavController navController;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ProfileViewModel viewModel;
    private ImageButton back_Account_btn;
    private String name;
    private String datebirth;
    private String address;
    private String email;
    private String imagePath;
    private TextView name_tv;
    private EditText name_edt;
    private EditText datebirth_edt;
    private EditText email_edt;
    private ImageView ava_image;
    private Button edit_profile_btn;
    private Button change_ava_btn;
    private Button cancel_btn;
    private Button save_btn;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> thumbnailLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Uri thumbnailImage;
    private ContentResolver contentResolver;
    private String path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if(isGranted) {
                openGallery();
            } else {
                // do nothing
                //show message (disabled)
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == FragmentActivity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    if (data.getClipData() != null) {
                        // Image was selected
                        processSelectedImage(data.getData());
                    }
                }
            }
        });

        thumbnailLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == FragmentActivity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    thumbnailImage = data.getData();
                    Log.d(TAG, "link" + thumbnailImage);
                    Glide.with(getContext()).load(thumbnailImage).fitCenter().into(ava_image);
                    //viewModel.uploadAvatar(contentResolver, user, thumbnailImage);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.actitvity_user_editprofile, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        contentResolver = getContext().getContentResolver();

        name_tv = v.findViewById(R.id.name_textview);
        name_edt = v.findViewById(R.id.name_edittext);
        datebirth_edt = v.findViewById(R.id.datebirth_edittext);
        email_edt = v.findViewById(R.id.email_edittext);
        ava_image = v.findViewById(R.id.image_of_user);
        change_ava_btn = v.findViewById(R.id.change_ava_btn);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        if (viewModel.showUserName() != null){
            viewModel.showUserName().observe(requireActivity(), userInfoResource -> {
                switch (userInfoResource.status){
                    case LOADING:
                        break;
                    case SUCCESS:
                        name = userInfoResource.data.getUsername();
                        datebirth = userInfoResource.data.getDatebirth();
                        address = userInfoResource.data.getAddress();
                        email = userInfoResource.data.getEmail();
                        imagePath = userInfoResource.data.getImagePath();
                        path = userInfoResource.data.getImagePath();
                        Glide.with(this).load(imagePath).fitCenter().into(ava_image);

                        name_tv.setText(name);
                        name_edt.setText(name);
                        datebirth_edt.setText(datebirth);
                        email_edt.setText(email);

                        name_edt.setEnabled(false);
                        datebirth_edt.setEnabled(false);
                        email_edt.setEnabled(false);
                        break;
                    case ERROR:
                        CustomToast loginErrorToast = new CustomToast();
                        loginErrorToast.ShowToastMessage(requireActivity(), 2, userInfoResource.message);
                        break;
                }
            });
        }

        return v;
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(requireView());
        back_Account_btn = (ImageButton) view.findViewById(R.id.back_info);

        edit_profile_btn = (Button) view.findViewById(R.id.edit_profile_btn);
        cancel_btn = (Button) view.findViewById(R.id.cancel_btn);
        save_btn = (Button) view.findViewById(R.id.save_btn);

        back_Account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.popBackStack();
            }
        });
        change_ava_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the permission is already granted
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request permission if it has not been granted
                    requestGalleryPermission();
                } else {
                    // Permission already granted, proceed with gallery access
                    OpenThumbnailGallery();
                }
            }

        });
        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name_edt.setEnabled(true);
                datebirth_edt.setEnabled(true);
                email_edt.setEnabled(true);
                CustomToast successToast = new CustomToast();
                successToast.ShowToastMessage(requireActivity(), 1, "Mời bạn thay đổi thông tin ở phần phía trên");
                name_edt.requestFocus();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                navController.navigate(R.id.accountFragment);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                updateUser();
                                navController.navigate(R.id.accountFragment);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }
    public void updateUser(){
        String name = name_edt.getText().toString();
        String datebirth = datebirth_edt.getText().toString();
        String email = email_edt.getText().toString();
        User user = new User();
        user.setUsername(name);
        user.setDatebirth(datebirth);
        user.setAddress(address);
        user.setEmail(email);
        user.setImagePath(path);
        Log.d(TAG, "link" + path);
        if(thumbnailImage != null) {
            viewModel.updateUser(user, contentResolver, thumbnailImage);
        } else {
            viewModel.updateUser(user);
        }

    }
    private void processSelectedImage(Uri imageUri) {
        //SlideAdapter.addItem(new ItemModel(imageUri, null));
    }
    private void requestGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void OpenThumbnailGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        thumbnailLauncher.launch(intent);
    }

}