package org.guodman.groceryAssistant

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import android.view.MenuInflater
import android.view.Menu
import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout
import android.widget.Button

class Main extends Activity {
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    var menu: LinearLayout = new LinearLayout(this)
    menu.setOrientation(LinearLayout.VERTICAL)
    setContentView(menu)
    var grocList: Button = new Button(this)
    grocList.setText("Grocery List")
    menu.addView(grocList)
    var itemList: Button = new Button(this)
    itemList.setText("Item List")
    itemList.setOnClickListener(new GroceryView(this))
    menu.addView(itemList)
    var createItem: Button = new Button(this)
    createItem.setText("Create Item")
    menu.addView(createItem)
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    var inflater = getMenuInflater()
    inflater.inflate(R.menu.mainmenu, menu)
    return true
  }

  class GroceryView (c : Context) extends OnClickListener {
    override def onClick(v: View): Unit = {
      startActivity(new Intent(c, classOf[AvailableGroceries]))
    }
  }
}
