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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tech9teen.LoginActivity;
import com.tech9teen.R;
import com.tech9teen.RegisterActivity;

import io.github.meness.easyintro.EasyIntroFragment;


public class FifthFragment extends EasyIntroFragment {
    private Button btnCreateAccount;
    private Context context = getActivity();
    private TextView txtLogin1;
    private Button btnRegister1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container2, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fifth, container2, false);
        txtLogin1 = (TextView) view.findViewById(R.id.txvLogin1);
        btnRegister1 = (Button) view.findViewById(R.id.btnCreateAccount1);

        btnRegister1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getActivity(), RegisterActivity.class);
                startActivity(in);


            }
        });
        txtLogin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getActivity(), LoginActivity.class);
                startActivity(in);


            }
        });
        return view;
    }
}
