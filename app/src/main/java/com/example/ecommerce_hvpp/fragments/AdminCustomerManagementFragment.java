package com.example.ecommerce_hvpp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce_hvpp.adapter.AdminCustomItemCustomerAdapter;
import com.example.ecommerce_hvpp.adapters.ChatAdapter;
import com.example.ecommerce_hvpp.databinding.AdminFragmentCustomerManagementBinding;
import com.example.ecommerce_hvpp.model.Customer;
import com.example.ecommerce_hvpp.model.User;
import com.example.ecommerce_hvpp.repositories.AdminCustomerManagementRepository;
import com.example.ecommerce_hvpp.util.Resource;
import com.example.ecommerce_hvpp.viewmodel.admin_customer_management.AdminCustomerManagementViewModel;

import java.util.List;
import java.util.Objects;

public class AdminCustomerManagementFragment extends Fragment {
    AdminFragmentCustomerManagementBinding mFragmentAdminManageCustomerBinding;
    AdminCustomerManagementRepository repo;
    AdminCustomerManagementViewModel vmAdminCustomerManagement;
    AdminCustomItemCustomerAdapter adapterAdminCustomItemCustomer;
    MutableLiveData<Resource<List<Customer>>> _mldListCustomer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentAdminManageCustomerBinding = AdminFragmentCustomerManagementBinding.inflate(inflater, container, false);

        // init view model
        vmAdminCustomerManagement = new ViewModelProvider(requireActivity()).get(AdminCustomerManagementViewModel.class);

        // get data from firebase
        getData();

        // display data into app
        displayAllCustomers(_mldListCustomer);

        // on click back page
        mFragmentAdminManageCustomerBinding.adminCustomerManagementHeaderBack.setOnClickListener(onClickBackPage());

        return mFragmentAdminManageCustomerBinding.getRoot();
    }

    private void getData() {
        repo = new AdminCustomerManagementRepository();
        _mldListCustomer = (MutableLiveData<Resource<List<Customer>>>) repo.getAllCustomer();
    }

    private void displayAllCustomers(MutableLiveData<Resource<List<Customer>>> mldListCustomer) {
        if(mldListCustomer.getValue() != null) {
            mldListCustomer.observe(requireActivity(), resource -> {
                switch(resource.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        adapterAdminCustomItemCustomer = new AdminCustomItemCustomerAdapter(getContext(), Objects.requireNonNull(resource.data));
                        //set up recyclerview
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        mFragmentAdminManageCustomerBinding.adminCustomerManagementRcvItemCustomer.setLayoutManager(layoutManager);
                        mFragmentAdminManageCustomerBinding.adminCustomerManagementRcvItemCustomer.setAdapter(adapterAdminCustomItemCustomer);

                        layoutManager.scrollToPositionWithOffset(adapterAdminCustomItemCustomer.getItemCount() - 1, 0);
                        break;
                    case ERROR:
                        Log.i("VuError", resource.message);
                }
            });
        }
    }

    private View.OnClickListener onClickBackPage() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.popBackStack();
            }
        };
    }
}
