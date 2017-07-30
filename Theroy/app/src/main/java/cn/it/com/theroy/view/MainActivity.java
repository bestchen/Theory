package cn.it.com.theroy.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.it.com.theroy.R;
import cn.it.com.theroy.bean.TheoryItemBean;
import cn.it.com.theroy.download.TaskInfo;
import cn.it.com.theroy.download.db.SQLDownLoadInfo;
import cn.it.com.theroy.presenter.ITheroyPresenter;
import cn.it.com.theroy.presenter.impl.TheroyPresenterImpl;
import cn.it.com.theroy.uitls.FileUtils;
import cn.it.com.theroy.uitls.UIUitls;
import cn.it.com.theroy.widget.ItemBookView;
import cn.it.com.theroy.widget.TheroyDialog;
import cn.it.com.theroy.widget.TheroyDialogContentView;
import cn.it.com.theroy.widget.TitleTextView;

public class MainActivity extends FragmentActivity implements ITheoryView {

    private TitleTextView holderTitleView;
    private TitleTextView recordTitleView;
    private TitleTextView originTitleView;

    private ArrayList<ItemBookView> itemBookViews = new ArrayList<>();

    private ITheroyPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        recordTitleView = (TitleTextView) findViewById(R.id.title_textview_record);
        originTitleView = (TitleTextView) findViewById(R.id.title_textview_origin);
        holderTitleView = (TitleTextView) findViewById(R.id.title_textview_holder);

    }

    private void initEvent() {

    }

    private void initData() {
        presenter = new TheroyPresenterImpl(this);
        presenter.init();
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
    public void initRecord(final ArrayList<TheoryItemBean> itemBeans) {
        if (itemBeans == null || itemBeans.isEmpty()) {
            return;
        }
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
            } else if (i % 3 == 1) {
                params.rightMargin = UIUitls.getPixels(0);
            }
            final TheoryItemBean itemBean = itemBeans.get(i);
            itemBookView.setDownloadText(itemBean.getDownlaodText());
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
                    presenter.downLoad(itemBookView, itemBean);
                }
            });
        }
    }

    @Override
    public void initOrigin(ArrayList<TheoryItemBean> itemBeans) {
        if (itemBeans == null || itemBeans.isEmpty()) {
            return;
        }
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
            } else if (i % 3 == 1 || i == 13) {
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
        }
    }

    @Override
    public void onStartDownload(ItemBookView itemBookView, SQLDownLoadInfo sqlDownLoadInfo) {
        TaskInfo taskInfo = presenter.getTaskInfo(sqlDownLoadInfo.getTaskID());
        if (taskInfo != null) {
            itemBookView.startDownload(taskInfo.getProgress());
        }
    }

    @Override
    public void onProgressDownload(ItemBookView itemBookView, SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
        TaskInfo taskInfo = presenter.getTaskInfo(sqlDownLoadInfo.getTaskID());
        if (taskInfo != null) {
            itemBookView.startDownload(taskInfo.getProgress());
        }
    }

    @Override
    public void onStopDownload(ItemBookView itemBookView, SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {

    }

    @Override
    public void onErrorDownload(ItemBookView itemBookView, SQLDownLoadInfo sqlDownLoadInfo) {

    }

    @Override
    public void onSucessDownload(ItemBookView itemBookView, SQLDownLoadInfo sqlDownLoadInfo) {
        itemBookView.downloadSucess();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
