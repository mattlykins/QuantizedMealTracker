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

import android.app.Activity;
import android.os.Bundle;

public class SetPrefActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		

		//Use the android.R.id.content = no need for layout
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PrefsFragment()).commit();
	}
}
