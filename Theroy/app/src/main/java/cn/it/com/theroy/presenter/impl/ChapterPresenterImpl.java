package cn.it.com.theroy.presenter.impl;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

import cn.it.com.theroy.R;
import cn.it.com.theroy.activity.DetailActivity;
import cn.it.com.theroy.iview.IChapterView;
import cn.it.com.theroy.presenter.IChapterPresenter;
import cn.it.com.theroy.uitls.PreferencesUtils;
import cn.it.com.theroy.uitls.UIUitls;
import cn.it.com.theroy.widget.ChapterItemView;


public class ChapterPresenterImpl implements IChapterPresenter {

    private WeakReference<IChapterView> iView;

    public ChapterPresenterImpl(IChapterView iView) {
        this.iView = new WeakReference<IChapterView>(iView);
    }

    @Override
    public ChapterItemView buildChapterItem() {
        if (iViewNull()) {
            return null;
        }
        ChapterItemView itemView = new ChapterItemView(iView.get().getActivity());
        return itemView;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public LinearLayout buildRowContainer() {
        LinearLayout container = new LinearLayout(iView.get().getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUitls.getRealScreenHeight() / 4);
        container.setLayoutParams(params);
        params.gravity = Gravity.BOTTOM;
        container.setBackground(UIUitls.getDrawable(R.drawable.ic_book_shelf));
        return container;
    }

    @Override
    public LinearLayout.LayoutParams buildItemLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UIUitls.getScreenWidth() / 4 - UIUitls.getPixels(20), LinearLayout.LayoutParams.MATCH_PARENT);
        params.leftMargin = UIUitls.getPixels(10);
        params.rightMargin = UIUitls.getPixels(10);
        params.topMargin = UIUitls.getPixels(40);
        params.bottomMargin = UIUitls.getPixels(15);
        return params;
    }

    @Override
    public void onClick(String path, String name, String audioPath) {
        PreferencesUtils.putString(PreferencesUtils.SPKeys.DETAIL_TXT_PATH, path);
        PreferencesUtils.putString(PreferencesUtils.SPKeys.DETAIL_TXT_TITLE, name);
        PreferencesUtils.putString(PreferencesUtils.SPKeys.DETAIL_AUDIO_PATH, audioPath);
        startDetailActivity(path, name, audioPath);
    }

    @Override
    public void initIntent() {
        final String txtpath = PreferencesUtils.getString(PreferencesUtils.SPKeys.DETAIL_TXT_PATH, "");
        final String title = PreferencesUtils.getString(PreferencesUtils.SPKeys.DETAIL_TXT_TITLE, "");
        final String audioPath = PreferencesUtils.getString(PreferencesUtils.SPKeys.DETAIL_AUDIO_PATH, "");
        if (TextUtils.isEmpty(txtpath) || TextUtils.isEmpty(title) || TextUtils.isEmpty(audioPath)) {
            return;
        }
        startDetailActivity(txtpath, title, audioPath);
    }

    private boolean iViewNull() {
        return iView == null || iView.get() == null;
    }

    private void startDetailActivity(String path, String title, String audioPath) {
        if (iViewNull()) {
            return;
        }
        Intent intent = new Intent(iView.get().getActivity(), DetailActivity.class);
        intent.putExtra("path", path);
        intent.putExtra("title", title);
        intent.putExtra("audioPath", audioPath);
        iView.get().getActivity().startActivity(intent);
    }
}
