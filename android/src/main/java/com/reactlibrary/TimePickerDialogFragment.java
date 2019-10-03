/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 * <p>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.reactlibrary;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;

//import com.customdatepicker.OnClearClickedListener;

import javax.annotation.Nullable;

@SuppressLint("ValidFragment")
public class TimePickerDialogFragment extends DialogFragment {

    @Nullable
    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener;
    @Nullable
    private OnDismissListener mOnDismissListener;
    @Nullable
    private OnClearClickedListener mOnClearClickedListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        return createDialog(args, getActivity(), mOnTimeSetListener);
    }

    /*package*/
    Dialog createDialog(Bundle args, final Context activityContext,
                        @Nullable TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        final int minute = getMinuteFromArgs(args);
        final int hourOfDay = getHourOfDayFromArgs(args);
        final boolean is24HourView = getIs24HourViewFromArgs(args);

        final TimePickerDialog dialog = new TimePickerDialog(activityContext, onTimeSetListener, hourOfDay, minute, is24HourView);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "CLEAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mOnClearClickedListener != null) {
                    mOnClearClickedListener.onClearClicked(dialog);
                }
                dialog.dismiss();
            }
        });
        return dialog;
    }

    private static int getMinuteFromArgs(Bundle args) {
        return args != null && args.containsKey(CustomDatePickerModule.ARG_MINUTE_OF_DAY) ?
                args.getInt(CustomDatePickerModule.ARG_MINUTE_OF_DAY) : 0;
    }

    private static int getHourOfDayFromArgs(Bundle args) {
        return args != null && args.containsKey(CustomDatePickerModule.ARG_HOUR_OF_DAY) ?
                args.getInt(CustomDatePickerModule.ARG_HOUR_OF_DAY) : 0;
    }

    private static boolean getIs24HourViewFromArgs(Bundle args) {
        return args != null && args.containsKey(CustomDatePickerModule.ARG_IS_24_HOUR_VIEW) &&
                args.getBoolean(CustomDatePickerModule.ARG_IS_24_HOUR_VIEW);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    /*package*/ void setOnTimeSetListener(@Nullable TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        mOnTimeSetListener = onTimeSetListener;
    }

    /*package*/ void setOnDismissListener(@Nullable OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    /*package*/ void setOnClearClickedListener(@Nullable OnClearClickedListener onClearClickedListener) {
        mOnClearClickedListener = onClearClickedListener;
    }

}
