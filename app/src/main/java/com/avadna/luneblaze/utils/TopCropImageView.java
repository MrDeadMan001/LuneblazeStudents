package com.avadna.luneblaze.utils;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TopCropImageView extends androidx.appcompat.widget.AppCompatImageView {

    public TopCropImageView(Context context) {
        super(context);
        init();
    }

    public TopCropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopCropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        recomputeImgMatrix();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        recomputeImgMatrix();
        return super.setFrame(l, t, r, b);
    }

    private void init() {
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    private void recomputeImgMatrix() {
        final Matrix matrix = getImageMatrix();

        float scale;
        final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int drawableWidth = getDrawable().getIntrinsicWidth();
        final int drawableHeight = getDrawable().getIntrinsicHeight();

        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }

        matrix.setScale(scale, scale);
        if ((drawableWidth * scale) > viewWidth) {
            float tr = -(((drawableWidth * scale) - viewWidth) / 2);
            matrix.postTranslate(tr, 0);
        }
        setImageMatrix(matrix);
    }
}