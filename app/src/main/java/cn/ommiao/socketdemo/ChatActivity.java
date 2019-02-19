package cn.ommiao.socketdemo;

import android.content.Context;
import android.content.Intent;

import com.gyf.barlibrary.ImmersionBar;

import cn.ommiao.socketdemo.databinding.ActivityChatBinding;

public class ChatActivity extends BaseActivity<ActivityChatBinding> {

    public static void start(Context context, String nicname) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra("nicname", nicname);
        context.startActivity(starter);
    }

    @Override
    protected void immersionBar() {
        ImmersionBar.with(this).titleBar(R.id.toolbar).init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initViews() {
        String nicname = getIntent().getStringExtra("nicname");
        String title = getString(R.string.chat_room);
        String subTitle = "I'm " + nicname + ".";
        mBinding.toolbar.setTitle(title);
        mBinding.toolbar.setSubtitle(subTitle);
    }
}
