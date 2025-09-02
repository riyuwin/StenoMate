package com.example.stenomate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class HeartBeatView extends View {

    private Paint paint;
    private Path path;
    private float offset = 0;
    private boolean isSpeaking = false;
    private Random random = new Random();

    public HeartBeatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(0xFF000000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6f);
        paint.setAntiAlias(true);

        path = new Path();
    }

    public void startSpeaking() {
        isSpeaking = true;
        invalidate();
    }

    public void stopSpeaking() {
        isSpeaking = false;
        offset = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int centerY = height / 2;

        path.reset();
        path.moveTo(0, centerY);

        // Generate heartbeat-like wave
        for (int x = 0; x < width; x += 20) {
            float y = centerY;

            if (isSpeaking) {
                // Random spike effect like heart rate monitor
                if (x % 100 == 0) {
                    y = centerY - random.nextInt(height / 3);
                } else if (x % 100 == 40) {
                    y = centerY + random.nextInt(height / 3);
                }
            }

            path.lineTo(x, y);
        }

        canvas.drawPath(path, paint);

        if (isSpeaking) {
            postInvalidateDelayed(120); // refresh every 120ms
        }
    }
}