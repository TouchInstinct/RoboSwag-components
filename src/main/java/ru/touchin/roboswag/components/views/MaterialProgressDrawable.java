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

package ru.touchin.roboswag.components.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.TypedValue;

import ru.touchin.roboswag.components.utils.UiUtils;

/**
 * Created by Gavriil Sitnikov on 03/16.
 * TODO
 */
public class MaterialProgressDrawable extends Drawable implements Runnable, Animatable {

    private static final int UPDATE_INTERVAL = 1000 / 60;

    private static final float DEFAULT_STROKE_WIDTH_DP = 4.5f;
    private static final Parameters DEFAULT_PARAMETERS = new Parameters(20, 270, 4, 12, 4, 8);

    private final Paint paint;

    private Parameters parameters;
    private float rotationAngle;
    private float arcSize;
    private final RectF arcBounds = new RectF();
    private boolean running;

    public MaterialProgressDrawable(@NonNull final Context context) {
        super();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_STROKE_WIDTH_DP, UiUtils.getDisplayMetrics(context)));
        paint.setColor(Color.BLACK);

        parameters = DEFAULT_PARAMETERS;
    }

    public float getStrokeWidth() {
        return paint.getStrokeWidth();
    }

    public void setStrokeWidth(final float strokeWidth) {
        paint.setStrokeWidth(strokeWidth);
        updateArcBounds();
        invalidateSelf();
    }

    public void setColor(@ColorInt final int color) {
        paint.setColor(color);
        invalidateSelf();
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(final Parameters parameters) {
        this.parameters = parameters;
        invalidateSelf();
    }

    @Override
    protected void onBoundsChange(final Rect bounds) {
        super.onBoundsChange(bounds);
        updateArcBounds();
    }

    private void updateArcBounds() {
        arcBounds.left = getBounds().left;
        arcBounds.right = getBounds().right;
        arcBounds.top = getBounds().top;
        arcBounds.bottom = getBounds().bottom;
        arcBounds.inset(paint.getStrokeWidth() / 2, paint.getStrokeWidth() / 2);
    }

    @SuppressWarnings("PMD.NPathComplexity")
    @Override
    public void draw(final Canvas canvas) {
        final boolean isGrowingCycle = (((int) (arcSize / parameters.maxAngle)) % 2) == 0;
        final float angle = arcSize % parameters.maxAngle;
        final float shift = (angle / parameters.maxAngle) * parameters.gapAngle;
        canvas.drawArc(arcBounds, isGrowingCycle ? rotationAngle + shift : rotationAngle + parameters.gapAngle - shift,
                isGrowingCycle ? angle + parameters.gapAngle : parameters.maxAngle - angle + parameters.gapAngle, false, paint);
        //TODO: compute based on animation start time
        rotationAngle += isGrowingCycle ? parameters.rotationMagicNumber1 : parameters.rotationMagicNumber2;
        arcSize += isGrowingCycle ? parameters.arcMagicNumber1 : parameters.arcMagicNumber2;
        if (arcSize < 0) {
            arcSize = 0;
        }
        if (isRunning()) {
            scheduleSelf(this, SystemClock.uptimeMillis() + UPDATE_INTERVAL);
        }
    }

    @Override
    public void setAlpha(final int alpha) {
        paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(final ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void start() {
        if (!running) {
            running = true;
            run();
        }
    }

    @Override
    public void stop() {
        if (running) {
            unscheduleSelf(this);
            running = false;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        if (running) {
            invalidateSelf();
        }
    }

    public static class Parameters {

        private final float gapAngle;
        private final float maxAngle;
        private final float rotationMagicNumber1;
        private final float rotationMagicNumber2;
        private final float arcMagicNumber1;
        private final float arcMagicNumber2;

        public Parameters(final float gapAngle, final float maxAngle,
                          final float rotationMagicNumber1, final float rotationMagicNumber2,
                          final float arcMagicNumber1, final float arcMagicNumber2) {
            this.gapAngle = gapAngle;
            this.maxAngle = maxAngle;
            this.rotationMagicNumber1 = rotationMagicNumber1;
            this.rotationMagicNumber2 = rotationMagicNumber2;
            this.arcMagicNumber1 = arcMagicNumber1;
            this.arcMagicNumber2 = arcMagicNumber2;
        }

        public float getGapAngle() {
            return gapAngle;
        }

        public float getMaxAngle() {
            return maxAngle;
        }

        public float getRotationMagicNumber1() {
            return rotationMagicNumber1;
        }

        public float getRotationMagicNumber2() {
            return rotationMagicNumber2;
        }

        public float getArcMagicNumber1() {
            return arcMagicNumber1;
        }

        public float getArcMagicNumber2() {
            return arcMagicNumber2;
        }

    }

}