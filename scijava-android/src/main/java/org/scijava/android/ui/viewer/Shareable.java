package org.scijava.android.ui.viewer;

import android.app.Activity;

/**
 * A resource which can be shared with other Android applications.
 *
 * @author Deborah Schmidt
 */
public interface Shareable {
    void share(Activity activity);
}
