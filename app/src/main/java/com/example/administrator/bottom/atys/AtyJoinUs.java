package com.example.administrator.bottom.atys;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.administrator.bottom.R;

public class AtyJoinUs extends AppCompatActivity {

    private CheckBox agree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aty_join_us);

        getSupportActionBar().hide();

        //---------------------状态栏透明 begin----------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = AtyJoinUs.this.getWindow();
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

        agree = (CheckBox) findViewById(R.id.cb_address_agree);
        agree.setChecked(true);

        findViewById(R.id.iv_joinUs_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (agree.isChecked()) {

                    Toast.makeText(AtyJoinUs.this, "申请成功！", Toast.LENGTH_LONG).show();
                    finish();
                    overridePendingTransition(R.transition.switch_still, R.transition.switch_slide_out_right);

                } else {
                    Toast.makeText(AtyJoinUs.this, R.string.check_agreement, Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
