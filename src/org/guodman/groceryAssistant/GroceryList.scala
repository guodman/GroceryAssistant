package org.guodman.groceryAssistant

import android.widget.ScrollView
import scala.collection.mutable.ListBuffer
import android.view.MenuItem
import android.widget.AdapterView.AdapterContextMenuInfo
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.MenuInflater
import android.view.Menu
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
	var checklist = ListBuffer[CheckBox]()
	var view: LinearLayout = null
	
	override def onCreate(savedInstanceState: Bundle): Unit = {
		super.onCreate(savedInstanceState)
		
		var sv: ScrollView = new ScrollView(this)
		setContentView(sv)
		view = new LinearLayout(this)
		view.setOrientation(LinearLayout.VERTICAL)
		sv.addView(view)
		var db = databaseManager.getDB(this)
		var items = db.getGroceryList()
		items foreach { arg =>
			var (foodid, item, isChecked) = arg
			var cb = new GroceryItem(this, item, isChecked, foodid)
			checklist += cb
			view.addView(cb)
		}
	}

	override def onCreateOptionsMenu(menu: Menu): Boolean = {
		var inflater = getMenuInflater()
		inflater.inflate(R.menu.grocerymenu, menu)
		return true
	}
	
	override def onOptionsItemSelected(item: MenuItem): Boolean = {
		if (item.getItemId == R.id.clearchecked) {
			checklist foreach { arg =>
				if (arg.isChecked) {
					view.removeView(arg)
					checklist -= arg
				}
			}
			databaseManager.db.removeCheckedItems
			return true
		}
		return false
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
