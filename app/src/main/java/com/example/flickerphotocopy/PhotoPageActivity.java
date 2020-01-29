package com.example.flickerphotocopy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.fragment.app.Fragment;


public class PhotoPageActivity extends SingleFragmentActivity {

    //This method will be  called in the activity that precedes it
    public static Intent newIntent (Context context, Uri photopageUri){

        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(photopageUri);
        return intent;
    }

    protected Fragment createFragment(){

        return PhotoPagefragment.newInstance(getIntent()
                .getData());
    }
}
