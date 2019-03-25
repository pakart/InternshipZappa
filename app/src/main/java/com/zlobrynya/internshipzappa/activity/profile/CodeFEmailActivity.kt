package com.zlobrynya.internshipzappa.activity.profile

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.authDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.regDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_code_f_email.*
import org.w3c.dom.Text
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class CodeFEmailActivity: AppCompatActivity() {

    private var code: String = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_f_email)

        supportActionBar!!.title = "Регистрация"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(R.drawable.actionbar))
        supportActionBar!!.elevation = 0F

        //принимаем параметры и формируем отсылку
        val newRegister = regDTO()
        newRegister.email = intent.getStringExtra("email")
        newRegister.code = intent.getStringExtra("code")
        newRegister.password = intent.getStringExtra("password")
        newRegister.name = intent.getStringExtra("name")
        newRegister.phone = intent.getStringExtra("phone")



        firstNumber.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (firstNumber.text.toString() == ""){
                    firstNumber.requestFocus()
                } else {
                    secondNumber.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        secondNumber.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (secondNumber.text.toString() == ""){
                    firstNumber.requestFocus()
                } else {
                    thirdNumber.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        thirdNumber.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (thirdNumber.text.toString() == ""){
                    secondNumber.requestFocus()
                } else {
                    fourthNumber.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        fourthNumber.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (fourthNumber.text.toString() == ""){
                    thirdNumber.requestFocus()
                } else {
                    fifthNumber.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        fifthNumber.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (fifthNumber.text.toString() == ""){
                    fourthNumber.requestFocus()
                } else {
                    fifthNumber.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        button2.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                code = firstNumber.text.toString() + secondNumber.text.toString() +
                        thirdNumber.text.toString() + fourthNumber.text.toString() + fifthNumber.text.toString()
                Log.d("code", "$code")

                newRegister.code = code


                Log.i("data", newRegister.email)
                Log.i("data", newRegister.code)
                Log.i("data", newRegister.phone)
                Log.i("data", newRegister.password)
                Log.i("data", newRegister.name)

                RetrofitClientInstance.getInstance()
                    .postRegData(newRegister)
                    .subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : Observer<Response<respDTO>> {

                        override fun onComplete() {}

                        override fun onSubscribe(d: Disposable) {}

                        override fun onNext(t: Response<respDTO>) {
                            Log.i("checkReg", t.code().toString())
                            if(t.isSuccessful) {
                                    allert_text.visibility = View.GONE
                                    firstNumber.setTextColor(resources.getColor(R.color.white))
                                    secondNumber.setTextColor(resources.getColor(R.color.white))
                                    thirdNumber.setTextColor(resources.getColor(R.color.white))
                                    fourthNumber.setTextColor(resources.getColor(R.color.white))
                                    fifthNumber.setTextColor(resources.getColor(R.color.white))
                                Log.i("checkReg", t.body()!!.desc)
                            }else{
                                allert_text.visibility = View.VISIBLE
                                firstNumber.setTextColor(resources.getColor(R.color.color_accent))
                                secondNumber.setTextColor(resources.getColor(R.color.color_accent))
                                thirdNumber.setTextColor(resources.getColor(R.color.color_accent))
                                fourthNumber.setTextColor(resources.getColor(R.color.color_accent))
                                fifthNumber.setTextColor(resources.getColor(R.color.color_accent))
                            }
                        }
                        override fun onError(e: Throwable) {
                            Log.i("checkReg", "that's not fineIn")
                        }
                    })
            }
        })
    }


}
