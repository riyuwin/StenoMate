package com.example.stenomate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class VisualizerView extends View {

    private Paint paint;
    private float lineHeight = 0;
    private Random random = new Random();
    private boolean isSpeaking = false;

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(0xFF000000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f);
        paint.setAntiAlias(true);
    }

    public void startSpeaking() {
        isSpeaking = true;
        postInvalidate();
    }

    public void stopSpeaking() {
        isSpeaking = false;
        lineHeight = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerY = getHeight() / 2;
        float halfHeight = lineHeight / 2;

        canvas.drawLine(0, centerY - halfHeight, getWidth(), centerY + halfHeight, paint);

        if (isSpeaking) {
            // Random "pulse" effect
            lineHeight = random.nextInt(getHeight());
            postInvalidateDelayed(100); // redraw every 100ms
        }
    }
}