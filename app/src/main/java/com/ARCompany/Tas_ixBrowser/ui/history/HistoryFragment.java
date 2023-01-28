package com.ARCompany.Tas_ixBrowser.ui.history;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ARCompany.Tas_ixBrowser.Model.MultiLanguageApp;
import com.ARCompany.Tas_ixBrowser.Model.history.HistoryDBHelper;
import com.ARCompany.Tas_ixBrowser.Model.history.HistoryListAdapter;
import com.ARCompany.Tas_ixBrowser.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

public class HistoryFragment extends Fragment {

    private HistoryViewModel mViewModel;
    private View root;
    private ExpandableListView elvHistoryList;
    private ExpandableListAdapter expandableListAdapter;
    private HistoryDBHelper historyDBHelper;
    private ImageView imgCloseAd;
    private LinearLayout layoutAd;
    private AdView adView;
    private Handler adHandler;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_history, container, false);

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

    @Override
    public void onDestroyView() {
        adView=null;
        super.onDestroyView();
    }

    private void setValues(View root) {
        imgCloseAd=(ImageView)root.findViewById(R.id.history_close_ad);
        imgCloseAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layoutAd.getVisibility()== View.GONE){
                    adView.loadAd();
                    layoutAd.setVisibility(View.VISIBLE);
                    imgCloseAd.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }else{
                    layoutAd.setVisibility(View.GONE);
                    imgCloseAd.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
            }
        });

        layoutAd=(LinearLayout)root.findViewById(R.id.history_ad_layout);
        adView = new AdView(getContext(), "266300714543198_275479650291971", AdSize.BANNER_HEIGHT_50);
        AdSettings.setTestMode(false);
        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
            }

            @Override
            public void onAdLoaded(Ad ad) {
                adHandler.sendEmptyMessageDelayed(1, 60000);
                imgCloseAd.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        layoutAd.addView(adView);
        adView.loadAd();

        historyDBHelper=new HistoryDBHelper(getContext());

        GridLayout grid = (GridLayout) root.findViewById(R.id.history_grid);

        float density = getResources().getDisplayMetrics().density;

        LinearLayout layoutClear = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_site_icon, null);
        CardView cardViewClear=layoutClear.findViewById(R.id.cardview_site_icon);
        cardViewClear.setRadius((int)(20*density));
        GridLayout.LayoutParams paramsDelete = new GridLayout.LayoutParams();
        paramsDelete.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f);
        paramsDelete.width = 0;
        layoutClear.setLayoutParams(paramsDelete);

        ImageView iconClear = layoutClear.findViewById(R.id.home_site_icon);

        iconClear.getLayoutParams().height=(int)(45*density);
        iconClear.getLayoutParams().width=(int)(45*density);
        iconClear.setImageDrawable(getContext().getDrawable(R.drawable.ic_clear_all_black_24dp));
        iconClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.title_clear_history);
                builder.setMessage(R.string.alert_clear_history);
                builder.setPositiveButton(R.string.btn_clear, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        historyDBHelper.clearHistory();
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_tools);
                    }
                });
                builder.setNegativeButton(R.string.btn_cancel, null);
                builder.create().show();
            }
        });
        TextView textDelete = layoutClear.findViewById(R.id.home_site_url);
        textDelete.setText(R.string.title_clear_history);
        grid.addView(layoutClear);

        elvHistoryList=root.findViewById(R.id.history_list);
        elvHistoryList.setPadding(0,210,0,0);

        expandableListAdapter=new HistoryListAdapter(getContext());
        elvHistoryList.setAdapter(expandableListAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        // TODO: Use the ViewModel
    }

}
