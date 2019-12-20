package com.example.weaterapp.activities

 import android.content.Context
 import android.content.Intent
 import android.content.SharedPreferences
 import android.net.ConnectivityManager
 import android.os.AsyncTask
 import android.os.Bundle
 import android.util.Log
 import android.view.LayoutInflater
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
 import kotlinx.android.synthetic.main.item.view.*
 import retrofit2.Call
 import retrofit2.Callback
 import retrofit2.Response

class MainActivity : AppCompatActivity(), Callback<FindResult> {
    private lateinit var mItemView: View

    private lateinit var lang: String

    private lateinit var tempi: String

    private val sp: SharedPreferences by lazy {
        getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE)
    }
    private val db : RoomManager? by lazy {
        RoomManager.getInstance(this)
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

        mItemView = LayoutInflater.from(this).inflate(R.layout.item, null)

        mItemView.btnFavorite.setOnClickListener {
            InsertFavoriteAync(this).execute()
            Log.d("po", "problema")
        }

        btn_busca.setOnClickListener {
            if (isDeviceConnected()){
                getPreferences()
                getCities(lang, tempi)
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
    }

    override fun onResponse(call: Call<FindResult>, response: Response<FindResult>) {
        if (response.isSuccessful) {
            adapter.updateData(response.body()?.list)
            Log.d("ok", response.toString())
        }
        progressBar.visibility = View.GONE
    }

    fun getPreferences(){
        val temp = sp.getBoolean(Constants.ISC, true)
        val lan = sp.getBoolean(Constants.ISEN, true)

        lang = if (lan){
            "en"
        } else {
            "pt"
        }

        tempi = if (temp){
            "metric"
            //mItemView.tvUnit.text = "ff"
        } else {
            "imperial"
        }
    }

    class InsertFavoriteAync(context: Context) : AsyncTask<Void, Void, Boolean>() {
        val db = RoomManager.getInstance(context)
        override fun doInBackground(vararg params: Void?): Boolean {
            for (i in 0..10){
                val favorite = Favorite(i, "Cidade $i")
                db?.getCityDao()?.insertFavorite(favorite)
            }
            db?.getCityDao()?.allFavorities()?.forEach {
                Log.d("okdb", it.toString())
            }
            return true
        }

    }
}
