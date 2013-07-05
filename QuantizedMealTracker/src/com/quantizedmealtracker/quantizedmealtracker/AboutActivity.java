/*
Copyright (c) 2013 Matt Lykins

This file is part of Quantized Meal Tracker.

Quantized Meal Tracker is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Quantized Meal Tracker is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Quantized Meal Tracker.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.quantizedmealtracker.quantizedmealtracker;

import android.os.Bundle;
import android.app.Activity;

public class AboutActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}
}
