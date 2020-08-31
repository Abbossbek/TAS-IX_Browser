package com.ARCompany.Tas_ixBrowser.Model.history;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;

import com.ARCompany.Tas_ixBrowser.Model.Global;
import com.ARCompany.Tas_ixBrowser.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private HistoryDBHelper historyDBHelper;
    private List<HistoryWebPage> history_of_today = new ArrayList<>(), history_of_this_week = new ArrayList<>(), history_of_this_month = new ArrayList<>();
    private SimpleDateFormat day = new SimpleDateFormat("DDD");
    private SimpleDateFormat week = new SimpleDateFormat("ww");
    private SimpleDateFormat month = new SimpleDateFormat("MM");

    public HistoryListAdapter(Context context) {
        this.context = context;
        historyDBHelper = new HistoryDBHelper(context);
        loadDB();
    }

    private void loadDB() {
        history_of_today.clear();
        history_of_this_week.clear();
        history_of_this_month.clear();
        historyDBHelper = new HistoryDBHelper(context);

        for (HistoryWebPage webPage : historyDBHelper.loadHistory()) {
            if (day.format(webPage.getDate()).equals(day.format(Calendar.getInstance().getTime()))) {
                history_of_today.add(0,webPage);
                history_of_this_week.add(0,webPage);
                history_of_this_month.add(0,webPage);
            } else {
                if (week.format(webPage.getDate()).equals(week.format(Calendar.getInstance().getTime()))) {
                    history_of_this_week.add(0,webPage);
                    history_of_this_month.add(0,webPage);
                } else {
                    if (month.format(webPage.getDate()).equals(month.format(Calendar.getInstance().getTime()))) {
                        history_of_this_month.add(0,webPage);
                    } else {
                        historyDBHelper.deleteHistory(webPage.getId());
                    }
                }
            }
        }
    }

    @Override
    public int getGroupCount() {
        return 3;
    }

    @Override
    public int getChildrenCount(int i) {
        int size = ((List<HistoryWebPage>) getGroup(i)).size();
        return size;
    }

    @Override
    public Object getGroup(int i) {
        switch (i) {
            case 0:
                return history_of_today;
            case 1:
                return history_of_this_week;
            case 2:
                return history_of_this_month;
            default:
                return null;
        }
    }

    public String getGroupName(int i) {
        switch (i) {
            case 0:
                return context.getResources().getString(R.string.history_today);
            case 1:
                return context.getResources().getString(R.string.history_week);
            case 2:
                return context.getResources().getString(R.string.history_month);
            default:
                return null;
        }
    }

    @Override
    public Object getChild(int i, int i1) {
        List<HistoryWebPage> webPageList = (List<HistoryWebPage>) getGroup(i);
        return webPageList.size() > i1 ? webPageList.get(i1) : null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(context.getResources().getLayout(R.layout.list_history_period), null);
        }

        TextView groupName = view.findViewById(R.id.period_name);
        groupName.setText(getGroupName(i));
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final HistoryWebPage webPage = (HistoryWebPage) getChild(i, i1);
        if(webPage!=null) {

            if (view == null || ((LinearLayout) view).getChildCount() == 0) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(context.getResources().getLayout(R.layout.list_history_website), null);
            }

            LinearLayout linearLayoutVertical = view.findViewById(R.id.history_linear_layout);
            linearLayoutVertical.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Global.newSearch = true;
                    Global.searchText = webPage.getUrl();
                    Navigation.findNavController((Activity) context, R.id.nav_host_fragment).navigate(R.id.navigation_browser);
                }
            });

            TextView title = view.findViewById(R.id.history_title);
            title.setText(webPage.getName());

            TextView url = view.findViewById(R.id.history_url);
            url.setText(webPage.getUrl());

            Button button = view.findViewById(R.id.history_delete);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LinearLayout) v.getParent().getParent()).removeView((LinearLayout) v.getParent());
                    historyDBHelper.deleteHistory(webPage.getId());
                    loadDB();
                }
            });
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
