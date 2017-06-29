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

package io.github.meness.easyintro.enums;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.BackgroundToForegroundTransformer;
import com.ToxicBakery.viewpager.transforms.CubeInTransformer;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;
import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.ToxicBakery.viewpager.transforms.FlipVerticalTransformer;
import com.ToxicBakery.viewpager.transforms.ForegroundToBackgroundTransformer;
import com.ToxicBakery.viewpager.transforms.RotateDownTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.ToxicBakery.viewpager.transforms.ScaleInOutTransformer;
import com.ToxicBakery.viewpager.transforms.StackTransformer;
import com.ToxicBakery.viewpager.transforms.TabletTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomInTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomOutTranformer;

public enum SlideTransformer {
    ACCORDION(new AccordionTransformer()),
    BACKGROUND_TO_FOREGROUND(new BackgroundToForegroundTransformer()),
    CUBE_IN(new CubeInTransformer()),
    CUBE_OUT(new CubeOutTransformer()),
    DEFAULT(new DefaultTransformer()),
    DEPTH_PAGE(new DepthPageTransformer()),
    FLIP_HORIZONTAL(new FlipHorizontalTransformer()),
    FLIP_VERTICAL(new FlipVerticalTransformer()),
    FOREGROUND_TO_BACKGROUND(new ForegroundToBackgroundTransformer()),
    ROTATE_DOWN(new RotateDownTransformer()),
    ROTATE_UP(new RotateUpTransformer()),
    SCALE_IN_OUT(new ScaleInOutTransformer()),
    STACK(new StackTransformer()),
    TABLET(new TabletTransformer()),
    ZOOM_IN(new ZoomInTransformer()),
    ZOOM_OUT_SLIDE(new ZoomOutSlideTransformer()),
    ZOOM_OUT(new ZoomOutTranformer());

    private final ABaseTransformer mTransformer;

    SlideTransformer(ABaseTransformer transformer) {
        mTransformer = transformer;
    }

    public ABaseTransformer getTransformer() {
        return mTransformer;
    }
}
