package cn.it.com.theroy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.it.com.theroy.R;
import cn.it.com.theroy.bean.TheoryItemBean;
import cn.it.com.theroy.download.db.FileHelper;
import cn.it.com.theroy.iview.IChapterView;
import cn.it.com.theroy.presenter.IChapterPresenter;
import cn.it.com.theroy.presenter.impl.ChapterPresenterImpl;
import cn.it.com.theroy.uitls.FileUtils;
import cn.it.com.theroy.uitls.PreferencesUtils;
import cn.it.com.theroy.widget.ChapterItemView;

public class ChapterActivity extends BaseActivity implements IChapterView {

    private final static int ROW_COUNT = 10;

    private TextView title;
    private LinearLayout container;
    private List<ChapterItemView> itemViews = new ArrayList<>();

    private int index;
    private String tag;
    private boolean isFromClick = true;
    private String dirName = "";
    private String[] fileName;

    private IChapterPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_chapter;
    }

    @Override
    protected void init() {
        super.init();
        Intent intent = getIntent();
        index = intent.getIntExtra("index", 1);
        tag = intent.getStringExtra("tag");
        isFromClick = intent.getBooleanExtra("click", true);

        dirName = "";
        if (TheoryItemBean.TAG_RECORD.equals(tag)) {
            dirName = "record/part" + index;
        } else {
            dirName = "origin/origin" + index;
        }
        fileName = FileUtils.getAssertsFileName(dirName);
        for (int i = 0, len = fileName.length; i < len; i++) {
            fileName[i] = fileName[i].replace(".txt", "");
        }
        if (TheoryItemBean.TAG_RECORD.equals(tag)) {
            for (int i = 0, len = fileName.length; i < len; i++) {
                for (int j = i + 1; j < len; j++) {
                    if (fileName[i].hashCode() > fileName[j].hashCode()) {
                        String tmp = fileName[i];
                        fileName[i] = fileName[j];
                        fileName[j] = tmp;
                    }
                }
            }
        }
        presenter = new ChapterPresenterImpl(this);
    }

    @Override
    protected void initView() {
        container = (LinearLayout) findViewById(R.id.container);
        title = (TextView) findViewById(R.id.title);
    }

    @Override
    protected void initData() {
        if (fileName == null || fileName.length <= 0) {
            return;
        }
        itemViews.clear();
        final int len = fileName.length;
        LinearLayout containerRow = null;
        for (int i = 1; i <= len; i++) {
            if (i % 4 == 1) {
                containerRow = presenter.buildRowContainer();
            }
            LinearLayout.LayoutParams params = presenter.buildItemLayoutParams();
            ChapterItemView item = presenter.buildChapterItem();
            item.setLabelText(fileName[i - 1]);
            item.setName(fileName[i - 1]);
            item.setPath(FileUtils.getDirPath() + dirName + "/" + fileName[i - 1] + ".txt");
            if (!TextUtils.isEmpty(tag) && TextUtils.equals(tag, TheoryItemBean.TAG_RECORD)) {
                String audioName = fileName[i - 1] + ".MP3";
                final int l = 4 - fileName[i - 1].length();
                for (int j = 0; j < l; j++) {
                    audioName = "0" + audioName;
                }
                item.setAudioPath(FileHelper.audioUnZipPath + "/part" + index + "/" + audioName);
            }
            item.setChapterItemOnClickListener(new ChapterItemView.ChapterItemOnClickListener() {
                @Override
                public void onClick(String path, String title, String audioPath, int position) {
                    PreferencesUtils.putInt(PreferencesUtils.SPKeys.DETAIL_POSITION,position);
                    showTipView(position);
                    presenter.onClick(path, title, audioPath);
                }
            });
            item.setPosition(i - 1);
            containerRow.addView(item, params);
            if (i % 4 == 0 || i == len) {
                container.addView(containerRow);
            }
            itemViews.add(item);
        }

        final int position = PreferencesUtils.getInt(PreferencesUtils.SPKeys.DETAIL_POSITION, -1);
        showTipView(position);

        if (!isFromClick) {
            presenter.initIntent();
        }
    }

    @Override
    public void showTipView(int position) {
        for (int i = 0, size = itemViews.size(); i < size; i++) {
            itemViews.get(i).setTipViewVisiable(position == i);
        }
    }

    @Override
    public ChapterActivity getActivity() {
        return this;
    }
}
