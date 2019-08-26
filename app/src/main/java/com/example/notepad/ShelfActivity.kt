package com.example.notepad


import android.app.SearchManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.row.*
import kotlinx.android.synthetic.main.row.view.*

class ShelfActivity : AppCompatActivity() {

    var listNotes = ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelf)

        // load for db
        LoadQuery("%")
    }

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
    }

    private  fun LoadQuery( title: String) {
        val DbManager =DbManager(this )
        val projections= arrayOf( "ID","Title","Description")
        val selectionArgs = arrayOf(title)
        val cursor =DbManager.Query(projections,"title like?",selectionArgs,"Title")
        listNotes.clear()
        if (cursor.moveToFirst()){

            do{
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))
                listNotes.add(Note(ID,Title,Description))
            }while (cursor.moveToNext())
        }
            // adapter
            var myNotesAdapter = myNotesAdapter(this, listNotes)
            //set adapter
             notesLv.adapter = myNotesAdapter

        // get total number of task  from list view
        val total = notesLv.count
        val mActionBar = supportActionBar
        if (mActionBar !=null){
            //set actionbar as subtitle
            mActionBar.subtitle = "You Have"+total+"note(s) in list..."
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu,menu)

        val sv: SearchView =menu!!.findItem(R.id.action_bar_search).actionView as SearchView
        val sm =getSystemService(Context.SEARCH_SERVICE)as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
               LoadQuery("%"+query+"%" )
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                LoadQuery("%"+query+"%")
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
        if(item !=null)
           when(item.itemId){
               R.id.addNote->{
                   startActivity(Intent(this,AddNoteActivity::class.java ))
               }
               R.id.action_settings->{
                   Toast.makeText(this,"settings",Toast.LENGTH_SHORT).show()
               }
           }
        return super.onOptionsItemSelected(item)
    }


    inner  class myNotesAdapter: BaseAdapter {

        var listNotesAdapter = ArrayList<Note>()
        var context:Context?=null

        constructor(context: Context?,listNoteArray: ArrayList<Note>) : super() {
            this.listNotesAdapter = listNoteArray
            this.context = context
        }

        override fun getView(position:Int, covertView:View?, parent:ViewGroup?): View {
            //Inflate layout row.xml
            var myView = layoutInflater.inflate(R.layout.row, null)
            var myNote = listNotesAdapter[position]
            myView.titleTv.text = myNote.nodeName
            myView.descTv.text = myNote.nodeDes

            //delete button click
            myView.deleteBtn.setOnClickListener {
            var dbManger = DbManager(this.context!!)
             val selectionArgs = arrayOf(myNote.nodeID.toString())
                dbManger.delete("ID=",selectionArgs)
                LoadQuery("%")
            }
            //edit and update button click
            myView.editBtn.setOnClickListener{
                GoToUpdate(myNote)
            }
            //copy button
            myView.copyBtn.setOnClickListener {
                val title = myView.titleTv.text.toString()
                val desc  =myView.descTv.text.toString()
                val s =title+"\n"+desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE)as ClipboardManager
                cb.text = s
                Toast.makeText(this@ShelfActivity,"Text Copied",Toast.LENGTH_SHORT).show()


            }

            //share button
           myView.shareBtn.setOnClickListener {
                val title = myView.titleTv.text.toString()
                val desc  =myView.descTv.text.toString()
                val s =title+"\n"+desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE)as ClipboardManager
                cb.text = s
             // share intent
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type="text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT,s)
                startActivity(Intent.createChooser(shareIntent,s))
            }

            return  myView
        }

        override fun getItem(position: Int): Any {
           return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }
    }

    private fun GoToUpdate(myNote: Note) {
        val intent=Intent(this,AddNoteActivity::class.java)
        intent.putExtra("ID",myNote.nodeName)
        intent.putExtra("des",myNote.nodeDes )
        startActivity(intent)

    }

}
