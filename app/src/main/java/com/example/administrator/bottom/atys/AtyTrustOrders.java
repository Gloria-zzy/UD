package com.example.administrator.bottom.atys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.administrator.bottom.R;
import com.example.administrator.bottom.custom.MultiSwipeRefreshLayout;
import com.example.administrator.bottom.custom.OrderView;
import com.example.administrator.bottom.net.DownloadTrustOrders;
import com.example.administrator.bottom.net.Order;
import com.example.administrator.bottom.Config;

import java.util.ArrayList;

public class AtyTrustOrders extends AppCompatActivity {

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

    private ScrollView scrollView;
    private MultiSwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aty_trust_orders);
        getSupportActionBar().hide();

        //---------------------状态栏透明 begin----------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = AtyTrustOrders.this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        //---------------------状态栏透明 end----------------------------------------

        findViewById(R.id.iv_taken_orders_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.transition.switch_still, R.transition.switch_slide_out_right);
            }
        });

        bindView();
        fresh();

        SharedPreferences sharedPreferences = AtyTrustOrders.this.getSharedPreferences(Config.APP_ID, Context.MODE_PRIVATE);
        phone = sharedPreferences.getString(Config.KEY_PHONE_NUM, "");

        //---------------------解决RefreshLayout和ScrollView的冲突 begin-----------------------------------
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                swipeRefreshLayout.setEnabled(scrollView.getScrollY() == 0);
            }
        });
        //---------------------解决RefreshLayout和ScrollView的冲突 end-----------------------------------

        //---------------------------下拉刷新 begin-------------------------------
        //setColorSchemeResources()可以改变加载图标的颜色。
        swipeRefreshLayout.setColorSchemeResources(new int[]{R.color.blue, R.color.theme_blue, R.color.colorPrimary, R.color.contents_text});
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                fresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //---------------------------下拉刷新 end-------------------------------


    }

    //UI组件初始化
    private void bindView() {
        sv = (LinearLayout) findViewById(R.id.sv_taken_orders);
        scrollView = (ScrollView) findViewById(R.id.sv_trustOrders);
        swipeRefreshLayout = (MultiSwipeRefreshLayout) findViewById(R.id.refreshLayout_taken_orders);
    }

    public void fresh() {

        if (sv != null) {
            sv.removeAllViews();
        }
        new DownloadTrustOrders(phone, new DownloadTrustOrders.SuccessCallback() {

            @Override
            public void onSuccess(ArrayList<Order> orders) {

                //        订单号   order_number
                //        下单时间 order_time
                //        快递体积 size(L M S)
                //        收货地点 arrive_address
                //        收货时间 arrive_time
                //        备注     note

                for (Order o : orders) {
                    final String orderNumber = o.getOrderNumber();
                    String arriveAddress = o.getArriveAddress();
                    String arriveTime = o.getArriveTime();
                    String note = o.getNote();
                    String size = o.getSize();
                    String orderTime = o.getOrderTime();
                    String orderStatus = o.getOrderStatus();
                    String trustFriend = o.getTrust_friend();

                    final OrderView newov = new OrderView(AtyTrustOrders.this);
                    newov.setTv_size(size);
                    newov.setTv_orderNumber(orderNumber);
//                    newov.setTv_arriveAddress(arriveAddress);
                    newov.setTv_arriveTime(arriveTime);
                    newov.setTv_orderTime(orderTime);
                    if (note.equals("none")) {
                        note = "无";
                    }
//                    newov.setTv_note(note);
                    if (trustFriend.equals("none")) {
                        //自己拿
                        newov.setTv_pickPattern("自己拿");
                    } else {
                        //信任好友代拿
                        newov.setTv_pickPattern("信任好友代拿");
                    }

                    newov.getLl_modOrder_allAround().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(AtyTrustOrders.this, AtyDetails.class);
                            intent.putExtra("orderNumber", orderNumber);
                            intent.putExtra("pattern","trust orders");
                            startActivity(intent);
                            AtyTrustOrders.this.overridePendingTransition(R.transition.switch_slide_in_right, R.transition.switch_still);
                        }
                    });

//                    if (orderStatus.equals("0")) {
//                        history.addView(newov);
//                    } else {
//                        ll.addView(newov);
//                    }
                }
            }
        }, new DownloadTrustOrders.FailCallback() {

            @Override
            public void onFail() {
                Toast.makeText(AtyTrustOrders.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
            }
        });
    }

}
