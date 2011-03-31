package org.guodman.groceryAssistant

import android.util.Log
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
	private val TAG = "GroceryList"
	
	var checklist = ListBuffer[CheckBox]()
	var view: LinearLayout = null
	var adapter: GroceryListAdapter = null
	var lastClicked: GroceryItem = null
	
	override def onCreate(savedInstanceState: Bundle): Unit = {
		super.onCreate(savedInstanceState)
		
		adapter = new GroceryListAdapter(this)
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
				startActivityForResult(i, 1)
				return true
			}
		}
		return super.onContextItemSelected(item)
	}
	
	override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
		Log.d(TAG, "onActivityResult")
		if (adapter != null) {
			adapter.refreshList
		}
	}
	
	override def onResume() {
		super.onResume()
		Log.d(TAG, "Resuming")
		if (adapter != null) {
			adapter.refreshList
		}
	}
}

class GroceryListAdapter(a: Activity) extends BaseAdapter {
	val TAG = "GroceryListAdapter"
	
	var db = databaseManager.getDB(a)
	var items = db.getGroceryList()
	
	override def getCount: Int = items.length
	
	override def getItem(position: Int): Object = items(position)
	
	override def getItemId(position: Int): Long = position
	
	override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
		var (foodid, item, aisle, isChecked) = items(position)
		if (convertView != null && convertView.isInstanceOf[GroceryItem]) {
			var gi = convertView.asInstanceOf[GroceryItem]
			gi.update(item, aisle, isChecked, foodid)
			return gi
		} else {
			return new GroceryItem(a, item, aisle, isChecked, foodid)
		}
	}
	
	def refreshList {
		Log.d(TAG, "List Refreshed");
		items = db.getGroceryList
		notifyDataSetChanged
	}
}

class CheckedListener (parent: Activity, gi : GroceryItem) extends OnCheckedChangeListener {
	override def onCheckedChanged(buttonView : CompoundButton, isChecked : Boolean) {
		databaseManager.getDB.markGroceryItemObtained(gi.foodid, isChecked)
		Toast.makeText(parent.getApplicationContext(), gi.foodid.toString, Toast.LENGTH_SHORT).show
	}
}

class GroceryItem (parent: Activity, var name: String, var aisle: String, var isFoodChecked: Boolean, var foodid: Long) extends CheckBox (parent) {
	setFocusable(false)
	setOnCheckedChangeListener(new CheckedListener(parent, this))
	parent.registerForContextMenu(this)
	update
	
	def getName = name
	
	def update(_name: String, _aisle: String, _isChecked: Boolean, _foodid: Long) {
		name = _name
		aisle = _aisle
		isFoodChecked = _isChecked
		foodid = _foodid
		update
	}
	
	def update {
		setText("(" + aisle + ") " + name)
		setChecked(isChecked)
	}
}
