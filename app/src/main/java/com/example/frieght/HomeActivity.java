package com.example.frieght;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.frieght.databinding.ActivityHomeBinding;
import com.example.frieght.models.AvailableFraveller;
import com.example.frieght.models.CustomOrder;
import com.example.frieght.utils.Constants;
import com.example.frieght.utils.DateTimeUtils;
import com.example.frieght.utils.DialogUtil;
import com.example.frieght.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    ArrayAdapter<String> fromCityAdapter, toCityAdapter;
    List<String> fromCities, toCities;
    Pair<Long, Long> selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fromCities = Arrays.asList(getResources().getStringArray(R.array.cities));
        toCities = Arrays.asList(getResources().getStringArray(R.array.cities));
        fromCityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fromCities);
        toCityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, toCities);

        binding.autoCompleteFromCity.setAdapter(fromCityAdapter);
        binding.autoCompleteToCity.setAdapter(toCityAdapter);

        binding.etDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                        .build();
                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        HomeActivity.this.selection = selection;
                        Log.d(Constants.TAG, "onPositiveButtonClick:" + selection.first + ", " + selection.second);
                        String firstDate = DateTimeUtils.millisecondsToDate(selection.first);
                        String secondDate = DateTimeUtils.millisecondsToDate(selection.second);
                        binding.etDateRange.setText(String.format("%s - %s", firstDate, secondDate));
//                        Toast.makeText(CustomOrderDetails.this, "", Toast.LENGTH_SHORT).show();
                    }
                });
                picker.show(getSupportFragmentManager(), this.toString());
            }
        });

        binding.autoCompleteFromCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!fromCities.contains(binding.autoCompleteFromCity.getText().toString())) {
                        binding.tilFromCity.setError("Invalid city.");
                        binding.tilFromCity.setErrorEnabled(true);
                    } else {
                        binding.tilFromCity.setError("");
                        binding.tilFromCity.setErrorEnabled(false);
                    }
                }
            }
        });

        binding.autoCompleteToCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!toCities.contains(binding.autoCompleteToCity.getText().toString())) {
                        binding.tilToCity.setError("Invalid city.");
                        binding.tilToCity.setErrorEnabled(true);
                    } else {
                        binding.tilToCity.setError("");
                        binding.tilToCity.setErrorEnabled(false);
                    }
                }
            }
        });

        binding.btnCustom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fromCity = binding.autoCompleteFromCity.getText().toString();
                String toCity = binding.autoCompleteToCity.getText().toString();
                String description = binding.etDescription.getText().toString();
                String additionalNotes = binding.etAdditionalNotes.getText().toString();
                String uid = FirebaseAuth.getInstance().getUid();
                long fromDate = selection.first;
                long toDate = selection.second;
//                CustomOrder customOrder = new CustomOrder(FirebaseAuth.getInstance().getUid(), toCity, fromCity, fromDate, toDate, description, additionalNotes, uid);
                AvailableFraveller availableFraveller = new AvailableFraveller(
                        FirebaseAuth.getInstance().getUid(),
                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                        fromCity,
                        toCity,
                        fromDate,
                        toDate);
                DialogUtil.showSimpleProgressDialog(HomeActivity.this);
                FirebaseUtils.getCustomOrderReference()
                        .setValue(availableFraveller)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                DialogUtil.closeProgressDialog();
                                Toast.makeText(HomeActivity.this, "Form submitted", Toast.LENGTH_SHORT).show();
                                binding.etAdditionalNotes.setText("");
                                binding.etDateRange.setText("");
                                binding.etDescription.setText("");
                                binding.autoCompleteFromCity.setText("");
                                binding.autoCompleteToCity.setText("");

                            }
                        });
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}