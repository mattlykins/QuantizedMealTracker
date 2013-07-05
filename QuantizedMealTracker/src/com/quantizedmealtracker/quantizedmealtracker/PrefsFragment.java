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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.util.Log;



public class PrefsFragment extends PreferenceFragment implements OnPreferenceChangeListener
{

	EditTextPreference dayCals, dayMeals;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);		
		dayCals = (EditTextPreference)findPreference("dayCals");
		dayMeals = (EditTextPreference)findPreference("dayMeals");
		
		dayCals.setOnPreferenceChangeListener(this);
		dayMeals.setOnPreferenceChangeListener(this);
		
		dayCals.setSummary("Daily Calorie Intake = " + dayCals.getText());
		dayMeals.setSummary("Meals Per Day = " + dayMeals.getText());
	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1)
	{
		Log.d(MainActivity.TAG,"onSharedPreferenceChanged() called. arg0 = " + arg0 + " arg1 = " + arg1);
		long longarg1 = Long.parseLong(arg1.toString());
		boolean lgDayCals = true;
		boolean lgDayMeals = true;
		String errorMessage = "";
		String errorTitle = "";
		
		if( arg0.getKey().contentEquals("dayCals") )
		{
			if( longarg1 < 1000 )
			{				
				Log.d(MainActivity.TAG,"onSharedPreferenceChanged() is returning false. dayCals = " + longarg1);
				lgDayCals = false;	
				errorMessage = "Enter a value greater than 1000.";
				errorTitle = "Invalid number of calories per day";
			}
		}
		else if( arg0.getKey().contentEquals("dayMeals"))
		{
			if( longarg1 < 1)
			{
				Log.d(MainActivity.TAG,"onSharedPreferenceChanged() is returning false. dayMeals = " + longarg1);
				lgDayMeals = false;
				errorMessage = "Enter a value greater than 0.";
				errorTitle = "Invalid number of meals per day";
			}
		}
		
		if( !lgDayCals || !lgDayMeals )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(errorMessage).setTitle(errorTitle);
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
			{
		           public void onClick(DialogInterface dialog, int id)
		           {
		               
		           }
			});			
			AlertDialog dialog = builder.create();
			dialog.show();	
		}
		else
		{
			if( arg0.getKey().contentEquals("dayCals") )
			{
				dayCals.setSummary("Daily Calorie Intake = " + longarg1);
			}
			else if( arg0.getKey().contentEquals("dayMeals"))
			{
				dayMeals.setSummary("Meals Per Day = " + longarg1);
			}
		}
		
		return (lgDayCals && lgDayMeals);
	}

}
