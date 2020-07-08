package com.tommunyiri.mpesadaraja

interface MpesaListener {

    fun sendSuccesfull(amount: String, phone: String, date: String, receipt: String)
    fun sendFailed(reason: String)
}