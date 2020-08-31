package com.ARCompany.Tas_ixBrowser.ui.setting;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.ARCompany.Tas_ixBrowser.AboutAppActivity;
import com.ARCompany.Tas_ixBrowser.Model.LocaleManager;
import com.ARCompany.Tas_ixBrowser.R;

import java.io.IOException;

public class SettingFragment extends Fragment {

    private SettingViewModel mViewModel;
    private SharedPreferences preferences;
    private final String SETTING = "setting";
    private View root;
    private LinearLayout layoutLang, layoutTheme, layoutAbout, layoutTelegram;
    private ImageView imageLang, imageTheme, imageAbout;
    private Switch switchOnlyTasix;
    private TextView textOnlyTasix;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_setting, container, false);
        setValues(root);


        return root;
    }

    private void setValues(View root) {
        preferences = getContext().getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        ((ImageView) root.findViewById(R.id.setting_telegram_icon)).setImageBitmap(getBitmapFromAssets("Images/telegram.png"));
        imageLang = (ImageView) root.findViewById(R.id.setting_lang_icon);
        switch (LocaleManager.getLanguagePref(getContext())) {
            case "uz":
                imageLang.setImageBitmap(getBitmapFromAssets("Images/uz.png"));
                ((TextView) root.findViewById(R.id.setting_lang_text)).setText("O'zbekcha");
                break;
            case "ru":
                imageLang.setImageBitmap(getBitmapFromAssets("Images/ru.png"));
                ((TextView) root.findViewById(R.id.setting_lang_text)).setText("Русский");
                break;
            case "en":
                imageLang.setImageBitmap(getBitmapFromAssets("Images/en.png"));
                ((TextView) root.findViewById(R.id.setting_lang_text)).setText("English");
                break;
        }
        layoutLang = (LinearLayout) root.findViewById(R.id.setting_lang_layout);
        layoutLang.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                final LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.list_setting_langs, null);

                ImageView uzlang = layout.findViewById(R.id.list_lang_uz_icon);
                uzlang.setImageBitmap(getBitmapFromAssets("Images/uz.png"));
                ImageView rulang = layout.findViewById(R.id.list_lang_ru_icon);
                rulang.setImageBitmap(getBitmapFromAssets("Images/ru.png"));
                ImageView enlang = layout.findViewById(R.id.list_lang_en_icon);
                enlang.setImageBitmap(getBitmapFromAssets("Images/en.png"));

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(layout);
                builder.setTitle(R.string.choose_lang);
                final AlertDialog dialog = builder.create();
                dialog.show();

                for (int i = 0; i < layout.getChildCount(); i++) {
                    LinearLayout layout1 = (LinearLayout) layout.getChildAt(i);
                    layout1.setTag(i);
                    layout1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            switch ((int) view.getTag()) {
                                case 0:
                                    imageLang.setImageBitmap(getBitmapFromAssets("Images/uz.png"));
                                    LocaleManager.setNewLocale(getContext(), LocaleManager.UZBEK);
                                    break;
                                case 1:
                                    imageLang.setImageBitmap(getBitmapFromAssets("Images/en.png"));
                                    LocaleManager.setNewLocale(getContext(), LocaleManager.ENGLISH);
                                    break;
                                case 2:
                                    imageLang.setImageBitmap(getBitmapFromAssets("Images/ru.png"));
                                    LocaleManager.setNewLocale(getContext(), LocaleManager.RUSSIAN);
                                    break;
                                default:
                            }
                            restartActivity();
                        }
                    });
                }
            }
        });
        imageTheme = (ImageView) root.findViewById(R.id.setting_theme_icon);
        switch (preferences.getString("theme", "green")) {
            case "black":
                imageTheme.setImageBitmap(getBitmapFromAssets("Images/black.png"));
                ((TextView) root.findViewById(R.id.setting_theme_text)).setText(R.string.theme_black);
                break;
            case "green":
                imageTheme.setImageBitmap(getBitmapFromAssets("Images/green.png"));
                ((TextView) root.findViewById(R.id.setting_theme_text)).setText(R.string.theme_green);
                break;
            case "red":
                imageTheme.setImageBitmap(getBitmapFromAssets("Images/red.png"));
                ((TextView) root.findViewById(R.id.setting_theme_text)).setText(R.string.theme_red);
                break;
            case "blue":
                imageTheme.setImageBitmap(getBitmapFromAssets("Images/blue.png"));
                ((TextView) root.findViewById(R.id.setting_theme_text)).setText(R.string.theme_blue);
                break;
            case "pink":
                imageTheme.setImageBitmap(getBitmapFromAssets("Images/pink.png"));
                ((TextView) root.findViewById(R.id.setting_theme_text)).setText(R.string.theme_pink);
                break;
            case "brown":
                imageTheme.setImageBitmap(getBitmapFromAssets("Images/brown.png"));
                ((TextView) root.findViewById(R.id.setting_theme_text)).setText(R.string.theme_brown);
                break;
        }
        layoutTheme = (LinearLayout) root.findViewById(R.id.setting_theme_layout);
        layoutTheme.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                ScrollView scrollView = new ScrollView(getContext());
                scrollView.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                final LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.list_setting_themes, null);

                ImageView blackIcon = layout.findViewById(R.id.theme_black_icon);
                blackIcon.setImageBitmap(getBitmapFromAssets("Images/black.png"));
                ImageView greenIcon = layout.findViewById(R.id.theme_green_icon);
                greenIcon.setImageBitmap(getBitmapFromAssets("Images/green.png"));
                ImageView redIcon = layout.findViewById(R.id.theme_red_icon);
                redIcon.setImageBitmap(getBitmapFromAssets("Images/red.png"));
                ImageView blueIcon = layout.findViewById(R.id.theme_blue_icon);
                blueIcon.setImageBitmap(getBitmapFromAssets("Images/blue.png"));
                ImageView pinkIcon = layout.findViewById(R.id.theme_pink_icon);
                pinkIcon.setImageBitmap(getBitmapFromAssets("Images/pink.png"));
                ImageView brownIcon = layout.findViewById(R.id.theme_brown_icon);
                brownIcon.setImageBitmap(getBitmapFromAssets("Images/brown.png"));
                scrollView.addView(layout);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(scrollView);
                builder.setTitle(R.string.choose_theme);
                final AlertDialog dialog = builder.create();
                dialog.show();

                for (int i = 0; i < layout.getChildCount(); i++) {
                    LinearLayout layout1 = (LinearLayout) layout.getChildAt(i);
                    layout1.setTag(i);
                    layout1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            switch (view.getId()) {
                                case R.id.theme_black_layout:
                                    imageTheme.setImageBitmap(getBitmapFromAssets("Images/black.png"));
                                    preferences.edit().putString("theme", "black").apply();
                                    break;
                                case R.id.theme_green_layout:
                                    imageTheme.setImageBitmap(getBitmapFromAssets("Images/green.png"));
                                    preferences.edit().putString("theme", "green").apply();
                                    break;
                                case R.id.theme_blue_layout:
                                    imageTheme.setImageBitmap(getBitmapFromAssets("Images/blue.png"));
                                    preferences.edit().putString("theme", "blue").apply();
                                    break;
                                case R.id.theme_red_layout:
                                    imageTheme.setImageBitmap(getBitmapFromAssets("Images/red.png"));
                                    preferences.edit().putString("theme", "red").apply();
                                    break;
                                case R.id.theme_pink_layout:
                                    imageTheme.setImageBitmap(getBitmapFromAssets("Images/pink.png"));
                                    preferences.edit().putString("theme", "pink").apply();
                                    break;
                                case R.id.theme_brown_layout:
                                    imageTheme.setImageBitmap(getBitmapFromAssets("Images/brown.png"));
                                    preferences.edit().putString("theme", "brown").apply();
                                    break;
                                default:
                            }
                            restartActivity();
                        }
                    });
                }
            }
        });
        boolean onlyTasix = preferences.getBoolean("onlyTasix", true);
        switchOnlyTasix = (Switch) root.findViewById(R.id.setting_onlyTasix_switch);
        switchOnlyTasix.setChecked(onlyTasix);
        textOnlyTasix = (TextView) root.findViewById(R.id.setting_onlyTasix_text);
        textOnlyTasix.setText(onlyTasix?R.string.on:R.string.off);
        switchOnlyTasix.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean("onlyTasix", b).apply();
                textOnlyTasix.setText(b?R.string.on:R.string.off);
            }
        });

        layoutAbout = (LinearLayout) root.findViewById(R.id.setting_about_layout);
        layoutAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AboutAppActivity.class));
            }
        });
        layoutTelegram = (LinearLayout) root.findViewById(R.id.setting_telegram_layout);
        layoutTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://t.me/TASIXBrowser"));
                startActivity(Intent.createChooser(intent, getString(R.string.telegram_channel)));
            }
        });

    }

    private void restartActivity() {
        getActivity().finish();
        Intent intent = getActivity().getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private Bitmap getBitmapFromAssets(String fileName) {
        AssetManager assetManager = getActivity().getAssets();
        try {
            return BitmapFactory.decodeStream(assetManager.open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
        // TODO: Use the ViewModel
    }

}
