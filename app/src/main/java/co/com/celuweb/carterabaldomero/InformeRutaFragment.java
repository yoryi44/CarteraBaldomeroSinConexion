package co.com.celuweb.carterabaldomero;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import businessObject.DataBaseBO;
import dataobject.Usuario;
import utilidades.Utilidades;

public class InformeRutaFragment extends Fragment  {
    private View viewInformeRutaFragment;
    private ArrayList<String> emails = new ArrayList<>();

    private RecyclerView recyclerViewEmail;

    private String userAux = "";
    private Usuario user = new Usuario();

    public InformeRutaFragment() {/* Required empty public constructor*/}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewInformeRutaFragment = inflater.inflate(R.layout.fragment_informe_ruta, container, false);

       bannersUsiarios();

        // Inflate the layout for this fragment
        return viewInformeRutaFragment;
    }


    private void bannersUsiarios() {

        String empresa = "";

        empresa = DataBaseBO.cargarEmpresa(InformeRutaFragment.this.getContext());

        if (empresa.equals("AGCO")) {

            LinearLayout li = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout);
            li.setBackgroundResource(R.color.agco);
            LinearLayout li2 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout2);
            li2.setBackgroundResource(R.color.agco);
            LinearLayout li3 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout3);
            li3.setBackgroundResource(R.color.agco);
            LinearLayout li4 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout4);
            li4.setBackgroundResource(R.color.agco);
            LinearLayout li5 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout5);
            li5.setBackgroundResource(R.color.agco);
            LinearLayout li6 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout6);
            li6.setBackgroundResource(R.color.agco);
            LinearLayout li7 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout7);
            li7.setBackgroundResource(R.color.agco);
            ActionBar barVista = new ActionBar() {
                @Override
                public void setCustomView(View view) {

                }

                @Override
                public void setCustomView(View view, LayoutParams layoutParams) {

                }

                @Override
                public void setCustomView(int resId) {

                }

                @Override
                public void setIcon(int resId) {

                }

                @Override
                public void setIcon(Drawable icon) {

                }

                @Override
                public void setLogo(int resId) {

                }

                @Override
                public void setLogo(Drawable logo) {

                }

                @Override
                public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener callback) {

                }

                @Override
                public void setSelectedNavigationItem(int position) {

                }

                @Override
                public int getSelectedNavigationIndex() {
                    return 0;
                }

                @Override
                public int getNavigationItemCount() {
                    return 0;
                }

                @Override
                public void setTitle(CharSequence title) {

                }

                @Override
                public void setTitle(int resId) {

                }

                @Override
                public void setSubtitle(CharSequence subtitle) {

                }

                @Override
                public void setSubtitle(int resId) {

                }

                @Override
                public void setDisplayOptions(int options) {

                }

                @Override
                public void setDisplayOptions(int options, int mask) {

                }

                @Override
                public void setDisplayUseLogoEnabled(boolean useLogo) {

                }

                @Override
                public void setDisplayShowHomeEnabled(boolean showHome) {

                }

                @Override
                public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {

                }

                @Override
                public void setDisplayShowTitleEnabled(boolean showTitle) {

                }

                @Override
                public void setDisplayShowCustomEnabled(boolean showCustom) {

                }

                @Override
                public void setBackgroundDrawable(@Nullable Drawable d) {

                }

                @Override
                public View getCustomView() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getTitle() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getSubtitle() {
                    return null;
                }

                @SuppressLint("WrongConstant")
                @Override
                public int getNavigationMode() {
                    return 0;
                }

                @Override
                public void setNavigationMode(int mode) {

                }

                @Override
                public int getDisplayOptions() {
                    return 0;
                }

                @Override
                public Tab newTab() {
                    return null;
                }

                @Override
                public void addTab(Tab tab) {

                }

                @Override
                public void addTab(Tab tab, boolean setSelected) {

                }

                @Override
                public void addTab(Tab tab, int position) {

                }

                @Override
                public void addTab(Tab tab, int position, boolean setSelected) {

                }

                @Override
                public void removeTab(Tab tab) {

                }

                @Override
                public void removeTabAt(int position) {

                }

                @Override
                public void removeAllTabs() {

                }

                @Override
                public void selectTab(Tab tab) {

                }

                @Nullable
                @Override
                public Tab getSelectedTab() {
                    return null;
                }

                @Override
                public Tab getTabAt(int index) {
                    return null;
                }

                @Override
                public int getTabCount() {
                    return 0;
                }

                @Override
                public int getHeight() {
                    return 0;
                }

                @Override
                public void show() {

                }

                @Override
                public void hide() {

                }

                @Override
                public boolean isShowing() {
                    return false;
                }

                @Override
                public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

                @Override
                public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

            };
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#A6194525")));

        } else if (empresa.equals("AGSC")) {

            LinearLayout li = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout);
            li.setBackgroundResource(R.color.agsc);
            LinearLayout li2 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout2);
            li2.setBackgroundResource(R.color.agsc);
            LinearLayout li3 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout3);
            li3.setBackgroundResource(R.color.agsc);
            LinearLayout li4 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout4);
            li4.setBackgroundResource(R.color.agsc);
            LinearLayout li5 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout5);
            li5.setBackgroundResource(R.color.agsc);
            LinearLayout li6 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout6);
            li6.setBackgroundResource(R.color.agsc);
            LinearLayout li7 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout7);
            li7.setBackgroundResource(R.color.agsc);
            ActionBar barVista = new ActionBar() {
                @Override
                public void setCustomView(View view) {

                }

                @Override
                public void setCustomView(View view, LayoutParams layoutParams) {

                }

                @Override
                public void setCustomView(int resId) {

                }

                @Override
                public void setIcon(int resId) {

                }

                @Override
                public void setIcon(Drawable icon) {

                }

                @Override
                public void setLogo(int resId) {

                }

                @Override
                public void setLogo(Drawable logo) {

                }

                @Override
                public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener callback) {

                }

                @Override
                public void setSelectedNavigationItem(int position) {

                }

                @Override
                public int getSelectedNavigationIndex() {
                    return 0;
                }

                @Override
                public int getNavigationItemCount() {
                    return 0;
                }

                @Override
                public void setTitle(CharSequence title) {

                }

                @Override
                public void setTitle(int resId) {

                }

                @Override
                public void setSubtitle(CharSequence subtitle) {

                }

                @Override
                public void setSubtitle(int resId) {

                }

                @Override
                public void setDisplayOptions(int options) {

                }

                @Override
                public void setDisplayOptions(int options, int mask) {

                }

                @Override
                public void setDisplayUseLogoEnabled(boolean useLogo) {

                }

                @Override
                public void setDisplayShowHomeEnabled(boolean showHome) {

                }

                @Override
                public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {

                }

                @Override
                public void setDisplayShowTitleEnabled(boolean showTitle) {

                }

                @Override
                public void setDisplayShowCustomEnabled(boolean showCustom) {

                }

                @Override
                public void setBackgroundDrawable(@Nullable Drawable d) {

                }

                @Override
                public View getCustomView() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getTitle() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getSubtitle() {
                    return null;
                }

                @SuppressLint("WrongConstant")
                @Override
                public int getNavigationMode() {
                    return 0;
                }

                @Override
                public void setNavigationMode(int mode) {

                }

                @Override
                public int getDisplayOptions() {
                    return 0;
                }

                @Override
                public Tab newTab() {
                    return null;
                }

                @Override
                public void addTab(Tab tab) {

                }

                @Override
                public void addTab(Tab tab, boolean setSelected) {

                }

                @Override
                public void addTab(Tab tab, int position) {

                }

                @Override
                public void addTab(Tab tab, int position, boolean setSelected) {

                }

                @Override
                public void removeTab(Tab tab) {

                }

                @Override
                public void removeTabAt(int position) {

                }

                @Override
                public void removeAllTabs() {

                }

                @Override
                public void selectTab(Tab tab) {

                }

                @Nullable
                @Override
                public Tab getSelectedTab() {
                    return null;
                }

                @Override
                public Tab getTabAt(int index) {
                    return null;
                }

                @Override
                public int getTabCount() {
                    return 0;
                }

                @Override
                public int getHeight() {
                    return 0;
                }

                @Override
                public void show() {

                }

                @Override
                public void hide() {

                }

                @Override
                public boolean isShowing() {
                    return false;
                }

                @Override
                public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

                @Override
                public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

            };
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B4DF2E22")));

        } else if (empresa.equals("AABR")) {

            LinearLayout li = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout);
            li.setBackgroundResource(R.color.aabr);
            LinearLayout li2 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout2);
            li2.setBackgroundResource(R.color.aabr);
            LinearLayout li3 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout3);
            li3.setBackgroundResource(R.color.aabr);
            LinearLayout li4 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout4);
            li4.setBackgroundResource(R.color.aabr);
            LinearLayout li5 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout5);
            li5.setBackgroundResource(R.color.aabr);
            LinearLayout li6 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout6);
            li6.setBackgroundResource(R.color.aabr);
            LinearLayout li7 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout7);
            li7.setBackgroundResource(R.color.aabr);
            ActionBar barVista = new ActionBar() {
                @Override
                public void setCustomView(View view) {

                }

                @Override
                public void setCustomView(View view, LayoutParams layoutParams) {

                }

                @Override
                public void setCustomView(int resId) {

                }

                @Override
                public void setIcon(int resId) {

                }

                @Override
                public void setIcon(Drawable icon) {

                }

                @Override
                public void setLogo(int resId) {

                }

                @Override
                public void setLogo(Drawable logo) {

                }

                @Override
                public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener callback) {

                }

                @Override
                public void setSelectedNavigationItem(int position) {

                }

                @Override
                public int getSelectedNavigationIndex() {
                    return 0;
                }

                @Override
                public int getNavigationItemCount() {
                    return 0;
                }

                @Override
                public void setTitle(CharSequence title) {

                }

                @Override
                public void setTitle(int resId) {

                }

                @Override
                public void setSubtitle(CharSequence subtitle) {

                }

                @Override
                public void setSubtitle(int resId) {

                }

                @Override
                public void setDisplayOptions(int options) {

                }

                @Override
                public void setDisplayOptions(int options, int mask) {

                }

                @Override
                public void setDisplayUseLogoEnabled(boolean useLogo) {

                }

                @Override
                public void setDisplayShowHomeEnabled(boolean showHome) {

                }

                @Override
                public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {

                }

                @Override
                public void setDisplayShowTitleEnabled(boolean showTitle) {

                }

                @Override
                public void setDisplayShowCustomEnabled(boolean showCustom) {

                }

                @Override
                public void setBackgroundDrawable(@Nullable Drawable d) {

                }

                @Override
                public View getCustomView() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getTitle() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getSubtitle() {
                    return null;
                }

                @SuppressLint("WrongConstant")
                @Override
                public int getNavigationMode() {
                    return 0;
                }

                @Override
                public void setNavigationMode(int mode) {

                }

                @Override
                public int getDisplayOptions() {
                    return 0;
                }

                @Override
                public Tab newTab() {
                    return null;
                }

                @Override
                public void addTab(Tab tab) {

                }

                @Override
                public void addTab(Tab tab, boolean setSelected) {

                }

                @Override
                public void addTab(Tab tab, int position) {

                }

                @Override
                public void addTab(Tab tab, int position, boolean setSelected) {

                }

                @Override
                public void removeTab(Tab tab) {

                }

                @Override
                public void removeTabAt(int position) {

                }

                @Override
                public void removeAllTabs() {

                }

                @Override
                public void selectTab(Tab tab) {

                }

                @Nullable
                @Override
                public Tab getSelectedTab() {
                    return null;
                }

                @Override
                public Tab getTabAt(int index) {
                    return null;
                }

                @Override
                public int getTabCount() {
                    return 0;
                }

                @Override
                public int getHeight() {
                    return 0;
                }

                @Override
                public void show() {

                }

                @Override
                public void hide() {

                }

                @Override
                public boolean isShowing() {
                    return false;
                }

                @Override
                public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

                @Override
                public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

            };
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B4DF2E22")));

        } else if (empresa.equals("ADHB")) {

            LinearLayout li = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout);
            li.setBackgroundResource(R.color.adhb);
            LinearLayout li2 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout2);
            li2.setBackgroundResource(R.color.adhb);
            LinearLayout li3 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout3);
            li3.setBackgroundResource(R.color.adhb);
            LinearLayout li4 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout4);
            li4.setBackgroundResource(R.color.adhb);
            LinearLayout li5 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout5);
            li5.setBackgroundResource(R.color.adhb);
            LinearLayout li6 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout6);
            li6.setBackgroundResource(R.color.adhb);
            LinearLayout li7 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout7);
            li7.setBackgroundResource(R.color.adhb);
            ActionBar barVista = new ActionBar() {
                @Override
                public void setCustomView(View view) {

                }

                @Override
                public void setCustomView(View view, LayoutParams layoutParams) {

                }

                @Override
                public void setCustomView(int resId) {

                }

                @Override
                public void setIcon(int resId) {

                }

                @Override
                public void setIcon(Drawable icon) {

                }

                @Override
                public void setLogo(int resId) {

                }

                @Override
                public void setLogo(Drawable logo) {

                }

                @Override
                public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener callback) {

                }

                @Override
                public void setSelectedNavigationItem(int position) {

                }

                @Override
                public int getSelectedNavigationIndex() {
                    return 0;
                }

                @Override
                public int getNavigationItemCount() {
                    return 0;
                }

                @Override
                public void setTitle(CharSequence title) {

                }

                @Override
                public void setTitle(int resId) {

                }

                @Override
                public void setSubtitle(CharSequence subtitle) {

                }

                @Override
                public void setSubtitle(int resId) {

                }

                @Override
                public void setDisplayOptions(int options) {

                }

                @Override
                public void setDisplayOptions(int options, int mask) {

                }

                @Override
                public void setDisplayUseLogoEnabled(boolean useLogo) {

                }

                @Override
                public void setDisplayShowHomeEnabled(boolean showHome) {

                }

                @Override
                public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {

                }

                @Override
                public void setDisplayShowTitleEnabled(boolean showTitle) {

                }

                @Override
                public void setDisplayShowCustomEnabled(boolean showCustom) {

                }

                @Override
                public void setBackgroundDrawable(@Nullable Drawable d) {

                }

                @Override
                public View getCustomView() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getTitle() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getSubtitle() {
                    return null;
                }

                @SuppressLint("WrongConstant")
                @Override
                public int getNavigationMode() {
                    return 0;
                }

                @Override
                public void setNavigationMode(int mode) {

                }

                @Override
                public int getDisplayOptions() {
                    return 0;
                }

                @Override
                public Tab newTab() {
                    return null;
                }

                @Override
                public void addTab(Tab tab) {

                }

                @Override
                public void addTab(Tab tab, boolean setSelected) {

                }

                @Override
                public void addTab(Tab tab, int position) {

                }

                @Override
                public void addTab(Tab tab, int position, boolean setSelected) {

                }

                @Override
                public void removeTab(Tab tab) {

                }

                @Override
                public void removeTabAt(int position) {

                }

                @Override
                public void removeAllTabs() {

                }

                @Override
                public void selectTab(Tab tab) {

                }

                @Nullable
                @Override
                public Tab getSelectedTab() {
                    return null;
                }

                @Override
                public Tab getTabAt(int index) {
                    return null;
                }

                @Override
                public int getTabCount() {
                    return 0;
                }

                @Override
                public int getHeight() {
                    return 0;
                }

                @Override
                public void show() {

                }

                @Override
                public void hide() {

                }

                @Override
                public boolean isShowing() {
                    return false;
                }

                @Override
                public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

                @Override
                public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

            };
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B4DF2E22")));

        } else if (empresa.equals("AFPN")) {

            LinearLayout li = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout);
            li.setBackgroundResource(R.color.afpn);
            LinearLayout li2 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout2);
            li2.setBackgroundResource(R.color.afpn);
            LinearLayout li3 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout3);
            li3.setBackgroundResource(R.color.afpn);
            LinearLayout li4 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout4);
            li4.setBackgroundResource(R.color.afpn);
            LinearLayout li5 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout5);
            li5.setBackgroundResource(R.color.afpn);
            LinearLayout li6 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout6);
            li6.setBackgroundResource(R.color.afpn);
            LinearLayout li7 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout7);
            li7.setBackgroundResource(R.color.afpn);
            ActionBar barVista = new ActionBar() {
                @Override
                public void setCustomView(View view) {

                }

                @Override
                public void setCustomView(View view, LayoutParams layoutParams) {

                }

                @Override
                public void setCustomView(int resId) {

                }

                @Override
                public void setIcon(int resId) {

                }

                @Override
                public void setIcon(Drawable icon) {

                }

                @Override
                public void setLogo(int resId) {

                }

                @Override
                public void setLogo(Drawable logo) {

                }

                @Override
                public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener callback) {

                }

                @Override
                public void setSelectedNavigationItem(int position) {

                }

                @Override
                public int getSelectedNavigationIndex() {
                    return 0;
                }

                @Override
                public int getNavigationItemCount() {
                    return 0;
                }

                @Override
                public void setTitle(CharSequence title) {

                }

                @Override
                public void setTitle(int resId) {

                }

                @Override
                public void setSubtitle(CharSequence subtitle) {

                }

                @Override
                public void setSubtitle(int resId) {

                }

                @Override
                public void setDisplayOptions(int options) {

                }

                @Override
                public void setDisplayOptions(int options, int mask) {

                }

                @Override
                public void setDisplayUseLogoEnabled(boolean useLogo) {

                }

                @Override
                public void setDisplayShowHomeEnabled(boolean showHome) {

                }

                @Override
                public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {

                }

                @Override
                public void setDisplayShowTitleEnabled(boolean showTitle) {

                }

                @Override
                public void setDisplayShowCustomEnabled(boolean showCustom) {

                }

                @Override
                public void setBackgroundDrawable(@Nullable Drawable d) {

                }

                @Override
                public View getCustomView() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getTitle() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getSubtitle() {
                    return null;
                }

                @SuppressLint("WrongConstant")
                @Override
                public int getNavigationMode() {
                    return 0;
                }

                @Override
                public void setNavigationMode(int mode) {

                }

                @Override
                public int getDisplayOptions() {
                    return 0;
                }

                @Override
                public Tab newTab() {
                    return null;
                }

                @Override
                public void addTab(Tab tab) {

                }

                @Override
                public void addTab(Tab tab, boolean setSelected) {

                }

                @Override
                public void addTab(Tab tab, int position) {

                }

                @Override
                public void addTab(Tab tab, int position, boolean setSelected) {

                }

                @Override
                public void removeTab(Tab tab) {

                }

                @Override
                public void removeTabAt(int position) {

                }

                @Override
                public void removeAllTabs() {

                }

                @Override
                public void selectTab(Tab tab) {

                }

                @Nullable
                @Override
                public Tab getSelectedTab() {
                    return null;
                }

                @Override
                public Tab getTabAt(int index) {
                    return null;
                }

                @Override
                public int getTabCount() {
                    return 0;
                }

                @Override
                public int getHeight() {
                    return 0;
                }

                @Override
                public void show() {

                }

                @Override
                public void hide() {

                }

                @Override
                public boolean isShowing() {
                    return false;
                }

                @Override
                public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

                @Override
                public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

            };
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B4DF2E22")));

        } else if (empresa.equals("AFPP")) {

            LinearLayout li = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout);
            li.setBackgroundResource(R.color.afpp);
            LinearLayout li2 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout2);
            li2.setBackgroundResource(R.color.afpp);
            LinearLayout li3 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout3);
            li3.setBackgroundResource(R.color.afpp);
            LinearLayout li4 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout4);
            li4.setBackgroundResource(R.color.afpp);
            LinearLayout li5 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout5);
            li5.setBackgroundResource(R.color.afpp);
            LinearLayout li6 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout6);
            li6.setBackgroundResource(R.color.afpp);
            LinearLayout li7 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout7);
            li7.setBackgroundResource(R.color.afpp);
            ActionBar barVista = new ActionBar() {
                @Override
                public void setCustomView(View view) {

                }

                @Override
                public void setCustomView(View view, LayoutParams layoutParams) {

                }

                @Override
                public void setCustomView(int resId) {

                }

                @Override
                public void setIcon(int resId) {

                }

                @Override
                public void setIcon(Drawable icon) {

                }

                @Override
                public void setLogo(int resId) {

                }

                @Override
                public void setLogo(Drawable logo) {

                }

                @Override
                public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener callback) {

                }

                @Override
                public void setSelectedNavigationItem(int position) {

                }

                @Override
                public int getSelectedNavigationIndex() {
                    return 0;
                }

                @Override
                public int getNavigationItemCount() {
                    return 0;
                }

                @Override
                public void setTitle(CharSequence title) {

                }

                @Override
                public void setTitle(int resId) {

                }

                @Override
                public void setSubtitle(CharSequence subtitle) {

                }

                @Override
                public void setSubtitle(int resId) {

                }

                @Override
                public void setDisplayOptions(int options) {

                }

                @Override
                public void setDisplayOptions(int options, int mask) {

                }

                @Override
                public void setDisplayUseLogoEnabled(boolean useLogo) {

                }

                @Override
                public void setDisplayShowHomeEnabled(boolean showHome) {

                }

                @Override
                public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {

                }

                @Override
                public void setDisplayShowTitleEnabled(boolean showTitle) {

                }

                @Override
                public void setDisplayShowCustomEnabled(boolean showCustom) {

                }

                @Override
                public void setBackgroundDrawable(@Nullable Drawable d) {

                }

                @Override
                public View getCustomView() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getTitle() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getSubtitle() {
                    return null;
                }

                @SuppressLint("WrongConstant")
                @Override
                public int getNavigationMode() {
                    return 0;
                }

                @Override
                public void setNavigationMode(int mode) {

                }

                @Override
                public int getDisplayOptions() {
                    return 0;
                }

                @Override
                public Tab newTab() {
                    return null;
                }

                @Override
                public void addTab(Tab tab) {

                }

                @Override
                public void addTab(Tab tab, boolean setSelected) {

                }

                @Override
                public void addTab(Tab tab, int position) {

                }

                @Override
                public void addTab(Tab tab, int position, boolean setSelected) {

                }

                @Override
                public void removeTab(Tab tab) {

                }

                @Override
                public void removeTabAt(int position) {

                }

                @Override
                public void removeAllTabs() {

                }

                @Override
                public void selectTab(Tab tab) {

                }

                @Nullable
                @Override
                public Tab getSelectedTab() {
                    return null;
                }

                @Override
                public Tab getTabAt(int index) {
                    return null;
                }

                @Override
                public int getTabCount() {
                    return 0;
                }

                @Override
                public int getHeight() {
                    return 0;
                }

                @Override
                public void show() {

                }

                @Override
                public void hide() {

                }

                @Override
                public boolean isShowing() {
                    return false;
                }

                @Override
                public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

                @Override
                public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

            };
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B4DF2E22")));

        } else if (empresa.equals("AFPZ")) {

            LinearLayout li = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout);
            li.setBackgroundResource(R.color.afpz);
            LinearLayout li2 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout2);
            li2.setBackgroundResource(R.color.afpz);
            LinearLayout li3 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout3);
            li3.setBackgroundResource(R.color.afpz);
            LinearLayout li4 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout4);
            li4.setBackgroundResource(R.color.afpz);
            LinearLayout li5 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout5);
            li5.setBackgroundResource(R.color.afpz);
            LinearLayout li6 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout6);
            li6.setBackgroundResource(R.color.afpz);
            LinearLayout li7 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout7);
            li7.setBackgroundResource(R.color.afpz);
            ActionBar barVista = new ActionBar() {
                @Override
                public void setCustomView(View view) {

                }

                @Override
                public void setCustomView(View view, LayoutParams layoutParams) {

                }

                @Override
                public void setCustomView(int resId) {

                }

                @Override
                public void setIcon(int resId) {

                }

                @Override
                public void setIcon(Drawable icon) {

                }

                @Override
                public void setLogo(int resId) {

                }

                @Override
                public void setLogo(Drawable logo) {

                }

                @Override
                public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener callback) {

                }

                @Override
                public void setSelectedNavigationItem(int position) {

                }

                @Override
                public int getSelectedNavigationIndex() {
                    return 0;
                }

                @Override
                public int getNavigationItemCount() {
                    return 0;
                }

                @Override
                public void setTitle(CharSequence title) {

                }

                @Override
                public void setTitle(int resId) {

                }

                @Override
                public void setSubtitle(CharSequence subtitle) {

                }

                @Override
                public void setSubtitle(int resId) {

                }

                @Override
                public void setDisplayOptions(int options) {

                }

                @Override
                public void setDisplayOptions(int options, int mask) {

                }

                @Override
                public void setDisplayUseLogoEnabled(boolean useLogo) {

                }

                @Override
                public void setDisplayShowHomeEnabled(boolean showHome) {

                }

                @Override
                public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {

                }

                @Override
                public void setDisplayShowTitleEnabled(boolean showTitle) {

                }

                @Override
                public void setDisplayShowCustomEnabled(boolean showCustom) {

                }

                @Override
                public void setBackgroundDrawable(@Nullable Drawable d) {

                }

                @Override
                public View getCustomView() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getTitle() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getSubtitle() {
                    return null;
                }

                @SuppressLint("WrongConstant")
                @Override
                public int getNavigationMode() {
                    return 0;
                }

                @Override
                public void setNavigationMode(int mode) {

                }

                @Override
                public int getDisplayOptions() {
                    return 0;
                }

                @Override
                public Tab newTab() {
                    return null;
                }

                @Override
                public void addTab(Tab tab) {

                }

                @Override
                public void addTab(Tab tab, boolean setSelected) {

                }

                @Override
                public void addTab(Tab tab, int position) {

                }

                @Override
                public void addTab(Tab tab, int position, boolean setSelected) {

                }

                @Override
                public void removeTab(Tab tab) {

                }

                @Override
                public void removeTabAt(int position) {

                }

                @Override
                public void removeAllTabs() {

                }

                @Override
                public void selectTab(Tab tab) {

                }

                @Nullable
                @Override
                public Tab getSelectedTab() {
                    return null;
                }

                @Override
                public Tab getTabAt(int index) {
                    return null;
                }

                @Override
                public int getTabCount() {
                    return 0;
                }

                @Override
                public int getHeight() {
                    return 0;
                }

                @Override
                public void show() {

                }

                @Override
                public void hide() {

                }

                @Override
                public boolean isShowing() {
                    return false;
                }

                @Override
                public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

                @Override
                public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

            };
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B4DF2E22")));

        } else if (empresa.equals("AGAH")) {

            LinearLayout li = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout);
            li.setBackgroundResource(R.color.agah);
            LinearLayout li2 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout2);
            li2.setBackgroundResource(R.color.agah);
            LinearLayout li3 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout3);
            li3.setBackgroundResource(R.color.agah);
            LinearLayout li4 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout4);
            li4.setBackgroundResource(R.color.agah);
            LinearLayout li5 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout5);
            li5.setBackgroundResource(R.color.agah);
            LinearLayout li6 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout6);
            li6.setBackgroundResource(R.color.agah);
            LinearLayout li7 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout7);
            li7.setBackgroundResource(R.color.agah);
            ActionBar barVista = new ActionBar() {
                @Override
                public void setCustomView(View view) {

                }

                @Override
                public void setCustomView(View view, LayoutParams layoutParams) {

                }

                @Override
                public void setCustomView(int resId) {

                }

                @Override
                public void setIcon(int resId) {

                }

                @Override
                public void setIcon(Drawable icon) {

                }

                @Override
                public void setLogo(int resId) {

                }

                @Override
                public void setLogo(Drawable logo) {

                }

                @Override
                public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener callback) {

                }

                @Override
                public void setSelectedNavigationItem(int position) {

                }

                @Override
                public int getSelectedNavigationIndex() {
                    return 0;
                }

                @Override
                public int getNavigationItemCount() {
                    return 0;
                }

                @Override
                public void setTitle(CharSequence title) {

                }

                @Override
                public void setTitle(int resId) {

                }

                @Override
                public void setSubtitle(CharSequence subtitle) {

                }

                @Override
                public void setSubtitle(int resId) {

                }

                @Override
                public void setDisplayOptions(int options) {

                }

                @Override
                public void setDisplayOptions(int options, int mask) {

                }

                @Override
                public void setDisplayUseLogoEnabled(boolean useLogo) {

                }

                @Override
                public void setDisplayShowHomeEnabled(boolean showHome) {

                }

                @Override
                public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {

                }

                @Override
                public void setDisplayShowTitleEnabled(boolean showTitle) {

                }

                @Override
                public void setDisplayShowCustomEnabled(boolean showCustom) {

                }

                @Override
                public void setBackgroundDrawable(@Nullable Drawable d) {

                }

                @Override
                public View getCustomView() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getTitle() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getSubtitle() {
                    return null;
                }

                @SuppressLint("WrongConstant")
                @Override
                public int getNavigationMode() {
                    return 0;
                }

                @Override
                public void setNavigationMode(int mode) {

                }

                @Override
                public int getDisplayOptions() {
                    return 0;
                }

                @Override
                public Tab newTab() {
                    return null;
                }

                @Override
                public void addTab(Tab tab) {

                }

                @Override
                public void addTab(Tab tab, boolean setSelected) {

                }

                @Override
                public void addTab(Tab tab, int position) {

                }

                @Override
                public void addTab(Tab tab, int position, boolean setSelected) {

                }

                @Override
                public void removeTab(Tab tab) {

                }

                @Override
                public void removeTabAt(int position) {

                }

                @Override
                public void removeAllTabs() {

                }

                @Override
                public void selectTab(Tab tab) {

                }

                @Nullable
                @Override
                public Tab getSelectedTab() {
                    return null;
                }

                @Override
                public Tab getTabAt(int index) {
                    return null;
                }

                @Override
                public int getTabCount() {
                    return 0;
                }

                @Override
                public int getHeight() {
                    return 0;
                }

                @Override
                public void show() {

                }

                @Override
                public void hide() {

                }

                @Override
                public boolean isShowing() {
                    return false;
                }

                @Override
                public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

                @Override
                public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

            };
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E002E86")));

        } else if (empresa.equals("AGDP")) {

            LinearLayout li = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout);
            li.setBackgroundResource(R.color.agdp);
            LinearLayout li2 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout2);
            li2.setBackgroundResource(R.color.agdp);
            LinearLayout li3 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout3);
            li3.setBackgroundResource(R.color.agdp);
            LinearLayout li4 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout4);
            li4.setBackgroundResource(R.color.agdp);
            LinearLayout li5 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout5);
            li5.setBackgroundResource(R.color.agdp);
            LinearLayout li6 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout6);
            li6.setBackgroundResource(R.color.agdp);
            LinearLayout li7 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout7);
            li7.setBackgroundResource(R.color.agdp);
            ActionBar barVista = new ActionBar() {
                @Override
                public void setCustomView(View view) {

                }

                @Override
                public void setCustomView(View view, LayoutParams layoutParams) {

                }

                @Override
                public void setCustomView(int resId) {

                }

                @Override
                public void setIcon(int resId) {

                }

                @Override
                public void setIcon(Drawable icon) {

                }

                @Override
                public void setLogo(int resId) {

                }

                @Override
                public void setLogo(Drawable logo) {

                }

                @Override
                public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener callback) {

                }

                @Override
                public void setSelectedNavigationItem(int position) {

                }

                @Override
                public int getSelectedNavigationIndex() {
                    return 0;
                }

                @Override
                public int getNavigationItemCount() {
                    return 0;
                }

                @Override
                public void setTitle(CharSequence title) {

                }

                @Override
                public void setTitle(int resId) {

                }

                @Override
                public void setSubtitle(CharSequence subtitle) {

                }

                @Override
                public void setSubtitle(int resId) {

                }

                @Override
                public void setDisplayOptions(int options) {

                }

                @Override
                public void setDisplayOptions(int options, int mask) {

                }

                @Override
                public void setDisplayUseLogoEnabled(boolean useLogo) {

                }

                @Override
                public void setDisplayShowHomeEnabled(boolean showHome) {

                }

                @Override
                public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {

                }

                @Override
                public void setDisplayShowTitleEnabled(boolean showTitle) {

                }

                @Override
                public void setDisplayShowCustomEnabled(boolean showCustom) {

                }

                @Override
                public void setBackgroundDrawable(@Nullable Drawable d) {

                }

                @Override
                public View getCustomView() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getTitle() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getSubtitle() {
                    return null;
                }

                @SuppressLint("WrongConstant")
                @Override
                public int getNavigationMode() {
                    return 0;
                }

                @Override
                public void setNavigationMode(int mode) {

                }

                @Override
                public int getDisplayOptions() {
                    return 0;
                }

                @Override
                public Tab newTab() {
                    return null;
                }

                @Override
                public void addTab(Tab tab) {

                }

                @Override
                public void addTab(Tab tab, boolean setSelected) {

                }

                @Override
                public void addTab(Tab tab, int position) {

                }

                @Override
                public void addTab(Tab tab, int position, boolean setSelected) {

                }

                @Override
                public void removeTab(Tab tab) {

                }

                @Override
                public void removeTabAt(int position) {

                }

                @Override
                public void removeAllTabs() {

                }

                @Override
                public void selectTab(Tab tab) {

                }

                @Nullable
                @Override
                public Tab getSelectedTab() {
                    return null;
                }

                @Override
                public Tab getTabAt(int index) {
                    return null;
                }

                @Override
                public int getTabCount() {
                    return 0;
                }

                @Override
                public int getHeight() {
                    return 0;
                }

                @Override
                public void show() {

                }

                @Override
                public void hide() {

                }

                @Override
                public boolean isShowing() {
                    return false;
                }

                @Override
                public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

                @Override
                public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

            };
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E002E86")));

        } else if (empresa.equals("AGGC")) {

            LinearLayout li = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout);
            li.setBackgroundResource(R.color.aggc);
            LinearLayout li2 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout2);
            li2.setBackgroundResource(R.color.aggc);
            LinearLayout li3 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout3);
            li3.setBackgroundResource(R.color.aggc);
            LinearLayout li4 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout4);
            li4.setBackgroundResource(R.color.aggc);
            LinearLayout li5 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout5);
            li5.setBackgroundResource(R.color.aggc);
            LinearLayout li6 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout6);
            li6.setBackgroundResource(R.color.aggc);
            LinearLayout li7 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout7);
            li7.setBackgroundResource(R.color.aggc);
            ActionBar barVista = new ActionBar() {
                @Override
                public void setCustomView(View view) {

                }

                @Override
                public void setCustomView(View view, LayoutParams layoutParams) {

                }

                @Override
                public void setCustomView(int resId) {

                }

                @Override
                public void setIcon(int resId) {

                }

                @Override
                public void setIcon(Drawable icon) {

                }

                @Override
                public void setLogo(int resId) {

                }

                @Override
                public void setLogo(Drawable logo) {

                }

                @Override
                public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener callback) {

                }

                @Override
                public void setSelectedNavigationItem(int position) {

                }

                @Override
                public int getSelectedNavigationIndex() {
                    return 0;
                }

                @Override
                public int getNavigationItemCount() {
                    return 0;
                }

                @Override
                public void setTitle(CharSequence title) {

                }

                @Override
                public void setTitle(int resId) {

                }

                @Override
                public void setSubtitle(CharSequence subtitle) {

                }

                @Override
                public void setSubtitle(int resId) {

                }

                @Override
                public void setDisplayOptions(int options) {

                }

                @Override
                public void setDisplayOptions(int options, int mask) {

                }

                @Override
                public void setDisplayUseLogoEnabled(boolean useLogo) {

                }

                @Override
                public void setDisplayShowHomeEnabled(boolean showHome) {

                }

                @Override
                public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {

                }

                @Override
                public void setDisplayShowTitleEnabled(boolean showTitle) {

                }

                @Override
                public void setDisplayShowCustomEnabled(boolean showCustom) {

                }

                @Override
                public void setBackgroundDrawable(@Nullable Drawable d) {

                }

                @Override
                public View getCustomView() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getTitle() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getSubtitle() {
                    return null;
                }

                @SuppressLint("WrongConstant")
                @Override
                public int getNavigationMode() {
                    return 0;
                }

                @Override
                public void setNavigationMode(int mode) {

                }

                @Override
                public int getDisplayOptions() {
                    return 0;
                }

                @Override
                public Tab newTab() {
                    return null;
                }

                @Override
                public void addTab(Tab tab) {

                }

                @Override
                public void addTab(Tab tab, boolean setSelected) {

                }

                @Override
                public void addTab(Tab tab, int position) {

                }

                @Override
                public void addTab(Tab tab, int position, boolean setSelected) {

                }

                @Override
                public void removeTab(Tab tab) {

                }

                @Override
                public void removeTabAt(int position) {

                }

                @Override
                public void removeAllTabs() {

                }

                @Override
                public void selectTab(Tab tab) {

                }

                @Nullable
                @Override
                public Tab getSelectedTab() {
                    return null;
                }

                @Override
                public Tab getTabAt(int index) {
                    return null;
                }

                @Override
                public int getTabCount() {
                    return 0;
                }

                @Override
                public int getHeight() {
                    return 0;
                }

                @Override
                public void show() {

                }

                @Override
                public void hide() {

                }

                @Override
                public boolean isShowing() {
                    return false;
                }

                @Override
                public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

                @Override
                public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

            };
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B4DF2E22")));

        } else if (empresa.equals("AGUC")) {

            LinearLayout li = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout);
            li.setBackgroundResource(R.color.aguc);
            LinearLayout li2 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout2);
            li2.setBackgroundResource(R.color.aguc);
            LinearLayout li3 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout3);
            li3.setBackgroundResource(R.color.aguc);
            LinearLayout li4 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout4);
            li4.setBackgroundResource(R.color.aguc);
            LinearLayout li5 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout5);
            li5.setBackgroundResource(R.color.aguc);
            LinearLayout li6 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout6);
            li6.setBackgroundResource(R.color.aguc);
            LinearLayout li7 = (LinearLayout) viewInformeRutaFragment.findViewById(R.id.informeslayaout7);
            li7.setBackgroundResource(R.color.aguc);
            ActionBar barVista = new ActionBar() {
                @Override
                public void setCustomView(View view) {

                }

                @Override
                public void setCustomView(View view, LayoutParams layoutParams) {

                }

                @Override
                public void setCustomView(int resId) {

                }

                @Override
                public void setIcon(int resId) {

                }

                @Override
                public void setIcon(Drawable icon) {

                }

                @Override
                public void setLogo(int resId) {

                }

                @Override
                public void setLogo(Drawable logo) {

                }

                @Override
                public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener callback) {

                }

                @Override
                public void setSelectedNavigationItem(int position) {

                }

                @Override
                public int getSelectedNavigationIndex() {
                    return 0;
                }

                @Override
                public int getNavigationItemCount() {
                    return 0;
                }

                @Override
                public void setTitle(CharSequence title) {

                }

                @Override
                public void setTitle(int resId) {

                }

                @Override
                public void setSubtitle(CharSequence subtitle) {

                }

                @Override
                public void setSubtitle(int resId) {

                }

                @Override
                public void setDisplayOptions(int options) {

                }

                @Override
                public void setDisplayOptions(int options, int mask) {

                }

                @Override
                public void setDisplayUseLogoEnabled(boolean useLogo) {

                }

                @Override
                public void setDisplayShowHomeEnabled(boolean showHome) {

                }

                @Override
                public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {

                }

                @Override
                public void setDisplayShowTitleEnabled(boolean showTitle) {

                }

                @Override
                public void setDisplayShowCustomEnabled(boolean showCustom) {

                }

                @Override
                public void setBackgroundDrawable(@Nullable Drawable d) {

                }

                @Override
                public View getCustomView() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getTitle() {
                    return null;
                }

                @Nullable
                @Override
                public CharSequence getSubtitle() {
                    return null;
                }

                @SuppressLint("WrongConstant")
                @Override
                public int getNavigationMode() {
                    return 0;
                }

                @Override
                public void setNavigationMode(int mode) {

                }

                @Override
                public int getDisplayOptions() {
                    return 0;
                }

                @Override
                public Tab newTab() {
                    return null;
                }

                @Override
                public void addTab(Tab tab) {

                }

                @Override
                public void addTab(Tab tab, boolean setSelected) {

                }

                @Override
                public void addTab(Tab tab, int position) {

                }

                @Override
                public void addTab(Tab tab, int position, boolean setSelected) {

                }

                @Override
                public void removeTab(Tab tab) {

                }

                @Override
                public void removeTabAt(int position) {

                }

                @Override
                public void removeAllTabs() {

                }

                @Override
                public void selectTab(Tab tab) {

                }

                @Nullable
                @Override
                public Tab getSelectedTab() {
                    return null;
                }

                @Override
                public Tab getTabAt(int index) {
                    return null;
                }

                @Override
                public int getTabCount() {
                    return 0;
                }

                @Override
                public int getHeight() {
                    return 0;
                }

                @Override
                public void show() {

                }

                @Override
                public void hide() {

                }

                @Override
                public boolean isShowing() {
                    return false;
                }

                @Override
                public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

                @Override
                public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

                }

            };
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E002E86")));

        }


    }

    public boolean validateString(String text) {
        boolean answer = true;

        if (text.equals(""))
            answer = false;
        else if (!text.contains("@") || !text.contains(".com"))
            answer = false;


        return answer;
    }

    public void showIntentEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/pdf");
        String[] emailAux = new String[emails.size()];
        for (int i = 0; i < emails.size(); i++) {
            if (!emails.get(i).contains("//co.")) {
                emailAux[i] = emails.get(i);
            }
        }
        //emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sebastian.quintero@celuweb.com", "juan.alape@celuweb.com"});
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAux);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Informe Actividades " + Utilidades.fechaActual("yyyy-MM-dd"));
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Informe Actividades: " + Utilidades.fechaActual("yyyy-MM-dd"));
        //emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");


        File file = new File(Utilidades.dirApp(requireContext()), "/informes/informes.pdf");
        if (!file.exists() || !file.canRead()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", file);
            emailIntent.setData(uri);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(emailIntent, "Seleccione la app"));
            emails = new ArrayList<>();
        } else {
            Uri uri = Uri.fromFile(file);
            emailIntent.setData(uri);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(emailIntent, "Seleccione la App"));
        }


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CobrosProgramadosFragment.
     */
    public static InformeRutaFragment newInstance() {

        InformeRutaFragment fragment = new InformeRutaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



}
