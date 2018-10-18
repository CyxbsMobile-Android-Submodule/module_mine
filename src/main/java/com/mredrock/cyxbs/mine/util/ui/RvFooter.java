package com.mredrock.cyxbs.mine.util.ui;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by zia on 2018/8/17.
 */
public class RvFooter extends LinearLayout {

    private TextView textView;
    private int textSize;
    private State state;

    public RvFooter(Context context) {
        this(context, 13);
    }

    public RvFooter(Context context, int textSize) {
        super(context);
        this.textSize = textSize;
        init();
    }

    private void init() {
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        textView = new TextView(getContext());
        textView.setTextSize(textSize);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 40, 0, 40);
        addView(textView);
        showLoading();
    }

    public void showLoading() {
        textView.setText("加载中...");
        state = State.LOADING;
    }

    public void showLoadError() {
        textView.setText("加载错误(＞﹏＜)");
        state = State.ERROR;
    }

    public void showNoMore() {
        textView.setText("没有更多了~");
        state = State.NOMORE;
    }

    public void showSuccess() {
        textView.setText("加载成功(‾◡◝)");
        state = State.SUCCESS;
    }

    public void showNothing(){
        textView.setText("");
        state = State.NOTHING;
    }

    public State getState() {
        return state;
    }

    public enum State{
        SUCCESS,LOADING,ERROR,NOMORE,NOTHING
    }
}
