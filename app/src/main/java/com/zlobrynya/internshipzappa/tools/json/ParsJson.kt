package com.zlobrynya.internshipzappa.tools.json

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.zlobrynya.internshipzappa.tools.MenuDish
import com.zlobrynya.internshipzappa.tools.parcelable.Dish
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.io.*


/*
 * ����� ��� ���������� ������ �� ����
 * �������� ������ � �������, ���� ����� � ����, ���� ����� �������� ������ � �����.
*/

class ParsJson {
    private val URLSERVER = "https://118dbdc8-bed8-4697-9c8e-452c67b8eeec.mock.pstmn.io/get_menu/"
    private val NAME_FILE = "/cache_menu.json"
    private lateinit var context: Context

    fun getMenu(context: Context): Observable<MenuDish> {
        this.context = context
        //� ������� RxJava2 �������� � ��������, ��� ������ ��������
        return Observable.create(object : ObservableOnSubscribe<MenuDish> {
            override fun subscribe(emitter: ObservableEmitter<MenuDish>) {
                if (checkCacheMenu()){
                    parsJson(downloadFile(), emitter)
                }else{
                    downloadServer(URLSERVER, emitter)
                }
            }
        })
    }

    private fun downloadServer(url: String, emitter: ObservableEmitter<MenuDish>){
        var strJson: String
         doAsync {
             val jsonUrl = URL(url)
             val conn = jsonUrl.openConnection() as HttpURLConnection
             conn.requestMethod = "GET"
             val input = BufferedInputStream(conn.inputStream)
             //������������ ����� � ������
             strJson = convertStreamToString(input)
             writeCacheMenu(strJson)
             parsJson(strJson, emitter)
        }
     }

    private fun checkCacheMenu(): Boolean{
        Log.i("CheckCacheMenu", File(context.cacheDir.toString() + NAME_FILE).exists().toString())
        return File(context.cacheDir.toString() + NAME_FILE).exists()
    }


    private fun writeCacheMenu(strJson: String){
        val cacheFile = File(context.cacheDir.toString() + NAME_FILE)
        if (!cacheFile.exists()){
            val inputStream = ByteArrayInputStream(strJson.toByteArray(Charset.forName("windows-1251")))
            val outputStream = FileOutputStream(cacheFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    private fun convertStreamToString(input: InputStream): String {
        val reader = BufferedReader(InputStreamReader(input))
        val sb = StringBuilder()

        var line: String?
        try {
            do{
                line = reader.readLine()
                if (line == null)
                    break
                sb.append(line).append('\n')
            }while (true)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return sb.toString()
    }

    private fun downloadFile(): String{
        val inputStream = File(context.cacheDir.toString() + NAME_FILE).inputStream()
        val byteArray = ByteArray(inputStream.available())

        inputStream.read(byteArray)
        inputStream.close()
        //Log.i("JSON", String(byteArray, Charset.forName("windows-1251")))
        return String(byteArray, Charset.forName("windows-1251"))
    }

    private fun parsJson(strJson: String, emitter: ObservableEmitter<MenuDish>) {
        val jsonObj = JSONObject(strJson)
        val menuDish = MenuDish()
        menuDish.hotArray = getArray(jsonObj.getJSONArray("hot"),"hot")
        menuDish.saladsArray = getArray(jsonObj.getJSONArray("salads"), "salads")
        menuDish.soupArray = getArray(jsonObj.getJSONArray("soup"),"soup")
        menuDish.burgerArray = getArray(jsonObj.getJSONArray("burger"), "burger")
        menuDish.nonalcArray = getArray(jsonObj.getJSONArray("nonalc"), "nonalc")
        menuDish.beerArray = getArray(jsonObj.getJSONArray("beer"), "beer")
        //��� ����� RxJava2, ��� �� ��������� ��������, ��� ������ ���������� � ����������
        //https://startandroid.ru/ru/courses/rxjava/19-course/rxjava/435-urok-1.html
        //http://www.vogella.com/tutorials/RxJava/article.html
        emitter.onNext(menuDish)
    }

    private fun getArray(jsonArray: JSONArray, name: String = ""): ArrayList<Dish> {
        val locArrayList = ArrayList<Dish>()
        for (i in 0..jsonArray.length()-1){
            val jsObject = jsonArray.get(i) as JSONObject
            locArrayList.add(
                Dish(
                    jsObject.getString("name"), jsObject.getInt("price"),
                    jsObject.getString("descr"), jsObject.getString("weight"), name
                )
            )
        }
        return locArrayList
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: ParsJson? = null
        fun getInstance(): ParsJson {
            if (instance == null)
                instance = ParsJson()
            return instance as ParsJson
        }
    }
}

