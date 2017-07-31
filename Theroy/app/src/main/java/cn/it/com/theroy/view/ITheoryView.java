package cn.it.com.theroy.view;

import android.app.Activity;

import java.util.ArrayList;

import cn.it.com.theroy.bean.TheoryItemBean;
import cn.it.com.theroy.download.db.SQLDownLoadInfo;
import cn.it.com.theroy.widget.ItemBookView;

/**
 * Created by Chenweiwei on 2017/7/29.
 */

public interface ITheoryView {

    Activity getActivity();

    void initRecord(ArrayList<TheoryItemBean> itemBeans);

    void initOrigin(ArrayList<TheoryItemBean> itemBeans);

    void onStartDownload(SQLDownLoadInfo sqlDownLoadInfo);

    void onProgressDownload(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint);

    void onStopDownload(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint);

    void onErrorDownload(SQLDownLoadInfo sqlDownLoadInfo);

    void onSucessDownload(SQLDownLoadInfo sqlDownLoadInfo);
}
