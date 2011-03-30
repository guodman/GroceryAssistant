package org.guodman.groceryAssistant

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.LinearLayout
import android.os.Bundle
import android.app.Activity

class EditItem extends Activity {
	var foodid: Int = 0
	
	override def onCreate(savedInstanceState: Bundle): Unit = {
	    super.onCreate(savedInstanceState)
	    val i = getIntent
	    var foodName = "NO FOOD NAME"
	    if (i != null && i.getStringExtra("name") != null) {
	    	foodName = i.getStringExtra("name")
	    } else {
	    	throw new NullPointerException("No name passed in on the extras")
	    }
	    
	    foodid = databaseManager.db.getFoodId(foodName)
	    
	    var l: LinearLayout = new LinearLayout(this)
	    l.setOrientation(LinearLayout.VERTICAL)
	    setContentView(l)
	    
	    var title : TextView = new TextView(this)
	    title.setText("Edit Food " + foodName)
	    l.addView(title)
	    
	    var name : EditText = new EditText(this)
	    name.setText(foodName)
	    l.addView(name)
	    
	    var aisle : EditText = new EditText(this)
	    aisle.setText(databaseManager.db.getAisle(foodName))
	    l.addView(aisle)
	    
	    var addChild: Button = new Button(this)
	    addChild.setText("Add Component")
	    addChild.setOnClickListener(new OpenAddChildActivity(this))
	    
	    var save: Button = new Button(this)
	    save.setText("Save")
	    save.setOnClickListener(new SaveItem(name, aisle))
	    l.addView(save)
	}

	class SaveItem (name : EditText, aisle : EditText) extends OnClickListener {
		override def onClick(v: View): Unit = {
			var db = databaseManager.getDB
			db.editFood(foodid, name.getText.toString, aisle.getText.toString)
			finish
		}
	}
	
	class OpenAddChildActivity(context: Context) extends OnClickListener {
		override def onClick(v: View): Unit = {
			startActivity(new Intent(context, classOf[FoodList]))
		}
	}
}
