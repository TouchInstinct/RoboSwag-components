/*
 *  Copyright (c) 2015 RoboSwag (Gavriil Sitnikov, Vsevolod Ivanov)
 *
 *  This file is part of RoboSwag library.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.roboswag.components.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gavriil Sitnikov on 18/07/2014.
 * Typefaces manager
 */
public class Typefaces {

    private static final HashMap<String, Typeface> TYPEFACES = new HashMap<>();

    /**
     * Returns typeface by name from assets 'fonts' folder
     */
    @NonNull
    public synchronized static Typeface getByName(@NonNull Context context, @NonNull String name) {
        Typeface result = TYPEFACES.get(name);
        if (result == null) {
            AssetManager assetManager = context.getAssets();
            try {
                List<String> fonts = Arrays.asList(assetManager.list("fonts"));
                if (fonts.contains(name + ".ttf")) {
                    result = Typeface.createFromAsset(assetManager, "fonts/" + name + ".ttf");
                } else if (fonts.contains(name + ".otf")) {
                    result = Typeface.createFromAsset(assetManager, "fonts/" + name + ".otf");
                } else
                    throw new IllegalStateException("Can't find .otf or .ttf file in folder 'fonts' with name: " + name);
            } catch (IOException e) {
                throw new IllegalStateException("Typefaces files should be in folder named 'fonts'");
            }
            TYPEFACES.put(name, result);
        }

        return result;
    }
}