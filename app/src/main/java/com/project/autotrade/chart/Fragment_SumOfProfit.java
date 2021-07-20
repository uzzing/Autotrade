package com.project.autotrade.chart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.BarDataSet;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.autotrade.R;

import org.jetbrains.annotations.NotNull;

public class Fragment_SumOfProfit extends Fragment {

    private static float sumOfProfit;
    private static float preProfit;

    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference chartRef;








//        System.out.println(Fragment_5minute.barList);
//
//        int listSize = Fragment_5minute.barList.size();
//
//        if (listSize != 0) {
//
//            float lastProfit= Fragment_5minute.barList.get(listSize - 1).getY();
//
//            System.out.println(lastProfit);
//
//            sumOfProfit += lastProfit;
//
//            //System.out.println(sumOfProfit);
//        }
    }
}