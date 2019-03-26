package com.zlobrynya.internshipzappa.activity.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.authDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.authRespDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.zlobrynya.internshipzappa.R.layout.activity_login)

        supportActionBar!!.title = "Вход"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(R.drawable.actionbar))
        supportActionBar!!.elevation = 0F

        val newAuth = authDTO()


        btnLinkToRegisterActivity.setOnClickListener {
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }

        val icon = resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.error)

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        log_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                log_email.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val email = log_email_input_layout.editText!!.text.toString()
                        val validateEmail = validateEmail(email)

                        if (!validateEmail) {
                            log_email_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_email)
                            log_email.setCompoundDrawables(null, null, icon, null)
                        } else {
                            log_email_input_layout.isErrorEnabled = false
                            log_email.setCompoundDrawables(null, null, null, null)
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        log_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                log_password.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val password = log_password_input_layout.editText!!.text.toString()
                        val validatePassword = validatePassword(password)

                        if (!validatePassword) {
                            log_password_input_layout.error =
                                getString(com.zlobrynya.internshipzappa.R.string.error_password)
                            log_password.setCompoundDrawables(null, null, icon, null)
                        } else {
                            log_password_input_layout.isErrorEnabled = false
                            log_password.setCompoundDrawables(null, null, null, null)
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        forgot_password.setOnClickListener {
            val intent = Intent(this, PasswordRecovery::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = log_email_input_layout.editText!!.text.toString()
            val password = log_password_input_layout.editText!!.text.toString()

            val validateEmail = validateEmail(email)
            val validatePassword = validatePassword(password)

            newAuth.email = email
            newAuth.password = password
            if (validateEmail && validatePassword) {

                RetrofitClientInstance.getInstance()
                    .postAuthData(newAuth)
                    .subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : Observer<Response<authRespDTO>> {

                        override fun onComplete() {}

                        override fun onSubscribe(d: Disposable) {}

                        override fun onNext(t: Response<authRespDTO>) {
                            Log.i("checkAuth", "${t.code()}")
                            if (t.isSuccessful) {
                                val sharedPreferencesStat = applicationContext.getSharedPreferences(
                                    applicationContext.getString(R.string.user_info),
                                    Context.MODE_PRIVATE
                                )
                                val savedEmail = applicationContext.getString(R.string.user_email)
                                val access_token = applicationContext.getString(R.string.access_token)
                                val editor = sharedPreferencesStat.edit()
                                editor.putString(savedEmail, t.body()!!.email)
                                editor.putString(access_token, t.body()!!.access_token)
                                editor.apply()

                                Log.i("checkAuth", t.body()!!.email)
                                Log.i("checkAuth", t.body()!!.access_token)
                                //onBackPressed()
                                updateUserBookingList()
                                setResult(Activity.RESULT_OK)
                                finish()
                            } else {
                                log_password_input_layout.error =
                                    getString(com.zlobrynya.internshipzappa.R.string.wrong_password_email)
                                log_password.setCompoundDrawables(null, null, icon, null)
                                Log.i("checkAuth", "введены некоректные данные")
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.i("checkAuth", "that's not fineIn")
                        }

                    })

            }

        }
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.matches("((?=.*[a-z0-9]).{4,20})".toRegex())
    }

    override fun onBackPressed() {
        /*super.onBackPressed()
        val intent = Intent(this, Menu2Activity::class.java)
        startActivity(intent)
        finish()*/

        super.onBackPressed()
        // Закрываем активити с кодом RESULT_CANCELED если юзер закрыл авторизацию
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        // Закрываем активити с кодом RESULT_CANCELED если юзер закрыл авторизацию
        setResult(Activity.RESULT_CANCELED)
        finish()
        return true
    }

    /**
     * Обновляет список броней юзера
     * Вызывать надо при любом чихе с авторизацией\бронью\отменой брони
     */
    private fun updateUserBookingList() {
        val fragment = supportFragmentManager.findFragmentByTag("USER_BOOKING_LIST")
        if (fragment != null) {
            fragment.onResume()
        }
    }
}
