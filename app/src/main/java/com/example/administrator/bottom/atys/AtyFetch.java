package com.example.administrator.bottom.atys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.aliyuncs.exceptions.ClientException;
import com.example.administrator.bottom.Config;
import com.example.administrator.bottom.R;
import com.example.administrator.bottom.alipush.PushMessage;
import com.example.administrator.bottom.net.UploadOrder;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

import static com.example.administrator.bottom.Config.APP_ID;

public class AtyFetch extends AppCompatActivity {

    private Spinner point_spinner;
    private Spinner loc_spinner;
    private Spinner time_spinner;
    private EditText note_edittext;
    private EditText takenum_edittext;
    private RadioGroup radioGroup;
    private LinearLayout linearLayout;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private String point;
    private String loc;
    private String time;
    private String note;
    private String takenum;

    //UI组件初始化
    private void bindView() {
        point_spinner = (Spinner) findViewById(R.id.point_spinner);
        loc_spinner = (Spinner) findViewById(R.id.loc_spinner);
        time_spinner = (Spinner) findViewById(R.id.time_spinner);
        note_edittext = (EditText) findViewById(R.id.fetch_note);
        takenum_edittext = (EditText) findViewById(R.id.tv_order_takenum);
        radioGroup = (RadioGroup) findViewById(R.id.rg_aty_fetch);
        linearLayout = (LinearLayout) findViewById(R.id.ll_aty_fetch_temp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aty_fetch2);
        getSupportActionBar().hide();

        bindView();
        findViewById(R.id.Fetch_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //---------------------状态栏透明 begin----------------------------------------
        Window window = AtyFetch.this.getWindow();
        //设置透明状态栏,这样才能让 ContentView 向上
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(Color.TRANSPARENT);

        ViewGroup mContentView = (ViewGroup) AtyFetch.this.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }
        //---------------------状态栏透明 end----------------------------------------

        //----------------------------快递点 begin---------------------------------
        //数据
        data_list = new ArrayList<String>();
        data_list.add("北门盘锦花园新生活");
        data_list.add("北门盘锦花园内右拐第七家");
        data_list.add("小东门外菜鸟驿站");
        data_list.add("中苑老食堂菜鸟驿站");

        //适配器 android.R.layout.simple_spinner_item
        arr_adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, data_list);
        //设置样式 android.R.layout.simple_spinner_dropdown_item
        arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        //加载适配器
        point_spinner.setAdapter(arr_adapter);
        point_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                point = (String) point_spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //----------------------------快递点 end---------------------------------

        //----------------------------收货地点 begin---------------------------------
        //数据
        data_list = new ArrayList<String>();
        SharedPreferences sharedPreferences = getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
        String abr = sharedPreferences.getString(Config.ADDRESS, "");
        data_list.add(abr);
        data_list.add("明德楼");
        data_list.add("文德楼");
        data_list.add("信息中心");

        //适配器
        arr_adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        //加载适配器
        loc_spinner.setAdapter(arr_adapter);
        loc_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loc = (String) loc_spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //----------------------------收货地点 end---------------------------------

        //----------------------------收货时间 begin---------------------------------
        //数据
        data_list = new ArrayList<String>();
        data_list.add("18：30~20：30");
        data_list.add("20：30~22：00");

        //适配器
        arr_adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        //加载适配器
        time_spinner.setAdapter(arr_adapter);
        time_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                time = (String) time_spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //----------------------------收货时间 end---------------------------------





        linearLayout.setVisibility(View.GONE);

        findViewById(R.id.fetch_summit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = sDateFormat.format(new java.util.Date());

                // 获得phoneNum
                note = note_edittext.getText().toString();
                if (note.equals("")) {
                    note = "none";
                }
                takenum = takenum_edittext.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
                String phone = sharedPreferences.getString(Config.KEY_PHONE_NUM, "");

                if (takenum.equals("") || takenum == null) {
                    Toast.makeText(AtyFetch.this, "取货号不能为空！", Toast.LENGTH_LONG).show();
                } else {
                    new UploadOrder(phone, point, takenum, loc, note, date, new UploadOrder.SuccessCallback() {

                        @Override
                        public void onSuccess() {

                            //-------------------下单成功 给自己发一条推送-----------------------
                            SharedPreferences sharedPreferences = AtyFetch.this.getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
                            final String deviceId = sharedPreferences.getString(Config.DEVICEID, "");

                            Runnable networkTask = new Runnable() {

                                @Override
                                public void run() {
                                    // TODO
                                    // 在这里进行 http request.网络请求相关操作
                                    PushMessage pushMessage = new PushMessage();
                                    try {
                                        pushMessage.PushToSelf(deviceId, "下单成功！", "UDers正在努力派送中…");
                                    } catch (ClientException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            Thread thread = new Thread(networkTask);
                            thread.start();
                            //---------------------------推送结束-----------------------------

                            Toast.makeText(AtyFetch.this, "提交成功！", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(AtyFetch.this, AtyMainFrame.class);
                            i.putExtra("page", "order");
                            startActivity(i);
                            finish();

                        }
                    }, new UploadOrder.FailCallback() {

                        @Override
                        public void onFail() {
                            Toast.makeText(AtyFetch.this, R.string.fail_to_commit, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_aty_fetch_old:
                        linearLayout.setVisibility(View.GONE);
                        break;
                    case R.id.rb_aty_fetch_temp:
                        linearLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });


    }
}
