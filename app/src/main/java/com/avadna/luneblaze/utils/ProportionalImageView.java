package com.avadna.luneblaze.utils;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class ProportionalImageView  extends androidx.appcompat.widget.AppCompatImageView {
    int MAXHEIGHT=300;

    public ProportionalImageView(Context context) {
        super(context);
        init();
    }

    public ProportionalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProportionalImageView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        setScaleType(ScaleType.MATRIX);
    }

    private void recomputeImgMatrix() {

        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        final Matrix matrix = getImageMatrix();

        float scale;
         int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
         int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
         int drawableWidth = drawable.getIntrinsicWidth();
         int drawableHeight = drawable.getIntrinsicHeight();


       /* int px =(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MAXHEIGHT, getResources().getDisplayMetrics());
        if(drawableHeight>px){
            drawableHeight=px;
            viewHeight=px;
        }*/

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