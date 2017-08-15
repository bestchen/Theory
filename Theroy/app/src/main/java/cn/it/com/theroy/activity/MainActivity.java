package cn.it.com.theroy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.it.com.theroy.R;
import cn.it.com.theroy.bean.TheoryItemBean;
import cn.it.com.theroy.download.TaskInfo;
import cn.it.com.theroy.download.db.SQLDownLoadInfo;
import cn.it.com.theroy.iview.ITheoryView;
import cn.it.com.theroy.presenter.ITheroyPresenter;
import cn.it.com.theroy.presenter.impl.TheroyPresenterImpl;
import cn.it.com.theroy.uitls.FileUtils;
import cn.it.com.theroy.uitls.PreferencesUtils;
import cn.it.com.theroy.uitls.UIUitls;
import cn.it.com.theroy.widget.ItemBookView;
import cn.it.com.theroy.widget.TitleTextView;

public class MainActivity extends BaseActivity implements ITheoryView {

    private View splashView;
    private TitleTextView holderTitleView;
    private TitleTextView recordTitleView;
    private TitleTextView originTitleView;

    private ArrayList<ItemBookView> itemRecordViews = new ArrayList<>();
    private ArrayList<ItemBookView> itemOriginViews = new ArrayList<>();

    private ITheroyPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        splashView = findViewById(R.id.splash);
        recordTitleView = (TitleTextView) findViewById(R.id.title_textview_record);
        originTitleView = (TitleTextView) findViewById(R.id.title_textview_origin);
        holderTitleView = (TitleTextView) findViewById(R.id.title_textview_holder);

    }


    @Override
    protected void initData() {
        presenter = new TheroyPresenterImpl(this);
        presenter.init();
        presenter.onLoadAssetsRes();
        recordTitleView.setTitlePre(UIUitls.getString(R.string.name_record_pre));
        recordTitleView.setTitle(UIUitls.getString(R.string.name_record_title));
        originTitleView.setTitlePre(UIUitls.getString(R.string.name_origin_title_pre));
        originTitleView.setTitle(UIUitls.getString(R.string.name_origin_title));

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onLoadAssetsResSucess() {
        splashView.setVisibility(View.GONE);
        presenter.initRecordItems();
        presenter.initOriginItems();
        final int index = PreferencesUtils.getInt(PreferencesUtils.SPKeys.CHAPTER_INDEX, -1);
        final String tag = PreferencesUtils.getString(PreferencesUtils.SPKeys.CHAPTER_TAG, "");
        showTipView(tag, index);
        presenter.openLastChapter(tag, index);
    }

    @Override
    public void initRecord(final ArrayList<TheoryItemBean> itemBeans) {
        if (itemBeans == null || itemBeans.isEmpty()) {
            return;
        }
        itemRecordViews.clear();
        final int size = itemBeans.size();
        LinearLayout recordContainer1 = (LinearLayout) findViewById(R.id.ll_record_container_1);
        LinearLayout recordContainer2 = (LinearLayout) findViewById(R.id.ll_record_container_2);
        LinearLayout.LayoutParams params = null;
        for (int i = 0; i < size; i++) {
            final ItemBookView itemBookView = new ItemBookView(this);
            itemBookView.setDownloadVisiable(true);
            params = UIUitls.generateLayoputParams();
            if (i % 4 == 0) {
                params.leftMargin = UIUitls.getPixels(0);
            } else if (i % 4 == 3) {
                params.rightMargin = UIUitls.getPixels(0);
            }
            final TheoryItemBean itemBean = itemBeans.get(i);
            itemBookView.setDownloadText(itemBean.getDownloadTip());
            itemBookView.setChapterText(itemBean.getChapterText());
            if (i < 4 * 1) {
                recordContainer1.addView(itemBookView, params);
            } else if (i < 4 * 2) {
                recordContainer2.addView(itemBookView, params);
            }
            itemBookView.setItemBean(itemBean);
            itemBookView.setOnItemClickListener(new ItemBookView.OnItemClickListener() {
                @Override
                public void itemClick(View v) {
                    presenter.openTheory(itemBookView, itemBean);
                }

                @Override
                public void downloadClick(View v) {
                    presenter.downLoad(itemBean);
                }
            });
            itemRecordViews.add(itemBookView);
        }
    }

    @Override
    public void initOrigin(ArrayList<TheoryItemBean> itemBeans) {
        if (itemBeans == null || itemBeans.isEmpty()) {
            return;
        }
        itemOriginViews.clear();
        final int size = itemBeans.size();
        LinearLayout originContainer1 = (LinearLayout) findViewById(R.id.ll_origin_container_1);
        LinearLayout originContainer2 = (LinearLayout) findViewById(R.id.ll_origin_container_2);
        LinearLayout originContainer3 = (LinearLayout) findViewById(R.id.ll_origin_container_3);
        LinearLayout originContainer4 = (LinearLayout) findViewById(R.id.ll_origin_container_4);
        LinearLayout.LayoutParams params = null;
        for (int i = 0; i < size; i++) {
            final ItemBookView itemBookView = new ItemBookView(this);
            itemBookView.setDownloadVisiable(false);
            params = UIUitls.generateLayoputParams();
            if (i % 4 == 0) {
                params.leftMargin = UIUitls.getPixels(0);
            } else if (i % 4 == 3 || i == 13) {
                params.rightMargin = UIUitls.getPixels(0);
            }
            final TheoryItemBean itemBean = itemBeans.get(i);
            itemBookView.setChapterText(itemBean.getChapterText());
            if (i < 4 * 1) {
                originContainer1.addView(itemBookView, params);
            } else if (i < 4 * 2) {
                originContainer2.addView(itemBookView, params);
            } else if (i < 4 * 3) {
                originContainer3.addView(itemBookView, params);
            } else if (i < 4 * 4) {
                final int childCount = originContainer4.getChildCount();
                originContainer4.addView(itemBookView, childCount > 0 ? childCount - 1 : childCount, params);
            }
            itemBookView.setOnItemClickListener(new ItemBookView.OnItemClickListener() {
                @Override
                public void itemClick(View v) {
                    presenter.openTheory(itemBookView, itemBean);
                }

                @Override
                public void downloadClick(View v) {
                }
            });
            itemOriginViews.add(itemBookView);
        }
    }

    @Override
    public void onStartDownload(SQLDownLoadInfo sqlDownLoadInfo) {
        TaskInfo taskInfo = presenter.getTaskInfo(sqlDownLoadInfo.getTaskID());
        if (taskInfo != null) {
            ItemBookView bookView = findItemViewById(taskInfo.getTaskID());
            if (bookView != null) {
                bookView.startDownload(taskInfo.getProgress());
            }
        }
    }

    @Override
    public void onProgressDownload(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
        TaskInfo taskInfo = presenter.getTaskInfo(sqlDownLoadInfo.getTaskID());
        if (taskInfo != null) {
            ItemBookView bookView = findItemViewById(taskInfo.getTaskID());
            if (bookView != null) {
                bookView.startDownload(taskInfo.getProgress());
            }
        }
    }

    @Override
    public void onStopDownload(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {

    }

    @Override
    public void onErrorDownload(SQLDownLoadInfo sqlDownLoadInfo) {

    }

    @Override
    public void onSucessDownload(SQLDownLoadInfo sqlDownLoadInfo) {
        TaskInfo taskInfo = presenter.getTaskInfo(sqlDownLoadInfo.getTaskID());
        if (taskInfo != null) {
            ItemBookView bookView = findItemViewById(taskInfo.getTaskID());
            if (bookView != null) {
                bookView.downloadSucess();
                TheoryItemBean itemBean = bookView.getItemBean();
                if (itemBean != null) {
                    presenter.saveTheoryItem(FileUtils.getRecordPath(itemBean.getId()), itemBean);
                }
            }
        }
    }

    private ItemBookView findItemViewById(String id) {
        if (TextUtils.isEmpty(id) || itemRecordViews.isEmpty()) {
            return null;
        }
        for (int i = 0, size = itemRecordViews.size(); i < size; i++) {
            final ItemBookView view = itemRecordViews.get(i);
            if (view == null) {
                continue;
            }
            final TheoryItemBean itemBean = view.getItemBean();
            if (itemBean == null) {
                continue;
            }
            if (id.equals(itemBean.getId())) {
                return view;
            }
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        final int index = PreferencesUtils.getInt(PreferencesUtils.SPKeys.CHAPTER_INDEX, -1);
        final String tag = PreferencesUtils.getString(PreferencesUtils.SPKeys.CHAPTER_TAG, "");
        showTipView(tag, index);
    }

    private void showTipView(String tag, int index) {
        for (int i = 0, size = itemRecordViews.size(); i < size; i++) {
            itemRecordViews.get(i).setTipViewVisiable(false);
        }
        for (int i = 0, size = itemOriginViews.size(); i < size; i++) {
            itemOriginViews.get(i).setTipViewVisiable(false);
        }
        if (index != -1 && !TextUtils.isEmpty(tag)) {
            if (TextUtils.equals(TheoryItemBean.TAG_RECORD, tag) && itemRecordViews.size() > index) {
                itemRecordViews.get(index - 1).setTipViewVisiable(true);
            } else if (TextUtils.equals(TheoryItemBean.TAG_ORIGIN, tag) && itemOriginViews.size() > index) {
                itemOriginViews.get(index - 1).setTipViewVisiable(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
