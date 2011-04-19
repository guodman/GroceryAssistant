package org.guodman.groceryAssistant

import org.guodman.groceryAssistant.databaseManager.DatabaseManager
import java.io.File
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
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
		
		var backup = new Button(this)
		backup.setText("Backup Database")
		menu.addView(backup)
		backup.setOnClickListener(new OnClickListener() {
			override def onClick(v: View) {
				var f = new File(databaseManager.usageDatabaseFile)
				if (f.exists) {
					databaseManager.copyDataBase(databaseManager.usageDatabaseFile, databaseManager.backupDatabaseFile)
					Toaster.doToast(Main.this, "Backup Complete")
				} else {
					Toaster.doToast(Main.this, "There is no database to back up")
				}
			}
		})
		
		var restore = new Button(this)
		restore.setText("Restore Database")
		menu.addView(restore)
		restore.setOnClickListener(new OnClickListener() {
			override def onClick(v: View) {
				var f = new File(databaseManager.backupDatabaseFile)
				if (f.exists) {
					databaseManager.copyDataBase(databaseManager.backupDatabaseFile, databaseManager.usageDatabaseFile)
					Toaster.doToast(Main.this, "Restore Complete")
				} else {
					Toaster.doToast(Main.this, "There is no database to restore")
				}
			}
		})
	}
}

class ViewSwitcher (context : Context, cls : Class[_]) extends OnClickListener {
	override def onClick(v: View): Unit = {
		context.startActivity(new Intent(context, cls))
	}
}
