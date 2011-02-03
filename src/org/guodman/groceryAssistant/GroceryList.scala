package org.guodman.groceryAssistant

import android.view.MenuItem
import android.widget.AdapterView.AdapterContextMenuInfo
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
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
		var db = databaseManager.getDB(this)
		var items = db.getGroceryList()
		var checklist = List[CheckBox]()
		items.foreach { arg =>
			var (foodid, item, isChecked) = arg
			var cb = new GroceryItem(this, item, isChecked, foodid)
			checklist = cb :: checklist
			view.addView(cb)
		}
	}
}

class CheckedListener (parent: Activity, foodid : Long) extends OnCheckedChangeListener {
	override def onCheckedChanged(buttonView : CompoundButton, isChecked : Boolean) {
		databaseManager.getDB.markGroceryItemObtained(foodid, isChecked)
		Toast.makeText(parent.getApplicationContext(), foodid.toString, Toast.LENGTH_SHORT).show
	}
}

class GroceryItem (parent: Activity, val name: String, isChecked: Boolean, foodid: Long) extends CheckBox (parent) with View.OnLongClickListener {
	setText(name)
	setChecked(isChecked)
	setOnCheckedChangeListener(new CheckedListener(parent, foodid))
	//parent.registerForContextMenu(this)
	
	def getName = name
	
	setOnLongClickListener(this)
	
	override def onLongClick(v: View): Boolean = {
		databaseManager.getDB.removeFromGroceryList(name)
		setVisibility(View.GONE)
		return true
	}
}
