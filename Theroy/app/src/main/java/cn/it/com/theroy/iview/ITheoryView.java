package cn.it.com.theroy.iview;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import cn.it.com.theroy.bean.TheoryItemBean;
import cn.it.com.theroy.download.db.SQLDownLoadInfo;
import cn.it.com.theroy.widget.ItemBookView;

public interface ITheoryView {

    Activity getActivity();

    void onLoadAssetsResSucess();

    void initRecord(ArrayList<TheoryItemBean> itemBeans);

    void initOrigin(ArrayList<TheoryItemBean> itemBeans);

    void onStartDownload(SQLDownLoadInfo sqlDownLoadInfo);

    void onProgressDownload(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint);

    void onStopDownload(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint);

    void onErrorDownload(SQLDownLoadInfo sqlDownLoadInfo);

    void onSucessDownload(SQLDownLoadInfo sqlDownLoadInfo);
}
