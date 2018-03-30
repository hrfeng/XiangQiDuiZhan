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

import com.hrf.chess.R;

/**
 * User: HRF
 * Date: 2017/8/25
 * Time: 17:09
 * Description: Too
 */
public class ChessDialog extends Dialog {

    private Context context;
    private Button bt_closs;
    private Button bt_restart;
    private ChessDialogListener chessDialogListener;

    public ChessDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public ChessDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected ChessDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_chess);
        bt_closs = (Button) findViewById(R.id.bt_closs);
        bt_restart = (Button) findViewById(R.id.bt_restart);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;
        LinearLayout ll_content = (LinearLayout) findViewById(R.id.ll_content);
        ViewGroup.LayoutParams layoutParams = ll_content.getLayoutParams();
        layoutParams.width = w * 4 / 5;
        layoutParams.height = h * 3 / 5;
        ll_content.setLayoutParams(layoutParams);
        bt_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chessDialogListener != null) {
                    chessDialogListener.onRestart();
                }
            }
        });
        bt_closs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chessDialogListener != null) {
                    chessDialogListener.onCloss();
                }
            }
        });

    }

    public interface ChessDialogListener {
        void onCloss();

        void onRestart();
    }

    public void setChessDialogListener(ChessDialogListener chessDialogListener) {
        this.chessDialogListener = chessDialogListener;
    }

    @Override
    public void show() {
        super.show();
    }
}
