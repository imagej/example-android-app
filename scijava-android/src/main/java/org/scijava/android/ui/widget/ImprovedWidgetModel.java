/*
 * #%L
 * SciJava Common shared library for SciJava software.
 * %%
 * Copyright (C) 2009 - 2020 SciJava developers.
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

package org.scijava.android.ui.widget;

import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.thread.ThreadService;
import org.scijava.widget.ChoiceWidget;
import org.scijava.widget.DefaultWidgetModel;
import org.scijava.widget.InputWidget;
import org.scijava.widget.ObjectWidget;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * The backing data model for a particular {@link InputWidget}.
 * 
 * @author Curtis Rueden
 * @author Deborah Schmidt
 */
public class ImprovedWidgetModel extends DefaultWidgetModel {

	private ConvertService myConvertService;
	private ThreadService myThreadService;

	private final Map<Object, Object> myConvertedObjects;

	public ImprovedWidgetModel(final Context context, final Module module, final ModuleItem<?> item, final List<?> objectPool) {
		super(context, null, module, item, objectPool);
		myConvertService = context.service(ConvertService.class);
		myThreadService = context.service(ThreadService.class);
		myConvertedObjects = new WeakHashMap<>();
	}

	@Override
	public Object getValue() {
		final Object value = getItem().getValue(getModule());

		if (isMultipleChoice()) return ensureValidChoice(value);
		if (getObjectPool().size() > 0) return ensureValidObject(value);
		return value;
	}

	@Override
	public void setValue(final Object value) {
		final String name = getItem().getName();
		if (Objects.equals(getItem().getValue(getModule()), value)) return; // no change

		// Check if a converted value is present
		Object convertedInput = myConvertedObjects.get(value);
		if (convertedInput != null &&
				Objects.equals(getItem().getValue(getModule()), convertedInput)) {
			return; // no change
		}

		// Pass the value through the convertService
		convertedInput = myConvertService.convert(value, getItem().getType());

		// If we get a different (converted) value back, cache it weakly.
		if (convertedInput != value) {
			myConvertedObjects.put(value, convertedInput);
		}

		getModule().setInput(name, convertedInput);

		if (isInitialized()) {
			myThreadService.queue(() -> {
				callback();
//				inputPanel.refresh(); // must be on AWT thread?
				getModule().preview();
			});
		}
	}

	// -- Helper methods --

	/**
	 * For multiple choice widgets, ensures the value is a valid choice.
	 *
	 * @see #getChoices()
	 * @see ChoiceWidget
	 */
	private Object ensureValidChoice(final Object value) {
		return ensureValid(value, Arrays.asList(getChoices()));
	}

	/**
	 * For object widgets, ensures the value is a valid object.
	 *
	 * @see #getObjectPool()
	 * @see ObjectWidget
	 */
	private Object ensureValidObject(final Object value) {
		return ensureValid(value, getObjectPool());
	}

	/**
	 * Ensures the value is on the given list.
	 */
	private Object ensureValid(final Object value, final List<?> list) {
		for (final Object o : list) {
			if (o.equals(value)) return value; // value is valid
			// check if value was converted and cached
			final Object convertedValue = myConvertedObjects.get(o);
			if (value.equals(convertedValue)) {
				return convertedValue;
			}
		}

		// value is not valid; override with the first item on the list instead
		final Object validValue = list.get(0);
		// CTR TODO: Mutating the model in a getter is dirty. Find a better way?
		setValue(validValue);
		return validValue;
	}
}
