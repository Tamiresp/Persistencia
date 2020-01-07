package com.example.weaterapp.activities

 import android.content.Context
 import android.content.Intent
 import android.content.SharedPreferences
 import android.net.ConnectivityManager
 import android.os.AsyncTask
 import android.os.Bundle
 import android.text.Editable
 import android.text.TextWatcher
 import android.util.Log
 import android.view.Menu
 import android.view.MenuItem
 import android.view.View
 import androidx.appcompat.app.AppCompatActivity
 import com.example.weaterapp.R
 import com.example.weaterapp.adapters.ListAdapter
 import com.example.weaterapp.data.RoomManager
 import com.example.weaterapp.requests.Api
 import com.example.weaterapp.requests.entity_requests.City
 import com.example.weaterapp.requests.entity_requests.Favorite
 import com.example.weaterapp.requests.entity_requests.FindResult
 import com.example.weaterapp.utils.Constants
 import com.example.weaterapp.utils.Constants.API_KEY
 import com.google.android.material.snackbar.Snackbar
 import kotlinx.android.synthetic.main.activity_main.*
 import retrofit2.Call
 import retrofit2.Callback
 import retrofit2.Response


class MainActivity : AppCompatActivity(), Callback<FindResult> {
    private lateinit var lang: String

    lateinit var unit: String

    private var list: List<Int> = ArrayList()

    val sp: SharedPreferences by lazy {
        getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE)
    }

    private val adapter: ListAdapter by lazy {
        ListAdapter()
    }

    private fun initRecyclerView() {
        recycler.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()

        btn_busca.setOnClickListener {
            if (isDeviceConnected()){
                getPreferences()
                getCities(lang, unit)
            } else {
                Snackbar.make(findViewById(R.id.main_layout), R.string.no_network, Snackbar.LENGTH_LONG).show()
            }
        }

        city_edit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(city_edit.text.toString().isEmpty()){
                    if (isDeviceConnected())
                        getList()
                    else
                        Snackbar.make(findViewById(R.id.main_layout), R.string.no_network, Snackbar.LENGTH_LONG).show()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        if (isDeviceConnected())
            getList()
        else
            Snackbar.make(findViewById(R.id.main_layout), R.string.no_network, Snackbar.LENGTH_LONG).show()
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
        ListFavoriteAsync(this, this).execute()
    }

    fun setList(list: List<Int>){
        getPreferences()
        this.list = list
        progressBar.visibility = View.VISIBLE
        val call = Api.getInstance()
            .findGroup(list.joinToString(separator= ","), API_KEY, lang, unit)

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

    class InsertFavoriteAsync(context: Context, city: City) : AsyncTask<Void, Void, Boolean>() {
        private val db = RoomManager.getInstance(context)
        private var city = city

        override fun doInBackground(vararg p0: Void?): Boolean {
            val favorite = Favorite(city.id, city.name)

            val idDb = db?.getCityDao()?.favoriteById(favorite.id)

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

    class DeleteFavorite(context: Context, city: City) : AsyncTask<Void, Void, Boolean>(){
        private val db = RoomManager.getInstance(context)
        private var city = city
        override fun doInBackground(vararg params: Void?): Boolean {
            val favorite = Favorite(city.id,city.name)
            db?.getCityDao()?.deleteFavorite(favorite)
            return true
        }
    }


    class ListFavoriteAsync(context: Context, activity: MainActivity?) : AsyncTask<Void, Void, List<Int>>() {
        private val db = RoomManager.getInstance(context)

        private var list = ArrayList<Int>()

        private var activity = activity

        override fun doInBackground(vararg p0: Void?): List<Int> {
            val result = db?.getCityDao()?.allFavorites()!!

            if (result != null) {
                for (x in result.indices) {
                    list.add(result[x])
                }
            }
            return list
        }

        override fun onPostExecute(result: List<Int>?) {
            super.onPostExecute(result)
            activity?.setList(list)
        }
    }
}
