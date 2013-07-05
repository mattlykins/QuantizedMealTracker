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

import java.util.Arrays;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
{
	public static final String TAG = "FERRET";
	public static final String FILENAME = "InternalData";
	public static final String KEY_TOTAL = "total";
	public static final String KEY_FIRSTRUN = "firstrun";
	public static final String ACTION_TEXT_CHANGED = "com.quantizedmealtracker.quantizedmealtracker.TEXT_CHANGED";
	public static final String ACTION_PLUS_HALF = "com.quantizedmealtracker.quantizedmealtracker.PLUS_HALF";
	public static final String ACTION_PLUS_ONE = "com.quantizedmealtracker.quantizedmealtracker.PLUS_ONE";
	public static final String ACTION_PLUS_TWO = "com.quantizedmealtracker.quantizedmealtracker.PLUS_TWO";

	float total;
	float[] transactionList;
	final int undosize = 10;
	SharedPreferences sp;
	int index;
	long calsperday, mealsperday, meals, calspermeal;
	boolean countdown;
	Button bAddHalfMeal, bAddOneMeal, bAddTwoMeals, bUndo, bReset;
	TextView tvTotalMeals;


	@Override
	protected void onResume()
	{
		super.onResume();
		loadPref();
		total = GetTotal();
		UpdateTextViews();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		initialize();	
		

		boolean ftc = firstTimeCheck();
		if (ftc)
		{
			// This is the first time the app has ever run.
			Intent prefintent = new Intent(this, SetPrefActivity.class);
			startActivityForResult(prefintent, 0);

			// Store a bool value to indicate that the program has run before
			sp = getSharedPreferences(FILENAME, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean(KEY_FIRSTRUN, false);
			editor.commit();

			index = 0;
			calsperday = 2000;
			mealsperday = 5;
			calspermeal = calsperday / mealsperday;
			countdown = true;
			total = 0;

			CommitTotal(total);
			UpdateTextViews();
		}
		else
		{
			loadPref();
			total = GetTotal();
		}

		UpdateTextViews();

	}

	private boolean firstTimeCheck()
	{
		Log.d(TAG, "Called firstTimeCheck()");
		sp = getSharedPreferences(FILENAME, 0);
		boolean temp = sp.getBoolean(KEY_FIRSTRUN, true);
		Log.d(TAG, "firstTimeCheck returns " + temp);

		return temp;
	}

	private void initialize()
	{
		Log.d(TAG, "Called Initialize()");

		// index = 0;
		transactionList = new float[undosize];
		bAddHalfMeal = (Button) findViewById(R.id.bAddHalfMeal);
		bAddOneMeal = (Button) findViewById(R.id.bAddOneMeal);
		bAddTwoMeals = (Button) findViewById(R.id.bAddTwoMeals);
		bUndo = (Button) findViewById(R.id.bUndo);
		bReset = (Button) findViewById(R.id.bReset);
		tvTotalMeals = (TextView) findViewById(R.id.tvTotalMeals);

		bAddHalfMeal.setOnClickListener((OnClickListener) this);
		bAddOneMeal.setOnClickListener((OnClickListener) this);
		bAddTwoMeals.setOnClickListener((OnClickListener) this);
		bUndo.setOnClickListener((OnClickListener) this);
		bReset.setOnClickListener((OnClickListener) this);

	}

	@Override
	protected void onStop()
	{
		// TODO Auto-generated method stub
		super.onStop();
		CommitTotal(total);
		
		// index = 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case (R.id.action_settings):
				Intent prefintent = new Intent(this, SetPrefActivity.class);
				startActivityForResult(prefintent, 99);
				UpdateTextViews();
				break;
			case(R.id.about):
				Intent aboutintent = new Intent(this, AboutActivity.class);
				startActivityForResult(aboutintent, 0);
				break;
			case (R.id.exit):
				finish();
				break;
		}

		return true;
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case (R.id.bAddHalfMeal):
				AddMeals((float) 0.5);
				break;
			case (R.id.bAddOneMeal):
				AddMeals(1);
				break;
			case (R.id.bAddTwoMeals):
				AddMeals(2);
				break;
			case (R.id.bUndo):
				if (index >= 1 && index < undosize)
				{
					index--;
					total += transactionList[index];
					CommitTotal(total);
					UpdateTextViews();
				}
				else if (index > (undosize - 1))
				{
					index = undosize - 1;
					total += transactionList[index];
					CommitTotal(total);
					UpdateTextViews();
				}
				else
				{
					Toast.makeText(this, "No transactions to undo", Toast.LENGTH_LONG).show();
				}
				break;
			case (R.id.bReset):
				total = 0;
				CommitTotal(total);
				UpdateTextViews();
				index = 0;
				break;
		}
	}

	private void AddMeals(float Meals)
	{
		total += Meals;
		CommitTotal(total);
		UpdateTextViews();
		TrackUndo(Meals, index, transactionList);
		index++;

	}

	private void CommitTotal(float total2)
	{
		sp = getSharedPreferences(FILENAME, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putFloat(KEY_TOTAL, (float) total2);
		editor.apply();
	}

	private float GetTotal()
	{
		Log.d(TAG, "Called GetTotal()");
		float temp = 0;
		sp = getSharedPreferences(FILENAME, 0);
		temp = sp.getFloat(KEY_TOTAL, (float) 0);
		Log.d(TAG, "sp.getFloat returned " + temp);
		return temp;
	}

	private void UpdateTextViews()
	{
		Log.d(TAG, "Called UpdateTextViews()");
		total = GetTotal();

		Intent intent = new Intent(ACTION_TEXT_CHANGED);
		if (countdown)
		{
			tvTotalMeals.setText("" + (mealsperday - total));
			// intent.putExtra("WidgetTotal", mealsperday-total);
		}
		else
		{
			tvTotalMeals.setText("" + total);
			// intent.putExtra("WidgetTotal", total);
		}

		getApplicationContext().sendBroadcast(intent);
	}

	// Keep track of the transactions so they can be undone
	private void TrackUndo(float increment, int index, float transactionList[])
	{
		Log.d(TAG, "Called TrackUndo(" + increment + ", " + index + ", " + Arrays.toString(transactionList) + ")");
		if (index < undosize)
		{
			transactionList[index] = -1 * increment;
		}
		else
		{
			for (int i = 0; i < (undosize - 1); i++)
			{
				transactionList[i] = transactionList[i + 1];
			}
			index = undosize - 1;
			transactionList[index] = -1 * increment;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{	
		//requstCode 99 is for preferences
		if( requestCode == 99 )
		{
			loadPref();
			UpdateTextViews();
			Toast.makeText(this, "Set to " + calspermeal + " calories per meal.", Toast.LENGTH_LONG).show();
			Log.d(TAG, calsperday + ":" + mealsperday + ":" + countdown);
		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
		
	}

	private void loadPref()
	{

		Log.d(TAG, "Called loadPref()");
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		String temp = "";
		temp = mySharedPreferences.getString("dayCals", "-1");
		calsperday = (long) Integer.parseInt(temp);

		temp = mySharedPreferences.getString("dayMeals", "-1");
		mealsperday = (long) Integer.parseInt(temp);

		calspermeal = calsperday / mealsperday;

		countdown = mySharedPreferences.getBoolean("CountDown", true);

		// Change button labels to match whether counting up or down
		if (!countdown)
		{
			bAddHalfMeal.setText(getString(R.string.plus_half_meal));
			bAddOneMeal.setText(getString(R.string.plus_one_meal));
			bAddTwoMeals.setText(getString(R.string.plus_two_meal));
			;
		}
		else
		{
			bAddHalfMeal.setText(getString(R.string.minus_half_meal));
			bAddOneMeal.setText(getString(R.string.minus_one_meal));
			bAddTwoMeals.setText(getString(R.string.minus_two_meal));
			;
		}

		// Call widget update
		Intent intent = new Intent(ACTION_TEXT_CHANGED);
		getApplicationContext().sendBroadcast(intent);

	}

}
