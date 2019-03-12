package com.zlobrynya.internshipzappa.adapter.booking

/**
 * Класс данных для адаптера столиков
 * (Временный, до создания работы с сервером)
 */
data class Table(val seatCount: Int, val seatPosition: String? = "", val seatType: String, val seatId: Int)