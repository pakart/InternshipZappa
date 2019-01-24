package com.zlobrynya.internshipzappa.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.AdapterTab
import com.zlobrynya.internshipzappa.tools.MenuDish
import com.zlobrynya.internshipzappa.tools.json.ParsJson
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_scrolling.*
import android.support.design.widget.AppBarLayout
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.TextView
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MenuActivity: AppCompatActivity() {
//    private val imageResId = intArrayOf(R.mipmap.ic_hot, R.mipmap.ic_salad, R.mipmap.ic_broth,
//        R.mipmap.ic_soda, R.mipmap.ic_burger, R.mipmap.ic_beer )

    private val stringId = intArrayOf(R.string.hot, R.string.salad, R.string.broth,
        R.string.soda, R.string.burger, R.string.beer )

    //Я догадываюсь, что то это та еще херня, но я не знаю как это можно сделать по нормальному
    private var menuActivity: MenuActivity? = null

    var itemAuto: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        val title = getString(R.string.menu_toolbar)
        menuActivity = this

        //Listener для проверки состояние actionbar, при раскрытом состоянии титул, кнопка скрывается
        //при закрытом состоянии отображается титул и кнопка
        appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbarLayout.title = title
                    itemAuto?.isVisible = true
                    isShow = true
                } else if (isShow) {
                    toolbarLayout.setTitle(" ")
                    itemAuto?.isVisible = false
                    isShow = false
                }
            }
        })

        //Get json data from file
        //В отдельном потоке подключаемся к серверу и какчаем json файл, парсим его
        //и получаем обьект ManuDish, в котом содержатся данные разбитые по категориям
        ParsJson.getInstance().getMenu(this).subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<MenuDish> {
                override fun onComplete() {
                    println("Complete")
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(menuDish: MenuDish) {
                    if (menuDish.connect){
                        viewPagerMenu.adapter = AdapterTab(supportFragmentManager, menuDish, 6)
                        sliding_tabs.setupWithViewPager(viewPagerMenu)
                        for (i in 0..5){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                //sliding_tabs.getTabAt(i)?.icon = getDrawable(imageResId[i])
                                sliding_tabs.getTabAt(i)?.text = getString(stringId[i])
                            }
                        }
                    }else{
                        menuActivity!!.start()
                    }
                }

                override fun onError(e: Throwable) {
                    println(e.toString())
                }
            })
    }

    fun start(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    //Нажатие на элементы actionbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //проверка куда тыкнули и если это иконка авторизаци запускаем активити авторизации
        if (item.itemId == R.id.action_authentication) {
            //val intent = Intent(this, SearchUsersActivity::class.java)
            //startActivity(intent)
            // Toast.makeText(this, "Тут должна была открыться авторизация. \n Но её не успели сделать ;(. ", Toast.LENGTH_SHORT).show();

            val toast = Toast.makeText(this, "Тут должна была открыться авторизация. \n Но её не успели сделать ;(. ", Toast.LENGTH_SHORT)
            val v = toast.view.findViewById<View>(android.R.id.message) as TextView
            v.gravity = Gravity.CENTER

            toast.show()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // создаем меню
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        itemAuto = menu.findItem(R.id.action_authentication)
        itemAuto?.isVisible = false
        return true
    }
}