package com.ARCompany.Tas_ixBrowser.ui.notes;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ARCompany.Tas_ixBrowser.Model.MultiLanguageApp;
import com.ARCompany.Tas_ixBrowser.Model.Global;
import com.ARCompany.Tas_ixBrowser.Model.note.Note;
import com.ARCompany.Tas_ixBrowser.Model.note.NoteDBHelper;
import com.ARCompany.Tas_ixBrowser.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

public class NotesFragment extends Fragment {

    private NotesViewModel mViewModel;
    private View root;
    private LinearLayout notesLayout;
    private NoteDBHelper noteDBHelper;
    private boolean selectEnabled = false;
    private AdView adView;
    private LinearLayout layoutAd;
    private ImageView imgCloseAd;
    private final String SETTING = "setting";
    private SharedPreferences preferences;
    private boolean hasRateAlertOpened;
    private Handler adHandler;

    public static NotesFragment newInstance() {
        return new NotesFragment();
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notes, container, false);

        preferences = getContext().getSharedPreferences(SETTING, Context.MODE_PRIVATE);
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
        imgCloseAd=(ImageView)root.findViewById(R.id.notes_close_ad);
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

        layoutAd=(LinearLayout)root.findViewById(R.id.notes_ad_layout);
        adView = new AdView(getContext(), "266300714543198_275478816958721", AdSize.BANNER_HEIGHT_50);
        AdSettings.setTestMode(false);
        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {

                adHandler.sendEmptyMessageDelayed(1, 15000);
            }

