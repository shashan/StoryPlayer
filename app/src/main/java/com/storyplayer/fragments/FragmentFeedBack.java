
package com.storyplayer.fragments;

import com.storyplayer.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentFeedBack extends Fragment {

	public FragmentFeedBack() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootview = inflater.inflate(R.layout.fragment_feedback, null);
		setupInitialViews(rootview);
		return rootview;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void setupInitialViews(View inflatreView) {

	}
}
