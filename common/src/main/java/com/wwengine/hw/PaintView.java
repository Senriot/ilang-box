package com.wwengine.hw;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.InputStream;

/**
 * Created by Allen on 15/8/31.
 */
public class PaintView extends View
{
    private Path mPath;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private static char[]  mResult;
    private static short[] mTracks;
    private static int     mCount;

    private OnResultListener listener;

    public OnResultListener getListener()
    {
        return listener;
    }

    public void setListener(OnResultListener listener)
    {
        this.listener = listener;
    }

    public PaintView(Context context)
    {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    private void initialize()
    {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);

        byte[] hwData = readData(getContext().getAssets(), "hwdata.bin");
        if (hwData == null)
        {
            return;
        }

        if (WWHandWrite.hwInit(hwData, 0) != 0)
        {
            return;
        }

        mResult = new char[256];
        mTracks = new short[1024];
        mCount = 0;
    }

    private static byte[] readData(AssetManager am, String name)
    {
        try
        {
            InputStream is = am.open(name);
            if (is == null)
            {
                return null;
            }

            int length = is.available();
            if (length <= 0)
            {
                return null;
            }

            byte[] buf = new byte[length];

            if (is.read(buf, 0, length) == -1)
            {
                return null;
            }

            is.close();

            return buf;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                onTouchStart(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                onTouchMove(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                onTouchUp();
                invalidate();
                break;
        }
        return true;
    }

    private void onTouchStart(float x, float y)
    {
        try
        {
            mPath.moveTo(x, y);
            mX = x;
            mY = y;

            mTracks[mCount++] = (short) x;
            mTracks[mCount++] = (short) y;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void onTouchMove(float x, float y)
    {
        try
        {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
            {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
            mTracks[mCount++] = (short) x;
            mTracks[mCount++] = (short) y;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void onTouchUp()
    {
        try
        {
            mPath.lineTo(mX, mY);

            mTracks[mCount++] = -1;
            mTracks[mCount++] = 0;
            onRecognize();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void onRecognize()
    {
        try
        {
            short[] mTracksTemp;
            int     countTemp = mCount;

            mTracksTemp = mTracks.clone();
            mTracksTemp[countTemp++] = -1;
            mTracksTemp[countTemp++] = -1;

            WWHandWrite.hwRecognize(mTracksTemp, mResult, 10, 0xFFFF);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void resetRecognize()
    {
        mCount = 0;
        mResult = new char[256];
        {
            mPath.reset();
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
        if (this.listener != null)
        {
            listener.onResult(mResult);
        }

    }

    public interface OnResultListener
    {
        void onResult(char[] restlt);
    }
}
