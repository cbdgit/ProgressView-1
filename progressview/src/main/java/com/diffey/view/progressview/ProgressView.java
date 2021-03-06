package com.diffey.view.progressview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by diff on 2016/1/22.
 */
public class ProgressView extends View {
    private static final int DEF_MAX = 100;
    private static final int DEF_PROGRESS = 0;
    private static final int DEF_PROGRESS_COLOR = Color.BLUE;
    private static final int DEF_BACKGROUND_COLOR = Color.TRANSPARENT;
    private static final int DEF_TEXT_COLOR = Color.WHITE;
    private static final int DEF_TEXT_OVER_COLOR = Color.BLACK;
    private static final float DEF_TEXT_SIZE = 25;
    private static final int DEF_TEXT_GRAVITY = 0;
    private static final float DEF_TEXT_GRAVITY_PADDING = 10;


    private int max = DEF_MAX;
    private int progress = DEF_PROGRESS;
    private int progressColor = DEF_PROGRESS_COLOR;
    private int backgroundColor = DEF_BACKGROUND_COLOR;
    private String text;
    private int textColor = DEF_TEXT_COLOR;
    private int textOverColor = DEF_TEXT_OVER_COLOR;
    private float textSize = DEF_TEXT_SIZE;
    private int textGravity = DEF_TEXT_GRAVITY;
    private float textGravityPadding = DEF_TEXT_GRAVITY_PADDING;
    private boolean isAnimator = false;

    private Paint mPaint;

    public ProgressView(Context context) {
        super(context);
        inflateAttrs(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateAttrs(context, attrs);
    }

    private void inflateAttrs(Context context, AttributeSet attrs) {
        mPaint = new Paint();

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
            max = typedArray.getInt(R.styleable.ProgressView_max, DEF_MAX);
            progress = typedArray.getInt(R.styleable.ProgressView_progress, DEF_PROGRESS);
            progressColor = typedArray.getColor(R.styleable.ProgressView_progressColor, DEF_PROGRESS_COLOR);
            backgroundColor = typedArray.getColor(R.styleable.ProgressView_backgroundColor, DEF_BACKGROUND_COLOR);
            text = typedArray.getString(R.styleable.ProgressView_text);
            textColor = typedArray.getColor(R.styleable.ProgressView_textColor, DEF_TEXT_COLOR);
            textOverColor = typedArray.getColor(R.styleable.ProgressView_textOverColor, DEF_TEXT_OVER_COLOR);
            textSize = typedArray.getDimension(R.styleable.ProgressView_textSize, DEF_TEXT_SIZE);
            textGravity = typedArray.getInt(R.styleable.ProgressView_textGravity, DEF_TEXT_GRAVITY);
            textGravityPadding = typedArray.getDimension(R.styleable.ProgressView_textGravityPadding, DEF_TEXT_GRAVITY_PADDING);
            typedArray.recycle();
        }
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        invalidate();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        invalidate();
    }

    public float getTextGravityPadding() {
        return textGravityPadding;
    }

    public void setTextGravityPadding(float textGravityPadding) {
        this.textGravityPadding = textGravityPadding;
        invalidate();
    }

    public int getTextOverColor() {
        return textOverColor;
    }

    public void setTextOverColor(int textOverColor) {
        this.textOverColor = textOverColor;
        invalidate();
    }

    /**
     * 显示进度加载动画
     *
     * @param duration 动画间隔
     */
    public void showAnimator(int duration) {
        ValueAnimator animator = ValueAnimator.ofInt(0, progress);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateInterpolator(2f));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setProgress((int) animation.getAnimatedValue());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimator = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimator = false;
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景
        canvas.drawColor(backgroundColor);

        //绘制progress
        checkProgress();
        int progressWidth = getWidth() * progress / max;
        int progressHeight = getHeight();
        mPaint.setColor(progressColor);
        canvas.drawRect(0, 0, progressWidth, progressHeight, mPaint);

        //绘制文字
        if (text != null && !isAnimator) {
            mPaint.setColor(textColor);
            mPaint.setTextSize(textSize);
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();

            float tX;
            if (progressWidth < mPaint.measureText(text)) {
                mPaint.setColor(textOverColor);
                tX = textGravityPadding + progressWidth;
            } else {
                if (textGravity == 0) {
                    tX = textGravityPadding;
                } else {
                    tX = progressWidth - (mPaint.measureText(text) + textGravityPadding);
                }
            }
            canvas.drawText(text, tX, (progressHeight - fontMetrics.top - fontMetrics.bottom) / 2, mPaint);
        }
    }

    /**
     * 检查Progress
     */
    private void checkProgress() {
        if (progress >= max) {
            progress = max;
        }
    }
}
