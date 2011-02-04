package org.guodman.groceryAssistant

import android.widget.AdapterView.AdapterContextMenuInfo
import android.view.MenuItem
import android.view.ContextMenu.ContextMenuInfo
import android.view.ContextMenu
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.ArrayAdapter
import android.os.Bundle
import android.app.ListActivity
import android.view.View
import android.widget.Toast
import android.widget.TextView

class FoodList extends ListActivity {
	var adapter: ArrayAdapter[String] = null
	
	override def onCreate(savedInstanceState: Bundle): Unit = {
		super.onCreate(savedInstanceState)
		
		var db = databaseManager.getDB(this)
		val items = db.getAllFood
		val i: java.util.List[String] = java.util.Arrays.asList(items.toArray: _*)
		adapter = new ArrayAdapter[String](this, R.layout.list_item, i)
		setListAdapter(adapter)
		val lv: ListView = getListView()
		lv.setTextFilterEnabled(true)
		lv.setOnItemClickListener(new oicl())
		this.registerForContextMenu(lv)
	}
	
	override def onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo): Unit = {
		super.onCreateContextMenu(menu, v, menuInfo)
		var inflater = getMenuInflater()
		inflater.inflate(R.menu.foodmenu, menu)
	}
	
	override def onContextItemSelected(item: MenuItem): Boolean = {
		var info = item.getMenuInfo().asInstanceOf[AdapterContextMenuInfo]
		if (item.getItemId() == R.id.addtolist) {
			var groceryItem = info.id
			var name = adapter.getItem(info.id.toInt)
			Toast.makeText(getApplicationContext(), "Added "  + name, Toast.LENGTH_SHORT).show()
			databaseManager.getDB.addToGroceryList(name)
			//groceryItem.setVisibility(View.GONE)
			return true
		}
		return super.onContextItemSelected(item);
	}
}

class oicl extends OnItemClickListener {
	def onItemClick(parent: AdapterView[_], view: View,
                   position: Int, id: Long): Unit = {
		// When clicked, show a toast with the TextView text
		Toaster.doToast(view.asInstanceOf[TextView].getText)
		databaseManager.getDB.addToGroceryList(view.asInstanceOf[TextView].getText.asInstanceOf[String])
	}
}
