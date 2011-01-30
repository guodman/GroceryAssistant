package org.guodman.groceryAssistant

import android.widget.CompoundButton
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.CheckBox
import android.widget.ListView
import android.widget.ArrayAdapter
import android.os.Bundle
import android.app.Activity;
import android.app.ListActivity
import android.view.View
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.TextView

class GroceryList extends Activity {
	override def onCreate(savedInstanceState: Bundle): Unit = {
		super.onCreate(savedInstanceState)
		
		var view: LinearLayout = new LinearLayout(this)
		view.setOrientation(LinearLayout.VERTICAL)
		setContentView(view)
		//var items = List("Bread", "Peanut Butter", "Jelly")
		var db = new DatabaseManager(this)
		var items = db.getGroceryList()
		var checklist = List[CheckBox]()
		items.foreach { arg =>
			var (foodid, item, isChecked) = arg
			var cb = new CheckBox(this)
			cb.setText(item)
			if (isChecked) {
				cb.setChecked(true)
			}
			cb.setOnCheckedChangeListener(new CheckedListener(foodid, db))
			view.addView(cb)
		}
	}
	
	class CheckedListener (foodid : Long, db : DatabaseManager) extends OnCheckedChangeListener {
		override def onCheckedChanged(buttonView : CompoundButton, isChecked : Boolean) {
			db.markGroceryItemObtained(foodid, isChecked)
			Toast.makeText(getApplicationContext(), foodid.toString, Toast.LENGTH_SHORT).show
		}
	}
}
