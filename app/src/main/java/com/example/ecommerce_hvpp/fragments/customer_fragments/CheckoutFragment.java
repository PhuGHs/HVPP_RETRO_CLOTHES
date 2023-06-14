package com.example.ecommerce_hvpp.fragments.customer_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ecommerce_hvpp.R;
import com.example.ecommerce_hvpp.activities.MainActivity;
import com.example.ecommerce_hvpp.adapter.CheckoutAdapter;
import com.example.ecommerce_hvpp.adapter.VoucherAdapter;
import com.example.ecommerce_hvpp.model.Voucher;
import com.example.ecommerce_hvpp.viewmodel.Customer.VoucherViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckoutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CheckoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CheckoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckoutFragment newInstance(String param1, String param2) {
        CheckoutFragment fragment = new CheckoutFragment();
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
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }
    private ImageButton btnBackToCart, navToAddress, navToVoucher;
    private ListView listVoucherAppliedLv;
    private ArrayList<Pair<String, Double>> listVoucherApplied;
    private TextView addressApplied, cartItems, cartPrice, totalOrder;
    Double shipping = 1.99;
    Spinner spinnerTypeCheckout;
    ArrayList<String> listTypeCheckout;
    private NavController navController;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize
        navController = Navigation.findNavController(requireView());
        listVoucherAppliedLv = (ListView) view.findViewById(R.id.listVoucherApplied);
        listVoucherApplied = new ArrayList<>();
        btnBackToCart = (ImageButton) view.findViewById(R.id.btnBackToCart);
        navToAddress = (ImageButton) view.findViewById(R.id.navToAddress);
        navToVoucher = (ImageButton) view.findViewById(R.id.navToVoucher);
        addressApplied = (TextView) view.findViewById(R.id.addressApplied);
        cartItems = (TextView) view.findViewById(R.id.cartItems);
        cartPrice = (TextView) view.findViewById(R.id.cartPrice);
        totalOrder = (TextView) view.findViewById(R.id.totalOrder);
        spinnerTypeCheckout = (Spinner) view.findViewById(R.id.typeCheckout);
        listTypeCheckout = new ArrayList<>();

        getListVoucherApplied();
        getAddressApplied();
        getTypeCheckout();
        getCartItemsAndPrice();
        calcTotalOrder();

        CheckoutAdapter checkoutAdapter = new CheckoutAdapter(getContext(), R.layout.simple_spinner_string_item, listTypeCheckout);
        checkoutAdapter.setDropDownViewResource(R.layout.simple_spinner_string_item);
        spinnerTypeCheckout.setAdapter(checkoutAdapter);

        VoucherAdapter voucherAdapter = new VoucherAdapter(getContext(), R.layout.voucher_item, listVoucherApplied);
        listVoucherAppliedLv.setAdapter(voucherAdapter);

        //navigate
        btnBackToCart.setOnClickListener(view1 -> navController.navigate(R.id.cartFragment));
        navToAddress.setOnClickListener(view12 -> navController.navigate(R.id.RecepientInfoFragment));
        navToVoucher.setOnClickListener(view13 -> navController.navigate(R.id.VoucherFragment));
    }
    private void getListVoucherApplied(){
        VoucherViewModel voucherViewModel = new ViewModelProvider(this).get(VoucherViewModel.class);
        voucherViewModel.showVoucherList().observe(getViewLifecycleOwner(), vouchers -> {
            for (Voucher voucher : vouchers) {
                if (MainActivity.PDviewModel.checkVoucherApply(voucher))
                    listVoucherApplied.add(new Pair<>(voucher.getId(), voucher.getDiscountedValue()));
            }
        });
    }
    private void getTypeCheckout(){
        listTypeCheckout.add("Visa");
        listTypeCheckout.add("Card Debit");
        listTypeCheckout.add("Momo");
    }
    private void getAddressApplied(){
        MainActivity.PDviewModel.getAddressApplied().observe(getViewLifecycleOwner(), info -> addressApplied.setText(info));
    }
    private void getCartItemsAndPrice(){
        int items = MainActivity.PDviewModel.getTotalCartItems().getValue();
        String text = "";
        if (items > 1) text = " items)"; else text = " item)";
        cartItems.setText("(" + items + text);
        cartPrice.setText("$" + Math.round(MainActivity.PDviewModel.getTotalPriceCart().getValue() * 100.0) / 100.0);
    }
    public void calcTotalOrder(){
        double total = shipping;
        total += Math.round(MainActivity.PDviewModel.getTotalPriceCart().getValue() * 100.0) / 100.0;
        for (int i = 0; i < listVoucherApplied.size(); i++){
            total -= listVoucherApplied.get(i).second;
        }
        totalOrder.setText("$" + Math.round(total * 100.0) / 100.0);
    }
}