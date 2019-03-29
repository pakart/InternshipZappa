package com.zlobrynya.internshipzappa.activity.profile

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.annotation.RequiresApi
import android.support.design.widget.TextInputEditText
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.Menu2Activity
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.regDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyRespDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.Observer
import kotlinx.android.synthetic.main.activity_personal_info.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Response
import java.util.*

class RegisterActivity : AppCompatActivity() {

    var lastCLickTime: Long = 0

    /*private val blockCharacterSet: String = ".,-~@№:;_=#^|$%&*! "

    private val filter = object : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            if (source != null && blockCharacterSet.contains(("" + source))) {
                return ""
            }
            return null
        }
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar!!.title = "Регистрация"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(R.drawable.actionbar))
        supportActionBar!!.elevation = 0F

        val icon = resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.error)

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        reg_username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reg_username.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val name = reg_username_input_layout.editText!!.text.toString()
                        val validateName = validateName(name)

                        if (!hasFocus && !validateName) {
                            reg_username_input_layout.error =
                                getString(com.zlobrynya.internshipzappa.R.string.error_name)
                            reg_username.setCompoundDrawables(null, null, icon, null)
                        } else {
                            reg_username_input_layout.isErrorEnabled = false
                            reg_username.setCompoundDrawables(null, null, null, null)
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        reg_phone_number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reg_phone_number.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val phone = reg_phone_number_input_layout.editText!!.text.toString()
                        val validatePhone = validatePhone(phone)

                        if (!hasFocus && !validatePhone) {
                            reg_phone_number_input_layout.error =
                                getString(com.zlobrynya.internshipzappa.R.string.error_phone)
                            reg_phone_number.setCompoundDrawables(null, null, icon, null)
                        } else {
                            reg_phone_number_input_layout.isErrorEnabled = false
                            reg_phone_number.setCompoundDrawables(null, null, null, null)
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        reg_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reg_email.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val email = reg_email_input_layout.editText!!.text.toString()
                        val validateEmail = validateEmail(email)

                        if (!hasFocus && !validateEmail) {
                            reg_email_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_email)
                            reg_email.setCompoundDrawables(null, null, icon, null)
                        } else {
                            reg_email_input_layout.isErrorEnabled = false
                            reg_email.setCompoundDrawables(null, null, null, null)
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        reg_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reg_password.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val password = reg_password_input_layout.editText!!.text.toString()
                        val validatePassword = validatePassword(password)

                        if (!hasFocus && !validatePassword) {
                            reg_password_input_layout.error =
                                getString(com.zlobrynya.internshipzappa.R.string.error_password)
                            reg_password.setCompoundDrawables(null, null, icon, null)
                        } else {
                            reg_password_input_layout.isErrorEnabled = false
                            reg_password.setCompoundDrawables(null, null, null, null)
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        reg_confirm_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                        val password = reg_password_input_layout.editText!!.text.toString()
                        val confirmPassword = reg_confirm_password_input_layout.editText!!.text.toString()
                        val validateConfirmPassword = validateConfirmPassword(password, confirmPassword)

                        if (!validateConfirmPassword) {
                            reg_confirm_password_input_layout.error =
                                getString(com.zlobrynya.internshipzappa.R.string.error_confirm_password)
                            reg_confirm_password.setCompoundDrawables(null, null, icon, null)
                        } else {
                            reg_confirm_password_input_layout.isErrorEnabled = false
                            reg_confirm_password.setCompoundDrawables(null, null, null, null)
                        }

                }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        btnRegister.setOnClickListener {
            val name = reg_username_input_layout.editText!!.text.toString()
            val phone = reg_phone_number_input_layout.editText!!.text.toString()
            val email = reg_email_input_layout.editText!!.text.toString()
            val password = reg_password_input_layout.editText!!.text.toString()
            val confirmPassword = reg_confirm_password_input_layout.editText!!.text.toString()


            val validateName = validateName(name)
            val validatePhone = validatePhone(phone)
            val validateEmail = validateEmail(email)
            val validatePassword = validatePassword(password)
            val validateConfirmPassword = validateConfirmPassword(password, confirmPassword)


            if (validateName && validatePhone && validateEmail && validatePassword && validateConfirmPassword) {
                val newVerify = verifyEmailDTO()

                newVerify.email = reg_email.text.toString()
                if (SystemClock.elapsedRealtime() - lastCLickTime < 10000) {
                    lastCLickTime = 0
                } else {
                    lastCLickTime = SystemClock.elapsedRealtime()
                    checkExistenceEmail(newVerify)
                }
                lastCLickTime = 0
            } else {
                Toast.makeText(this@RegisterActivity, "Заполните данные", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lastCLickTime = 0
    }

    private fun checkExistenceEmail(newVerify: verifyEmailDTO) {
        progress_spinner.visibility = View.VISIBLE

        RetrofitClientInstance.getInstance()
            .getEmailExistence(newVerify.email)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<respDTO>) {
                    Log.i("checkEmailExistence", t.code().toString())
                    if (t.isSuccessful) {
                        Log.i("checkEmailExistence", "${t.code()}")
                        Log.i("checkEmailExistence", t.body()!!.desc)
                        val icon = resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.error)
                        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
                        reg_email_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.exist_email)
                        reg_email.setCompoundDrawables(null, null, icon, null)
                        progress_spinner.visibility = View.GONE
                    } else {
                        RetrofitClientInstance.getInstance()
                            .postVerifyData(newVerify)
                            .subscribeOn(Schedulers.io())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.subscribe(object : Observer<Response<verifyRespDTO>> {

                                override fun onComplete() {}

                                override fun onSubscribe(d: Disposable) {}

                                override fun onNext(t: Response<verifyRespDTO>) {
                                    Log.i("checkCode", "${t.code()}")

                                    if (t.isSuccessful) {
                                        val intent = Intent(applicationContext, CodeFEmailActivity::class.java)
                                        intent.putExtra("name", reg_username.text.toString())
                                        intent.putExtra("phone", reg_phone_number.text.toString())
                                        intent.putExtra("email", reg_email.text.toString())
                                        intent.putExtra("password", reg_password.text.toString())
                                        intent.putExtra("code", t.body()!!.email_code)
                                        intent.putExtra("id", "0")
                                        startActivity(intent)
                                    }
                                    progress_spinner.visibility = View.GONE
                                }

                                override fun onError(e: Throwable) {
                                    Log.i("check", "that's not fineIn")
                                }

                            })
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("checkReg", "that's not fineIn")
                }
            })
    }

    /*fun Editable.limitLength(maxLength: Int) {
        filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }*/

    private fun validateName(name: String): Boolean {
        val nameLength = 2
        return name.matches("[a-zA-Zа-яА-ЯёЁ]*".toRegex()) && name.length >= nameLength
    }

    private fun validatePhone(phone: String): Boolean {
        //val phoneLength7 = 16
        //val phoneLength8 = 17
        //val firstChar: Char = phone[0]
        /*return if (firstChar == '8') {
            phone.length == phoneLength8
        } else {
            phone.length == phoneLength7
        }*/
        val phoneLength = 16
        return phone.length == phoneLength
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.matches("((?=.*[a-z0-9]).{4,20})".toRegex())
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
