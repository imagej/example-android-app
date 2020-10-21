/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2015 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.android;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.scijava.AbstractGateway;
import org.scijava.Context;
import org.scijava.Gateway;
import org.scijava.android.AndroidService;
import org.scijava.app.App;
import org.scijava.app.AppService;
import org.scijava.plugin.Plugin;
import org.scijava.service.SciJavaService;

import java.util.Map;

/**
 * Main entry point into SciJava on Android.
 * 
 * @author Deborah Schmidt
 */
@Plugin(type = Gateway.class)
public class AndroidSciJavaGateway extends AbstractGateway {

	private static final String NAME = "SciJava-Android";

	/**
	 * Creates a new SciJava Android application context.
	 */
	public AndroidSciJavaGateway(Activity activity) {
		this(new Context(SciJavaService.class, AndroidService.class), activity);
	}

	/**
	 * Creates a new SciJava Android application context which wraps the given existing
	 * SciJava context.
	 * 
	 * @see Context
	 */
	public AndroidSciJavaGateway(final Context context, Activity activity) {
		super(NAME, context);

		get(AndroidService.class).setActivity(activity);


//		RecyclerView controlView = activity.findViewById(R.id.scijava_control);
//		if(controlView != null) {
//			controlView.setLayoutManager(new LinearLayoutManager(activity));
//		}

//		// make view and control list scale item to full width if only one item is present
//		setSingleItemFullWidth(activity.findViewById(R.id.scijava_view));
//		setSingleItemFullWidth(activity.findViewById(R.id.scijava_control));

		// fixes NPE caused by not implemented Class::getProtectionDomain() method on Android
		// called from AppUtils::getBaseDirectory
		Map<String, App> apps = get(AppService.class).getApps();
		apps.forEach((name, app) -> {
			System.setProperty(app.getSystemProperty(), activity.getFilesDir().getAbsolutePath());
		});
	}

	public static void setSingleItemFullWidth(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter != null) {
			if(listAdapter.getCount() == 1) {
				View onlyChild = listView.getChildAt(0);
				ViewGroup.LayoutParams params = onlyChild.getLayoutParams();
				params.width = ViewGroup.LayoutParams.MATCH_PARENT;
				onlyChild.setLayoutParams(params);
				onlyChild.requestLayout();
				listView.requestLayout();
			}
		}

	}

	@Override
	public String getShortName() {
		return "android-scijava";
	}
}
