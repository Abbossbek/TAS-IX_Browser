package com.ARCompany.Tas_ixBrowser.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AutoCompleteTextView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.ARCompany.Tas_ixBrowser.Model.Global;
import com.ARCompany.Tas_ixBrowser.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private SearchView searchView;
    private View root;
    private AdView adView;
    private SharedPreferences preferences;
    private final String SETTING = "setting";
    private Handler adHandler;

    @SuppressLint("HandlerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        preferences = getContext().getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        new RetrieveJson().execute("https://nbu.uz/uz/exchange-rates/json/");
        setValues(root);

        adHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (adView != null)
                    adView.loadAd();
            }
        };
        return root;
    }

    class RetrieveJson extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... urls) {
            try (InputStream is = new URL(urls[0]).openStream()) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String jsonText = readAll(rd);
                JSONArray json = new JSONArray(jsonText);
                return json;
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(JSONArray feed) {
            TextView usdText = root.findViewById(R.id.text_usd);
            TextView rubText = root.findViewById(R.id.text_rub);
            TextView eurText = root.findViewById(R.id.text_eur);
            TextView dateText = root.findViewById(R.id.text_date_currency);

            ImageView tranding_icon_usd=root.findViewById(R.id.tranding_icon_usd);
            ImageView tranding_icon_rub=root.findViewById(R.id.tranding_icon_rub);
            ImageView tranding_icon_eur=root.findViewById(R.id.tranding_icon_eur);

            Float old_usd = preferences.getFloat("usd", 0);
            Float old_rub = preferences.getFloat("rub", 0);
            Float old_eur = preferences.getFloat("eur", 0);

            String date = Calendar.getInstance().getTime().toLocaleString();
            try {
                Double usd = feed.getJSONObject(23).getDouble("cb_price");
                Double rub = feed.getJSONObject(18).getDouble("cb_price");
                Double eur = feed.getJSONObject(7).getDouble("cb_price");

                usdText.setText(String.valueOf(usd + " " + getString(R.string.sum)));
                rubText.setText(String.valueOf(rub + " " + getString(R.string.sum)));
                eurText.setText(String.valueOf(eur + " " + getString(R.string.sum)));
                dateText.setText(date);

                if(usd>old_usd){
                    tranding_icon_usd.setImageResource(R.drawable.ic_baseline_trending_up_24);
                    preferences.edit().putFloat("usd",usd.floatValue()).apply();
                    preferences.edit().putBoolean("usd_tranding", true).apply();
                }else if(usd<old_usd) {
                    tranding_icon_usd.setImageResource(R.drawable.ic_baseline_trending_down_24);
                    preferences.edit().putFloat("usd",usd.floatValue()).apply();
                    preferences.edit().putBoolean("usd_tranding", false).apply();
                }
                if(rub>old_rub){
                    tranding_icon_rub.setImageResource(R.drawable.ic_baseline_trending_up_24);
                    preferences.edit().putFloat("rub",rub.floatValue()).apply();
                    preferences.edit().putBoolean("rub_tranding", true).apply();
                }else if(rub<old_rub){
                    tranding_icon_rub.setImageResource(R.drawable.ic_baseline_trending_down_24);
                    preferences.edit().putFloat("rub",rub.floatValue()).apply();
                    preferences.edit().putBoolean("rub_tranding", false).apply();
                }
                if(eur>old_eur){
                    tranding_icon_eur.setImageResource(R.drawable.ic_baseline_trending_up_24);
                    preferences.edit().putFloat("eur",eur.floatValue()).apply();
                    preferences.edit().putBoolean("eur_tranding", true).apply();
                }else if(eur<old_eur){
                    tranding_icon_eur.setImageResource(R.drawable.ic_baseline_trending_down_24);
                    preferences.edit().putFloat("eur",eur.floatValue()).apply();
                    preferences.edit().putBoolean("eur_tranding", false).apply();
                }

                preferences.edit().putString("date_currency",date).apply();
            } catch (Exception e) {
                e.printStackTrace();
                usdText.setText(String.valueOf(old_usd + " " + getString(R.string.sum)));
                rubText.setText(String.valueOf(old_rub + " " + getString(R.string.sum)));
                eurText.setText(String.valueOf(old_eur + " " + getString(R.string.sum)));
                dateText.setText(preferences.getString("date_currency", "Internet aloqasi yo'q!"));

                if(preferences.getBoolean("usd_tranding", true)){
                    tranding_icon_usd.setImageResource(R.drawable.ic_baseline_trending_up_24);
                }else{
                    tranding_icon_usd.setImageResource(R.drawable.ic_baseline_trending_down_24);
                }
                if(preferences.getBoolean("rub_tranding", true)){
                    tranding_icon_rub.setImageResource(R.drawable.ic_baseline_trending_up_24);
                }else{
                    tranding_icon_rub.setImageResource(R.drawable.ic_baseline_trending_down_24);
                }
                if(preferences.getBoolean("eur_tranding", true)){
                    tranding_icon_eur.setImageResource(R.drawable.ic_baseline_trending_up_24);
                }else{
                    tranding_icon_eur.setImageResource(R.drawable.ic_baseline_trending_down_24);
                }
            }
        }
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private void setValues(final View root) {

        String[] sites = new String[]{"Islom", "Muslim", "AllPlay", "Kinohit", "Mover", "LoveMusic", "OnlineTV", "Vimo", "Kun", "Lex", "Borku", "Ziyonet", "MyTube", "TopMusic", "Olx", "Tribuna"};

        GridLayout gridSites = (GridLayout) root.findViewById(R.id.grid_sites);

        for (final String site : sites) {
            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_site_icon, null);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f);
            params.width = 0;
            layout.setLayoutParams(params);

            ImageView icon = layout.findViewById(R.id.home_site_icon);
            icon.setImageBitmap(getBitmapFromAssets("Images/" + site + ".jpg"));

            TextView url = layout.findViewById(R.id.home_site_url);
            url.setText(site + ".uz");

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Global.newSearch = true;
                    Global.searchText = "http://" + site + ".uz";
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_browser);
                }
            });

            gridSites.addView(layout);
        }

        searchView = (SearchView) root.findViewById(R.id.main_searchview);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.onActionViewExpanded();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Global.newSearch = true;
                s = s.trim();
                if (s.split(" ").length < 2 && URLUtil.isValidUrl(s)) {
                    Global.searchText = s;
                } else {
                    Global.searchText = URLEncoder.encode(s);
                }
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_browser);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView textView = (AutoCompleteTextView) searchView.findViewById(id);
        textView.setTextColor(Color.BLACK);
        textView.setHintTextColor(Color.LTGRAY);

        adView = new AdView(getContext(), "266300714543198_268162227690380", AdSize.BANNER_HEIGHT_50);
        AdSettings.setTestMode(false);
        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                adHandler.sendEmptyMessageDelayed(1, 15000);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                adHandler.sendEmptyMessageDelayed(1, 15000);
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        ((LinearLayout) root.findViewById(R.id.home_ad_layout)).addView(adView);
        adView.loadAd();
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }

        super.onDestroy();
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

}