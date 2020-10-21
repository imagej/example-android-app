package org.scijava.android;

import android.app.Activity;

import org.scijava.service.SciJavaService;
import org.scijava.service.Service;

public interface AndroidService extends SciJavaService {
	void setActivity(Activity activity);
	Activity getActivity();
}
