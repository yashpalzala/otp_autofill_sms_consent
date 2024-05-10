

package com.yashpalz.otp_autofill_sms_consent

import android.app.Activity
import android.content.*
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import io.flutter.plugin.common.MethodChannel
import androidx.core.content.ContextCompat
import android.os.Build

class SMSConsentBroadcastReceiver(private val activity: Activity, private val channel: MethodChannel) : BroadcastReceiver() {
    

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (smsRetrieverStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    try {
                        activity.startActivityForResult(consentIntent, SMS_CONSENT_REQUEST_CODE)
                    } catch (e: ActivityNotFoundException) {
                      
                    }
                }
                CommonStatusCodes.TIMEOUT -> onTimeout()
            }
        }
    }

    private fun onTimeout() {
        channel.invokeMethod("onTimeout", null)
        stop()
    }

    fun start(phone: String?) {
        SmsRetriever.getClient(activity).startSmsUserConsent(phone)

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        
        if (Build.VERSION.SDK_INT >= 33) {
    activity.registerReceiver(this, intentFilter, SmsRetriever.SEND_PERMISSION, null,ContextCompat.RECEIVER_EXPORTED)
}else {
     activity.registerReceiver(this, intentFilter, SmsRetriever.SEND_PERMISSION, null)
}
    }

    fun stop() {
        try {
            activity.unregisterReceiver(this)
        } catch (e: Exception) {
            
        }
    }

    companion object {
        const val SMS_CONSENT_REQUEST_CODE = 0x7890
    }
}