            @Override
            public void onAdLoaded(Ad ad) {
//                layoutAd.setVisibility(View.VISIBLE);

                adHandler.sendEmptyMessageDelayed(1, 15000);
                imgCloseAd.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                int openCount=preferences.getInt("openCount", 0);
                if(openCount>50) {
                    hasRateAlertOpened = preferences.getBoolean("hasRateAlertOpened", false);
                    if(!hasRateAlertOpened){
                        layoutAd.setVisibility(View.GONE);
                        imgCloseAd.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.opinion_about_app);
                        builder.setMessage(getString(R.string.rate_alert_message_1));
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlertDialog.Builder builder1=new AlertDialog.Builder(getContext());
                                builder1.setTitle(R.string.opinion_about_app);
                                builder1.setMessage(getString(R.string.rate_alert_message_2));
                                builder1.setPositiveButton(R.string.ok_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        AlertDialog.Builder builder2=new AlertDialog.Builder(getContext());
                                        builder2.setTitle(R.string.rate_app);
                                        builder2.setMessage(getString(R.string.rate_alert_message_3));
                                        builder2.setPositiveButton(R.string.rate, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                preferences.edit().putBoolean("hasRateAlertOpened", true).apply();

                                                Intent intent=new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.ARCompany.Tas_ixBrowser"));
                                                Intent chooser = Intent.createChooser(intent, getString(R.string.rate_app));
                                                startActivity(chooser);
                                            }
                                        })
                                                .setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        preferences.edit().putInt("openCount",0).apply();
                                                    }
                                                });

                                        builder2.create().show();

                                    }
                                }).setNeutralButton(R.string.later, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        preferences.edit().putInt("openCount",0).apply();
                                    }
                                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        preferences.edit().putBoolean("hasRateAlertOpened", true).apply();
                                    }
                                });
                                builder1.create().show();
                            }
                        });
                        builder.setNeutralButton(R.string.later, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                preferences.edit().putInt("openCount",0).apply();
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                preferences.edit().putBoolean("hasRateAlertOpened", true).apply();
                            }
                        });
                        builder.create().show();
                    }else{
                        preferences.edit().putInt("openCount",0).apply();
                    }
                }
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

        GridLayout grid = (GridLayout) root.findViewById(R.id.notes_grid);

        float density = getResources().getDisplayMetrics().density;

        LinearLayout layoutAdd = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_site_icon, null);
        CardView cardViewAdd=layoutAdd.findViewById(R.id.cardview_site_icon);
        cardViewAdd.setRadius((int)(20*density));
        GridLayout.LayoutParams paramsAdd = new GridLayout.LayoutParams();
        paramsAdd.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f);
        paramsAdd.width = 0;
        layoutAdd.setLayoutParams(paramsAdd);

        ImageView iconAdd = layoutAdd.findViewById(R.id.home_site_icon);
        iconAdd.setImageDrawable(getContext().getDrawable(R.drawable.ic_add_white_24dp));

        iconAdd.getLayoutParams().height=(int)(45*density);
        iconAdd.getLayoutParams().width=(int)(45*density);
        iconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.layout_add_note, null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(layout);
                builder.setTitle(R.string.note_add);
                builder.setPositiveButton(R.string.btn_add, null);
                builder.setNegativeButton(R.string.btn_cancel, null);
                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialogInterface) {
                        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("ShowToast")
                            @Override
                            public void onClick(View view) {
                                String title = ((EditText) layout.findViewById(R.id.input_note_title)).getText().toString();
                                String url = ((EditText) layout.findViewById(R.id.input_note_url)).getText().toString();
                                if (title.isEmpty() || url.isEmpty()) {
                                    dialog.setTitle(R.string.toastr_empty_text);
                                    dialog.setIcon(R.drawable.ic_warning_black_24dp);
                                    return;
                                }
                                if (!URLUtil.isValidUrl(url)) {
                                    dialog.setTitle(R.string.toastr_incorrect_url);
                                    dialog.setIcon(R.drawable.ic_warning_black_24dp);
                                    return;
                                }
                                noteDBHelper.addNote(title, url);
                                loadNotes();
                                dialogInterface.dismiss();
                            }
                        });
                    }
                });
                dialog.show();
            }
        });

        TextView textAdd = layoutAdd.findViewById(R.id.home_site_url);
        textAdd.setText(R.string.title_add_note);
        grid.addView(layoutAdd);

        LinearLayout layoutDelete = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_site_icon, null);
        CardView cardViewDelete=layoutDelete.findViewById(R.id.cardview_site_icon);
        cardViewDelete.setRadius(50f);
        GridLayout.LayoutParams paramsDelete = new GridLayout.LayoutParams();
        paramsDelete.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f);
        paramsDelete.width = 0;
        layoutDelete.setLayoutParams(paramsDelete);

        ImageView iconDelete = layoutDelete.findViewById(R.id.home_site_icon);
        iconDelete.getLayoutParams().height=(int)(45*density);
        iconDelete.getLayoutParams().width=(int)(45*density);
        iconDelete.setImageDrawable(getContext().getDrawable(R.drawable.ic_delete_white_24dp));
        iconDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectEnabled) {
                    selectEnabled = true;
                    for (int i = 0; i < notesLayout.getChildCount(); i++) {
                        LinearLayout cardLayout = (LinearLayout) notesLayout.getChildAt(i);
                        CheckBox checkBox = cardLayout.findViewById(R.id.cardview_note_check);
                        checkBox.setVisibility(View.VISIBLE);
                        ((ImageView)view).setImageDrawable(getContext().getDrawable(R.drawable.ic_delete_forever_white_24dp));
                    }
                } else {
                    selectEnabled = false;
                    for (int i = 0; i < notesLayout.getChildCount(); i++) {
                        LinearLayout cardLayout = (LinearLayout) notesLayout.getChildAt(i);
                        CheckBox checkBox = cardLayout.findViewById(R.id.cardview_note_check);
                        if (checkBox.isChecked()) {
                            i--;
                            notesLayout.removeView(cardLayout);
                            noteDBHelper.deleteNote(Integer.parseInt(cardLayout.getTag().toString()));
                        } else {
                            checkBox.setVisibility(View.GONE);
                        }
                    }
                    ((ImageView)view).setImageDrawable(getContext().getDrawable(R.drawable.ic_delete_white_24dp));
                }
            }
        });
        TextView textDelete = layoutDelete.findViewById(R.id.home_site_url);
        textDelete.setText(R.string.title_delete_note);
        grid.addView(layoutDelete);

        notesLayout = (LinearLayout) root.findViewById(R.id.notes_layout);
        notesLayout.setPadding(0,210,0,0);

        noteDBHelper = new NoteDBHelper(getContext());

        loadNotes();
    }

    private void loadNotes() {
        notesLayout.removeAllViews();
        for (final Note note : noteDBHelper.loadNotes()) {
            LinearLayout cardLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.card_view_note, null);
            cardLayout.setTag(note.getId());

            CardView cardView = (CardView) cardLayout.findViewById(R.id.cardview_note);

            LinearLayout linearLayout = cardView.findViewById(R.id.cardview_note_layout);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectEnabled) {
                        LinearLayout layout = (LinearLayout) view.getParent();
                        CheckBox checkBox = layout.findViewById(R.id.cardview_note_check);
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(false);
                        } else {
                            checkBox.setChecked(true);
                        }
                    } else {
                        Global.newSearch = true;
                        Global.searchText = note.getUrl();
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_browser);
                    }
                }
            });

            TextView title = linearLayout.findViewById(R.id.cardview_note_title);
            title.setText(note.getName());

            TextView url = linearLayout.findViewById(R.id.cardview_note_url);
            url.setText(note.getUrl());

            notesLayout.addView(cardLayout);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NotesViewModel.class);
        // TODO: Use the ViewModel
    }

}
