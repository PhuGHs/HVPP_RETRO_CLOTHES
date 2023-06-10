package com.example.ecommerce_hvpp.repositories.adminRepositories;

import static com.example.ecommerce_hvpp.util.CustomFormat.dateFormatter;
import static com.example.ecommerce_hvpp.util.CustomFormat.decimalFormatter;
import static com.example.ecommerce_hvpp.util.CustomFormat.monthFormatter;
import static com.example.ecommerce_hvpp.util.constant.STATISTIC_CLUB;
import static com.example.ecommerce_hvpp.util.constant.STATISTIC_ORDERS;
import static com.example.ecommerce_hvpp.util.constant.STATISTIC_PRODUCT_SOLD;
import static com.example.ecommerce_hvpp.util.constant.STATISTIC_REVENUE;
import static com.example.ecommerce_hvpp.util.constant.STATISTIC_VISITORS;
import static com.example.ecommerce_hvpp.util.CustomFormat.templateDate;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.ecommerce_hvpp.R;
import com.example.ecommerce_hvpp.firebase.FirebaseHelper;
import com.example.ecommerce_hvpp.util.Resource;
import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import io.reactivex.rxjava3.core.Observable;


public class AdminStatisticsRepository {
    NavController navController;
    private final FirebaseHelper firebaseHelper = FirebaseHelper.getInstance();
    public static Map<String, Integer> dayOrdersDataStatistics;
    public static Map<String, Integer> dayProductSoldDataStatistics;
    public static Map<String, Integer> dayVisitorsDataStatistics;
    public static Map<String, Double> dayRevenueDataStatistics;

    public AdminStatisticsRepository() {}

    public void onClickOption(View view, int option) {
        navController = Navigation.findNavController(view);
        switch (option) {
            case STATISTIC_VISITORS:
                navController.navigate(R.id.adminStatisticVisitorsFragment);
                break;
            case STATISTIC_ORDERS:
                navController.navigate(R.id.adminStatisticOrdersFragment);
                break;
            case STATISTIC_REVENUE:
                navController.navigate(R.id.adminStatisticRevenueFragment);
                break;
            case STATISTIC_PRODUCT_SOLD:
                navController.navigate(R.id.adminStatisticProductSoldFragment);
                break;
            case STATISTIC_CLUB:
                navController.navigate(R.id.adminStatisticClubFragment);
                break;
        }
    }

