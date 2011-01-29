package org.guodman.groceryAssistant

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
    var createItem: Button = new Button(this)
    createItem.setText("Create Item")
    menu.addView(createItem)
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    var inflater = getMenuInflater()
    inflater.inflate(R.menu.mainmenu, menu)
    return true
  }
}
