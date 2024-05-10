

package com.yashpalz.otp_autofill_sms_consent

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.yashpalz.otp_autofill_sms_consent.SMSConsentBroadcastReceiver.Companion.SMS_CONSENT_REQUEST_CODE

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry


class OtpAutofillSmsConsentPlugin : FlutterPlugin, MethodCallHandler,
    PluginRegistry.ActivityResultListener, ActivityAware {
    private lateinit var channel: MethodChannel

    private var activity: Activity? = null
    private var smsConsentReceiver: SMSConsentBroadcastReceiver? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "otp_autofill_sms_consent")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "start" -> {
                if (activity != null) {
                    if (smsConsentReceiver == null) {
                        smsConsentReceiver = SMSConsentBroadcastReceiver(activity!!, channel)
                    }
                    smsConsentReceiver!!.start(call.arguments?.toString())
                    result.success(null)
                }
            }
            "stop" -> smsConsentReceiver?.stop()
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?): Boolean {
        when (requestCode) {
            SMS_CONSENT_REQUEST_CODE -> {
                val consentAccepted = resultCode == Activity.RESULT_OK

                if (consentAccepted && intent != null) {
                    val message = intent.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    channel.invokeMethod("onAllowed", message)
                } else {
                    channel.invokeMethod("onDenied", null)
                }
                smsConsentReceiver?.stop()
            }
        }
        return true
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {}

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}

    override fun onDetachedFromActivity() {
        activity = null
        smsConsentReceiver?.stop()
    }
}
