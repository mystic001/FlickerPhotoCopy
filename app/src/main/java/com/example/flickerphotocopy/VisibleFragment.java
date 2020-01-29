package com.example.flickerphotocopy;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class VisibleFragment extends Fragment {
    private static final String TAG = "VisibleFragment";
    public VisibleFragment() {
        // Required empty public constructor
    }
    BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isOrderedBroadcast()) {
                Log.i(TAG, "canceling notification");
                setResultCode(Activity.RESULT_CANCELED);
            }

        }
    };

    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mOnShowNotification,filter,PollService.PERM_PRIVATE,null);
    }

    public void onStop(){
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);

    }



}
