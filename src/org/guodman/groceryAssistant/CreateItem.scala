package org.guodman.groceryAssistant

import android.widget.Toast
import android.widget.EditText
import android.content.Intent
import android.view.View
import android.widget.TextView
import android.view.View.OnClickListener
import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import android.os.Bundle
import android.app.Activity

class CreateItem extends Activity {
	override def onCreate(savedInstanceState: Bundle): Unit = {
	    super.onCreate(savedInstanceState)
	    var l: LinearLayout = new LinearLayout(this)
	    l.setOrientation(LinearLayout.VERTICAL)
	    setContentView(l)
	    
	    var title : TextView = new TextView(this)
	    title.setText("Add a food")
	    l.addView(title)
	    
	    var name : EditText = new EditText(this)
	    l.addView(name)
	    
	    var aisle : EditText = new EditText(this)
	    l.addView(aisle)
	    
	    var save: Button = new Button(this)
	    save.setText("Save")
	    save.setOnClickListener(new SaveItem(this,
	    		classOf[Main], name,
	    		aisle))
	    l.addView(save)
	}

	class SaveItem (context : Context, cls : Class[_], name : EditText, aisle : EditText) extends OnClickListener {
		override def onClick(v: View): Unit = {
			var db = databaseManager.getDB(context)
			db.createFood(name.getText.toString, aisle.getText.toString)
			//Toast.makeText(getApplicationContext(), name.getText.toString + " - " + aisle.getText.toString, Toast.LENGTH_SHORT).show();
			//context.startActivity(new Intent(context, cls))
			finish
		}
	}
}
