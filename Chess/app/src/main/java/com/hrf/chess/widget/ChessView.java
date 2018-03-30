package com.hrf.chess.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hrf.chess.bean.ChessViewBean;
import com.hrf.chess.bean.WifiBean;

/**
 * User: HRF
 * Date: 2017/8/28
 * Time: 16:17
 * Description: 象棋view
 */
public class ChessView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private String ip;

    private SurfaceHolder mCallback;
    private Canvas mCanvas;
    private boolean isRuning;
    private Thread mThread;

    private Paint paintBackgroundBoard;//棋盘背景
    private Paint paintFrame;//外框
    private Paint paintLine;//棋盘线
    private Paint paintBoardText;//文字 楚河 汉界
    private Paint paintText;//棋子文字
    private Paint paintWaxText;//棋子变大文字
    private Paint paintBorderText;// //棋子文字边框
    private Paint paintChess;//棋子
    private Paint paintTag;//对方下棋标记
    private Paint paintHint;//提示下棋位置

    private float widthPixels;
    private float heightPixels;
    private float density;
    private int densityDpi;

    private int width;//包括Padding的宽度
    private int height;//包括Padding的高度
    private float widthPractical;//实际宽度去除Padding
    private float heightPractical;//实际高度去除Padding

    private float wPractical;//格子
    private float wExcursion;//短线长
    private float wInnerSkewing;//内偏移量
    private float wOuterSkewing;//外偏移量
    private float wStrokeWidth;//笔画粗细
    private float StrokeWidth;//外边框笔画粗细
    private float R;//外r
    private float tagR;//选中外r
    private float rr;//内r
    private float tagrr;//选中内r
    private float tagMinR;//标记小r
    private float tagMaxR;//标记大r;
    private float l;//
    private float t;//
    private float r;//
    private float b;//
    private ChessViewBean[][] chessViewBeen;
    private ChessViewBean[][] chessViewBeen1;

    private Context context;

    private WifiBean wifiBean;
    private WifiBean.Piece piece;
    private ChessViewBean cvb;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ChessView(Context context) {
        super(context);
        init(context);
    }

    public ChessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        widthPixels = dm.widthPixels;
        heightPixels = dm.heightPixels;
        density = dm.density;
        densityDpi = dm.densityDpi;
        width = (int) (widthPixels / 4);
        height = width;
        mCallback = getHolder();
        mCallback.addCallback(this);
        mThread = new Thread(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpec = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpec = MeasureSpec.getMode(heightMeasureSpec);
        if (widthSpec == MeasureSpec.AT_MOST && heightSpec == MeasureSpec.AT_MOST) {
            widthSize = width;
            heightSize = height;
        } else if (widthSpec == MeasureSpec.AT_MOST) {
            widthSize = width;
        } else if (heightSpec == MeasureSpec.AT_MOST) {
            heightSize = height;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        widthPractical = width - getPaddingLeft() - getPaddingRight();
        heightPractical = height - getPaddingTop() - getPaddingBottom();
        wPractical = widthPractical / 10;
        wExcursion = wPractical / 8;
        wOuterSkewing = wPractical / 8;
        wInnerSkewing = wPractical / 15;
        StrokeWidth = wPractical / 12;
        wStrokeWidth = wPractical / 25;
        R = wPractical / 21 * 10;
        tagR = wPractical / 21 * 11;
        rr = wPractical / 5 * 2;
        tagrr = wPractical / 105 * 46.2f;
        tagMinR = wPractical / 15;
        tagMaxR = wPractical / 2;
        l = wPractical;
        t = (heightPractical - (wPractical * 9)) / 2;
        r = widthPractical - l;
        b = heightPractical - t;
        initData();
    }


    private void initData() {
        chessViewBeen = new ChessViewBean[10][9];
        chessViewBeen1 = new ChessViewBean[10][9];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                chessViewBeen[i][j] = new ChessViewBean(wPractical * j + l, wPractical * i + t);
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                chessViewBeen1[i][j] = new ChessViewBean(wPractical * j + l, -wPractical * i + b);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRuning = true;
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRuning = false;
    }

    @Override
    public void run() {
        initPaint();
        while (isRuning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - start < 100) {
                try {
                    Thread.sleep(100 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {
        try {
            mCanvas = mCallback.lockCanvas();
            mCanvas.drawColor(Color.parseColor("#52381F"));
            drawCell();
            if (wifiBean != null) {
                drawText();
            }
        } catch (Exception e) {
        } finally {
            if (mCanvas != null) {
                mCallback.unlockCanvasAndPost(mCanvas);
            }
        }
    }


    private void initPaint() {
        paintBackgroundBoard = new Paint();
        paintBackgroundBoard.setStyle(Paint.Style.FILL);
        paintBackgroundBoard.setColor(Color.parseColor("#EBC999"));
        paintBackgroundBoard.setAntiAlias(true);
        //
        paintFrame = new Paint();
        paintFrame.setStrokeWidth(StrokeWidth);
        paintFrame.setStyle(Paint.Style.STROKE);
        paintFrame.setAntiAlias(true);
        paintFrame.setColor(Color.parseColor("#896337"));
        //
        paintLine = new Paint();
        paintLine.setStrokeWidth(wStrokeWidth);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.parseColor("#896337"));
        //
        paintBoardText = new Paint();
        paintBoardText.setTypeface(Typeface.DEFAULT_BOLD);
        paintBoardText.setAntiAlias(true);
        paintBoardText.setTextSize(wPractical / 5 * 3);
        paintBoardText.setColor(Color.parseColor("#896337"));
        paintText = new Paint();
        paintText.setTextSize(wPractical / 5 * 3);
        paintText.setTypeface(Typeface.DEFAULT_BOLD);
        paintText.setAntiAlias(true);
        paintWaxText = new Paint();
        paintWaxText.setTypeface(Typeface.DEFAULT_BOLD);
        paintWaxText.setAntiAlias(true);
        paintWaxText.setColor(Color.parseColor("#896337"));
        paintWaxText.setTextSize(wPractical / 105 * 69.5f);
        //
        paintBorderText = new Paint();
        paintBorderText.setStyle(Paint.Style.STROKE);
        paintBorderText.setAntiAlias(true);
        paintBorderText.setStrokeWidth(wStrokeWidth);
        //
        paintChess = new Paint();
        paintChess.setColor(Color.parseColor("#BC8E58"));
        paintChess.setStyle(Paint.Style.FILL);
        paintChess.setAntiAlias(true);
        //
        paintTag = new Paint();
        paintTag.setColor(Color.parseColor("#7DFFFF"));
        paintTag.setAntiAlias(true);
        //
        paintHint = new Paint();
        paintHint.setColor(Color.parseColor("#FF8080"));
        paintHint.setTypeface(Typeface.DEFAULT_BOLD);
        paintHint.setStyle(Paint.Style.FILL);
        paintHint.setAntiAlias(true);
    }


    /**
     * 画格子
     */
    public void drawCell() {
        //棋盘背景
        mCanvas.drawRect(l - wOuterSkewing * 6, t - wOuterSkewing * 6, r + wOuterSkewing * 6, b + wOuterSkewing * 6, paintBackgroundBoard);
        //外框
        mCanvas.drawRect(l - wOuterSkewing, t - wOuterSkewing, r + wOuterSkewing, b + wOuterSkewing, paintFrame);
        //竖线
        for (int i = 0; i < 9; i++) {
            if (i == 0 || i == 8) {
                mCanvas.drawLine(l + wPractical * i, t, l + wPractical * i, b, paintLine);
            } else {
                mCanvas.drawLine(l + wPractical * i, t, l + wPractical * i, t + wPractical * 4, paintLine);
                mCanvas.drawLine(l + wPractical * i, b - wPractical * 4, l + wPractical * i, b, paintLine);
            }
        }
        //横线
        for (int i = 0; i < 10; i++) {
            mCanvas.drawLine(l, t + wPractical * i, r, t + wPractical * i, paintLine);
        }
        //辅助线
        mCanvas.drawLine(chessViewBeen[0][3].x, chessViewBeen[0][3].y, chessViewBeen[2][5].x, chessViewBeen[2][5].y, paintLine);
        mCanvas.drawLine(chessViewBeen[0][5].x, chessViewBeen[0][5].y, chessViewBeen[2][3].x, chessViewBeen[2][3].y, paintLine);
        mCanvas.drawLine(chessViewBeen[9][3].x, chessViewBeen[9][3].y, chessViewBeen[7][5].x, chessViewBeen[7][5].y, paintLine);
        mCanvas.drawLine(chessViewBeen[9][5].x, chessViewBeen[9][5].y, chessViewBeen[7][3].x, chessViewBeen[7][3].y, paintLine);

        float x = 0, y = 0;
        //炮
        x = chessViewBeen[2][1].x;
        y = chessViewBeen[2][1].y;
        leftTo(x, y, x, y, paintLine);
        leftBelow(x, y, x, y, paintLine);
        rightTo(x, y, x, y, paintLine);
        rightBelow(x, y, x, y, paintLine);
        //
        x = chessViewBeen[2][7].x;
        y = chessViewBeen[2][7].y;
        leftTo(x, y, x, y, paintLine);
        leftBelow(x, y, x, y, paintLine);
        rightTo(x, y, x, y, paintLine);
        rightBelow(x, y, x, y, paintLine);
        //炮
        x = chessViewBeen[7][1].x;
        y = chessViewBeen[7][1].y;
        leftTo(x, y, x, y, paintLine);
        leftBelow(x, y, x, y, paintLine);
        rightTo(x, y, x, y, paintLine);
        rightBelow(x, y, x, y, paintLine);
        //
        x = chessViewBeen[7][7].x;
        y = chessViewBeen[7][7].y;
        leftTo(x, y, x, y, paintLine);
        leftBelow(x, y, x, y, paintLine);
        rightTo(x, y, x, y, paintLine);
        rightBelow(x, y, x, y, paintLine);
        //兵
        x = chessViewBeen[3][0].x;
        y = chessViewBeen[3][0].y;
        rightTo(x, y, x, y, paintLine);
        rightBelow(x, y, x, y, paintLine);
        //
        x = chessViewBeen[3][2].x;
        y = chessViewBeen[3][2].y;
        leftTo(x, y, x, y, paintLine);
        leftBelow(x, y, x, y, paintLine);
        rightTo(x, y, x, y, paintLine);
        rightBelow(x, y, x, y, paintLine);
        //
        x = chessViewBeen[3][4].x;
        y = chessViewBeen[3][4].y;
        leftTo(x, y, x, y, paintLine);
        leftBelow(x, y, x, y, paintLine);
        rightTo(x, y, x, y, paintLine);
        rightBelow(x, y, x, y, paintLine);
        //
        x = chessViewBeen[3][6].x;
        y = chessViewBeen[3][6].y;
        leftTo(x, y, x, y, paintLine);
        leftBelow(x, y, x, y, paintLine);
        rightTo(x, y, x, y, paintLine);
        rightBelow(x, y, x, y, paintLine);
        //
        x = chessViewBeen[3][8].x;
        y = chessViewBeen[3][8].y;
        leftTo(x, y, x, y, paintLine);
        leftBelow(x, y, x, y, paintLine);
        //兵
        x = chessViewBeen[6][0].x;
        y = chessViewBeen[6][0].y;
        rightTo(x, y, x, y, paintLine);
        rightBelow(x, y, x, y, paintLine);
        //
        x = chessViewBeen[6][2].x;
        y = chessViewBeen[6][2].y;
        leftTo(x, y, x, y, paintLine);
        leftBelow(x, y, x, y, paintLine);
        rightTo(x, y, x, y, paintLine);
        rightBelow(x, y, x, y, paintLine);
        //
        x = chessViewBeen[6][4].x;
        y = chessViewBeen[6][4].y;
        leftTo(x, y, x, y, paintLine);
        leftBelow(x, y, x, y, paintLine);
        rightTo(x, y, x, y, paintLine);
        rightBelow(x, y, x, y, paintLine);
        //
        x = chessViewBeen[6][6].x;
        y = chessViewBeen[6][6].y;
        leftTo(x, y, x, y, paintLine);
        leftBelow(x, y, x, y, paintLine);
        rightTo(x, y, x, y, paintLine);
        rightBelow(x, y, x, y, paintLine);
        //
        x = chessViewBeen[6][8].x;
        y = chessViewBeen[6][8].y;
        leftTo(x, y, x, y, paintLine);
        leftBelow(x, y, x, y, paintLine);

        float textWidth = getTextWidth(paintBoardText, "楚河");
        float textHeight = getTextHeight(paintBoardText, "楚河");
        mCanvas.drawText("楚河", width / 4 - textWidth / 2, height / 2 + textHeight / 5 * 2, paintBoardText);
        mCanvas.drawText("汉界", width - widthPractical / 4 - textWidth / 2, height / 2 + textHeight / 5 * 2, paintBoardText);
    }

    //左上
    private void leftTo(float startX, float startY, float endX, float endY, Paint paint) {
        mCanvas.drawLine(startX - wInnerSkewing + wStrokeWidth / 2, startY - wInnerSkewing, endX - wInnerSkewing - wExcursion, endY - wInnerSkewing, paint);
        mCanvas.drawLine(startX - wInnerSkewing, startY - wInnerSkewing + wStrokeWidth / 2, endX - wInnerSkewing, endY - wInnerSkewing - wExcursion, paint);
    }

    //右上
    private void rightTo(float startX, float startY, float endX, float endY, Paint paint) {
        mCanvas.drawLine(startX + wInnerSkewing - wStrokeWidth / 2, startY - wInnerSkewing, endX + wInnerSkewing + wExcursion, endY - wInnerSkewing, paint);
        mCanvas.drawLine(startX + wInnerSkewing, startY - wInnerSkewing + wStrokeWidth / 2, endX + wInnerSkewing, endY - wInnerSkewing - wExcursion, paint);
    }

    //左下
    private void leftBelow(float startX, float startY, float endX, float endY, Paint paint) {
        mCanvas.drawLine(startX - wInnerSkewing + wStrokeWidth / 2, startY + wInnerSkewing, endX - wInnerSkewing - wExcursion, endY + wInnerSkewing, paint);
        mCanvas.drawLine(startX - wInnerSkewing, startY + wInnerSkewing - wStrokeWidth / 2, endX - wInnerSkewing, endY + wInnerSkewing + wExcursion, paint);
    }

    //右下
    private void rightBelow(float startX, float startY, float endX, float endY, Paint paint) {
        mCanvas.drawLine(startX + wInnerSkewing - wStrokeWidth / 2, startY + wInnerSkewing, endX + wInnerSkewing + wExcursion, endY + wInnerSkewing, paint);
        mCanvas.drawLine(startX + wInnerSkewing, startY + wInnerSkewing + wStrokeWidth / 2, endX + wInnerSkewing, endY + wInnerSkewing + wExcursion, paint);
    }

    public void drawText() {
        ChessViewBean[][] cvb = null;
        if (TextUtils.isEmpty(wifiBean.firstIp) || getIp().equals(wifiBean.firstIp)) {
            cvb = chessViewBeen;
        } else {
            cvb = chessViewBeen1;
        }
        for (int i = 0; i < wifiBean.pieceList.size(); i++) {
            WifiBean.Piece p = wifiBean.pieceList.get(i);
            float textWidth = getTextWidth(paintText, p.name);
            float textHeight = getTextHeight(paintText, p.name);
            //背景
            if (p.direction) {
                paintText.setColor(Color.parseColor("#8A3D23"));
                paintBorderText.setColor(Color.parseColor("#8A3D23"));
            } else {
                paintText.setColor(Color.parseColor("#32392F"));
                paintBorderText.setColor(Color.parseColor("#32392F"));
            }
            //对方下棋标记
            if (!TextUtils.isEmpty(wifiBean.runIp) && wifiBean.runIp.equals(getIp()) && wifiBean.onePiece != null && wifiBean.towPiece != null) {
                paintTag.setStyle(Paint.Style.FILL);
                mCanvas.drawCircle(cvb[wifiBean.onePiece.x][wifiBean.onePiece.y].x, cvb[wifiBean.onePiece.x][wifiBean.onePiece.y].y, tagMinR, paintTag);
                paintTag.setStyle(Paint.Style.STROKE);
                mCanvas.drawCircle(cvb[wifiBean.onePiece.x][wifiBean.onePiece.y].x, cvb[wifiBean.onePiece.x][wifiBean.onePiece.y].y, tagMinR / 2 * 3, paintTag);
                mCanvas.drawCircle(cvb[wifiBean.towPiece.x][wifiBean.towPiece.y].x, cvb[wifiBean.towPiece.x][wifiBean.towPiece.y].y, tagMaxR, paintTag);
            }
            float x = cvb[p.x][p.y].x;
            float y = cvb[p.x][p.y].y;
            //棋子
            mCanvas.drawCircle(x, y, R, paintChess);
            mCanvas.drawCircle(x, y, rr, paintBorderText);
            //字
            mCanvas.drawText(p.name, x - textWidth / 9 * 5, y + textHeight / 5 * 2, paintText);
        }
        if (piece != null) {
            float textWidth = getTextWidth(paintText, piece.name);
            float textHeight = getTextHeight(paintText, piece.name);
            //背景
            if (piece.direction) {
                paintWaxText.setColor(Color.parseColor("#8A3D23"));
                paintBorderText.setColor(Color.parseColor("#8A3D23"));
            } else {
                paintWaxText.setColor(Color.parseColor("#32392F"));
                paintBorderText.setColor(Color.parseColor("#32392F"));
            }
            //选中棋子的提示标记
            if (wifiBean.hintList.size() > 0) {
                for (WifiBean.Piece pl : wifiBean.hintList) {
                    mCanvas.drawCircle(cvb[pl.x][pl.y].x, cvb[pl.x][pl.y].y, tagMinR, paintHint);
                }
            }

            float x = cvb[piece.x][piece.y].x;
            float y = cvb[piece.x][piece.y].y;
            //棋子文字边框
            mCanvas.drawCircle(x, y, tagR, paintChess);
            mCanvas.drawCircle(x, y, tagrr, paintBorderText);
            //字
            mCanvas.drawText(piece.name, x - textWidth / 9 * 5, y + textHeight / 5 * 2, paintWaxText);
        }
    }

    private float getTextWidth(Paint paint, String textContent) {
        Rect textRect = new Rect();
        paint.getTextBounds(textContent, 0, textContent.length(), textRect);
        return textRect.width();
    }

    private float getTextHeight(Paint paint, String textContent) {
        Rect textRect = new Rect();
        paint.getTextBounds(textContent, 0, textContent.length(), textRect);
        return textRect.height();
    }

    public void setWifiBean(WifiBean wifiBean) {
        this.wifiBean = wifiBean;
        invalidate();
    }

    private float startX, startY, endX, endY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                endX = event.getX();
                endY = event.getY();
                if (wifiBean.isStart && !TextUtils.isEmpty(wifiBean.runIp) && wifiBean.runIp.equals(getIp())) {
                    if (wifiBean != null) {
                        if (isClickOneselfChess(startX, startY, endX, endY)) {
                            oneClick(startX, startY, endX, endY);
                            return true;
                        } else {
                            if (cvb != null)
                                towClick(startX, startY, endX, endY);
                            return true;
                        }
                    }
                }
        }
        return super.onTouchEvent(event);
    }

    private boolean isClickOneselfChess(float startX, float startY, float endX, float endY) {
        ChessViewBean[][] cvb = null;
        if (TextUtils.isEmpty(wifiBean.firstIp) || getIp().equals(wifiBean.firstIp)) {
            cvb = chessViewBeen;
            Log.i("My", "isClickOneselfChess chessViewBeen");
        } else {
            cvb = chessViewBeen1;
            Log.i("My", "isClickOneselfChess chessViewBeen1");
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                float scopeStartX = cvb[i][j].x - R;
                float scopeStartY = cvb[i][j].y - R;
                float scopeEndX = cvb[i][j].x + R;
                float scopeEndY = cvb[i][j].y + R;
                if (scopeStartX < startX && scopeEndX > startX && scopeStartX < endX && scopeEndX > endX && scopeStartY < startY && scopeEndY > startY && scopeStartY < endY && scopeEndY > endY) {
                    for (WifiBean.Piece p : wifiBean.pieceList) {
                        if (p.x == i && p.y == j) {
                            if (TextUtils.isEmpty(wifiBean.firstIp) || getIp().equals(wifiBean.firstIp)) {
                                if (p.direction) {
                                    return false;
                                }
                            } else {
                                if (!p.direction) {
                                    return false;
                                }
                            }
                            return isNneselfChess(p);
                        }
                    }
                    break;
                }
            }
        }
        return false;
    }

    private void oneClick(float startX, float startY, float endX, float endY) {
        ChessViewBean[][] c = null;
        if (TextUtils.isEmpty(wifiBean.firstIp) || getIp().equals(wifiBean.firstIp)) {
            c = chessViewBeen;
            Log.i("My", "oneClick chessViewBeen");
        } else {
            c = chessViewBeen1;
            Log.i("My", "oneClick chessViewBeen1");
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                float scopeStartX = c[i][j].x - R;
                float scopeStartY = c[i][j].y - R;
                float scopeEndX = c[i][j].x + R;
                float scopeEndY = c[i][j].y + R;
                if (scopeStartX < startX && scopeEndX > startX && scopeStartX < endX && scopeEndX > endX && scopeStartY < startY && scopeEndY > startY && scopeStartY < endY && scopeEndY > endY) {
                    for (WifiBean.Piece p : wifiBean.pieceList) {
                        if (p.x == i && p.y == j) {
                            if (TextUtils.isEmpty(wifiBean.firstIp) || getIp().equals(wifiBean.firstIp)) {
                                if (p.direction) {
                                    return;
                                }
                            } else {
                                if (!p.direction) {
                                    return;
                                }
                            }
                            piece = p;
                            cvb = c[i][j];
                            WifiBean.Piece pi = wifiBean.new Piece();
                            pi.x = i;
                            pi.y = j;
                            wifiBean.runPiece = pi;
                            addHint(p);
                            wifiBean.runIp = getIp();
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    private void towClick(float startX, float startY, float endX, float endY) {
        if (cvb != null) {
            float scopeStartX = cvb.x - R;
            float scopeStartY = cvb.y - R;
            float scopeEndX = cvb.x + R;
            float scopeEndY = cvb.y + R;
            if (scopeStartX < startX && scopeEndX > startX && scopeStartX < endX && scopeEndX > endX && scopeStartY < startY && scopeEndY > startY && scopeStartY < endY && scopeEndY > endY) {
                return;
            }
        }

        ChessViewBean[][] c = null;
        if (TextUtils.isEmpty(wifiBean.firstIp) || getIp().equals(wifiBean.firstIp)) {
            c = chessViewBeen;
        } else {
            c = chessViewBeen1;
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                float scopeStartX = c[i][j].x - R;
                float scopeStartY = c[i][j].y - R;
                float scopeEndX = c[i][j].x + R;
                float scopeEndY = c[i][j].y + R;
                if (scopeStartX < startX && scopeEndX > startX && scopeStartX < endX && scopeEndX > endX && scopeStartY < startY && scopeEndY > startY && scopeStartY < endY && scopeEndY > endY) {
                    if (isNneselfChess(i, j)) {
                        return;
                    }
                    if (!isCorrect(i, j)) {
                        return;
                    }
                    cvb = null;
                    piece.x = i;
                    piece.y = j;
                    cat();
                    WifiBean.Piece pi = wifiBean.new Piece();
                    pi.x = i;
                    pi.y = j;
                    wifiBean.onePiece = wifiBean.runPiece;
                    wifiBean.towPiece = pi;
                    wifiBean.hintList.clear();
                    piece = null;
                    if (chessVhewListener != null) {
                        chessVhewListener.onSend();
                    }
                    break;
                }
            }
        }
    }

    /**
     * 是否可以走
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isCorrect(int x, int y) {
        for (int i = 0; i < wifiBean.hintList.size(); i++) {
            if (wifiBean.hintList.get(i).x == x && wifiBean.hintList.get(i).y == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * 增加提示位置标记
     */
    private void addHint(WifiBean.Piece piece) {
        wifiBean.hintList.clear();
        switch (piece.name) {
            case "车":
                car(piece);
                break;
            case "马":
                horse(piece);
                break;
            case "象":
            case "相":
                mutually(piece);
                break;
            case "士":
            case "仕":
                official(piece);
                break;
            case "炮":
                gun(piece);
                break;
            case "卒":
            case "兵":
                pawn(piece);
                break;
            case "将":
            case "帅":
                will(piece);
                break;
        }
    }

    //兵
    private void pawn(WifiBean.Piece piece) {
        int x = piece.x;
        int y = piece.y;
        WifiBean.Piece p = null;
        if (piece.direction) {
            if (x < 5) {
                if (!isNneselfChess(x + 1, y)) {
                    p = wifiBean.new Piece("", x + 1, y, true);
                    wifiBean.hintList.add(p);
                }
            } else {
                if (x < 9) {
                    if (!isNneselfChess(x + 1, y)) {
                        p = wifiBean.new Piece("", x + 1, y, true);
                        wifiBean.hintList.add(p);
                    }
                }
                if (y > 0) {
                    if (!isNneselfChess(x, y - 1)) {
                        p = wifiBean.new Piece("", x, y - 1, true);
                        wifiBean.hintList.add(p);
                    }
                }
                if (y < 8) {
                    Log.i("My", "8");
                    if (!isNneselfChess(x, y + 1)) {
                        Log.i("My", "9");
                        p = wifiBean.new Piece("", x, y + 1, true);
                        wifiBean.hintList.add(p);
                    }
                }
            }
        } else {
            if (x > 4) {
                if (!isNneselfChess(x - 1, y)) {
                    p = wifiBean.new Piece("", x - 1, y, false);
                    wifiBean.hintList.add(p);
                }
            } else {
                if (x > 0) {
                    if (!isNneselfChess(x - 1, y)) {
                        p = wifiBean.new Piece("", x - 1, y, false);
                        wifiBean.hintList.add(p);
                    }
                }
                if (y > 0) {
                    if (!isNneselfChess(x, y - 1)) {
                        p = wifiBean.new Piece("", x, y - 1, false);
                        wifiBean.hintList.add(p);
                    }
                }
                if (y < 8) {
                    if (!isNneselfChess(x, y + 1)) {
                        p = wifiBean.new Piece("", x, y + 1, false);
                        wifiBean.hintList.add(p);
                    }
                }
            }
        }
    }

    /**
     * 将
     */
    private void will(WifiBean.Piece piece) {
        int x = piece.x;
        int y = piece.y;
        WifiBean.Piece p = null;
        //一条线上是否没有棋子
        if (x <= 2) {
            for (int i = 1; i <= 9 - x; i++) {
                if (isChess(x + i, y)) {
                    WifiBean.Piece pe = getXYPiece(x + i, y);
                    if (pe != null && pe.name.equals("帅")) {
                        p = wifiBean.new Piece("", x + i, y, false);
                        wifiBean.hintList.add(p);
                    }
                    break;
                }
            }
        } else if (x >= 7) {
            for (int i = 1; i <= x; i++) {
                if (isChess(x - i, y)) {
                    WifiBean.Piece pe = getXYPiece(x + i, y);
                    if (pe != null && pe.name.equals("将")) {
                        p = wifiBean.new Piece("", x - i, y, false);
                        wifiBean.hintList.add(p);
                    }
                    break;
                }
            }
        }
        switch (x) {
            case 0:
            case 7:
                if (y == 3) {
                    if (!isNneselfChess(x + 1, y)) {
                        p = wifiBean.new Piece("", x + 1, y, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x, y + 1)) {
                        p = wifiBean.new Piece("", x, y + 1, false);
                        wifiBean.hintList.add(p);
                    }
                } else if (y == 4) {
                    if (!isNneselfChess(x, y - 1)) {
                        p = wifiBean.new Piece("", x, y - 1, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x, y + 1)) {
                        p = wifiBean.new Piece("", x, y + 1, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x + 1, y)) {
                        p = wifiBean.new Piece("", x + 1, y, false);
                        wifiBean.hintList.add(p);
                    }
                } else if (y == 5) {
                    if (!isNneselfChess(x, y - 1)) {
                        p = wifiBean.new Piece("", x, y - 1, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x + 1, y)) {
                        p = wifiBean.new Piece("", x + 1, y, false);
                        wifiBean.hintList.add(p);
                    }
                }
                break;
            case 1:
            case 8:
                if (y == 3) {
                    if (!isNneselfChess(x, y + 1)) {
                        p = wifiBean.new Piece("", x, y + 1, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x - 1, y)) {
                        p = wifiBean.new Piece("", x - 1, y, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x + 1, y)) {
                        p = wifiBean.new Piece("", x + 1, y, false);
                        wifiBean.hintList.add(p);
                    }

                } else if (y == 4) {
                    if (!isNneselfChess(x, y + 1)) {
                        p = wifiBean.new Piece("", x, y + 1, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x, y - 1)) {
                        p = wifiBean.new Piece("", x, y - 1, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x - 1, y)) {
                        p = wifiBean.new Piece("", x - 1, y, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x + 1, y)) {
                        p = wifiBean.new Piece("", x + 1, y, false);
                        wifiBean.hintList.add(p);
                    }
                } else if (y == 5) {
                    if (!isNneselfChess(x, y - 1)) {
                        p = wifiBean.new Piece("", x, y - 1, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x - 1, y)) {
                        p = wifiBean.new Piece("", x - 1, y, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x + 1, y)) {
                        p = wifiBean.new Piece("", x + 1, y, false);
                        wifiBean.hintList.add(p);
                    }
                }
                break;
            case 2:
            case 9:
                if (y == 3) {
                    if (!isNneselfChess(x, y + 1)) {
                        p = wifiBean.new Piece("", x, y + 1, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x - 1, y)) {
                        p = wifiBean.new Piece("", x - 1, y, false);
                        wifiBean.hintList.add(p);
                    }
                } else if (y == 4) {
                    if (!isNneselfChess(x, y - 1)) {
                        p = wifiBean.new Piece("", x, y - 1, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x, y + 1)) {
                        p = wifiBean.new Piece("", x, y + 1, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x - 1, y)) {
                        p = wifiBean.new Piece("", x - 1, y, false);
                        wifiBean.hintList.add(p);
                    }
                } else if (y == 5) {
                    if (!isNneselfChess(x, y - 1)) {
                        p = wifiBean.new Piece("", x, y - 1, false);
                        wifiBean.hintList.add(p);
                    }
                    if (!isNneselfChess(x - 1, y)) {
                        p = wifiBean.new Piece("", x - 1, y, false);
                        wifiBean.hintList.add(p);
                    }
                }
                break;
        }
    }

    /**
     * 仕
     */
    private void official(WifiBean.Piece piece) {
        int x = piece.x;
        int y = piece.y;
        WifiBean.Piece p = null;
        if (piece.direction) {
            if (y >= 3 && y < 5 && x >= 0 && x < 2) {
                if (!isNneselfChess(x + 1, y + 1)) {
                    p = wifiBean.new Piece("", x + 1, y + 1, false);
                    wifiBean.hintList.add(p);
                }
            }
            if (y <= 5 && y > 3 && x <= 2 && x > 0) {
                if (!isNneselfChess(x - 1, y - 1)) {
                    p = wifiBean.new Piece("", x - 1, y - 1, false);
                    wifiBean.hintList.add(p);
                }
            }
            if (y <= 5 && y > 3 && x >= 0 && x < 2) {
                if (!isNneselfChess(x + 1, y - 1)) {
                    p = wifiBean.new Piece("", x + 1, y - 1, false);
                    wifiBean.hintList.add(p);
                }
            }
            if (y >= 3 && y < 5 && x <= 2 && x > 0) {
                if (!isNneselfChess(x - 1, y + 1)) {
                    p = wifiBean.new Piece("", x - 1, y + 1, false);
                    wifiBean.hintList.add(p);
                }
            }
        } else {
            if (y >= 3 && y < 5 && x <= 9 && x > 7) {
                if (!isNneselfChess(x - 1, y + 1)) {
                    p = wifiBean.new Piece("", x - 1, y + 1, false);
                    wifiBean.hintList.add(p);
                }
            }
            if (y <= 5 && y > 3 && x >= 7 && x < 9) {
                if (!isNneselfChess(x + 1, y - 1)) {
                    p = wifiBean.new Piece("", x + 1, y - 1, false);
                    wifiBean.hintList.add(p);
                }
            }
            if (y <= 5 && y > 3 && x <= 9 && x > 7) {
                if (!isNneselfChess(x - 1, y - 1)) {
                    p = wifiBean.new Piece("", x - 1, y - 1, false);
                    wifiBean.hintList.add(p);
                }
            }
            if (y >= 3 && y < 5 && x >= 7 && x < 9) {
                if (!isNneselfChess(x + 1, y + 1)) {
                    p = wifiBean.new Piece("", x + 1, y + 1, false);
                    wifiBean.hintList.add(p);
                }
            }
        }
    }

    /**
     * 相
     */
    private void mutually(WifiBean.Piece piece) {
        int x = piece.x;
        int y = piece.y;
        WifiBean.Piece p = null;
        switch (x) {
            case 0:
            case 5:
                if (y == 2 || y == 6) {
                    if (!isChess(x + 1, y - 1)) {
                        if (!isNneselfChess(x + 2, y - 2)) {
                            p = wifiBean.new Piece("", x + 2, y - 2, false);
                            wifiBean.hintList.add(p);
                        }
                    }
                    if (!isChess(x + 1, y + 1)) {
                        if (!isNneselfChess(x + 2, y + 2)) {
                            p = wifiBean.new Piece("", x + 2, y + 2, false);
                            wifiBean.hintList.add(p);
                        }
                    }
                }
                break;
            case 2:
            case 7:
                if (y == 0) {
                    if (!isChess(x + 1, y + 1)) {
                        if (!isNneselfChess(x + 2, y + 2)) {
                            p = wifiBean.new Piece("", x + 2, y + 2, false);
                            wifiBean.hintList.add(p);
                        }
                    }
                    if (!isChess(x - 1, y + 1)) {
                        if (!isNneselfChess(x - 2, y + 2)) {
                            p = wifiBean.new Piece("", x - 2, y + 2, false);
                            wifiBean.hintList.add(p);
                        }
                    }
                } else if (y == 4) {
                    if (!isChess(x + 1, y + 1)) {
                        if (!isNneselfChess(x + 2, y + 2)) {
                            p = wifiBean.new Piece("", x + 2, y + 2, false);
                            wifiBean.hintList.add(p);
                        }
                    }
                    if (!isChess(x - 1, y + 1)) {
                        if (!isNneselfChess(x - 2, y + 2)) {
                            p = wifiBean.new Piece("", x - 2, y + 2, false);
                            wifiBean.hintList.add(p);
                        }
                    }
                    if (!isChess(x - 1, y - 1)) {
                        if (!isNneselfChess(x - 2, y - 2)) {
                            p = wifiBean.new Piece("", x - 2, y - 2, false);
                            wifiBean.hintList.add(p);
                        }
                    }
                    if (!isChess(x + 1, y - 1)) {
                        if (!isNneselfChess(x + 2, y - 2)) {
                            p = wifiBean.new Piece("", x + 2, y - 2, false);
                            wifiBean.hintList.add(p);
                        }
                    }
                } else if (y == 8) {
                    if (!isChess(x - 1, y - 1)) {
                        if (!isNneselfChess(x - 2, y - 2)) {
                            p = wifiBean.new Piece("", x - 2, y - 2, false);
                            wifiBean.hintList.add(p);
                        }
                    }
                    if (!isChess(x + 1, y - 1)) {
                        if (!isNneselfChess(x + 2, y + 2)) {
                            p = wifiBean.new Piece("", x + 2, y - 2, false);
                            wifiBean.hintList.add(p);
                        }
                    }
                }
                break;
            case 4:
            case 9:
                if (y == 2 || y == 6) {
                    if (!isChess(x - 1, y - 1)) {
                        if (!isNneselfChess(x - 2, y - 2)) {
                            p = wifiBean.new Piece("", x - 2, y - 2, false);
                            wifiBean.hintList.add(p);
                        }
                    }
                    if (!isChess(x - 1, y + 1)) {
                        if (!isNneselfChess(x - 2, y + 2)) {
                            p = wifiBean.new Piece("", x - 2, y + 2, false);
                            wifiBean.hintList.add(p);
                        }
                    }
                }
                break;
        }
    }

    /**
     * 马
     */
    private void horse(WifiBean.Piece piece) {
        int x = piece.x;
        int y = piece.y;
        WifiBean.Piece p = null;
        if (x - 2 >= 0) {
            if (y - 1 >= 0) {
                if (!isChess(x - 1, y)) {
                    if (!isNneselfChess(x - 2, y - 1)) {
                        p = wifiBean.new Piece("", x - 2, y - 1, false);
                        wifiBean.hintList.add(p);
                    }
                }
            }
            if (y + 1 <= 8) {
                if (!isChess(x - 1, y)) {
                    if (!isNneselfChess(x - 2, y + 1)) {
                        p = wifiBean.new Piece("", x - 2, y + 1, false);
                        wifiBean.hintList.add(p);
                    }
                }
            }
        }
        if (x - 1 >= 0) {
            if (y - 2 >= 0) {
                if (!isChess(x, y - 1)) {
                    if (!isNneselfChess(x - 1, y - 2)) {
                        p = wifiBean.new Piece("", x - 1, y - 2, false);
                        wifiBean.hintList.add(p);
                    }
                }
            }
            if (y + 2 <= 8) {
                if (!isChess(x, y + 1)) {
                    if (!isNneselfChess(x - 1, y + 2)) {
                        p = wifiBean.new Piece("", x - 1, y + 2, false);
                        wifiBean.hintList.add(p);
                    }
                }
            }
        }
        if (x + 1 <= 9) {
            if (y - 2 >= 0) {
                if (!isChess(x, y - 1)) {
                    if (!isNneselfChess(x + 1, y - 2)) {
                        p = wifiBean.new Piece("", x + 1, y - 2, false);
                        wifiBean.hintList.add(p);
                    }
                }
            }
            if (y + 2 <= 8) {
                if (!isChess(x, y + 1)) {
                    if (!isNneselfChess(x + 1, y + 2)) {
                        p = wifiBean.new Piece("", x + 1, y + 2, false);
                        wifiBean.hintList.add(p);
                    }
                }
            }
        }
        if (x + 2 <= 9) {
            if (y - 1 >= 0) {
                if (!isChess(x + 1, y)) {
                    if (!isNneselfChess(x + 2, y - 1)) {
                        p = wifiBean.new Piece("", x + 2, y - 1, false);
                        wifiBean.hintList.add(p);
                    }
                }
            }
            if (y + 1 <= 8) {
                if (!isChess(x + 1, y)) {
                    if (!isNneselfChess(x + 2, y + 1)) {
                        p = wifiBean.new Piece("", x + 2, y + 1, false);
                        wifiBean.hintList.add(p);
                    }
                }
            }
        }
    }

    /**
     * 车
     */
    private void car(WifiBean.Piece piece) {
        int x = piece.x;
        int y = piece.y;
        WifiBean.Piece p = null;
        int n = 9 - x;
        int h = 8 - y;
        for (int i = 1; i <= n; i++) {
            if (isChess(x + i, y)) {
                if (!isNneselfChess(x + i, y)) {
                    p = wifiBean.new Piece("", x + i, y, false);
                    wifiBean.hintList.add(p);
                }
                break;
            } else {
                p = wifiBean.new Piece("", x + i, y, false);
                wifiBean.hintList.add(p);
            }
        }
        n = x;
        for (int i = 1; i <= n; i++) {
            if (isChess(x - i, y)) {
                if (!isNneselfChess(x - i, y)) {
                    p = wifiBean.new Piece("", x - i, y, false);
                    wifiBean.hintList.add(p);
                }
                break;
            } else {
                p = wifiBean.new Piece("", x - i, y, false);
                wifiBean.hintList.add(p);
            }
        }
        for (int i = 1; i <= h; i++) {
            if (isChess(x, y + i)) {
                if (!isNneselfChess(x, y + i)) {
                    p = wifiBean.new Piece("", x, y + i, false);
                    wifiBean.hintList.add(p);
                }
                break;
            } else {
                p = wifiBean.new Piece("", x, y + i, false);
                wifiBean.hintList.add(p);
            }
        }
        h = y;
        for (int i = 1; i <= h; i++) {
            if (isChess(x, y - i)) {
                if (!isNneselfChess(x, y - i)) {
                    p = wifiBean.new Piece("", x, y - i, false);
                    wifiBean.hintList.add(p);
                }
                break;
            } else {
                p = wifiBean.new Piece("", x, y - i, false);
                wifiBean.hintList.add(p);
            }
        }
    }

    /**
     * 炮
     */
    private void gun(WifiBean.Piece piece) {
        int x = piece.x;
        int y = piece.y;
        WifiBean.Piece p = null;
        int n = 9 - x;
        int h = 8 - y;
        boolean tag = false;
        int score = 0;
        for (int i = 1; i <= n; i++) {
            if (isChess(x + i, y) || tag) {
                tag = true;
                if (isChess(x + i, y)) {
                    score++;
                    if (score > 1) {
                        if (!isNneselfChess(x + i, y)) {
                            p = wifiBean.new Piece("", x + i, y, false);
                            wifiBean.hintList.add(p);
                        }
                        break;
                    }
                }
            } else {
                p = wifiBean.new Piece("", x + i, y, false);
                wifiBean.hintList.add(p);
            }
        }
        tag = false;
        score = 0;
        n = x;
        for (int i = 1; i <= n; i++) {
            if (isChess(x - i, y) || tag) {
                tag = true;
                if (isChess(x - i, y)) {
                    score++;
                    if (score > 1) {
                        if (!isNneselfChess(x - i, y)) {
                            p = wifiBean.new Piece("", x - i, y, false);
                            wifiBean.hintList.add(p);
                        }
                        break;
                    }
                }
            } else {
                p = wifiBean.new Piece("", x - i, y, false);
                wifiBean.hintList.add(p);
            }
        }
        tag = false;
        score = 0;
        for (int i = 1; i <= h; i++) {
            if (isChess(x, y + i) || tag) {
                tag = true;
                if (isChess(x, y + i)) {
                    score++;
                    if (score > 1) {
                        if (!isNneselfChess(x, y + i)) {
                            p = wifiBean.new Piece("", x, y + i, false);
                            wifiBean.hintList.add(p);
                        }
                        break;
                    }
                }
            } else {
                p = wifiBean.new Piece("", x, y + i, false);
                wifiBean.hintList.add(p);
            }
        }
        tag = false;
        h = y;
        score = 0;
        for (int i = 1; i <= h; i++) {
            if (isChess(x, y - i) || tag) {
                tag = true;
                if (isChess(x, y - i)) {
                    score++;
                    if (score > 1) {
                        if (!isNneselfChess(x, y - i)) {
                            p = wifiBean.new Piece("", x, y - i, false);
                            wifiBean.hintList.add(p);
                        }
                        break;
                    }
                }
            } else {
                p = wifiBean.new Piece("", x, y - i, false);
                wifiBean.hintList.add(p);
            }
        }
    }

    /**
     * 判断是否存在自己的棋子
     *
     * @return
     */
    private boolean isNneselfChess(int x, int y) {
        for (int i = 0; i < wifiBean.pieceList.size(); i++) {
            WifiBean.Piece p = wifiBean.pieceList.get(i);
            if (p.x == x && p.y == y) {
                if (p.direction == piece.direction) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否存在自己的棋子
     *
     * @return
     */
    private boolean isNneselfChess(WifiBean.Piece piece) {
        for (int i = 0; i < wifiBean.pieceList.size(); i++) {
            WifiBean.Piece p = wifiBean.pieceList.get(i);
            if (p.x == piece.x && p.y == piece.y) {
                if (p.direction == piece.direction) {
                    Log.i("My", "isNneselfChess " + true);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断某个点是否存在棋子
     *
     * @return
     */
    private boolean isChess(int x, int y) {
        for (int i = 0; i < wifiBean.pieceList.size(); i++) {
            WifiBean.Piece p = wifiBean.pieceList.get(i);
            if (p.x == x && p.y == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * 得到某个点的棋子
     *
     * @param x
     * @param y
     * @return
     */
    private WifiBean.Piece getXYPiece(int x, int y) {
        for (int i = 0; i < wifiBean.pieceList.size(); i++) {
            WifiBean.Piece p = wifiBean.pieceList.get(i);
            if (p.x == x && p.y == y) {
                return p;
            }
        }
        return null;
    }

    /**
     * 吃
     */
    private void cat() {
        for (int i = 0; i < wifiBean.pieceList.size(); i++) {
            WifiBean.Piece p = wifiBean.pieceList.get(i);
            if (p.x == piece.x && p.y == piece.y) {
                if (p.direction != piece.direction) {
                    Log.i("My", "吃掉了" + p.name);
                    if (p.name.equals("帅")) {
                        Log.i("My", "吃掉了" + p.name);
                        wifiBean.victoryIp = getIp();
                    } else if (p.name.equals("将")) {
                        Log.i("My", "吃掉了" + p.name);
                        wifiBean.victoryIp = getIp();
                    }
                    wifiBean.pieceList.remove(i);
                    return;
                }
            }
        }
    }

    private ChessViewListener chessVhewListener;

    public interface ChessViewListener {
        void onSend();

    }

    public void setChessVhewListener(ChessViewListener chessVhewListener) {
        this.chessVhewListener = chessVhewListener;
    }
}
