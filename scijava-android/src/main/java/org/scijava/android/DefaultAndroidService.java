package org.scijava.android;

import android.app.Activity;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

@Plugin(type = Service.class)
public class DefaultAndroidService extends AbstractService implements AndroidService {

	private Activity activity;

	@Override
	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	@Override
	public Activity getActivity() {
		return activity;
	}
}
