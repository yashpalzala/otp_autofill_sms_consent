# OTP autofill sms consent

Updated Flutter plugin to extract OTP from SMS for verification using the SMS User Consent API in Android along with working patch for Android 13 and above

This plugin uses the [SMS User Consent API](https://developers.google.com/identity/sms-retriever/user-consent/overview)

The following criteria must meet for the API to be triggered:
- The message contains a 4-10 character alphanumeric string with at least one number.
- The message was sent by a phone number that's not in the user's contacts.
- If you specified the sender's phone number, the message was sent by that number.

## Usage

```dart
  _otpAutofillSmsConsent = OtpAutofillSmsConsent(
      pattern: r'\d{4,}',
      onAllowed: (sms) {
        _sms = sms;
        setState(() {});
      },
      onDenied: () {
        _scaffoldMessenger?.showSnackBar(
          const SnackBar(content: Text('User denied!')),
        );
      },
      onPatternUnmatched: () {
        _scaffoldMessenger?.showSnackBar(
          const SnackBar(
            content: Text('OTP not found! Please try entering OTP manually.'),
          ),
        );
      },
    )
      ..start('otpSender');
```

This plugin is extended version of another plugin created to fix the requirements of Android 13 and above.