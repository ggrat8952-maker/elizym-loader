package com.elizym.id;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class floatingservice extends Service {
    private WindowManager windowManager;
    private LinearLayout iosHomeBar;
    private LinearLayout menuLayout;
    private LinearLayout aimCircle;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 1. ПОЛОСКА iPHONE ДЛЯ ОТКРЫТИЯ МЕНЮ (СВЕТЛАЯ ЛИНИЯ ВНИЗУ)
        final WindowManager.LayoutParams paramsLine = new WindowManager.LayoutParams(
            200, 16, 
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        paramsLine.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        paramsLine.y = 100;

        iosHomeBar = new LinearLayout(this);
        GradientDrawable lineStyle = new GradientDrawable();
        lineStyle.setColor(Color.parseColor("#E5E5EA"));
        lineStyle.setCornerRadius(15f);
        iosHomeBar.setBackground(lineStyle);
        windowManager.addView(iosHomeBar, paramsLine);

        // 2. СТРУКТУРА МЕНЮ (МАТОВОЕ СТЕКЛО + ФИОЛЕТОВЫЙ НЕОН)
        menuLayout = new LinearLayout(this);
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        menuLayout.setPadding(40, 40, 40, 40);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            menuLayout.setRenderEffect(android.graphics.RenderEffect.createBlurEffect(25f, 25f, android.graphics.Shader.TileMode.CLAMP));
        }

        final GradientDrawable menuShape = new GradientDrawable();
        menuShape.setCornerRadius(45f);
        menuShape.setColor(Color.parseColor("#B3101012"));
        menuShape.setStroke(4, Color.parseColor("#A832FC"));
        menuLayout.setBackground(menuShape);

        // Эффект падающего снега (лёгкое мерцание прозрачности меню)
        final int[] ticks = {0};
        final Handler snowHandler = new Handler(Looper.getMainLooper());
        snowHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ticks++;
                float alpha = 0.7f + (float)Math.sin(ticks * 0.4) * 0.12f;
                menuLayout.setAlpha(alpha);
                snowHandler.postDelayed(this, 150);
            }
        }, 150);

        // Вотермарка Elizym вверху панели софта
        TextView watermark = new TextView(this);
        watermark.setText("elizym.cc | Premium Build | 120 FPS");
        watermark.setTextColor(Color.parseColor("#A832FC"));
        watermark.setTextSize(12);
        watermark.setPadding(0, 0, 0, 25);
        menuLayout.addView(watermark);

        // Визуальный тумблер ВХ
        Switch switchEsp = new Switch(this);
        switchEsp.setText("Enable External ESP (ВХ)  ");
        switchEsp.setTextColor(Color.WHITE);
        menuLayout.addView(switchEsp);

        // Визуальный тумблер Аима
        Switch switchAim = new Switch(this);
        switchAim.setText("Silent Aimbot (Аим)  ");
        switchAim.setTextColor(Color.WHITE);
        menuLayout.addView(switchAim);

        // 3. ВИЗУАЛЬНЫЙ КРУГ АИМА (FOV) ПО ЦЕНТРУ ЭКРАНА
        final WindowManager.LayoutParams paramsCircle = new WindowManager.LayoutParams(
            420, 420,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        );
        paramsCircle.gravity = Gravity.CENTER;

        aimCircle = new LinearLayout(this);
        GradientDrawable circleDrawable = new GradientDrawable();
        circleDrawable.setColor(Color.TRANSPARENT);
        circleDrawable.setStroke(3, Color.parseColor("#A832FC"));
        circleDrawable.setCornerRadius(210f);
        aimCircle.setBackground(circleDrawable);
        aimCircle.setVisibility(View.GONE);
        windowManager.addView(aimCircle, paramsCircle);

        // Логика включения круга при активации тумблера
        switchAim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                aimCircle.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        // 4. МЕНЮ СКИНЧЕНДЖЕРА (ВЫБОР НОЖЕЙ)
        TextView skinTitle = new TextView(this);
        skinTitle.setText("\n[ SKIN CHANGER ]");
        skinTitle.setTextColor(Color.GRAY);
        menuLayout.addView(skinTitle);

        final Spinner knifeSpinner = new Spinner(this);
        final String[] knives = {"Выберите нож", "M9 Bayonet", "Butterfly", "Scorpion"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, knives);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        knifeSpinner.setAdapter(adapter);
        menuLayout.addView(knifeSpinner);

        final Spinner skinSpinner = new Spinner(this);
        menuLayout.addView(skinSpinner);

        knifeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = knives[position];
                String[] skins = {"Нет скинов"};
                if (selected.equals("M9 Bayonet")) skins = new String[]{"Scratch", "Dragon Glass", "Ancient"};
                if (selected.equals("Butterfly")) skins = new String[]{"Legacy", "Starfall", "Dragon Glass"};
                if (selected.equals("Scorpion")) skins = new String[]{"Scratch", "Cold Flame"};
                
                ArrayAdapter<String> skinAdapter = new ArrayAdapter<String>(floatingservice.this, android.R.layout.simple_spinner_item, skins);
                skinSpinner.setAdapter(skinAdapter);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Кнопка сворачивания меню назад в полоску
        Button hideBtn = new Button(this);
        hideBtn.setText("Свернуть меню");
        hideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(menuLayout);
                windowManager.addView(iosHomeBar, paramsLine);
            }
        });
        menuLayout.addView(hideBtn);

        // Открытие меню по клику на полоску айфона
        final WindowManager.LayoutParams paramsMenu = new WindowManager.LayoutParams(
            450, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        paramsMenu.gravity = Gravity.CENTER;

        iosHomeBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(iosHomeBar);
                windowManager.addView(menuLayout, paramsMenu);
            }
        });

        // 5. ТАЙМЕР НА 2 МИНУТЫ (ГРОЗНОЕ ПРЕДУПРЕЖДЕНИЕ)
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (menuLayout != null && menuLayout.isShown()) {
                    menuLayout.removeAllViews();
                    aimCircle.setVisibility(View.GONE);
                    
                    GradientDrawable alertBox = new GradientDrawable();
                    alertBox.setCornerRadius(30f);
                    alertBox.setColor(Color.parseColor("#1C1C1E"));
                    alertBox.setStroke(4, Color.RED);
                    menuLayout.setBackground(alertBox);
                    
                    TextView alertText = new TextView(floatingservice.this);
                    alertText.setText("СИСТЕМНОЕ УВЕДОМЛЕНИЕ\n\nПривет, друг! Ты скачал неофициальное или же стороннее ПО!\n\n" +
                                     "Это нарушает конфиденциальность нашей игры! Просим вас удалить стороннее ПО!\n\n" +
                                     "Если вы не удалите, нам придётся вам выдать блокировку навсегда!\n\n" +
                                     "С уважением, команда StandRise.");
                    alertText.setTextColor(Color.WHITE);
                    alertText.setTextSize(14);
                    menuLayout.addView(alertText);
                    
                    Button closeBtn = new Button(floatingservice.this);
                    closeBtn.setText("ОК / ЗАКРЫТЬ");
                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v2) {
                            stopSelf();
                        }
                    });
                    menuLayout.addView(closeBtn);
                }
            }
        }, 120000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iosHomeBar != null && iosHomeBar.isShown()) windowManager.removeView(iosHomeBar);
          if (menuLayout != null && menuLayout.isShown()) windowManager.removeView(menuLayout);
if (aimCircle != null && aimCircle.isShown()) windowManager.removeView(aimCircle);
}
}
