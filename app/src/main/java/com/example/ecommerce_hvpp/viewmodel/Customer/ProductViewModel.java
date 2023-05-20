package com.example.ecommerce_hvpp.viewmodel.Customer;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce_hvpp.firebase.FirebaseHelper;
import com.example.ecommerce_hvpp.model.Product;
import com.example.ecommerce_hvpp.util.Resource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductViewModel extends ViewModel {
    private FirebaseHelper helper;
    private List<Product> listNewArrivals, listBestSeller, listFavorite;
    private MutableLiveData<List<Product>> mldListNewArrivals, mldListBestSeller, mldListFavorite;
    private MutableLiveData<Product> detailProduct;
    private String TAG = "Product ViewModel";
    public ProductViewModel(){
        //init
        helper = FirebaseHelper.getInstance();
        mldListNewArrivals = new MutableLiveData<>();
        mldListBestSeller = new MutableLiveData<>();
        mldListFavorite = new MutableLiveData<>();

        initListNewArrivalsLiveData();
        initListBestSellerLiveData();
        initListFavoriteLiveData();
    }

    private void initListFavoriteLiveData() {
        listFavorite = new ArrayList<>();

        helper.getCollection("Product").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        String ID = documentSnapshot.getString("ID");
                        String Name = documentSnapshot.getString("Name");
                        String Club = documentSnapshot.getString("Club");
                        String Nation = documentSnapshot.getString("Nation");
                        String Season = documentSnapshot.getString("Season");
                        double Price = documentSnapshot.getDouble("Price");
                        long Quantity = documentSnapshot.getLong("Quantity");
                        double Point = documentSnapshot.getDouble("Point");
                        String URLmain = documentSnapshot.getString("URLmain");
                        String URLsub1 = documentSnapshot.getString("URLsub1");
                        String URLsub2 = documentSnapshot.getString("URLsub2");
                        String URLthumb = documentSnapshot.getString("URLthumb");
                        long sizeS = documentSnapshot.getLong("SizeS");
                        long sizeM = documentSnapshot.getLong("SizeM");
                        long sizeL = documentSnapshot.getLong("SizeL");
                        long sizeXL = documentSnapshot.getLong("SizeXL");
                        long sizeXXL = documentSnapshot.getLong("SizeXXL");

                        Log.d(TAG, ID + "--" + Name);

                        listFavorite.add(new Product(ID, Name, Club, Nation, Season, Price, Quantity, Point, URLmain, URLsub1, URLsub2, URLthumb, sizeS, sizeM, sizeL, sizeXL, sizeXXL));
                    }
                    mldListFavorite.setValue(listFavorite);
                });
    }

    private void initListBestSellerLiveData() {
        listBestSeller = new ArrayList<>();

        helper.getCollection("Product").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        String ID = documentSnapshot.getString("ID");
                        String Name = documentSnapshot.getString("Name");
                        String Club = documentSnapshot.getString("Club");
                        String Nation = documentSnapshot.getString("Nation");
                        String Season = documentSnapshot.getString("Season");
                        double Price = documentSnapshot.getDouble("Price");
                        long Quantity = documentSnapshot.getLong("Quantity");
                        double Point = documentSnapshot.getDouble("Point");
                        String URLmain = documentSnapshot.getString("URLmain");
                        String URLsub1 = documentSnapshot.getString("URLsub1");
                        String URLsub2 = documentSnapshot.getString("URLsub2");
                        String URLthumb = documentSnapshot.getString("URLthumb");
                        long sizeS = documentSnapshot.getLong("SizeS");
                        long sizeM = documentSnapshot.getLong("SizeM");
                        long sizeL = documentSnapshot.getLong("SizeL");
                        long sizeXL = documentSnapshot.getLong("SizeXL");
                        long sizeXXL = documentSnapshot.getLong("SizeXXL");

                        Log.d(TAG, ID + "--" + Name);

                        listBestSeller.add(new Product(ID, Name, Club, Nation, Season, Price, Quantity, Point, URLmain, URLsub1, URLsub2, URLthumb, sizeS, sizeM, sizeL, sizeXL, sizeXXL));
                    }
                    mldListBestSeller.setValue(listBestSeller);
                });
    }

    private void initListNewArrivalsLiveData() {
        listNewArrivals = new ArrayList<>();

        helper.getCollection("Product").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        String ID = documentSnapshot.getString("ID");
                        String Name = documentSnapshot.getString("Name");
                        String Club = documentSnapshot.getString("Club");
                        String Nation = documentSnapshot.getString("Nation");
                        String Season = documentSnapshot.getString("Season");
                        double Price = documentSnapshot.getDouble("Price");
                        long Quantity = documentSnapshot.getLong("Quantity");
                        double Point = documentSnapshot.getDouble("Point");
                        String URLmain = documentSnapshot.getString("URLmain");
                        String URLsub1 = documentSnapshot.getString("URLsub1");
                        String URLsub2 = documentSnapshot.getString("URLsub2");
                        String URLthumb = documentSnapshot.getString("URLthumb");
                        long sizeS = documentSnapshot.getLong("SizeS");
                        long sizeM = documentSnapshot.getLong("SizeM");
                        long sizeL = documentSnapshot.getLong("SizeL");
                        long sizeXL = documentSnapshot.getLong("SizeXL");
                        long sizeXXL = documentSnapshot.getLong("SizeXXL");

                        Log.d(TAG, ID + "--" + Name);

                        listNewArrivals.add(new Product(ID, Name, Club, Nation, Season, Price, Quantity, Point, URLmain, URLsub1, URLsub2, URLthumb, sizeS, sizeM, sizeL, sizeXL, sizeXXL));
                    }
                    mldListNewArrivals.setValue(listNewArrivals);
                });
    }

    public MutableLiveData<List<Product>> getMldListNewArrivals() {
        return mldListNewArrivals;
    }

    public MutableLiveData<List<Product>> getMldListBestSeller() {
        return mldListBestSeller;
    }

    public MutableLiveData<List<Product>> getMldListFavorite() {
        return mldListFavorite;
    }

    public MutableLiveData<Product> getDetailProduct(String productID) {
        detailProduct = new MutableLiveData<>();

        helper.getCollection("Product").document(productID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        String ID = documentSnapshot.getString("ID");
                        String Name = documentSnapshot.getString("Name");
                        String Club = documentSnapshot.getString("Club");
                        String Nation = documentSnapshot.getString("Nation");
                        String Season = documentSnapshot.getString("Season");
                        double Price = documentSnapshot.getDouble("Price");
                        long Quantity = documentSnapshot.getLong("Quantity");
                        double Point = documentSnapshot.getDouble("Point");
                        String URLmain = documentSnapshot.getString("URLmain");
                        String URLsub1 = documentSnapshot.getString("URLsub1");
                        String URLsub2 = documentSnapshot.getString("URLsub2");
                        String URLthumb = documentSnapshot.getString("URLthumb");
                        long sizeS = documentSnapshot.getLong("SizeS");
                        long sizeM = documentSnapshot.getLong("SizeM");
                        long sizeL = documentSnapshot.getLong("SizeL");
                        long sizeXL = documentSnapshot.getLong("SizeXL");
                        long sizeXXL = documentSnapshot.getLong("SizeXXL");

                        Product product = new Product(ID, Name, Club, Nation, Season, Price, Quantity, Point, URLmain, URLsub1, URLsub2, URLthumb, sizeS, sizeM, sizeL, sizeXL, sizeXXL);
                        detailProduct.setValue(product);
                    }
                });

        return detailProduct;
    }
}
