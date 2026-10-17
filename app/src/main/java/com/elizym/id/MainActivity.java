package com.elizym.id;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private LinearLayout panelLoad, panelInfo, panelSettings;
    private TextView tabLoad, tabInfo, tabSettings;
    private Button buttonLoad;
    private Spinner spinnerBuild;
    private LinearLayout cardBuild, cardSettings, cardInfo;
    private RelativeLayout cardStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        panelLoad = (LinearLayout) findViewById(R.id.panel_load);
        panelInfo = (LinearLayout) findViewById(R.id.panel_info);
        panelSettings = (LinearLayout) findViewById(R.id.panel_settings);

        tabLoad = (TextView) findViewById(R.id.tab_load);
        tabInfo = (TextView) findViewById(R.id.tab_info);
        tabSettings = (TextView) findViewById(R.id.tab_settings);

        buttonLoad = (Button) findViewById(R.id.button_load);
        spinnerBuild = (Spinner) findViewById(R.id.spinner_build);
        cardBuild = (LinearLayout) findViewById(R.id.card_build);
        cardStatus = (RelativeLayout) findViewById(R.id.card_status);
        cardInfo = (LinearLayout) findViewById(R.id.card_info);
        cardSettings = (LinearLayout) findViewById(R.id.card_settings);

        // Закругления в стиле iOS
        applyIosStyle(cardBuild, "#1C1C1E", 35f);
        applyIosStyle(cardStatus, "#1C1C1E", 35f);
        applyIosStyle(cardInfo, "#1C1C1E", 35f);
        applyIosStyle(cardSettings, "#1C1C1E", 35f);
        applyIosStyle(buttonLoad, "#A832FC", 35f);

        String[] builds = {"Elizym Remaster v1.0.0", "Elizym Legacy v0.39"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, builds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBuild.setAdapter(adapter);

        // Логика табов навигации
        tabLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(0);
            }
        });

        tabInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(1);
            }
        });

        tabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(2);
            }
        });

        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "Выдайте разрешение на отображение поверх окон!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Запуск визуального оверлея Elizym...", Toast.LENGTH_SHORT).show();
                    startService(new Intent(MainActivity.this, floatingservice.class));
                }
            }
        });
    }

    private void applyIosStyle(View view, String colorHex, float radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(radius);
        shape.setColor(Color.parseColor(colorHex));
        view.setBackground(shape);
    }

    private void switchTab(int tabIndex) {
        panelLoad.setVisibility(tabIndex == 0 ? View.VISIBLE : View.GONE);
        panelInfo.setVisibility(tabIndex == 1 ? View.VISIBLE : View.GONE);
        panelSettings.setVisibility(tabIndex == 2 ? View.VISIBLE : View.GONE);

        tabLoad.setTextColor(tabIndex == 0 ? Color.parseColor("#A832FC") : Color.parseColor("#8E8E93"));
        tabInfo.setTextColor(tabIndex == 1 ? Color.parseColor("#A832FC") : Color.parseColor("#8E8E93"));
        tabSettings.setTextColor(tabIndex == 2 ? Color.parseColor("#A832FC") : Color.parseColor("#8E8E93"));
    }
}
