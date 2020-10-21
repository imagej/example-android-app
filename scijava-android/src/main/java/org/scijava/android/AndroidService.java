package org.scijava.android;

import android.app.Activity;

import org.scijava.service.SciJavaService;

public interface AndroidService extends SciJavaService {
	void setActivity(Activity activity);
	Activity getActivity();
}
