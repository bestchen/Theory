package cn.it.com.theroy.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.it.com.theroy.R;


public class ChapterItemView extends LinearLayout implements View.OnClickListener {

    private TextView labelView;
    private View tipView;

    private String path;
    private String name = "";
    private String audioPath = "";

    private int position;

    public ChapterItemView(Context context) {
        this(context, null);
    }

    public ChapterItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.item_chapter, this);
        labelView = (TextView) findViewById(R.id.label);
        tipView = findViewById(R.id.record);
        this.setOnClickListener(this);
    }

    public void setLabelText(final String label) {
        labelView.setText(label);
    }

    public void setTipViewVisiable(boolean viewVisiable) {
        tipView.setVisibility(viewVisiable ? VISIBLE : GONE);
    }

    public void setMarginLeft(int margin) {
        LinearLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.leftMargin = margin;
        setLayoutParams(layoutParams);
    }

    public void setMarginRight(int margin) {
        LinearLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.rightMargin = margin;
        setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            onClickListener.onClick(path, name, audioPath, position);
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private ChapterItemOnClickListener onClickListener;

    public void setChapterItemOnClickListener(ChapterItemOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface ChapterItemOnClickListener {
        void onClick(String path, String name, String audioPath, int position);
    }
}
