package com.mercadopago.android.px.internal.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import com.squareup.picasso.Transformation;

public class CircleTransform implements Transformation {

    @Override
    public Bitmap transform(@NonNull final Bitmap source) {
        final int size = Math.min(source.getWidth(), source.getHeight());

        final int x = (source.getWidth() - size) / 2;
        final int y = (source.getHeight() - size) / 2;

        final Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (!squaredBitmap.equals(source)) {
            source.recycle();
        }

        final Bitmap.Config config = source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
        final Bitmap bitmap = Bitmap.createBitmap(size, size, config);

        final Canvas canvas = new Canvas(bitmap);
        final Paint paint = new Paint();
        final BitmapShader shader = new BitmapShader(squaredBitmap,
            BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        final float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}
