package org.guodman.groceryAssistant

import android.util.Log
import android.database.sqlite.SQLiteConstraintException
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.Context
class DatabaseManager (context : Context){
	val DATABASE_NAME : String= "example.db"
	val DATABASE_VERSION : Int = 1

	var openHelper = new OpenHelper(context, DATABASE_VERSION)
	var db : SQLiteDatabase = openHelper.getWritableDatabase()
	
	def getAllFood() : List[String] = {
		var lst : List[String] = Nil
		var cursor : Cursor = db.query("foods", Array[String]("name"), null, null, null, null, "name desc")
		if (cursor.moveToFirst()) {
			do {
				lst = cursor.getString(0) :: lst
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return lst;
	}
	
	def getFoodById(id : Int) : String = {
		var cursor = db.rawQuery("SELECT * FROM foods WHERE (id=?)", Array[String](id.toString))
		if (cursor != null && !cursor.isClosed()) {
			cursor.close()
		}
		return "TODO"
	}
	
	def getFoodByTags(tagid : Array[Int]) : Unit = {
		
	}
	
	def getChildren(foodid : Int) : Unit = {
		
	}
	
	def getGroceryList() : List[(Long, String, Boolean)] = {
		var lst : List[(Long, String, Boolean)] = Nil
		var cursor = db.rawQuery("SELECT foods.id, foods.name, groceryList.checked FROM foods INNER JOIN groceryList ON id = foodid", Array[String]())
		//var cursor : Cursor = db.query("groceryList", Array[String]("name"), null, null, null, null, "name desc")
		if (cursor.moveToFirst()) {
			do {
				val status : Boolean = cursor.getLong(2) == 1
				lst = (cursor.getLong(0), cursor.getString(1), status) :: lst
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close()
		}
		return lst
	}
	
	def addToGroceryList(foodid : Int) : Unit = {
		
	}
	
	def addToGroceryList(foodName : String) : Unit = {
		var cursor = db.rawQuery("SELECT id FROM foods WHERE name=?", Array[String](foodName))
		if (cursor.moveToFirst) {
			var foodid = cursor.getInt(0)
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			var insertStmt = db.compileStatement("INSERT INTO groceryList (foodid, checked) values (?, ?)")
			insertStmt.bindLong(1, foodid)
			insertStmt.bindLong(2, 0)
			try {
				insertStmt.executeInsert()
			} catch {
				case e:SQLiteConstraintException => Log.i("GroceryAssistant", e.toString)
			}
			insertStmt.close
		}
	}
	
	def markGroceryItemObtained(foodid : Long, status : Boolean) : Unit = {
		val insertFood = "UPDATE groceryList SET checked=? WHERE foodid=?"
		var insertStmt = db.compileStatement(insertFood)
		if (status) {
			insertStmt.bindLong(1, 1)
		} else {
			insertStmt.bindLong(1, 0)
		}
		insertStmt.bindLong(2, foodid)
		insertStmt.executeInsert()
		insertStmt.close
	}
	
	def createFood(name : String, aisle : String) : Unit = {
		val insertFood = "INSERT INTO foods (name, aisle) values (?, ?)"
		var insertStmt = db.compileStatement(insertFood)
		insertStmt.bindString(1, name)
		insertStmt.bindString(2, aisle)
		insertStmt.executeInsert()
		insertStmt.close
	}
	
	def createTag(name : String) : Unit = {
		
	}
	
	def addTagToFood(tagid : Int, foodid : Int) : Unit = {
		
	}
	
	def addChildToFood(parentid : Int, childid : Int) : Unit = {
		
	}
}

class OpenHelper (context : Context, version : Int) extends SQLiteOpenHelper (context, "GroceryAssistant", null, version) {
	override def onCreate(db : SQLiteDatabase) : Unit = {
		db.execSQL("CREATE TABLE foods (id INTEGER PRIMARY KEY, name TEXT, aisle TEXT)")
		db.execSQL("CREATE UNIQUE INDEX foodsKey ON foods (name)")
		db.execSQL("CREATE TABLE tags (id INTEGER PRIMARE KEY, name TEXT)")
		db.execSQL("CREATE UNIQUE INDEX tagsKey ON tags (name)")
		db.execSQL("CREATE TABLE foodRelations (parentid INTEGER, childid INTEGER)")
		db.execSQL("CREATE UNIQUE INDEX foodRelationKey ON foodRelations (parentid, childid)")
		db.execSQL("CREATE TABLE tagAssignments (foodid INTEGER, tagid INTEGER)")
		db.execSQL("CREATE UNIQUE INDEX tagAssignmentsKey ON tagAssignments (foodid, tagid)")
		db.execSQL("CREATE TABLE groceryList (foodid INTEGER PRIMARY KEY, checked INTEGER)")
	}

	override def onUpgrade(db : SQLiteDatabase, oldVersion : Int, newVersion : Int) : Unit = {
		//Log.w("Example", "Upgrading database, this will drop tables and recreate.");
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}