package com.hrf.chess.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hrf.chess.R;
import com.hrf.chess.utils.ViewUtils;

/**
 * User: HRF
 * Date: 2017/8/25
 * Time: 17:09
 * Description: Too
 */
public class InfoDialog extends Dialog {

    private Context context;
    private Button bt_confirm;
    private TextView tv_content;
    private InfoDialogListener infoDialogListener;
    private String content;

    public InfoDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public InfoDialog(@NonNull Context context, @StyleRes int themeResId, String content) {
        super(context, themeResId);
        this.context = context;
        this.content = content;
    }

    protected InfoDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_info);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);
        tv_content = (TextView) findViewById(R.id.tv_content);
        ViewUtils.setText(tv_content, content);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;
        LinearLayout ll_content = (LinearLayout) findViewById(R.id.ll_content);
        ViewGroup.LayoutParams layoutParams = ll_content.getLayoutParams();
        layoutParams.width = w * 4 / 5;
        layoutParams.height = h * 3 / 5;
        ll_content.setLayoutParams(layoutParams);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (infoDialogListener != null) {
                    infoDialogListener.onConfirm();
                }
            }
        });
    }

    public interface InfoDialogListener {
        void onConfirm();
    }

    public void setInfoDialogListener(InfoDialogListener infoDialogListener) {
        this.infoDialogListener = infoDialogListener;
    }

    @Override
    public void show() {
        super.show();
    }
}
