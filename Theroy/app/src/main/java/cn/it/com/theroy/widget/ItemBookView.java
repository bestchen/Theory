package cn.it.com.theroy.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.it.com.theroy.R;
import cn.it.com.theroy.bean.TheoryItemBean;
import cn.it.com.theroy.download.db.SQLDownLoadInfo;

/**
 * Created by Chenweiwei on 2017/7/29.
 */

public class ItemBookView extends RelativeLayout implements View.OnClickListener {

    private Context context;

    private View rootView;
    private TextView downloadView;
    private TextView tvProgressView;
    private ProgressBar progressBar;
    private TextView chapterView;

    private TheoryItemBean itemBean;
    private OnItemClickListener itemClickListener;

    public ItemBookView(Context context) {
        this(context, null);
    }

    public ItemBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        View.inflate(context, R.layout.item_chapter_layout, this);
        rootView = findViewById(R.id.rootview);
        downloadView = (TextView) findViewById(R.id.tv_downlaod_label);
        tvProgressView = (TextView) findViewById(R.id.tv_progressbar);
        progressBar = (ProgressBar) findViewById(R.id.pb_progressbar);
        chapterView = (TextView) findViewById(R.id.tv_cahpter);
        this.setOnClickListener(this);
        downloadView.setOnClickListener(this);
    }

    public void setMarginLeft(int leftMargin) {
        ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
        if (layoutParams instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) layoutParams).leftMargin = leftMargin;
        } else if (layoutParams instanceof RelativeLayout.LayoutParams) {
            ((LayoutParams) layoutParams).leftMargin = leftMargin;
        } else if (layoutParams instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) layoutParams).leftMargin = leftMargin;
        }
        rootView.setLayoutParams(layoutParams);
    }

    public void setMarginRinght(int rightMargin) {
        ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
        if (layoutParams instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) layoutParams).rightMargin = rightMargin;
        } else if (layoutParams instanceof RelativeLayout.LayoutParams) {
            ((LayoutParams) layoutParams).rightMargin = rightMargin;
        } else if (layoutParams instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) layoutParams).rightMargin = rightMargin;
        }
        rootView.setLayoutParams(layoutParams);
    }

    public void setDownloadText(final String downloadText) {
        if (TextUtils.isEmpty(downloadText)) {
            downloadView.setVisibility(GONE);
        } else {
            downloadView.setVisibility(VISIBLE);
        }
        downloadView.setText(downloadText);
    }

    public void setChapterText(final String chapterText) {
        chapterView.setText(chapterText);
    }

    public void setDownloadVisiable(boolean visiable) {
        downloadView.setVisibility(visiable ? VISIBLE : INVISIBLE);
    }

    public void setTvProgressViewVisiable(boolean visiable) {
        tvProgressView.setVisibility(visiable ? VISIBLE : INVISIBLE);
    }

    public void setProgressBarVisiable(boolean visiable) {
        progressBar.setVisibility(visiable ? VISIBLE : INVISIBLE);
    }

    public void setTvProgressView(int progress) {
        tvProgressView.setText(progress + "%");
    }

    public void updateProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public void startDownload(int progress) {
        setDownloadVisiable(false);
        setTvProgressViewVisiable(true);
        setProgressBarVisiable(true);
        setTvProgressView(progress);
        updateProgress(progress);
    }

    public void downloadSucess() {
        setDownloadVisiable(true);
        setDownloadText("已经下载");
        setTvProgressViewVisiable(false);
        setProgressBarVisiable(false);

        if (itemBean != null) {
            itemBean.setDownlaodText("已经下载");
            itemBean.setDownload(true);
        }
    }

    public TheoryItemBean getItemBean() {
        return itemBean;
    }

    public void setItemBean(TheoryItemBean itemBean) {
        this.itemBean = itemBean;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_downlaod_label:
                if (itemClickListener != null) {
                    itemClickListener.downloadClick(v);
                }
                break;
            case R.id.rootview:
                if (itemClickListener != null) {
                    itemClickListener.itemClick(v);
                }
                break;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void itemClick(View v);

        void downloadClick(View v);
    }
}
