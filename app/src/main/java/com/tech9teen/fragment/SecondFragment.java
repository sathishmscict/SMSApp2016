/*
 * Copyright 2016 Alireza Eskandarpour Shoferi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tech9teen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tech9teen.R;

import io.github.meness.easyintro.EasyIntroFragment;


public class SecondFragment extends EasyIntroFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_second, container, false);


        return view;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       /* view.findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withOverlaySlide(LoginFragment.instantiate(getContext(), LoginFragment.class.getName()), R.id.container, getChildFragmentManager(), true);
            }
        });
        view.findViewById(R.id.joinBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withOverlaySlide(JoinFragment.instantiate(getContext(), JoinFragment.class.getName()), R.id.container, getChildFragmentManager(), true);
            }
        });*/
    }
}
