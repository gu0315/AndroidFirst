package com.example.myapplication;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ChatAdapter chatAdapter;

    private RecyclerView chatRecyclerView;
    private TextView tabChat;
    private TextView tabContact;
    private TextView tabFind;
    private TextView tabMine;


    private static class  ChatItemVO {
        public final String avatarUrl;
        public final String chatName;
        public final String lestMessage;
        public final long lestMessageTime;
        public final boolean isMute;

        private ChatItemVO(java.lang.String avatarUrl, java.lang.String chatName, java.lang.String lestMessage, long lestMessageTime, boolean isMute) {
            this.avatarUrl = avatarUrl;
            this.chatName = chatName;
            this.lestMessage = lestMessage;
            this.lestMessageTime = lestMessageTime;
            this.isMute = isMute;
        }
    }

    private static class ChatViewHolder extends RecyclerView.ViewHolder {
        private ImageView  iv_avatar;
        private TextView tv_name;
        private TextView  tv_message;
        private TextView  tv_date;
        private ImageView  iv_icon;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_date = itemView.findViewById(R.id.tv_date);
            iv_icon = itemView.findViewById(R.id.iv_icon);
        }

        public void updateDate(ChatItemVO chatItemVO) {
            //Glide.with(iv_avatar.getContext()).load(chatItemVO.avatarUrl).into(iv_avatar);
            ImageLoader.load(iv_avatar, chatItemVO.avatarUrl);
            tv_name.setText(chatItemVO.chatName);
            tv_message.setText(chatItemVO.lestMessage);
            tv_date.setText(String.valueOf(chatItemVO.lestMessageTime));
            iv_icon.setVisibility(chatItemVO.isMute ? View.VISIBLE :View.INVISIBLE);
        }
     }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
        private final List<ChatItemVO> list = new ArrayList<>();

        public ChatAdapter()  {
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater  = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_chat_list_item, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatItemVO  chatItemVO = list.get(position);
            holder.updateDate(chatItemVO);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void  addAll(List<ChatItemVO> list) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        public void clear() {
           this.list.clear();
           notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        tabChat = findViewById(R.id.tabChat);
        tabContact = findViewById(R.id.tabContact);
        tabFind = findViewById(R.id.tabFind);
        tabMine = findViewById(R.id.tabMine);

        Drawable icon = getResources().getDrawable(R.drawable.tabbar_me2x);
        icon.setBounds(0, 0, 80, 80);
        tabMine.setCompoundDrawables(null, icon, null, null);

        icon = getResources().getDrawable(R.drawable.tabbar_mainframe_h2x);
        icon.setBounds(0, 0, 80, 80);
        tabChat.setCompoundDrawables(null, icon, null, null);

        icon = getResources().getDrawable(R.drawable.tabbar_contacts2x);
        icon.setBounds(0, 0, 80, 80);
        tabContact.setCompoundDrawables(null, icon, null, null);

        icon = getResources().getDrawable(R.drawable.tabbar_discover2x);
        icon.setBounds(0, 0, 80, 80);
        tabFind.setCompoundDrawables(null, icon, null, null);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        chatRecyclerView.setLayoutManager(llm);

        List<ChatItemVO> list = new ArrayList<>();
        chatAdapter = new ChatAdapter();
        chatRecyclerView.setAdapter(chatAdapter);
        startLoadChatList();
        /*Intent intent = FirstActivity.newIntent(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        },3 * 1000);*/
    }

    private  void startLoadChatList() {
        new Handler().postDelayed(() -> {
            List<ChatItemVO> list = new ArrayList<>();
            list.add(new ChatItemVO("https://img.souche.com/bolt/GHJY-4i8QdQwpCI4XBiKl/%E4%B8%8B%E8%BD%BD.jpeg", "老大", "你好",1000000000, true));
            list.add(new ChatItemVO("https://img.souche.com/bolt/GHJY-4i8QdQwpCI4XBiKl/%E4%B8%8B%E8%BD%BD.jpeg", "老二", "你好",1000000000, false));
            list.add(new ChatItemVO("https://img.souche.com/bolt/GHJY-4i8QdQwpCI4XBiKl/%E4%B8%8B%E8%BD%BD.jpeg", "老大", "你好",1000000000, true));
            list.add(new ChatItemVO("https://img.souche.com/bolt/GHJY-4i8QdQwpCI4XBiKl/%E4%B8%8B%E8%BD%BD.jpeg", "老二", "你好",1000000000, false));
            chatAdapter.addAll(list);
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}