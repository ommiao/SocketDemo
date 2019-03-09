package cn.ommiao.socketdemo.ui;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.Objects;

import cn.ommiao.socketdemo.R;
import cn.ommiao.socketdemo.adapter.FriendAdapter;
import cn.ommiao.socketdemo.databinding.FragmentFriendsBinding;
import cn.ommiao.socketdemo.socket.message.user.User;

public class FriendsFragment extends DialogFragment implements Toolbar.OnMenuItemClickListener {

    private FragmentFriendsBinding mBinding;

    private ArrayList<User> users = new ArrayList<>();
    private FriendAdapter adapter = new FriendAdapter(R.layout.item_online_friend, users);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FriendsDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.setCanceledOnTouchOutside(true);
        Window mWindow = dialog.getWindow();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        mWindow.setGravity(Gravity.TOP);
        mWindow.setWindowAnimations(R.style.FriendsDialogAnimation);
        mWindow.setLayout(screenWidth, screenHeight / 2);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        mBinding.toolbar.inflateMenu(R.menu.menu_friends_toolbar);
        mBinding.toolbar.setOnMenuItemClickListener(this);
        mBinding.rvFriends.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        adapter = new FriendAdapter(R.layout.item_online_friend, users);
        mBinding.rvFriends.setAdapter(adapter);
        mBinding.rvFriends.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initImmersionBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((ChatActivity) Objects.requireNonNull(getActivity())).getImmersionBar().keyboardEnable(true).init();
    }

    private void initImmersionBar() {
        ImmersionBar.with(this).titleBar(mBinding.toolbar).init();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if(id == R.id.toolbar_exit_f){
            dismiss();
        }
        return true;
    }

    public void setUsers(ArrayList<User> users){
        this.users.addAll(users);
        adapter.notifyDataSetChanged();
    }

    public void notifyUserAdded(User user){
        int size = users.size();
        users.add(user);
        adapter.notifyItemInserted(size);
    }

    public void notifyUserQuited(int index){
        users.remove(index);
        adapter.notifyItemRemoved(index);
    }
}
