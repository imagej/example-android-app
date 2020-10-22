package org.scijava.android.ui.viewer;

import android.view.View;

import org.scijava.ui.viewer.DisplayPanel;

/**
 * DisplayPanel::getDisplay clashes with Views::getDisplay - therefore android display panels cannot easily extend a View.
 * This interface makes it possible for the AndroidDisplayWindow to get the View from the display panels.
 *
 * @author Deborah Schmidt
 */
public interface AndroidDisplayPanel extends DisplayPanel {
    View getPanel();
}
