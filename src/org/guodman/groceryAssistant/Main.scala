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
		Toaster.context = getApplicationContext
		super.onCreate(savedInstanceState)
		var menu: LinearLayout = new LinearLayout(this)
		menu.setOrientation(LinearLayout.VERTICAL)
		setContentView(menu)
		var grocList: Button = new Button(this)
		grocList.setText("Grocery List")
		grocList.setOnClickListener(new ViewSwitcher(this, classOf[GroceryList]))
		menu.addView(grocList)
		var itemList: Button = new Button(this)
		itemList.setText("Item List")
		itemList.setOnClickListener(new ViewSwitcher(this, classOf[FoodList]))
		menu.addView(itemList)
		var createItem: Button = new Button(this)
		createItem.setText("Create Item")
		createItem.setOnClickListener(new ViewSwitcher(this, classOf[CreateItem]))
		menu.addView(createItem)
	}

	override def onCreateOptionsMenu(menu: Menu): Boolean = {
		var inflater = getMenuInflater()
		inflater.inflate(R.menu.mainmenu, menu)
		return true
	}
}

class ViewSwitcher (context : Context, cls : Class[_]) extends OnClickListener {
	override def onClick(v: View): Unit = {
		context.startActivity(new Intent(context, cls))
	}
}
