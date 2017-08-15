package cn.it.com.theroy.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import cn.it.com.theroy.R;
import cn.it.com.theroy.bean.TheoryItemBean;


public class TheroyDialog extends AlertDialog {


    private boolean cancelable = true;
    private boolean canceledOnTouchOutside = false;

    private TheroyDialogContentView contentView;


    public TheroyDialog(@NonNull Context context) {
        this(context, R.style.AlertDialogStyle);
    }

    public TheroyDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, R.style.AlertDialogStyle);
        android.view.WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (cancelable && canceledOnTouchOutside && event.getAction() == MotionEvent.ACTION_DOWN) {
            cancel();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        this.cancelable = cancelable;
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        this.canceledOnTouchOutside = cancel;
    }

    public void setView(int layoutId) {
        View contentView = getLayoutInflater().inflate(layoutId, null);
        setContentView(contentView);
    }

    public void setContentView() {
        contentView = new TheroyDialogContentView(getContext());
        setContentView(contentView);
    }

    public void setContentView(View view) {
        if (contentView == null) {
            contentView = new TheroyDialogContentView(getContext());
        }
        contentView.setDialog(this);
        super.setView(contentView);
    }

    public void setTag(TheoryItemBean bean){
        contentView.setConfirmTag(bean);
    }

    public void setTitle(String title) {
        contentView.setTitle(title);
    }

    public void setDesc(String desc) {
        contentView.setDesc(desc);
    }

    public void setCancelText(String cancelText) {
        contentView.setCancelText(cancelText);
    }

    public void setConfirmText(String confirmText) {
        contentView.setConfirmText(confirmText);
    }

    private boolean interceptDism = false;

    /**
     * 拦截所有的dismiss事件
     */
    public void acceptDismiss() {
        this.interceptDism = false;
    }

    public void interceptDismiss() {
        interceptDism = true;
    }

    @Override
    public void dismiss() {
        if (interceptDism) {
            interceptDism = false;
        } else {
            super.dismiss();
        }
    }

    public void setOnDialogClickListener(TheroyDialogContentView.OnDialogClickListener dialogClickListener) {
        if (contentView != null) {
            contentView.setOnDialogClickListener(dialogClickListener);
        }
    }
}
