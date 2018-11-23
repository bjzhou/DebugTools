package com.hinnka.devtools.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.hinnka.devtools.HinnkaFragment;
import com.hinnka.devtools.sample.databinding.FragmentSampleBinding;

public class SampleFragment extends HinnkaFragment {

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_sample;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getBinding(FragmentSampleBinding.class).textView.setText("Hello SampleFragment");
    }
}
