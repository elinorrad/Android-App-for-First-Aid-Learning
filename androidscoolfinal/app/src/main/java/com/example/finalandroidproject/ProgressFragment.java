package com.example.finalandroidproject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class ProgressFragment extends Fragment {

    private LineChart lineChart;
    private TextView progressTitle;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        // חיבור ל-IDs
        lineChart = view.findViewById(R.id.line_chart);
        progressTitle = view.findViewById(R.id.progress_title);

        // קבלת המשתמש המחובר
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "לא נמצא משתמש מחובר", Toast.LENGTH_SHORT).show();
            return view;
        }

        // קישור למסד הנתונים
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser.getUid()).child("testResults");

        // טעינת הנתונים מ-Firebase והצגת הגרף
        loadProgressData();

        return view;
    }

    private void loadProgressData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "אין נתונים להצגה", Toast.LENGTH_SHORT).show();
                    return;
                }

                TreeMap<Long, Integer> sortedResults = new TreeMap<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        String dateString = data.getKey();
                        int percentage = data.getValue(Integer.class);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = sdf.parse(dateString);
                        if (date != null) {
                            sortedResults.put(date.getTime(), percentage);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // יצירת הגרף
                setupChart(sortedResults);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupChart(TreeMap<Long, Integer> sortedResults) {
        ArrayList<Entry> entries = new ArrayList<>();
        int index = 0;

        for (Map.Entry<Long, Integer> entry : sortedResults.entrySet()) {
            entries.add(new Entry(index++, entry.getValue()));
        }

        if (entries.isEmpty()) {
            Toast.makeText(getContext(), "אין נתוני מבחנים להצגה", Toast.LENGTH_SHORT).show();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "התקדמות אישית");
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setCircleRadius(6f);
        dataSet.setDrawValues(true);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();

        // התאמת צירי הגרף
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // הוספת כותרת
        Description description = new Description();
        description.setText("אחוזי הצלחה במבחנים לפי תאריך");
        description.setTextColor(Color.WHITE);
        description.setTextSize(12f);
        lineChart.setDescription(description);
    }
}
