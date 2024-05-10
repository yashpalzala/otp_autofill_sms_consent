import 'package:otp_autofill_sms_consent/otp_autofill_sms_consent.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late final OtpAutofillSmsConsent _otpAutofillSmsConsent;
  ScaffoldMessengerState? _scaffoldMessenger;
  String? _responseText;

  String _sms = '';

  @override
  void initState() {
    super.initState();
    _otpAutofillSmsConsent = OtpAutofillSmsConsent(
      pattern: r'\d{4,}',
      onAllowed: (sms) {
        _sms = sms;
        setState(() {});
      },
      onDenied: () {
        _responseText = 'User denied!';
      },
      onPatternUnmatched: () {
        _responseText = 'OTP not found! Please try entering OTP manually.';
      },
    )..start('otpSender');
  }

  @override
  void dispose() {
    _otpAutofillSmsConsent.stop();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('OTP Autofill SMS Consent'),
        ),
        body: Builder(
          builder: (context) {
            _scaffoldMessenger ??= ScaffoldMessenger.of(context);

            return Center(
              child: Column(
                children: [
                  Text(
                    'SMS: $_sms',
                  ),
                  const SizedBox(
                    height: 12,
                  ),
                  Text(
                    'Response: $_responseText',
                  ),
                ],
              ),
            );
          },
        ),
      ),
    );
  }
}