    public View.OnClickListener onClickBackPage() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.popBackStack();
            }
        };
    }

    public String beginningOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return templateDate.format(calendar.getTime());
    }
    public String getCurrentDay() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.format(dateFormatter);
    }

    public String getPreviousDay() {
        LocalDate currentDate = LocalDate.now();
        LocalDate previousDate = currentDate.minusDays(1);
        return previousDate.format(dateFormatter);
    }

    public String getCurrentMonth() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.format(monthFormatter);
    }

    public String getPreviousMonth() {
        LocalDate currentDate = LocalDate.now();
        LocalDate previousMonth = currentDate.minusMonths(1);
        return previousMonth.format(monthFormatter);
    }

    public int handlePercent(int currentQuantity, int previousQuantity) {
        int result = 0;
        if (previousQuantity == 0 && currentQuantity != 0) {
            result = 100;
        } else if (previousQuantity != 0) {
            if (currentQuantity == 0) {
                result = -100;
            } else {
                float percent = (float) (currentQuantity - previousQuantity) / previousQuantity * 100;
                result = (int) Math.round(percent);
            }
        }

        return result;
    }

    public Pair<String, Integer> handleResult(Context context, int result) {
        if (result <= 0) {
            return new Pair<>("" + result + "%", ContextCompat.getColor(context, R.color.decrease_percent));
        } else {
            return new Pair<>("+" + result + "%", ContextCompat.getColor(context, R.color.increase_percent));
        }
    }

    @SuppressLint("SetTextI18n")
    public Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> handleComponentStatistics(Map<String, Integer> dayDataStatistics, TextView tvContent) {
        Map<String, Integer> monthDataStatistics = new HashMap<>();
        dayDataStatistics.forEach((key, value) -> {
            String strMonth = key.substring(3, 10);
            if (monthDataStatistics.containsKey(strMonth)) {
                monthDataStatistics.put(strMonth, monthDataStatistics.get(strMonth) + value);
            } else {
                monthDataStatistics.put(strMonth, value);
            }
        });

        int defaultValue = 0;

        int currDayQuantity = dayDataStatistics.getOrDefault(getCurrentDay(), defaultValue);
        int prevDayQuantity = dayDataStatistics.getOrDefault(getPreviousDay(), defaultValue);
        int resultDay = handlePercent(currDayQuantity, prevDayQuantity);

        int currMonthQuantity = monthDataStatistics.getOrDefault(getCurrentMonth(), defaultValue);
        int prevMonthQuantity = monthDataStatistics.getOrDefault(getPreviousMonth(), defaultValue);
        int resultMonth = handlePercent(currMonthQuantity, prevMonthQuantity);

        tvContent.setText(currDayQuantity + "");
        return new Pair<>(new Pair<>(resultDay, resultMonth), new Pair<>(currDayQuantity, currMonthQuantity));
    }

    public int handlePercentDouble(double currentQuantity, double previousQuantity) {
        int result = 0;
        if (previousQuantity == 0 && currentQuantity != 0) {
            result = 100;
        } else if (previousQuantity != 0) {
            if (currentQuantity == 0) {
                result = -100;
            } else {
                double percent = (double) (currentQuantity - previousQuantity) / previousQuantity * 100;
                result = (int) Math.round(percent);
            }
        }

        return result;
    }

    @SuppressLint("SetTextI18n")
    public Pair<Pair<Integer, Integer>, Pair<Double, Double>> handleComponentStatisticsDouble(Map<String, Double> dayDataStatistics, TextView tvContent) {
        Map<String, Double> monthDataStatistics = new HashMap<>();
        dayDataStatistics.forEach((key, value) -> {
            String strMonth = key.substring(3, 10);
            if (monthDataStatistics.containsKey(strMonth)) {
                monthDataStatistics.put(strMonth, monthDataStatistics.get(strMonth) + value);
            } else {
                monthDataStatistics.put(strMonth, value);
            }
        });

        double defaultValue = 0;

        double currDayQuantity = dayDataStatistics.getOrDefault(getCurrentDay(), defaultValue);
        double prevDayQuantity = dayDataStatistics.getOrDefault(getPreviousDay(), defaultValue);
        int resultDay = handlePercentDouble(currDayQuantity, prevDayQuantity);

        double currMonthQuantity = monthDataStatistics.getOrDefault(getCurrentMonth(), defaultValue);
        double prevMonthQuantity = monthDataStatistics.getOrDefault(getPreviousMonth(), defaultValue);
        int resultMonth = handlePercentDouble(currMonthQuantity, prevMonthQuantity);

        tvContent.setText(currDayQuantity + "");
        return new Pair<>(new Pair<>(resultDay, resultMonth), new Pair<>(currDayQuantity, currMonthQuantity));
    }

    public String getMinDate(Map<String, Integer> data) {
        return dateFormatter.format(data.keySet().stream()
                .map(integer -> LocalDate.parse(integer, dateFormatter))
                .min(Comparator.naturalOrder())
                .get());
    }

    public String getMinDateDouble(Map<String, Double> data) {
        return dateFormatter.format(data.keySet().stream()
                .map(integer -> LocalDate.parse(integer, dateFormatter))
                .min(Comparator.naturalOrder())
                .get());
    }

    public boolean isValidatedDate(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, dateFormatter);
        LocalDate end = LocalDate.parse(endDate, dateFormatter);
        return start.isBefore(end);
    }

    public View.OnClickListener createDatePickerDialog(Context mContext, String minDate) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleEventDatePickerDialog(mContext, (TextView) view, minDate);
            }
        };
    }

    // handle date picker click
    public void handleEventDatePickerDialog(Context mContext, TextView tvDate, String strMinDate) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        String selectedDate = tvDate.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(selectedDate);
            c.setTime(date);
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int currMonth = monthOfYear + 1;
                        @SuppressLint("DefaultLocale") String formattedDay = String.format(Locale.getDefault(), "%02d", dayOfMonth);
                        @SuppressLint("DefaultLocale") String formattedMonth = String.format(Locale.getDefault(), "%02d", currMonth);
                        tvDate.setText(formattedDay + "/" + formattedMonth + "/" + year);

                    }
                }, mYear, mMonth, mDay);

        // set min max date
        try {
            Date minDate = sdf.parse(strMinDate);
            assert minDate != null;
            datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());

        datePickerDialog.show();
    }
    
    public void formatLineChart(LineChart lineChart) {
        lineChart.setPinchZoom(false);
        lineChart.setScaleEnabled(false);
        lineChart.getAxisLeft().setDrawAxisLine(true);
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(true);
        lineChart.animateX(2000);

        lineChart.invalidate();
    }

    // visitors
    public Observable<Resource<Map<String, Integer>>> getObservableVisitorsDataStatistics() {
        return Observable.create(emitter -> {
            emitter.onNext(Resource.loading(null));
            firebaseHelper
                    .getCollection("Voucher")
                    .orderBy("date_begin", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Map<String, Integer> mDataStatistics = new HashMap<>();

                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            String date = templateDate
                                    .format(Objects.requireNonNull(snapshot.getTimestamp("date_begin")).toDate());
                            if (mDataStatistics.containsKey(date)) {
                                mDataStatistics.put(date, mDataStatistics.get(date) + 1);
                            } else {
                                mDataStatistics.put(date, 1);
                            }
                        }

                        emitter.onNext(Resource.success(mDataStatistics));
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        emitter.onNext(Resource.error(e.getMessage(), null));
                        emitter.onComplete();
                    });
        });
    }

    // orders
    public Observable<Resource<Map<String, Integer>>> getObservableOrdersDataStatistics() {
        return Observable.create(emitter -> {
            emitter.onNext(Resource.loading(null));
            firebaseHelper
                    .getCollection("Voucher")
                    .orderBy("date_begin", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Map<String, Integer> mDataStatistics = new HashMap<>();

                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            String date = templateDate
                                    .format(Objects.requireNonNull(snapshot.getTimestamp("date_begin")).toDate());
                            if (mDataStatistics.containsKey(date)) {
                                mDataStatistics.put(date, mDataStatistics.get(date) + 1);
                            } else {
                                mDataStatistics.put(date, 1);
                            }
                        }

                        emitter.onNext(Resource.success(mDataStatistics));
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        emitter.onNext(Resource.error(e.getMessage(), null));
                        emitter.onComplete();
                    });
        });
    }

    // product sold
    public Observable<Resource<Map<String, Integer>>> getObservableProductSoldDataStatistics() {
        return Observable.create(emitter -> {
            emitter.onNext(Resource.loading(null));
            firebaseHelper
                    .getCollection("Voucher")
                    .orderBy("date_begin", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Map<String, Integer> mDataStatistics = new HashMap<>();

                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            String date = templateDate
                                    .format(Objects.requireNonNull(snapshot.getTimestamp("date_begin")).toDate());
                            if (mDataStatistics.containsKey(date)) {
                                mDataStatistics.put(date, mDataStatistics.get(date) + 1);
                            } else {
                                mDataStatistics.put(date, 1);
                            }
                        }

                        emitter.onNext(Resource.success(mDataStatistics));
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        emitter.onNext(Resource.error(e.getMessage(), null));
                        emitter.onComplete();
                    });
        });
    }

    // revenue
    public Observable<Resource<Map<String, Double>>> getObservableRevenueDataStatistics() {
        return Observable.create(emitter -> {
            emitter.onNext(Resource.loading(null));
            firebaseHelper
                    .getCollection("Order")
                    .orderBy("updateDate", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Map<String, Double> mDataStatistics = new HashMap<>();

                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            String date = templateDate
                                    .format(Objects.requireNonNull(snapshot.getTimestamp("updateDate")).toDate());

                            Double price = snapshot.getDouble("totalPrice");
                            if (mDataStatistics.containsKey(date)) {
                                mDataStatistics.put(date, mDataStatistics.get(date) + price);
                            } else {
                                mDataStatistics.put(date, price);
                            }
                        }

                        mDataStatistics.forEach((key, value) -> {
                            String strRounded = decimalFormatter.format(value);
                            double res = Double.parseDouble(strRounded);
                            mDataStatistics.put(key, res);
                        });

                        emitter.onNext(Resource.success(mDataStatistics));
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        emitter.onNext(Resource.error(e.getMessage(), null));
                        emitter.onComplete();
                    });
        });
    }

}
