package com.zlobrynya.internshipzappa.fragment


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.booking.BookingEnd
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.userDataDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.bookingUserDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.deleteBookingDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_personal_info.view.*
import kotlinx.android.synthetic.main.fragment_personal_info.view.*
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * Фрагмент персональное инфо
 * Калька с одноименной активити
 * TODO доделать
 */
class PersonalInfoFragment : Fragment() {

    private val blockCharacterSet: String = ".,- "

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
    }

    /**
     * Обработчик нажатий на стрелочку в тулбаре
     */
    private val navigationClickListener = View.OnClickListener {
        closeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_personal_info, container, false)
        view = initToolBar(view)

        val bookDateBegin = arguments!!.getString("book_date_begin")
        val bookTimeBegin = arguments!!.getString("book_time_begin")
        val bookTimeEnd = arguments!!.getString("book_time_end")
        val bookDateEnd = arguments!!.getString("book_date_end")
        val bookTableId = arguments!!.getInt("table_id", 1)
        val seatCount = arguments!!.getInt("seat_count", 1).toString()
        val seatPosition = arguments!!.getString("seat_position")
        val seatType = arguments!!.getString("seat_type")

        Log.d("bookdata", bookDateBegin)
        Log.d("bookdata", bookTimeBegin)
        Log.d("bookdata", bookTimeEnd)
        Log.d("bookdata", bookDateEnd)
        Log.d("bookdata", bookTableId.toString())
        Log.d("bookdata", bookDateBegin)
        Log.d("bookdata", seatCount)
        Log.d("bookdata", seatType)


        val newBooking = bookingUserDTO()
        newBooking.date = bookDateBegin!!.toString()
        newBooking.time_from = bookTimeBegin!!.toString()
        newBooking.time_to = bookTimeEnd!!.toString()
        newBooking.table_id = bookTableId
        newBooking.date_to = bookDateEnd!!.toString()

        // TODO Работа с шаред преференс должна будет быть переделана
        /*val sharedPreferences =
            activity!!.getSharedPreferences(this.getString(R.string.key_shared_users), Context.MODE_PRIVATE)
        val savedName = this.getString(R.string.key_user_name)
        val savedPhone = this.getString(R.string.key_user_phone)
        val savedEmail = this.getString(R.string.key_user_email)
        if (sharedPreferences.getString(
                savedName,
                ""
            ) != ""
        ) username_input_layout.editText!!.setText(sharedPreferences.getString(savedName, ""))
        if (sharedPreferences.getString(savedPhone, "") != "") {
            val change_phone = sharedPreferences.getString(savedPhone, "")
            phone_number_input_layout.editText!!.setText(change_phone)
        }
        if (sharedPreferences.getString(savedEmail, "") != "") register_email_input_layout.editText!!.setText(
            sharedPreferences.getString(savedEmail, "")
        )*/

        if (seatPosition == "" && seatCount == "4") {
            val textTable = getString(com.zlobrynya.internshipzappa.R.string.table4, bookTableId, seatCount, seatType)
            view.fm_selected_table.setText(textTable)
        } else if (seatPosition == "" && seatCount != "4") {
            val textTable = getString(com.zlobrynya.internshipzappa.R.string.table68, bookTableId, seatCount, seatType)
            view.fm_selected_table.setText(textTable)
        } else if (seatPosition != "" && seatCount == "4") {
            val textTable = getString(com.zlobrynya.internshipzappa.R.string.table43, bookTableId, seatCount, seatPosition, seatType)
            view.fm_selected_table.setText(textTable)
        } else {
            val textTable =
                getString(com.zlobrynya.internshipzappa.R.string.table683, bookTableId, seatCount, seatPosition, seatType)
            view.fm_selected_table.setText(textTable)
        }

        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")
        val date: Date = inputFormat.parse(bookDateBegin)
        val outputDateStr = outputFormat.format(date)
        view.fm_selected_date.text = outputDateStr

        val textWoSecI = bookTimeBegin.toString()
        val textWoSecO = bookTimeEnd.toString()
        val woSecI = deleteSecInTime(textWoSecI)
        val woSecO = deleteSecInTime(textWoSecO)
        var text = getString(com.zlobrynya.internshipzappa.R.string.period, woSecI, woSecO)
        view.fm_selected_time.text = text

        view.fm_btnContinue.setOnClickListener {

            // TODO эти данные больше не нужно получать из полей в XML, а получать из ШередПреференс
            //val name = view.username_input_layout.text.toString()
            val name = "KEK"

            networkRxjavaPost(newBooking, it.context)
        }

        return view
    }

    //Пост запрос на размещение личной информации(RxJava2)
    private fun networkRxjavaPost(newBooking: bookingUserDTO, context: Context) {

        val sharedPreferences =
            activity!!.getSharedPreferences(this.getString(R.string.user_info), Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString(this.getString(R.string.access_token), "null")!!.toString()
        newBooking.email = sharedPreferences.getString(this.getString(R.string.user_email), "")!!.toString()

        RetrofitClientInstance.getInstance()
            .postReserve(jwt, newBooking)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<respDTO>) {
                    Log.d("booking", t.code().toString())
                    if (t.isSuccessful) {
                        Log.i("check1", "${t.code()}")
                        if (t.body() != null) {
                            val code = t.code()
                            if (code == 200) {
                                val sharedPreferences = context.getSharedPreferences(
                                    context.getString(R.string.key_shared_users),
                                    Context.MODE_PRIVATE
                                )
                                //преференсы будут не нужны, получать данные о юзере из функции
                                val savedName = context.getString(R.string.key_user_name)
                                val savedPhone = context.getString(R.string.key_user_phone)
                                val savedEmail = context.getString(R.string.key_user_email)
                                val editor = sharedPreferences.edit()
                                editor.putString(savedName, "временное имя")
                                editor.putString(savedPhone, "временный телефон")
                                editor.putString(savedEmail, newBooking.email)
                                editor.apply()
                            }
                            val intent = Intent(context, BookingEnd::class.java)
                            intent.putExtra("code", t.code())
                            intent.putExtra("name", "временное имя")
                            intent.putExtra("phone", "временный телефон")
                            context.startActivity(intent)
                        }
                    } else {
                        Log.i("check2", "${t.code()}")
                        val intent = Intent(context, BookingEnd::class.java)
                        intent.putExtra("code", t.code())
                        intent.putExtra("name", "временное имя")
                        intent.putExtra("phone", "временный телефон")
                        //finish()
                        closeFragment() // Закроем фрагмент
                        context.startActivity(intent)
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("check", "that's not fineIn")
                }

            })
    }

    private fun deleteSecInTime(str: String): String {
        return str.substring(0, str.length - 3)
    }

    /**
     * Настраивает тулбар
     */
    private fun initToolBar(view: View): View {
        view.fm_enter_personal_info.setNavigationIcon(R.drawable.ic_back_button) // Установим иконку в тулбаре
        view.fm_enter_personal_info.setNavigationOnClickListener(navigationClickListener) // Установим обработчик нажатий на тулбар
        return view
    }

    /**
     * На выхов этой функции ждать отмашку, бэк пока не готов
     * TODO юзаем вместо преф этот вызов, получаем данные пользователя с него
     * Закрывает текущий фрагмент и удаляет его со стека
     */
    private fun postUserCredentials(){
        val sharedPreferences =
            activity!!.getSharedPreferences(this.getString(R.string.user_info), Context.MODE_PRIVATE)
        val newEmail = verifyEmailDTO()
        newEmail.email = sharedPreferences.getString(this.getString(R.string.user_email), "")!!.toString()
        val jwt = sharedPreferences.getString(this.getString(R.string.access_token), "null")!!.toString()

        RetrofitClientInstance.getInstance()
            .postViewUserCredentials(jwt, newEmail)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<userDataDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<userDataDTO>) {
                    Log.i("checkMyBooking", "${t.code()}")

                    if(t.isSuccessful) {
                        /**
                         * TODO юзер авторизирован и запрос прошёл, сюда пихнуть обработку
                         */
                        //
                    }else{
                        /**
                         * TODO
                         * юзер неавторизирован или ещё какая херня, но запрос выполнен. Посмотреть код t.code() и обработать
                         *если 401 запустить активити авторизации, если успешно авторизовался выкинуть обратно сюда и обновить
                         *содержимое фрагмента, видимо через отслеживание результата активити опять, хз
                         */
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("check", "that's not fineIn")
                    //запрос не выполнен, всё плохо
                }

            })
    }


    /**
     * Закрывает текущий фрагмент и удаляет его со стека
     */
    private fun closeFragment() {
        val trans = fragmentManager!!.beginTransaction()
        trans.remove(this)
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        trans.commit()
        fragmentManager!!.popBackStack()
    }
}