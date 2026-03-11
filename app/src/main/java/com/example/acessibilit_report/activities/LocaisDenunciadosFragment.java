package com.example.acessibilit_report.activities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.acessibilit_report.R;

public class LocaisDenunciadosFragment extends Fragment {

    public LocaisDenunciadosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_locais_denunciados, container, false);
    }
}
