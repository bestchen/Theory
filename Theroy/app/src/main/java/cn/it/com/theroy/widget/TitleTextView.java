package cn.it.com.theroy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import cn.it.com.theroy.R;
import cn.it.com.theroy.uitls.UIUitls;

/**
 * Created by Chenweiwei on 2017/7/29.
 */

public class TitleTextView extends View {

    private int bgColor = UIUitls.getColor(R.color.color_336699);
    private int titlecolor=UIUitls.getColor(R.color.color_cc9900);
    private int titleSize = UIUitls.sp2pix(14);
    private int textHeight;
    private String titlePre;
    private String title;
    private Paint paint;

    public TitleTextView(Context context) {
        this(context, null);
    }

    public TitleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(titlecolor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float width = 0f;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width += widthSize;
            if (!TextUtils.isEmpty(titlePre)) {
                final float preWidth = paint.measureText(titlePre);
                width += preWidth;
            }
            if (!TextUtils.isEmpty(titlePre)) {
                final float titleWidth = paint.measureText(title);
                width += titleWidth;
            }
            width += getPaddingLeft() + getPaddingRight();
        }

        float height = 0f;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
//            height += heightSize;
            textHeight = 0;
            if (!TextUtils.isEmpty(titlePre)) {
                paint.setTextSize(titleSize);
                Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                textHeight = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
            }
            if (!TextUtils.isEmpty(title)) {
                paint.setTextSize(titleSize + UIUitls.sp2pix(4));
                Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                Math.max(textHeight, Math.ceil(fontMetrics.descent - fontMetrics.ascent));
            }
            height += textHeight;
            height += getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension((int) width, (int) height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(bgColor);
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop() + textHeight);
        int offsetX = 0;
        if (!TextUtils.isEmpty(titlePre)) {
            paint.setTextSize(titleSize);
            canvas.drawText(titlePre, 0, 0, paint);
            offsetX = (int) paint.measureText(titlePre);
        }
        if (!TextUtils.isEmpty(title)) {
            paint.setTextSize(titleSize + UIUitls.sp2pix(4));
            canvas.drawText(title, offsetX, 0, paint);
        }
        canvas.restore();
        canvas.translate(-getPaddingLeft(), -getPaddingTop());
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        invalidate();
    }

    public void setTitleColor(int color) {
        this.titlecolor = color;
        paint.setColor(titlecolor);
        invalidate();
    }

    public void setTitleSize(int textSize) {
        this.titleSize = textSize;
        paint.setTextSize(textSize);
        invalidate();
    }

    public void setTitlePre(String titlePre) {
        this.titlePre = titlePre;
        invalidate();
    }

    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }
}
