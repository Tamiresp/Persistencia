package com.example.weaterapp.adapters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weaterapp.R
import com.example.weaterapp.activities.MainActivity
import com.example.weaterapp.requests.entity_requests.City
import com.example.weaterapp.utils.Constants
import kotlinx.android.synthetic.main.item.view.*

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var list: List<City>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = list?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = LayoutInflater.from(holder.itemView.context).inflate(
            R.layout.item, null)

        list?.let {
            holder.bind(it[position])
        }

        holder.itemView.btnFavorite.setOnClickListener {
            list?.let {
                insert(view, it[position])
                save(holder)
            }
        }
    }

    private fun save(holder: ViewHolder){
        val sp: SharedPreferences by lazy {
            holder.itemView.context.getSharedPreferences("BUTTON", Context.MODE_PRIVATE)
        }
        sp.edit{
            putBoolean("FAV", holder.itemView.btnFavorite.isChecked)
        }
        Log.d("state", sp.getBoolean("FAV", true).toString())
    }

    private fun insert(view: View, city: City){
        MainActivity.InsertFavoriteAsync(view.context, city).execute()
        Log.d("ok", "inseriu")
    }


    fun updateData(list: List<City>?) {
        this.list = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val sp: SharedPreferences by lazy {
            view.context.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE)
        }

        val spf: SharedPreferences by lazy {
            view.context.getSharedPreferences("BUTTON", Context.MODE_PRIVATE)
        }

        fun bind(city: City) {
            itemView.tvCity.text = "${city.name}, ${city.sys.country}"
            itemView.tvPrevision.text = city.weather[0].description
            itemView.tvWind.text = "wind ${city.wind.speed} m/s"
            itemView.tvWeatherValue.text = "${city.main.temp.toInt()}"
            itemView.tvVelocity.text = "${city.main.pressure} hpa"
            itemView.tvClouds.text = "clouds ${city.clouds.all.toInt().toString()}%"

            val check = spf.getBoolean("FAV", true)

            itemView.btnFavorite.isChecked = check

            itemView.btnFavorite.background = itemView.context.getDrawable(R.drawable.selector_back)

            val c = sp.getBoolean(Constants.ISC, true)

            if (c){
                itemView.tvUnit.text = itemView.context.getString(R.string.c)
            } else {
                itemView.tvUnit.text = itemView.context.getString(R.string.f)
            }
            if (city.weather.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load("http://openweathermap.org/img/w/${city.weather[0].icon}.png")
                    .into(itemView.imgWeatherIcon)
            }
        }
    }

}