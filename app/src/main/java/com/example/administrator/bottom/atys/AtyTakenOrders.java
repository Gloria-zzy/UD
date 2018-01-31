package com.example.administrator.bottom.atys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.bottom.Config;
import com.example.administrator.bottom.R;
import com.example.administrator.bottom.custom.OrderView;
import com.example.administrator.bottom.custom.QQRefreshHeader;
import com.example.administrator.bottom.custom.RefreshLayout;
import com.example.administrator.bottom.net.DownloadTakenOrders;
import com.example.administrator.bottom.net.Order;
import com.example.administrator.bottom.net.UpdateOrder;

import java.util.ArrayList;

import static com.example.administrator.bottom.Config.APP_ID;

public class AtyTakenOrders extends AppCompatActivity {

    private String phone;
    private LinearLayout sv;
    private String number;
    private String point;
    private String takenum;
    private String loc;
    private String note;
    private String status;
    private String date;
    private String order_num;
    private String taker;
    private String publisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aty_taken_orders);
        getSupportActionBar().hide();

        //---------------------状态栏透明 begin----------------------------------------
        Window window = AtyTakenOrders.this.getWindow();
        //设置透明状态栏,这样才能让 ContentView 向上
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(Color.TRANSPARENT);

        ViewGroup mContentView = (ViewGroup) AtyTakenOrders.this.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }
        //---------------------状态栏透明 end----------------------------------------

        findViewById(R.id.iv_taken_orders_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.transition.switch_still, R.transition.switch_slide_out_right);
            }
        });

        final RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout_taken_orders);
        if (refreshLayout != null) {
            // 刷新状态的回调
            refreshLayout.setRefreshListener(new RefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // 延迟3秒后刷新成功
                    refreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.refreshComplete();
                            //-----------------BEGIN-----------------


                            sv = (LinearLayout) findViewById(R.id.sv_taken_orders);
                            SharedPreferences sharedPreferences = AtyTakenOrders.this.getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
                            phone = sharedPreferences.getString(Config.KEY_PHONE_NUM, "");
                            if (sv != null) {
                                sv.removeAllViews();
                            }
                            new DownloadTakenOrders(phone, new DownloadTakenOrders.SuccessCallback() {

                                @Override
                                public void onSuccess(ArrayList<Order> orders) {

                                    for (Order o : orders) {
                                        number = o.getOrderNum();
                                        point = o.getPoint();
                                        takenum = o.getTakenum();
                                        loc = o.getLocation();
                                        note = o.getNote();
                                        status = o.getStatus();
                                        date = o.getDate();
                                        order_num = o.getOrderNum();
                                        taker = o.getTaker();
                                        publisher = o.getPhone();
                                        final OrderView newov = new OrderView(AtyTakenOrders.this);

                                        newov.setOrder_intro("小件快递");
                                        newov.setOrder_num(number);
                                        newov.setOrder_point(point);
                                        newov.setOrder_takenum(takenum);
                                        newov.setOrder_loc(loc);
                                        newov.setNum(number);
                                        newov.setTime(date);
                                        if (note.equals("none")) {
                                            note = "无";
                                        }
                                        newov.setOrder_note(note);
                                        newov.getOrder_delete().setVisibility(View.GONE);
                                        newov.getOrder_change().setVisibility(View.GONE);
                                        newov.getOrder_code().setVisibility(View.GONE);
                                        newov.getOrder_cancel().setVisibility(View.GONE);
                                        if (status.equals("0")) {
                                            newov.setOrder_status("已结束");
                                            sv.addView(newov);
                                        } else if (status.equals("1")) {
                                            newov.setOrder_status("正在送货");
                                            sv.addView(newov);
                                        } else if (status.equals("2")) {
                                            newov.setOrder_status("待接单");
                                            sv.addView(newov);
                                        } else if (status.equals("3")) {
                                            newov.setOrder_status("订单异常");
                                            sv.addView(newov);
                                        }
                                        newov.getDischarge_order().setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                new UpdateOrder(publisher, taker, order_num, point, takenum, loc, note, date, "2", new UpdateOrder.SuccessCallback() {

                                                    @Override
                                                    public void onSuccess() {


                                                        Toast.makeText(AtyTakenOrders.this, "已放弃订单", Toast.LENGTH_LONG).show();
                                                        Intent i = new Intent(AtyTakenOrders.this, AtyTakenOrders.class);
                                                        startActivity(i);
                                                        finish();

//                                    AtyTakenOrders.this.overridePendingTransition(R.transition.switch_slide_in_right, R.transition.switch_still);

                                                    }
                                                }, new UpdateOrder.FailCallback() {

                                                    @Override
                                                    public void onFail() {
                                                        Toast.makeText(AtyTakenOrders.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            }, new DownloadTakenOrders.FailCallback() {

                                @Override
                                public void onFail() {
                                    Toast.makeText(AtyTakenOrders.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
                                }
                            });

                            //-----------------END-----------------
                        }
                    }, Config.DELAYMILLIS);
                }
            });
        }
        QQRefreshHeader header = new QQRefreshHeader(this);
        refreshLayout.setRefreshHeader(header);
        refreshLayout.autoRefresh();
    }
}
