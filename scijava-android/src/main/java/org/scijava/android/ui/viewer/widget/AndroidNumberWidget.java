/*
 * #%L
 * SciJava UI components for Java Swing.
 * %%
 * Copyright (C) 2010 - 2017 Board of Regents of the University of
 * Wisconsin-Madison.
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

package org.scijava.android.ui.viewer.widget;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.slider.Slider;

import org.scijava.android.AndroidService;
import org.scijava.android.ui.viewer.recyclable.LabeledViewHolder;
import org.scijava.log.LogService;
import org.scijava.module.ModuleService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.thread.ThreadService;
import org.scijava.widget.InputWidget;
import org.scijava.widget.NumberWidget;
import org.scijava.widget.WidgetModel;

/**
 * Android implementation of number chooser widget.
 *
 * @author Curtis Rueden
 * @author Deborah Schmidt
 */
@Plugin(type = InputWidget.class)
public class AndroidNumberWidget extends AndroidInputWidget<Number, Slider> implements
		NumberWidget<View>, Slider.OnChangeListener, Slider.OnSliderTouchListener {

	@Parameter
	private ThreadService threadService;

	@Parameter
	private ModuleService moduleService;

	@Parameter
	private LogService log;

	@Parameter
	private AndroidService androidService;

	private Number value;

	@Override
	public Slider createView(ViewGroup parent) {
		Slider slider = new Slider(androidService.getActivity());
		slider.setOnTouchListener((v, event) -> {
			int action = event.getAction();
			switch (action)
			{
				case MotionEvent.ACTION_DOWN:
					// Disallow ScrollView to intercept touch events.
					v.getParent().requestDisallowInterceptTouchEvent(true);
					break;

				case MotionEvent.ACTION_UP:
					// Allow ScrollView to intercept touch events.
					v.getParent().requestDisallowInterceptTouchEvent(false);
					break;
			}

			// Handle Seekbar touch events.
			v.onTouchEvent(event);
			return false;
		});
		return slider;
	}

	@Override
	public Class<Slider> getViewType() {
		return Slider.class;
	}

	@Override
	public void attach(LabeledViewHolder<Slider> holder) {
		super.attach(holder);
		Slider slider = holder.getItem();
		slider.addOnChangeListener(this);
		slider.addOnSliderTouchListener(this);
	}

	@Override
	public void detach(LabeledViewHolder<Slider> holder) {
		Slider slider = holder.getItem();
		slider.removeOnChangeListener(this);
		slider.removeOnSliderTouchListener(this);
		super.detach(holder);
	}

	@Override
	public void contentUpdated() {

	}

	@Override
	public void updateView(Slider slider) {
		final Number min = get().getMin();
		final Number max = get().getMax();
		final Number softMin = get().getSoftMin();
		final Number softMax = get().getSoftMax();
		final Number stepSize = get().getStepSize();

		if (min == null || max == null || stepSize == null) {
			log.warn("Invalid min/max/step; cannot render slider");
			return;
		}
		int sMin = 0;
		int sMax = (int) ((max.doubleValue() - min.doubleValue()) / stepSize.doubleValue());
		long range = sMax - sMin;
		if (range > Integer.MAX_VALUE) {
			log.warn("Slider span too large; max - min < 2^31 required.");
			return;
		}
		final Number value = (Number) get().getValue();
		if(min.intValue() >= 0) {
			slider.setValueFrom(min.floatValue());
		}
		if(max.intValue() >= 0) {
			slider.setValueTo(max.floatValue());
		}
		slider.setValue(value.floatValue());
	}

	// -- InputWidget methods --

	@Override
	public Number getValue() {
		return value;
	}

	// -- WrapperPlugin methods --

	@Override
	public void set(final WidgetModel model) {
		super.set(model);
	}

	// -- Typed methods --

	@Override
	public boolean supports(final WidgetModel model) {
		return model.isNumber();
	}

	@Override
	public void doRefresh() {
		// not happening
	}

	@Override
	public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
		// only update model from user action on stop tracking touch, not on every value change
		if(!fromUser) {
			this.value = value;
			threadService.run(this::updateModel);
		}
	}

	@Override
	public void onStartTrackingTouch(@NonNull Slider slider) {
	}

	@Override
	public void onStopTrackingTouch(@NonNull Slider slider) {
		this.value = slider.getValue();
		threadService.run(this::updateModel);
	}

}
