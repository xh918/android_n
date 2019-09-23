package com.nexlabs.comnexlabsnanowidget.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexlabs.comnexlabsnanowidget.R;

public class HostFragment extends BackStackFragment{

    Fragment fragment = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_host, container, false);

        if(fragment != null){
            replaceFragment(fragment, false);
        }

        return  rootView;

    }



    public void replaceFragment(Fragment fragment, boolean addToBackStack){
        if(addToBackStack){
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.hosted_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }else{
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.hosted_fragment, fragment)
                    .commit();
        }
    }

    public static HostFragment newInstance(Fragment fragment){
        HostFragment hostFragment = new HostFragment();
        hostFragment.fragment = fragment;
        return hostFragment;
    }
}
