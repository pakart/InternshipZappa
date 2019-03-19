package com.zlobrynya.internshipzappa.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.booking.BookingEnd
import kotlinx.android.synthetic.main.activity_menu.*
import com.zlobrynya.internshipzappa.fragment.KontaktiFragment
import com.zlobrynya.internshipzappa.fragment.menu.MenuFragment
import com.zlobrynya.internshipzappa.fragment.BookingFragment
import android.widget.Toast




class MenuActivity: AppCompatActivity() {

    private val menuFragment: Fragment = MenuFragment()

    private val kontaktiFragment = KontaktiFragment()

    private val bookingFragment: Fragment = BookingFragment()


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment? = null
        when (item.itemId) {

            // Профиль
            R.id.navigation_contacts -> {
                selectedFragment = kontaktiFragment
            }

            // Меню
            R.id.navigation_menu -> {
                selectedFragment = menuFragment
            }

            // Бронирование
            R.id.navigation_booking -> {
                selectedFragment = bookingFragment
            }
        }
        // Загружаем фрагмент
        if (selectedFragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
        }
        true
    }

    lateinit var toolbar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        toolbar = supportActionBar!!
        toolbar.hide()
        //supportActionBar!!.setTitle(R.string.menu_toolbar)
        //supportActionBar!!.elevation = 0.0F

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (savedInstanceState == null) {
            navigation.selectedItemId = R.id.navigation_menu
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}

