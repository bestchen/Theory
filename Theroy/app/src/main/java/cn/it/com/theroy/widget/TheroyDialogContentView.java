package cn.it.com.theroy.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.it.com.theroy.R;
import cn.it.com.theroy.bean.TheoryItemBean;


public class TheroyDialogContentView extends LinearLayout implements View.OnClickListener {

    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvCancel;
    private TextView tvConfirm;

    private TheroyDialog dialog;

    private TheoryItemBean confirmTag;

    private OnDialogClickListener dialogClickListener;

    public TheroyDialogContentView(Context context) {
        this(context, null);
    }

    public TheroyDialogContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.dialog_content_view, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
    }

    public void setTitle(final String title) {
        tvTitle.setText(title);
    }

    public void setDesc(final String desc) {
        tvDesc.setText(desc);
    }

    public void setCancelText(final String cancelText) {
        tvCancel.setText(cancelText);
    }

    public void setConfirmText(final String confirmText) {
        tvConfirm.setText(confirmText);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                if (dialogClickListener != null) {
                    dialogClickListener.onCancel();
                }
                break;
            case R.id.tv_confirm:
                if (dialogClickListener != null) {
                    dialogClickListener.onConfirm(confirmTag);
                }
                break;
        }
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void setConfirmTag(TheoryItemBean bean) {
        this.confirmTag = bean;
    }

    public TheroyDialog getDialog() {
        return dialog;
    }

    public void setDialog(TheroyDialog dialog) {
        this.dialog = dialog;
    }

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        this.dialogClickListener = listener;
    }

    public interface OnDialogClickListener {
        void onCancel();

        void onConfirm(TheoryItemBean bean);
    }
}
