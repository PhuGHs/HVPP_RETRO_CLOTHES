package com.example.ecommerce_hvpp.fragments.customer_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ecommerce_hvpp.R;
import com.example.ecommerce_hvpp.activities.MainActivity;
import com.example.ecommerce_hvpp.adapter.ProductAdapter;
import com.example.ecommerce_hvpp.model.Product;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CategoryDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryDetailFragment newInstance(String param1, String param2) {
        CategoryDetailFragment fragment = new CategoryDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_detail, container, false);
    }

    ImageButton back;
    TextView title;
    RecyclerView listCategoryDetailRv;
    ProductAdapter adapter;
    GridLayoutManager layoutManager;
    private NavController navController;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init
        navController = Navigation.findNavController(requireView());
        back = (ImageButton) view.findViewById(R.id.btnBackToCategory);
        title = (TextView) view.findViewById(R.id.categoryDetailTitle);
        listCategoryDetailRv = (RecyclerView) view.findViewById(R.id.listCategoryDetail);
        layoutManager = new GridLayoutManager(getContext(), 2);

        getDetailCategory();

        back.setOnClickListener(view1 -> navController.popBackStack());
    }
    public void getDetailCategory(){
        Bundle bundle = getArguments();
        if (bundle != null){
            String type = bundle.getString("Type");
            String category = bundle.getString("Category");
            MainActivity.PDviewModel.getDetailCategory(type, category).observe(getViewLifecycleOwner(), listDetailCategory -> {
                title.setText(category);

                Log.d("Size", listDetailCategory.size() + "/");

                adapter = new ProductAdapter(getContext(), (ArrayList<Product>) listDetailCategory, requireView(), false);
                listCategoryDetailRv.setAdapter(adapter);
                listCategoryDetailRv.setLayoutManager(layoutManager);
            });
        }
        else Log.d("Bundle", "null");
    }
}