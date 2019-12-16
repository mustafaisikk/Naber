package com.example.naber;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAdapter extends FragmentPagerAdapter {


    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            case 3:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;

            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Mesajlar";
            case 1:
                return "Gruplar";
            case 2:
                return "Arkadaşlar";
            case 3:
                return "İstekler";
            default:
                return null;
        }
    }
}
