package com.example.hanzhenyu.smoothpagerindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class SmoothPagerIndicator extends View {

    private static final String TAG = "SmoothPagerIndicator";

    private Paint paint;
    private Paint paint2;


    private float spaceWidth = 30;
    private float barWidth = 100;
    private float strokeWidth = 20;
    private float startX;
    private float startXx;
    private float startY;

    private ViewPager viewPager;

    private int scrollState;

    // 1向右滑 -1向左滑
    private int scrollDirection = 0;

    private int currentItem;

    public SmoothPagerIndicator(Context context) {
        super(context);
        initPaint();
    }

    public SmoothPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public SmoothPagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private float prePositionOffset = 0;

    private int currentPo;


    public void setViewPager(final ViewPager viewPager) {
        this.viewPager = viewPager;


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (prePositionOffset != 0 && positionOffset != 0) {
                    if (scrollState == 1) {
                        if (positionOffset - prePositionOffset > 0f) {
                            Log.i(TAG, "向右");
                            scrollDirection = 1;
                        } else if (positionOffset - prePositionOffset < 0f) {
                            Log.i(TAG, "向左");
                            scrollDirection = -1;
                        }
                    }
                }
                if (scrollDirection == 1) {
                    currentItem = position;
                } else if (scrollDirection == -1) {
                    currentItem = position + 1;
                }

                invalidate();

                prePositionOffset = positionOffset;

                Log.i(TAG, position + "");
//                Log.i(TAG, positionOffset + "");
//                Log.i(TAG, positionOffsetPixels + "");
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, position + "");
                currentPo = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG, state + "");
                scrollState = state;
                if (scrollState == 0) {
                    currentItem = viewPager.getCurrentItem();
                    scrollDirection = 0;
                }
                invalidate();
            }
        });
        postInvalidate();

    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(Color.argb(127, 255, 255, 255));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        paint2 = new Paint();
        paint2.setColor(Color.argb(210, 255, 255, 255));
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);
        paint2.setStrokeWidth(strokeWidth);
        paint2.setAntiAlias(true);
        paint2.setDither(true);
        paint2.setStrokeJoin(Paint.Join.ROUND);
        paint2.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), measureDimension((int) strokeWidth + 10, heightMeasureSpec));
    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;   //UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        startX = (w - ((float) viewPager.getAdapter().getCount() - 1) * (spaceWidth + strokeWidth) - barWidth) / 2f;
        startXx = startX + 0.001f;
        startY = h / 2;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        Paint paint;

        if (viewPager == null) {
            return;
        }

        for (int i = 0; i < viewPager.getAdapter().getCount(); i++) {
            if (i == currentPo) {
                paint = paint2;
            } else {
                paint = this.paint;
            }
            if (i == currentItem) {
                if (scrollDirection == 1) {
                    canvas.drawLine(startX + i * spaceWidth, startY, startXx + i * spaceWidth + barWidth * (1f - prePositionOffset), startY, paint);
                } else if (scrollDirection == -1) {
                    canvas.drawLine(startX + i * spaceWidth + barWidth * (1 - prePositionOffset), startY, startXx + i * spaceWidth + barWidth, startY, paint);
                } else if (scrollDirection == 0) {
                    canvas.drawLine(startX + i * spaceWidth, startY, startXx + i * spaceWidth + barWidth, startY, paint2);
                }
            } else if (i == currentItem + 1) {
                if (scrollDirection == 1) {
                    canvas.drawLine(startX + i * spaceWidth + barWidth * (1f - prePositionOffset), startY, startXx + i * spaceWidth + barWidth, startY, paint);
                } else {
                    canvas.drawLine(startX + i * spaceWidth + barWidth, startY, startXx + i * spaceWidth + barWidth, startY, paint);
                }
            } else if (i == currentItem - 1) {
                if (scrollDirection == -1) {

                    canvas.drawLine(startX + i * spaceWidth, startY, startXx + i * spaceWidth + barWidth * (1f - prePositionOffset), startY, paint);
                } else {

                    canvas.drawLine(startX + i * spaceWidth, startY, startXx + i * spaceWidth, startY, paint);
                }
            } else if (i < currentItem - 1) {

                canvas.drawLine(startX + i * spaceWidth, startY, startXx + i * spaceWidth, startY, paint);
            } else if (i > currentItem + 1) {
                canvas.drawLine(startX + i * spaceWidth + barWidth, startY, startXx + i * spaceWidth + barWidth, startY, paint);
            }
            super.onDraw(canvas);
        }
    }
}