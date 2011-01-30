package org.guodman.groceryAssistant

import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.ArrayAdapter
import android.os.Bundle
import android.app.ListActivity
import android.view.View
import android.widget.Toast
import android.widget.TextView

class GroceryList extends ListActivity {
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    //var items = List("Bread", "Peanut Butter", "Jelly")
    var db = new DatabaseManager(this)
    var items = db.getGroceryList()
    var i: java.util.List[String] = java.util.Arrays.asList(items.toArray: _*)
    setListAdapter(new ArrayAdapter[String](this, R.layout.list_item, i))
    var lv: ListView = getListView()
    lv.setTextFilterEnabled(true)
    lv.setOnItemClickListener(new oicl())
    lv.setItemChecked(1, true)
  }

  class oicl extends OnItemClickListener {
    def onItemClick(parent: AdapterView[_], view: View,
                    position: Int, id: Long): Unit = {
      // When clicked, show a toast with the TextView text
      Toast.makeText(getApplicationContext(), view.asInstanceOf[TextView].getText(), Toast.LENGTH_SHORT).show();
    }
  }
}
