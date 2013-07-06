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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class QMTWidgetProvider extends AppWidgetProvider
{
	SharedPreferences sp;
	float total;
	long calsperday, mealsperday, meals, calspermeal;
	boolean countdown;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		ComponentName thisWidget = new ComponentName(context, QMTWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds)
		{
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			loadPref(context);			
			total = GetTotal(context);			
			
			//Change button labels and total to match whether counting up or down
			if( !countdown )
			{
				rv.setTextViewText(R.id.bAddHalfMealWidget, context.getString(R.string.plus_half));
				rv.setTextViewText(R.id.bAddOneMealWidget, context.getString(R.string.plus_one));
				rv.setTextViewText(R.id.bAddTwoMealsWidget, context.getString(R.string.plus_two));
				rv.setTextViewText(R.id.tvTotalMealsWidget, String.valueOf(total));
			}
			else
			{
				rv.setTextViewText(R.id.bAddHalfMealWidget, context.getString(R.string.minus_half));
				rv.setTextViewText(R.id.bAddOneMealWidget, context.getString(R.string.minus_one));
				rv.setTextViewText(R.id.bAddTwoMealsWidget, context.getString(R.string.minus_two));
				rv.setTextViewText(R.id.tvTotalMealsWidget, String.valueOf(mealsperday-total));
			}
			
			Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            rv.setOnClickPendingIntent(R.id.tvTotalMealsWidget, pendingIntent);


			Intent active = new Intent(context, QMTWidgetProvider.class);
			active.setAction(MainActivity.ACTION_PLUS_HALF);
			PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
			rv.setOnClickPendingIntent(R.id.bAddHalfMealWidget, actionPendingIntent);

			active = new Intent(context, QMTWidgetProvider.class);
			active.setAction(MainActivity.ACTION_PLUS_ONE);
			actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
			rv.setOnClickPendingIntent(R.id.bAddOneMealWidget, actionPendingIntent);

			active = new Intent(context, QMTWidgetProvider.class);
			active.setAction(MainActivity.ACTION_PLUS_TWO);
			actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
			rv.setOnClickPendingIntent(R.id.bAddTwoMealsWidget, actionPendingIntent);		
			

			appWidgetManager.updateAppWidget(widgetId, rv);

		}
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO Auto-generated method stub
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), QMTWidgetProvider.class.getName());
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

		if (intent.getAction().equals(MainActivity.ACTION_TEXT_CHANGED))
		{			
			onUpdate(context, appWidgetManager, appWidgetIds);
		}
		else if (intent.getAction().equals(MainActivity.ACTION_PLUS_HALF))
		{
			AddMeals((float) 0.5, context);
			onUpdate(context, appWidgetManager, appWidgetIds);
		}
		else if (intent.getAction().equals(MainActivity.ACTION_PLUS_ONE))
		{
			AddMeals(1, context);
			onUpdate(context, appWidgetManager, appWidgetIds);
		}
		else if (intent.getAction().equals(MainActivity.ACTION_PLUS_TWO))
		{
			AddMeals(2, context);
			onUpdate(context, appWidgetManager, appWidgetIds);
		}

		super.onReceive(context, intent);
	}

	private void AddMeals(float Meals, Context context)
	{
		total = GetTotal(context);
		total += Meals;
		CommitTotal(total, context);		
	}

	private void CommitTotal(float total2, Context context)
	{
		sp = context.getSharedPreferences(MainActivity.FILENAME, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putFloat(MainActivity.KEY_TOTAL, (float) total2);
		editor.apply();
	}

	private float GetTotal(Context context)
	{
		Log.d(MainActivity.TAG, "Called GetTotal()");
		float temp = 0;
		sp = context.getSharedPreferences(MainActivity.FILENAME, 0);
		temp = sp.getFloat(MainActivity.KEY_TOTAL, (float) 0);
		Log.d(MainActivity.TAG, "sp.getFloat returned " + temp);
		return temp;
	}
	private void loadPref(Context context)
	{

		Log.d(MainActivity.TAG, "Called loadPref()");
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		String temp = "";
		temp = mySharedPreferences.getString("dayCals", "-1");
		calsperday = (long)Integer.parseInt(temp);		
		
		temp = mySharedPreferences.getString("dayMeals", "-1");
		mealsperday = (long)Integer.parseInt(temp);
		
		calspermeal = calsperday/mealsperday;
		
		countdown = mySharedPreferences.getBoolean("CountDown", true);	
	}

}
