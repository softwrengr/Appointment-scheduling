package com.techease.appointment.fragments.customer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techease.appointment.R;
import com.techease.appointment.fragments.customer.MakeAppointmentFragment;
import com.techease.appointment.fragments.customer.ShowCalendarFragment;
import com.techease.appointment.utilities.GeneralUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowRetailersFragment extends Fragment {
    View view;
    @BindView(R.id.retailer_one)
    TextView tvRetailerOne;
    @BindView(R.id.retailer_two)
    TextView tvRetailerTwo;
    @BindView(R.id.retailer_three)
    TextView tvRetailerThree;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_show_retailers, container, false);
        getActivity().setTitle("All Retailers");
        initUI();
        return view;
    }

    private void initUI(){
        ButterKnife.bind(this,view);
        showAllRetailers();
        final Bundle bundle = new Bundle();

        tvRetailerOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), tvRetailerOne.getText().toString(), Toast.LENGTH_SHORT).show();
                bundle.putString("name",tvRetailerOne.getText().toString());
                GeneralUtils.connectFragmentWithBack(getActivity(),new ShowCalendarFragment());
            }
        });

        tvRetailerTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("name",tvRetailerTwo.getText().toString());
                GeneralUtils.connectFragmentWithBack(getActivity(),new ShowCalendarFragment()).setArguments(bundle);
            }
        });

        tvRetailerThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("name",tvRetailerThree.getText().toString());
                GeneralUtils.connectFragmentWithBack(getActivity(),new ShowCalendarFragment()).setArguments(bundle);
            }
        });
    }

    private void showAllRetailers(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("available days");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<String> arrayList = new ArrayList<String>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                 arrayList.add(ds.getKey().toString());
//                    Iterator<String> iterator = arrayList.iterator();
//                    while (iterator.hasNext()) {
//                        tvRetailerOne.setText(iterator.next());
//                    }
                    String[] array = new String[]{ds.getKey()};
                    Toast.makeText(getActivity(), array[0], Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        ref.addListenerForSingleValueEvent(eventListener);
    }

}
