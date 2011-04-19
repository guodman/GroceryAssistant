package org.guodman.groceryAssistant

import android.widget.Toast
import android.content.Context

object Toaster {
	def doToast(context: Context, s: String) : Unit = {
		if (context != null) {
			Toast.makeText(context, s, Toast.LENGTH_SHORT).show
		}
	}
	
	def doToast(context: Context, c: CharSequence) : Unit = {
		if (context != null) {
			Toast.makeText(context, c, Toast.LENGTH_SHORT).show
		}
	}
}
