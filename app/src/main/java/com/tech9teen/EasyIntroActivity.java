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

package com.tech9teen;

import android.os.Handler;
import android.widget.Toast;

import com.tech9teen.fragment.FifthFragment;
import com.tech9teen.fragment.FirstFragment;
import com.tech9teen.fragment.SecondFragment;
import com.tech9teen.fragment.ThirdFragment;

import io.github.meness.easyintro.EasyIntro;
import io.github.meness.easyintro.enums.SlideTransformer;

public class EasyIntroActivity extends EasyIntro {
    boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void initIntro() {

        withSlide(FirstFragment.instantiate(getApplicationContext(), FirstFragment.class.getName()));
        withSlide(SecondFragment.instantiate(getApplicationContext(), SecondFragment.class.getName()));
        withSlide(ThirdFragment.instantiate(getApplicationContext(), ThirdFragment.class.getName()));
        /*withSlide(FourthFragment.instantiate(getApplicationContext(), FourthFragment.class.getName()));*/
        withSlide(FifthFragment.instantiate(getApplicationContext(), FifthFragment.class.getName()));

        //withSlideTransformer(SlideTransformer.ZOOM_OUT_SLIDE);
        withSlideTransformer(SlideTransformer.FLIP_HORIZONTAL);

        // define overlay slides animations once
        withOverlaySlideAnimation(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        withFullscreen(true);
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }
}
