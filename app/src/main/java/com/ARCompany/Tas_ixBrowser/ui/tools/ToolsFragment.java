package com.ARCompany.Tas_ixBrowser.ui.tools;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ARCompany.Tas_ixBrowser.Model.MultiLanguageApp;
import com.ARCompany.Tas_ixBrowser.Model.ViewPagerAdapter;
import com.ARCompany.Tas_ixBrowser.R;
import com.ARCompany.Tas_ixBrowser.ui.history.HistoryFragment;
import com.ARCompany.Tas_ixBrowser.ui.notes.NotesFragment;
import com.google.android.material.tabs.TabLayout;

public class ToolsFragment extends Fragment {

    private ToolsViewModel mViewModel;
    private View root;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static ToolsFragment newInstance() {
        return new ToolsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_tools, container, false);

        tabLayout=root.findViewById(R.id.tools_tablayout);
        viewPager=root.findViewById(R.id.tools_viewpager);

        ViewPagerAdapter adapter=new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.add(new NotesFragment(), getResources().getString(R.string.title_notes));
        adapter.add(new HistoryFragment(), getResources().getString(R.string.title_history));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return  root;
    }
}
