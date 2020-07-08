package com.tommunyiri.mpesadaraja

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.androidstudy.daraja.Daraja
import com.androidstudy.daraja.DarajaListener
import com.androidstudy.daraja.model.AccessToken
import com.androidstudy.daraja.model.LNMExpress
import com.androidstudy.daraja.model.LNMResult
import com.androidstudy.daraja.util.Env
import com.androidstudy.daraja.util.TransactionType
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MpesaListener {
    lateinit var daraja: Daraja

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mpesaListener = this
        daraja = Daraja.with(
            "YZKLO1coR3sC3BpMTLW8Jw2LLwSInLAI",
            "thFhg6N8vQMxTi87",
            Env.SANDBOX, //for Test use Env.PRODUCTION when in production
            object : DarajaListener<AccessToken> {
                override fun onResult(accessToken: AccessToken) {

                    Toast.makeText(
                        this@MainActivity,
                        "MPESA TOKEN : ${accessToken.access_token}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(error: String) {

                }
            })

        button.setOnClickListener {
            val phoneNumber = phone.text.trim().toString().trim()
            val lnmExpress = LNMExpress(
                "174379",
                "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",
                TransactionType.CustomerPayBillOnline,
                "1",
                phoneNumber,
                "174379",
                phoneNumber,
                "https://us-central1-mpesaandroidtest.cloudfunctions.net/api/myCallbackUrl",
                "001ABC",
                "Goods Payment"
            )

            daraja.requestMPESAExpress(lnmExpress,
                object : DarajaListener<LNMResult> {
                    override fun onResult(lnmResult: LNMResult) {

                        FirebaseMessaging.getInstance()
                            .subscribeToTopic(lnmResult.CheckoutRequestID.toString())


                        Toast.makeText(
                            this@MainActivity,
                            "Response here ${lnmResult.ResponseDescription}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(error: String) {

                        Toast.makeText(
                            this@MainActivity,
                            "Error here $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }


    }

    companion object {
        lateinit var mpesaListener: MpesaListener
    }

    override fun sendSuccesfull(amount: String, phone: String, date: String, receipt: String) {
        runOnUiThread {
            Toast.makeText(
                this, "Payment Succesfull\n" +
                        "Receipt: $receipt\n" +
                        "Date: $date\n" +
                        "Phone: $phone\n" +
                        "Amount: $amount", Toast.LENGTH_LONG
            ).show()

        }
    }

    override fun sendFailed(reason: String) {
        runOnUiThread {
            Toast.makeText(
                this, "Payment Failed\n" +
                        "Reason: $reason"
                , Toast.LENGTH_LONG
            ).show()
        }
    }
}