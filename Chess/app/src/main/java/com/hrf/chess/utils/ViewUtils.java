/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hrf.chess.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 视图工具箱
 */
public class ViewUtils {
    /**
     * 获取一个LinearLayout
     *
     * @param context     上下文
     * @param orientation 流向
     * @param width       宽
     * @param height      高
     * @return LinearLayout
     */
    public static LinearLayout createLinearLayout(Context context, int orientation, int width, int height) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(orientation);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        return linearLayout;
    }

    /**
     * 获取一个LinearLayout
     *
     * @param context     上下文
     * @param orientation 流向
     * @param width       宽
     * @param height      高
     * @param weight      权重
     * @return LinearLayout
     */
    public static LinearLayout createLinearLayout(Context context, int orientation, int width, int height, int weight) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(orientation);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(width, height, weight));
        return linearLayout;
    }

    /**
     * 根据ListView的所有子项的高度设置其高度
     *
     * @param listView
     */
    public static void setListViewHeightByAllChildrenViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            ((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
            listView.setLayoutParams(params);
        }
    }

    /**
     * 给给定的视图设置长按提示
     *
     * @param context     上下文
     * @param view        给定的视图
     * @param hintContent 提示内容
     */
    public static void setLongClickHint(final Context context, View view, final String hintContent) {
        view.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    /**
     * 给给定的视图设置长按提示
     *
     * @param context       上下文
     * @param view          给定的视图
     * @param hintContentId 提示内容的ID
     */
    public static void setLongClickHint(final Context context, View view, final int hintContentId) {
        setLongClickHint(context, view, context.getString(hintContentId));
    }

    /**
     * 设置给定视图的高度
     *
     * @param view      给定的视图
     * @param newHeight 新的高度
     */
    public static void setViewHeight(View view, int newHeight) {
        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) view.getLayoutParams();
        layoutParams.height = newHeight;
        view.setLayoutParams(layoutParams);
    }

    /**
     * 将给定视图的高度增加一点
     *
     * @param view            给定的视图
     * @param increasedAmount 增加多少
     */
    public static void addViewHeight(View view, int increasedAmount) {
        ViewGroup.LayoutParams headerLayoutParams = (ViewGroup.LayoutParams) view.getLayoutParams();
        headerLayoutParams.height += increasedAmount;
        view.setLayoutParams(headerLayoutParams);
    }

    /**
     * 设置给定视图的宽度
     *
     * @param view     给定的视图
     * @param newWidth 新的宽度
     */
    public static void setViewWidth(View view, int newWidth) {
        ViewGroup.LayoutParams headerLayoutParams = (ViewGroup.LayoutParams) view.getLayoutParams();
        headerLayoutParams.width = newWidth;
        view.setLayoutParams(headerLayoutParams);
    }

    /**
     * 将给定视图的宽度增加一点
     *
     * @param view            给定的视图
     * @param increasedAmount 增加多少
     */
    public static void addViewWidth(View view, int increasedAmount) {
        ViewGroup.LayoutParams headerLayoutParams = (ViewGroup.LayoutParams) view.getLayoutParams();
        headerLayoutParams.width += increasedAmount;
        view.setLayoutParams(headerLayoutParams);
    }

    /**
     * 获取流布局的底部外边距
     *
     * @param linearLayout
     * @return
     */
    public static int getLinearLayoutBottomMargin(LinearLayout linearLayout) {
        return ((LinearLayout.LayoutParams) linearLayout.getLayoutParams()).bottomMargin;
    }

    /**
     * 设置流布局的底部外边距
     *
     * @param linearLayout
     * @param newBottomMargin
     */
    public static void setLinearLayoutBottomMargin(LinearLayout linearLayout, int newBottomMargin) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        lp.bottomMargin = newBottomMargin;
        linearLayout.setLayoutParams(lp);
    }

    /**
     * 获取流布局的高度
     *
     * @param linearLayout
     * @return
     */
    public static int getLinearLayoutHiehgt(LinearLayout linearLayout) {
        return ((LinearLayout.LayoutParams) linearLayout.getLayoutParams()).height;
    }

    /**
     * 设置输入框的光标到末尾
     *
     * @param editText
     */
    public static final void setEditTextSelectionToEnd(EditText editText) {
        Editable editable = editText.getEditableText();
        Selection.setSelection((Spannable) editable, editable.toString().length());
    }

    /**
     * 执行测量，执行完成之后只需调用View的getMeasuredXXX()方法即可获取测量结果
     *
     * @param view
     * @return
     */
    public static final View measure(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec, childHeightSpec);
        return view;
    }

    /**
     * 获取给定视图的测量高度
     *
     * @param view
     * @return
     */
    public static final int getMeasuredHeight(View view) {
        return measure(view).getMeasuredHeight();
    }

    /**
     * 获取给定视图的测量宽度
     *
     * @param view
     * @return
     */
    public static final int getMeasuredWidth(View view) {
        return measure(view).getMeasuredWidth();
    }

    /**
     * 获取视图1相对于视图2的位置，注意在屏幕上看起来视图1应该被视图2包含，但是视图1和视图并不一定是绝对的父子关系也可以是兄弟关系，只是一个大一个小而已
     *
     * @param view1
     * @param view2
     * @return
     */
    public static final Rect getRelativeRect(View view1, View view2) {
        Rect childViewGlobalRect = new Rect();
        Rect parentViewGlobalRect = new Rect();
        view1.getGlobalVisibleRect(childViewGlobalRect);
        view2.getGlobalVisibleRect(parentViewGlobalRect);
        return new Rect(childViewGlobalRect.left - parentViewGlobalRect.left, childViewGlobalRect.top - parentViewGlobalRect.top, childViewGlobalRect.right - parentViewGlobalRect.left, childViewGlobalRect.bottom - parentViewGlobalRect.top);
    }

    /**
     * 删除监听器
     *
     * @param viewTreeObserver
     * @param onGlobalLayoutListener
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static final void removeOnGlobalLayoutListener(ViewTreeObserver viewTreeObserver, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            viewTreeObserver.removeGlobalOnLayoutListener(onGlobalLayoutListener);
        } else {
            viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }

    /**
     * 缩放视图
     *
     * @param view
     * @param scaleX
     * @param scaleY
     * @param originalSize
     */
    public static void zoomView(View view, float scaleX, float scaleY, Point originalSize) {
        int width = (int) (originalSize.x * scaleX);
        int height = (int) (originalSize.y * scaleY);
        ViewGroup.LayoutParams viewGroupParams = view.getLayoutParams();
        if (viewGroupParams != null) {
            viewGroupParams.width = width;
            viewGroupParams.height = height;
        } else {
            viewGroupParams = new ViewGroup.LayoutParams(width, height);
        }
        view.setLayoutParams(viewGroupParams);
    }

    /**
     * 缩放视图
     *
     * @param view
     * @param scaleX
     * @param scaleY
     */
    public static void zoomView(View view, float scaleX, float scaleY) {
        zoomView(view, scaleX, scaleY, new Point(view.getWidth(), view.getHeight()));
    }

    /**
     * 缩放视图
     *
     * @param view
     * @param scale        比例
     * @param originalSize
     */
    public static void zoomView(View view, float scale, Point originalSize) {
        zoomView(view, scale, scale, originalSize);
    }

    /**
     * 缩放视图
     *
     * @param view
     */
    public static void zoomView(View view, float scale) {
        zoomView(view, scale, scale, new Point(view.getWidth(), view.getHeight()));
    }

    public static void getFocus(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * 获取TextView的内容
     *
     * @param textView
     * @return
     */
    public static String getText(TextView textView) {
        String text = "";
        if (textView != null) {
            text = textView.getText().toString().trim();
        }
        return text;
    }

    /**
     * 设置ImageView的内容
     */
    public static void setImageResource(ImageView iv, int resId) {
        if (iv == null) {
            return;
        }

        iv.setImageResource(resId);
    }

    /**
     * @param iv      ImageView
     * @param resId_s 选中的id
     * @param resId_n 不选中的id
     * @param s_or_n  resId_s => true,resId_n => false
     */
    public static void setImageResource(ImageView iv, int resId_s, int resId_n, boolean s_or_n) {
        int resId = resId_n;
        if (s_or_n) {
            resId = resId_s;
        }
        setImageResource(iv, resId);
    }

    /**
     * 设置View的背景
     */
    public static void setBackgroundResource(View v, int resId) {
        if (v == null) {
            return;
        }

        v.setBackgroundResource(resId);
    }

    /**
     * @param v
     * @param color 设置view的背景颜色
     */
    public static void setBackgroundColor(View v, int color) {
        if (v == null) {
            return;
        }
        v.setBackgroundColor(color);
    }


    /**
     * 设置TextView颜色和字体大小
     */
    public static void setTextViewColorSize(TextView textView, int color, int size) {
        if (textView == null) {
            return;
        }
        textView.setTextColor(color);
        textView.setTextSize(size);
    }

    /**
     * 设置TextView内容
     *
     * @param textView
     * @param content
     */
    public static void setText(TextView textView, String content) {
        setText(textView, content, "");
    }

    public static void setText(TextView textView, String content, TextView.BufferType type) {
        setText(textView, content, "", type);
    }

    /**
     * 设置TextView内容，带格式
     *
     * @param textView
     * @param content
     * @param values
     */
    public static void setTextFormat(TextView textView, String content, Object... values) {
        setText(textView, formatString(content, values), "");
    }


    public static void setHtmlText(TextView textView, String content) {
        if (textView == null) {
            return;
        }
        if (!TextUtils.isEmpty(content)) {
            textView.setText(Html.fromHtml(content));
        } else {
            textView.setText("");
        }
    }


    /**
     * 设置TextView内容
     *
     * @param textView
     * @param content
     * @param defaultContent
     */
    public static void setText(TextView textView, String content, String defaultContent) {
        if (textView == null) {
            return;
        }
        if (!TextUtils.isEmpty(content)&&!content.equals("null")) {
            textView.setText(content);
        } else {
            textView.setText(defaultContent);
        }
    }

    public static void setText(TextView textView, String content, String defaultContent, TextView.BufferType type) {
        if (textView == null) {
            return;
        }
        if (!TextUtils.isEmpty(content)) {
            textView.setText(content, type);
        } else {
            textView.setText(defaultContent, type);
        }
    }

    /**
     * 设置TextView内容
     *
     * @param textView
     * @param res
     */
    public static void setText(TextView textView, int res) {
        if (textView == null) {
            return;
        }
        try {
            textView.setText(res + "");
        } catch (Exception e) {
            e.printStackTrace();
            textView.setText("");
        }
    }

    public static void setTextColor(Context context, TextView textView, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextColor(context.getResources().getColor(color, null));
        } else {
            textView.setTextColor(context.getResources().getColor(color));
        }
    }

    public static void setTextDrawable(Context context, TextView textView, int left, int top, int right, int bottom) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setCompoundDrawables(getDrawableByRes(context, left), getDrawableByRes(context, top), getDrawableByRes(context, right), getDrawableByRes(context, bottom));
        } else {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(left, top, right, bottom);
        }
    }

    public static Drawable getDrawableByRes(Context context, int res) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(res);
        } else {
            return context.getResources().getDrawable(res, null);
        }
    }


    /**
     * 格式化字符串
     *
     * @param content
     * @param values
     * @return
     */
    public static String formatString(String content, Object... values) {
        return String.format(content, values);
    }

    /**
     * 清除 TV 内容
     *
     * @param textView
     */
    public static void clearText(TextView textView) {
        if (textView != null) {
            textView.setText("");
        }
    }

    /**
     * Description: 显示
     */
    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Description: 隐藏
     */
    public static void hide(View view) {
        view.setVisibility(View.GONE);
    }

    /**
     * Description: 隐藏
     * <p/>
     * View.INVISIBLE
     */
    public static void visible(View view) {
        view.setVisibility(View.INVISIBLE);
    }

}