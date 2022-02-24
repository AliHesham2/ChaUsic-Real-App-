package com.example.chausic.view.dashboard.config

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chausic.view.util.FragmentsInstance

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter
    (fragmentManager,lifecycle){

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return    when (position) {
            0  -> {
                FragmentsInstance.chatsFragment
            }
            1 -> {
                FragmentsInstance.friendsFragment
            }
            2->{
                FragmentsInstance.profileFragment
            }
            else -> Fragment()
        }
    }




}