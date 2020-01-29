package com.example.flickerphotocopy;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoPagefragment extends VisibleFragment {

    private ProgressBar mProgressBar;

    private static final String ARG_URI = "photo_page_url";

    private Uri mUri;
    private WebView mWebView;

    //This method returns an instance of the fragment in the Activity
    public static PhotoPagefragment newInstance (Uri uri){

        Bundle args = new Bundle();
        args.putParcelable(ARG_URI,uri);
        PhotoPagefragment fragment = new PhotoPagefragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUri = getArguments().getParcelable(ARG_URI);
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_photo_pagefragment, container, false);

       mProgressBar = view.findViewById(R.id.contentLoadingProgressBar);
       mProgressBar.setMax(100);
       mWebView = view.findViewById(R.id.webView);
       mWebView.getSettings().setJavaScriptEnabled(true);
       mWebView.setWebViewClient(new WebViewClient());
       mWebView.setWebChromeClient(new WebChromeClient(){
           public void onProgressChanged(WebView webView,int newProgress){
               if(newProgress == 100){
                   mProgressBar.setVisibility(View.GONE);
               }else{
                   mProgressBar.setVisibility(View.VISIBLE);
                   mProgressBar.setProgress(newProgress);
               }
           }

           public void onReceivedTitle(WebView webView,String title){
               AppCompatActivity activity =(AppCompatActivity) getActivity();
               activity.getSupportActionBar().setSubtitle(title);
           }
       });
       mWebView.loadUrl(mUri.toString());
       return view;
    }

}
