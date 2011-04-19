package org.guodman.groceryAssistant

import android.os.Environment
import java.io.IOException
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import android.util.Log
import android.database.sqlite.SQLiteConstraintException
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.Context

object databaseManager {
	var _db: DatabaseManager = null
	
	def getDB(context: Context): DatabaseManager = {
		if (_db == null) {
			_db = new DatabaseManager(context)
		}
		return _db
	}
	
	def getDB(): DatabaseManager = {
		if (_db == null) {
			throw new NullPointerException("The DatabaseManager has not yet been initialized.")
		}
		return _db
	}
	
	val usageDatabaseFile = "/data/data/org.guodman.groceryAssistant/databases/GroceryAssistant"
	val backupDatabaseFile = Environment.getExternalStorageDirectory() + "/groceryAssistant.db"
	
	// usage location: "/data/data/org.guodman.groceryAssistant/databases/GroceryAssistant"
	// sdcard location: "/mnt/sdcard/groceryAssistant.db"
	def copyDataBase(source: String, destination: String) {
		// Open the local db as the input stream
		var myInput: InputStream = null
		try {
			//myInput = c.getAssets().open(DB_NAME);
			myInput = new FileInputStream(source)

			// Open the empty db as the output stream
			var myOutput: OutputStream = new FileOutputStream(destination)

			// transfer bytes from the inputfile to the outputfile
			var buffer: Array[Byte] = new Array[Byte](1024)
			var length: Int = myInput.read(buffer)
			while (length > 0) {
				myOutput.write(buffer, 0, length)
				length = myInput.read(buffer)
			}
			// Close the streams
			myOutput.flush()
			myOutput.close()
			myInput.close()
		} catch {
			// TODO Auto-generated catch block
			case e: IOException => e.printStackTrace()
		}
	}
	
	def db = getDB

	class DatabaseManager (context: Context) {
		val DATABASE_VERSION : Int = 1
		
		var openHelper = new OpenHelper(context, DATABASE_VERSION)
		var db : SQLiteDatabase = openHelper.getWritableDatabase()
		
		def getAllFood() : List[String] = {
			var lst: List[String] = Nil
			var cursor: Cursor = db.query("foods", Array[String]("name"), null, null, null, null, "name desc")
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
		
		def getFoodById(id: Int) : String = {
			var cursor = db.rawQuery("SELECT * FROM foods WHERE (id=?)", Array[String](id.toString))
			if (cursor != null && !cursor.isClosed()) {
				cursor.close()
			}
			return "TODO"
		}
	
		def getFoodByTags(tagid: Array[Int]) : Unit = {
		
		}
	
		def getChildren(foodid: Int) : Unit = {
		
		}
		
		def getAisle(foodName: String): String = {
			var cursor = db.rawQuery("SELECT aisle FROM foods WHERE name=?", Array[String](foodName))
			if (cursor.moveToFirst) {
				var aisle = cursor.getString(0)
				if (cursor != null && !cursor.isClosed()) {
					cursor.close()
				}
				return aisle
			} else {
				return 0
			}
		}
	
		def getGroceryList(): List[(Long, String, String, Boolean)] = {
			var lst: List[(Long, String, String, Boolean)] = Nil
			var cursor = db.rawQuery("SELECT foods.id, foods.name, foods.aisle, groceryList.checked FROM foods INNER JOIN groceryList ON id = foodid ORDER BY foods.aisle DESC", Array[String]())
			if (cursor.moveToFirst()) {
				do {
					val status: Boolean = cursor.getLong(2) == 1
					lst = (cursor.getLong(0), cursor.getString(1), cursor.getString(2), status) :: lst
				} while (cursor.moveToNext());
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close()
			}
			return lst
		}
	
		def addToGroceryList(foodName: String) : Unit = {
			var cursor = db.rawQuery("SELECT id FROM foods WHERE name=?", Array[String](foodName))
			if (cursor.moveToFirst) {
				var foodid = cursor.getInt(0)
				if (cursor != null && !cursor.isClosed()) {
					cursor.close()
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
		
		def getFoodId(foodName: String): Int = {
			var cursor = db.rawQuery("SELECT id FROM foods WHERE name=?", Array[String](foodName))
			if (cursor.moveToFirst) {
				var foodid = cursor.getInt(0)
				if (cursor != null && !cursor.isClosed()) {
					cursor.close()
				}
				return foodid
			} else {
				return 0
			}
		}
		
		def removeCheckedItems: Unit = {
			var cursor = db.rawQuery("DELETE FROM groceryList WHERE checked=1", Array[String]())
			cursor.moveToFirst
			if (cursor != null && !cursor.isClosed()) {
				cursor.close()
			}
		}
	
		def removeFromGroceryList(foodName: String) : Unit = {
			val foodid = getFoodId(foodName)
			Log.i("DatabaseManager", "Removing " + foodName + " id:" + foodid)
			var cursor = db.rawQuery("DELETE FROM groceryList WHERE foodid=?", Array[String](foodid))
			cursor.moveToFirst
			if (cursor != null && !cursor.isClosed()) {
				cursor.close()
			}
		}
	
		def markGroceryItemObtained(foodid: Long, status: Boolean) : Unit = {
			val insertFood = "UPDATE groceryList SET checked=? WHERE foodid=?"
			var insertStmt = db.compileStatement(insertFood)
			if (status) {
				insertStmt.bindLong(1, 1)
			} else {
				insertStmt.bindLong(1, 0)
			}
			insertStmt.bindLong(2, foodid)
			insertStmt.executeInsert
			insertStmt.close
		}
	
		def createFood(name: String, aisle: String) : Long = {
			val insertFood = "INSERT INTO foods (name, aisle) values (?, ?)"
			val insertStmt = db.compileStatement(insertFood)
			insertStmt.bindString(1, name)
			insertStmt.bindString(2, aisle)
			val id = insertStmt.executeInsert
			insertStmt.close
			return id
		}
		
		def editFood(foodid: Int, name: String, aisle: String) : Unit = {
			var cursor = db.rawQuery("UPDATE foods SET name=?, aisle=? WHERE id=?", Array[String](name, aisle, foodid))
			cursor.moveToFirst
			if (cursor != null && !cursor.isClosed()) {
				cursor.close()
			}
		}
	
		def createTag(name: String) : Unit = {
		
		}
	
		def addTagToFood(tagid: Int, foodid: Int) : Unit = {
		
		}
	
		def addChildToFood(parent: String, child: String) : Unit = {
			val parentid = getFoodId(parent)
			val childid = getFoodId(child)
			val insertFood = "INSERT INTO foorRelations (parentid, childid) values (?, ?)"
			val insertStmt = db.compileStatement(insertFood)
			insertStmt.bindString(1, parentid)
			insertStmt.bindString(2, childid)
			insertStmt.executeInsert
			insertStmt.close
		}
		
		implicit def int2str(i: Int): String = i.toString
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