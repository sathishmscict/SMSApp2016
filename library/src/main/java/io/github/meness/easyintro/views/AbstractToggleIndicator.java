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

package io.github.meness.easyintro.views;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;

import io.github.meness.easyintro.R;

public abstract class AbstractToggleIndicator extends AppCompatImageButton implements View.OnClickListener {
    public AbstractToggleIndicator(Context context) {
        this(context, null);
    }

    public AbstractToggleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.styleToggleIndicators);
    }

    public AbstractToggleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOnClickListener(this);
    }

    public boolean isDisabled() {
        return isEnabled();
    }

    public void withDisabled(boolean b) {
        if (b) {
            // disable
            setOnClickListener(null);
            setEnabled(false);
        } else {
            // enable
            setOnClickListener(this);
            setEnabled(true);
        }
    }

    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }
}
