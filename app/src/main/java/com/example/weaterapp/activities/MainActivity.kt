package com.example.weaterapp.activities

 import android.content.Context
 import android.content.Intent
 import android.content.SharedPreferences
 import android.net.ConnectivityManager
 import android.os.AsyncTask
 import android.os.Bundle
 import android.util.Log
 import android.view.Menu
 import android.view.MenuItem
 import android.view.View
 import androidx.appcompat.app.AppCompatActivity
 import com.example.weaterapp.R
 import com.example.weaterapp.adapters.ListAdapter
 import com.example.weaterapp.data.RoomManager
 import com.example.weaterapp.requests.Api
 import com.example.weaterapp.requests.entity_requests.Favorite
 import com.example.weaterapp.requests.entity_requests.FindResult
 import com.example.weaterapp.utils.Constants
 import com.example.weaterapp.utils.Constants.API_KEY
 import com.google.android.material.snackbar.Snackbar
 import kotlinx.android.synthetic.main.activity_main.*
 import retrofit2.Call
 import retrofit2.Callback
 import retrofit2.Response
 import java.util.ArrayList

class MainActivity : AppCompatActivity(), Callback<FindResult> {
    private lateinit var lang: String

    lateinit var unit: String

    private val sp: SharedPreferences by lazy {
        getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE)
    }
    private val db : RoomManager? by lazy {
        RoomManager.getInstance(this)
    }
    val adapter: ListAdapter by lazy {
        ListAdapter()
    }

    private fun initRecyclerView() {
        recycler.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        unit = getString(R.string.c)

        initRecyclerView()

        getList()

        btn_busca.setOnClickListener {
            if (isDeviceConnected()){
                getPreferences()
                getCities(lang, unit)
            } else {
                Snackbar.make(findViewById(R.id.main_layout), R.string.no_network, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun isDeviceConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    private fun getCities(lan: String, unit: String){
        progressBar.visibility = View.VISIBLE
        getPreferences()
        val call = Api.getInstance()
            .find(city_edit.text.toString(), API_KEY, lan, unit)
        call.enqueue(this)
    }

    override fun onFailure(call: Call<FindResult>, t: Throwable) {
        Log.d("Error", "fail")
        progressBar.visibility = View.GONE
        Snackbar.make(findViewById(R.id.main_layout), R.string.error, Snackbar.LENGTH_LONG).show()
    }

    override fun onResponse(call: Call<FindResult>, response: Response<FindResult>) {
        if (response.isSuccessful) {
            adapter.updateData(response.body()?.list)
            tv_no_result.visibility = View.GONE
            Log.d("ok", response.toString())
        } else if (response.body()?.list == null){
            tv_no_result.visibility = View.VISIBLE
            Snackbar.make(findViewById(R.id.main_layout), R.string.no_result, Snackbar.LENGTH_LONG).show()
        }
        progressBar.visibility = View.GONE
    }

    private fun getPreferences(){
        val temp = sp.getBoolean(Constants.ISC, true)
        val lan = sp.getBoolean(Constants.ISEN, true)

        lang = if (lan){
            getString(R.string.english)
        } else {
            getString(R.string.portuguese)
        }

        unit = if (temp){
            getString(R.string.metric)
        } else {
            getString(R.string.imperial)
        }
    }

    private fun getList(){
        progressBar.visibility = View.GONE
        ListFavoriteAsync(this).execute()
        val list = listOf(3435910, 3445993, 3397838)

        val call = Api.getInstance()
            .findGroup( API_KEY, list.joinToString())
        call.enqueue(this)
    }

    class InsertFavoriteAsync(context: Context) : AsyncTask<Void, Void, Boolean>() {
        val db = RoomManager.getInstance(context)

        override fun doInBackground(vararg p0: Void?): Boolean {
            val id = 3682292
            val favorite = Favorite(id, "Buenos Aires")

            val idDb = db?.getCityDao()?.favoriteById(id)

            if (idDb != favorite) {
                db?.getCityDao()?.insertFavorite(favorite)
                Log.d("ok", " id")
            } else {
                db?.getCityDao()?.deleteFavorite(favorite)
                Log.d("ok", "mesmo id")
            }

            return true
        }
    }

    class ListFavoriteAsync(context: Context) : AsyncTask<Void, Void, ArrayList<Favorite>>() {
        val db = RoomManager.getInstance(context)

        override fun doInBackground(vararg p0: Void?): ArrayList<Favorite>? {
            val result = db?.getCityDao()?.allFavorites()
            Log.d("db", result.toString())
            return result as ArrayList<Favorite>?
        }
    }
}
