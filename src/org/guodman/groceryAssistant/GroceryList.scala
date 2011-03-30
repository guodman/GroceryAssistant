package org.guodman.groceryAssistant

import android.view.View.OnLongClickListener
import android.widget.CheckedTextView
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.database.Cursor
import android.widget.ResourceCursorAdapter
import android.content.Context
import android.content.Intent
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

class GroceryList extends ListActivity {
	var checklist = ListBuffer[CheckBox]()
	var view: LinearLayout = null
	var adapter: ArrayAdapter[String] = null
	var lastClicked: GroceryItem = null
	
	override def onCreate(savedInstanceState: Bundle): Unit = {
		super.onCreate(savedInstanceState)
		
		var adapter = new GroceryListAdapter(this)
		setListAdapter(adapter)
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
	
	override def onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo): Unit = {
		super.onCreateContextMenu(menu, v, menuInfo)
		var inflater = getMenuInflater()
		inflater.inflate(R.menu.groceryitemmenu, menu)
		if (v.isInstanceOf[GroceryItem]) {
			lastClicked = v.asInstanceOf[GroceryItem]
		}
	}
	
	override def onContextItemSelected(item: MenuItem): Boolean = {
		var info = item.getMenuInfo().asInstanceOf[AdapterContextMenuInfo]
		if (item.getItemId == R.id.edit) {
			if (lastClicked != null) {
				var i = new Intent(this, classOf[EditItem])
				i.putExtra("name", lastClicked.name)
				startActivity(i)
			}
		}
		return super.onContextItemSelected(item);
	}
}

class GroceryListAdapter(a: Activity) extends BaseAdapter {
	var checklist = ListBuffer[CheckBox]()
	var db = databaseManager.getDB(a)
	var items = db.getGroceryList()
	var nextId = 0
	items foreach { arg =>
		var (foodid, item, aisle, isChecked) = arg
		var cb = new GroceryItem(a, item, aisle, isChecked, foodid)
		nextId += 1
		cb.setId(nextId)
		checklist += cb
	}
	
	override def getCount: Int = checklist.length
	
	override def getItem(position: Int): Object = checklist(position)
	
	override def getItemId(position: Int): Long = position
	
	override def getView(position: Int, convertView: View, parent: ViewGroup): View = checklist(position)
}

class CheckedListener (parent: Activity, foodid : Long) extends OnCheckedChangeListener {
	override def onCheckedChanged(buttonView : CompoundButton, isChecked : Boolean) {
		databaseManager.getDB.markGroceryItemObtained(foodid, isChecked)
		Toast.makeText(parent.getApplicationContext(), foodid.toString, Toast.LENGTH_SHORT).show
	}
}

class GroceryItem (parent: Activity, val name: String, aisle: String, isChecked: Boolean, foodid: Long) extends CheckBox (parent) {
	setText("(" + aisle + ") " + name)
	setChecked(isChecked)
	setFocusable(false)
	setOnCheckedChangeListener(new CheckedListener(parent, foodid))
	parent.registerForContextMenu(this)
	
	def getName = name
}
