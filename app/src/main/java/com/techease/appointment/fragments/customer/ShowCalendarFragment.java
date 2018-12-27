package com.techease.appointment.fragments.customer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savvi.rangedatepicker.CalendarPickerView;
import com.techease.appointment.R;
import com.techease.appointment.helpers.AppointCrud;
import com.techease.appointment.models.DateModel;
import com.techease.appointment.utilities.GeneralUtils;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ShowCalendarFragment extends Fragment {
    @BindView(R.id.calendar_picker)
    CalendarPickerView calendar;
    @BindView(R.id.tvRetailerDays)
    TextView tvRetailerDays;
    @BindView(R.id.tvRetailerName)
    TextView tvRetailerName;

    View view;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private String strRetailerName, strFrom = "", strTo = "", strSelectDate, strCustomerName;
    AppointCrud appointCrud;
    HashMap<String,String> hashMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_show_calendar, container, false);
        customActionBar();
        strCustomerName = GeneralUtils.getEmail(getActivity());
        String[] splitStr = strCustomerName.split("@");
        strCustomerName = splitStr[0];
        initUI();


        return view;
    }

    private void initUI() {
        ButterKnife.bind(this, view);
        firebaseDatabase = FirebaseDatabase.getInstance();

        strRetailerName = GeneralUtils.getName(getActivity());
       // tvRetailerName.setText(strRetailerName);
        appointCrud = new AppointCrud(getActivity());
        hashMap = new HashMap<>();

        showSelectedDate();
        //  showRetailerDate();
//        showCalendar(strFrom);
        showRetailerAvailability();


    }

    private void showRetailerDate() {
        databaseReference = firebaseDatabase.getReference("users").child(strCustomerName);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    Toast.makeText(getActivity(), "No data available", Toast.LENGTH_SHORT).show();
                } else {
                    strFrom = dataSnapshot.child("date").getValue().toString();
                    // strTo = dataSnapshot.child("to").getValue().toString();

                    showCalendar(strFrom);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("error", error.getMessage());
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        initUI();
    }

    public void customActionBar() {
        android.support.v7.app.ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        ImageView ivBack = mCustomView.findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);
        TextView tvTitle = mCustomView.findViewById(R.id.title);
        tvTitle.setText("Book your appointment");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtils.connectFragment(getActivity(), new CustomerHomeFragment());
            }
        });
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.show();

    }


    private void showCalendar(String date) {

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);


        ArrayList<Date> arrayList = new ArrayList<>();
        try {

            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

            Date newdate = dateformat.parse(date);
            arrayList.add(newdate);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());

        calendar.init(lastYear.getTime(), nextYear.getTime(), simpleDateFormat)
                .withSelectedDate(new Date())
                .withHighlightedDates(arrayList);


        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                formatedDate(date);
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });

    }

    private void formatedDate(Date date) {
        String strDate = null;
        SimpleDateFormat spf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        strDate = spf.format(date);
        if (date == null) {
            Toast.makeText(getActivity(), "no date selected", Toast.LENGTH_SHORT).show();
        } else {

            java.util.Date newDate = null;
            try {
                newDate = spf.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            strDate = sdf.format(newDate);

            Bundle bundle = new Bundle();
            bundle.putString("selected_date", strDate);
            GeneralUtils.connectFragmentWithBack(getActivity(), new MakeAppointmentFragment()).setArguments(bundle);
        }
    }

    private void showRetailerAvailability() {

        databaseReference = firebaseDatabase.getReference("available days").child(strRetailerName);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    Toast.makeText(getActivity(), "No data available", Toast.LENGTH_SHORT).show();
                } else {
                    String from = dataSnapshot.child("from").getValue().toString();
                    String to = dataSnapshot.child("to").getValue().toString();

                    tvRetailerDays.setText("From  " + from + "  To  " + to);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("error", error.getMessage());
            }
        });
    }

    private void showSelectedDate(){
        Cursor cursor = appointCrud.getData();
        List<String> dateArrayList = new ArrayList<String>();

        while (cursor.moveToNext()) {
            String strDates = cursor.getString(1);
            dateArrayList.add(strDates);
            for (int i = 0; i<dateArrayList.size(); i++){
                Log.d("zma for loop", String.valueOf(dateArrayList.size()));
            }
            for (String dates:dateArrayList){
                showCalendar(dates);
            }

        }
    }
}
