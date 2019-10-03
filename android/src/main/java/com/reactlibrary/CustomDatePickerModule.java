package com.reactlibrary;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TimePicker;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import javax.annotation.Nullable;

public class CustomDatePickerModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    public static final String FRAGMENT_TAG = TimePickerDialogFragment.class.getSimpleName();

    private static final String ERROR_NO_ACTIVITY = "E_NO_ACTIVITY";

    /* package */ static final String ARG_MINUTE_OF_DAY = "minuteOfDay";
    /* package */ static final String ARG_HOUR_OF_DAY = "hourOfDay";
    /* package */ static final String ARG_IS_24_HOUR_VIEW = "is24HourView";

    /* package */ static final String ACTION_TIME_SET = "timeSetAction";
    /* package */ static final String ACTION_DISMISSED = "dismissedAction";
    /* package */ static final String ACTION_CLEARED = "clearedAction";
    private static final String ARG_ACTION = "action";

    public CustomDatePickerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    private class TimePickerDialogListener
            implements TimePickerDialog.OnTimeSetListener, DialogInterface.OnDismissListener, OnClearClickedListener {

        private final Promise mPromise;
        private boolean mPromiseResolved = false;

        public TimePickerDialogListener(final Promise promise) {
            mPromise = promise;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            WritableMap result = new WritableNativeMap();
            result.putString(ARG_ACTION, ACTION_TIME_SET);
            result.putInt("hour", hourOfDay);
            result.putInt("minute", minute);
            resolvePromiseWithMap(result);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            WritableMap result = new WritableNativeMap();
            result.putString(ARG_ACTION, ACTION_DISMISSED);
            resolvePromiseWithMap(result);
        }

        @Override
        public void onClearClicked(DialogInterface dialog) {
            WritableMap map = new WritableNativeMap();
            map.putString(ARG_ACTION, ACTION_CLEARED);
            resolvePromiseWithMap(map);
        }

        private void resolvePromiseWithMap(WritableMap map) {
            if (!mPromiseResolved && getReactApplicationContext().hasActiveCatalystInstance()) {
                mPromise.resolve(map);
                mPromiseResolved = true;
            }
        }

    }

    @Override
    public String getName() {
        return "CustomDatePicker";
    }

    // @ReactMethod
    // public void sampleMethod(String stringArgument, int numberArgument, Callback
    // callback) {
    // // TODO: Implement some actually useful functionality
    // callback.invoke("Received numberArgument: " + numberArgument + "
    // stringArgument: " + stringArgument);
    // }

    /**
     * Show a date picker dialog.
     *
     * @param options a map containing options. Available keys are:
     *                <p>
     *                <ul>
     *                <li>{@code date} (timestamp in milliseconds) the date to show
     *                by default</li>
     *                <li>{@code minDate} (timestamp in milliseconds) the minimum
     *                date the user should be allowed to select</li>
     *                <li>{@code maxDate} (timestamp in milliseconds) the maximum
     *                date the user should be allowed to select</li>
     *                <li>{@code mode} To set the date picker mode to
     *                'calendar/spinner/default'</li>
     *                </ul>
     * @param promise This will be invoked with parameters action, hourOfDay,
     *                minute, where action is {@code timeSetAction} or
     *                {@code dismissedAction}, depending on what the user did. If
     *                the action is dismiss, hourOfDay and minute are undefined.
     */

    @ReactMethod
    public void open(@Nullable final ReadableMap options, Promise promise) {
        Activity activity = getCurrentActivity();
        if (activity == null) {
            promise.reject(ERROR_NO_ACTIVITY, "Tried to open a DatePicker dialog while not attached to an Activity");
            return;
        }
        FragmentManager fragmentManager = activity.getFragmentManager();
        DialogFragment oldFragment = (DialogFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (oldFragment != null) {
            oldFragment.dismiss();
        }
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        if (options != null) {
            final Bundle args = createFragmentArguments(options);
            fragment.setArguments(args);
        }
        final TimePickerDialogListener listener = new TimePickerDialogListener(promise);
        fragment.setOnDismissListener(listener);
        fragment.setOnTimeSetListener(listener);
        fragment.setOnClearClickedListener(listener);
        fragment.show(fragmentManager, FRAGMENT_TAG);
    }

    private Bundle createFragmentArguments(ReadableMap options) {
        final Bundle args = new Bundle();
        if (options.hasKey(ARG_MINUTE_OF_DAY) && !options.isNull(ARG_MINUTE_OF_DAY)) {
            args.putInt(ARG_MINUTE_OF_DAY, options.getInt(ARG_MINUTE_OF_DAY));
        }
        if (options.hasKey(ARG_HOUR_OF_DAY) && !options.isNull(ARG_HOUR_OF_DAY)) {
            args.putInt(ARG_HOUR_OF_DAY, options.getInt(ARG_HOUR_OF_DAY));
        }
        if (options.hasKey(ARG_IS_24_HOUR_VIEW) && !options.isNull(ARG_IS_24_HOUR_VIEW)) {
            args.putBoolean(ARG_IS_24_HOUR_VIEW, options.getBoolean(ARG_IS_24_HOUR_VIEW));
        }
        return args;
    }
}
