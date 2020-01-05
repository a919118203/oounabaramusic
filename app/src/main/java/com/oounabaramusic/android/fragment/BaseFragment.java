package com.oounabaramusic.android.fragment;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
