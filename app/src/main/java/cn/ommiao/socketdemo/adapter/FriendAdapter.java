package cn.ommiao.socketdemo.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.HashMap;
import java.util.List;

import cn.ommiao.socketdemo.R;
import cn.ommiao.socketdemo.socket.message.user.User;

public class FriendAdapter extends BaseQuickAdapter<User, BaseViewHolder> {

    public FriendAdapter(int layoutResId, @Nullable List<User> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, User item) {
        String code = item.getUserCode();
        String chara = code.substring(code.length() - 1);
        Integer id = navMap.get(chara);
        assert id != null;
        helper.setImageResource(R.id.iv_nav, id);
        helper.setText(R.id.tv_nickname, item.getNickname());
    }

    private static final HashMap<String, Integer> navMap = new HashMap<String, Integer>(){
        {
            put("0", R.drawable.nav_0);
            put("1", R.drawable.nav_1);
            put("2", R.drawable.nav_2);
            put("3", R.drawable.nav_3);
            put("4", R.drawable.nav_4);
            put("5", R.drawable.nav_5);
            put("6", R.drawable.nav_6);
            put("7", R.drawable.nav_7);
            put("8", R.drawable.nav_8);
            put("9", R.drawable.nav_9);
            put("a", R.drawable.nav_a);
            put("b", R.drawable.nav_b);
            put("c", R.drawable.nav_c);
            put("d", R.drawable.nav_d);
            put("e", R.drawable.nav_e);
            put("f", R.drawable.nav_f);
        }
    };
}
