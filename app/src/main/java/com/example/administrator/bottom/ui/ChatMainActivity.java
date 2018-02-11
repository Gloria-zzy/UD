package com.example.administrator.bottom.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.bottom.R;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.ui.EaseContactListFragment.EaseContactListItemClickListener;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment.EaseConversationListItemClickListener;

import java.util.HashMap;
import java.util.Map;

public class ChatMainActivity extends FragmentActivity {
    private TextView unreadLabel;
    private Button[] mTabs;
    private EaseConversationListFragment conversationListFragment;
    private EaseContactListFragment contactListFragment;
    private SettingsFragment settingFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;

    protected InputMethodManager inputMethodManager;

    //    protected void onCreate(Bundle arg0) {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        //http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
        // should be in launcher activity, but all app use this can avoid the problem
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        /*
        *  以上用于避免extends EaseBaseActivity，已经将EaseBaseActivity中的有价值内容复制过来了
        * */

        setContentView(R.layout.aty_chat_main);

        // 控件绑定
        unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
        // 用数组来存放三个按钮
        mTabs = new Button[3];
        // 三个按钮用来跳转到各自的fragment
        // 会话
        mTabs[0] = (Button) findViewById(R.id.btn_conversation);
        // 联系人列表
        mTabs[1] = (Button) findViewById(R.id.btn_address_list);
        // 设置
        mTabs[2] = (Button) findViewById(R.id.btn_setting);

        // set first tab as selected
        mTabs[0].setSelected(true);

        conversationListFragment = new EaseConversationListFragment();
        contactListFragment = new EaseContactListFragment();
        settingFragment = new SettingsFragment();
        contactListFragment.setContactsMap(getContacts());

        // 设置会话列表里的会话点击事件，就是点击会话之后的处理，即弹出会话窗口，会话窗口类是ChatActivity
        conversationListFragment.setConversationListItemClickListener(new EaseConversationListItemClickListener() {

            @Override
            public void onListItemClicked(EMConversation conversation) {
                startActivity(new Intent(ChatMainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()));
            }
        });

        // 设置联系人列表里的点击事件，点击联系人之后弹出会话窗口
        contactListFragment.setContactListItemClickListener(new EaseContactListItemClickListener() {

            @Override
            public void onListItemClicked(EaseUser user) {
                startActivity(new Intent(ChatMainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
            }
        });

        // 将三个fragment存入数组
        fragments = new Fragment[]{conversationListFragment, contactListFragment, settingFragment};
        // add and show first fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment)
                .add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(conversationListFragment)
                .commit();
    }

    // 可移植到fragment

    /**
     * onTabClicked
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_conversation:
                index = 0;
                break;
            case R.id.btn_address_list:
                index = 1;
                break;
            case R.id.btn_setting:
                index = 2;
                break;
        }
        // 如果选中的页面和当前页面不一致
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            // 将当前页面隐藏
            trx.hide(fragments[currentTabIndex]);
            // 如果选中的页面还没有被添加到transaction里面，那么就进行添加
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            // 呈现选中的页面
            trx.show(fragments[index]).commit();
        }
        // 将当前页面（跳转之前的页面）的选中状态设置为false
        mTabs[currentTabIndex].setSelected(false);
        // set current tab as selected.
        // 将要跳转到的页面的选中状态设置为true
        mTabs[index].setSelected(true);
        // 将当前页面编号设置为要跳转页面的编号
        currentTabIndex = index;
    }

    /**
     * prepared users, password is "123456"
     * you can use these user to test
     *
     * @return
     */
    private Map<String, EaseUser> getContacts() {
        Map<String, EaseUser> contacts = new HashMap<String, EaseUser>();
        for (int i = 1; i <= 10; i++) {
            EaseUser user = new EaseUser("easeuitest" + i);
            contacts.put("easeuitest" + i, user);
        }
        return contacts;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // cancel the notification
        EaseUI.getInstance().getNotifier().reset();
    }
}
