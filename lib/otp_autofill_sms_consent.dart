library otp_autofill_sms_consent;

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

/// A class to request one-time consent to read an SMS with alphanumeric string.
///
/// SMS is listened for only if it meets these criteria:
/// - The message contains a 4-10 character alphanumeric string with at least one number.
/// - The message was sent by a phone number that's not in the user's contacts.
/// - If you specified the sender's phone number, the message was sent by that number.
class OtpAutofillSmsConsent {
  /// Creates an instance of [OtpAutofillSmsConsent].
  OtpAutofillSmsConsent({
    /// onAllowed will give a value(OTP) that matches the pattern that has been given in pattern parameter.
    required ValueChanged<String> onAllowed,
    String? pattern,

    /// onDenied is a callback when user denies to read the OTP when OS shows the otp dialog box
    VoidCallback? onDenied,
    VoidCallback? onTimeout,
    VoidCallback? onPatternUnmatched,
  }) : _channel = const MethodChannel('otp_autofill_sms_consent') {
    _channel.setMethodCallHandler(
      (call) async {
        switch (call.method) {
          case 'onAllowed':
            final sms = call.arguments.toString();
            if (pattern == null) {
              onAllowed(sms);
            } else {
              final regex = RegExp(pattern);
              if (regex.hasMatch(sms)) {
                final message = regex.firstMatch(sms)!.group(0)!;
                onAllowed(message);
              } else {
                onPatternUnmatched?.call();
              }
            }
            break;
          case 'onDenied':
            onDenied?.call();
            break;
          case 'onTimeout':
            onTimeout?.call();
            break;
        }
      },
    );
  }

  final MethodChannel _channel;

  /// Starts listening for incoming messages(with 4-10 character alphanumeric string with at least one number)
  /// for the next 5 minutes.
  ///
  /// If [sender] is provided, messages from the sender is only listened for.
  /// Otherwise messages for any sender is listened.
  void start([String? sender]) => _invoke('start', sender);

  /// Stops the listener for incoming messages.
  void stop() => _invoke('stop');

  void _invoke(String method, [String? argument]) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      _channel.invokeMethod<void>(method, argument);
    }
  }
}
