package com.example.weaterapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weaterapp.R
import com.example.weaterapp.activities.MainActivity
import com.example.weaterapp.requests.entity_requests.City
import kotlinx.android.synthetic.main.item.view.*

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var list: List<City>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item, parent, false)

        view.btnFavorite.setOnClickListener {
            MainActivity.InsertFavoriteAsync(parent.context).execute()
            //view.btnFavorite.setBackgroundColor(R.drawable.selector_back)
        }
        return ViewHolder(view)
    }

    override fun getItemCount() = list?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.let {
            holder.bind(it[position])
        }
    }

    fun updateData(list: List<City>?) {
        this.list = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(city: City) {
            itemView.tvCity.text = "${city.name}, ${city.sys.country}"
            itemView.tvPrevision.text = city.weather[0].description
            itemView.tvWind.text = "wind ${city.wind.speed} m/s"
            itemView.tvWeatherValue.text = "${city.main.temp.toInt()}"
            itemView.tvVelocity.text = "${city.main.pressure} hpa"
            itemView.tvClouds.text = "clouds ${city.clouds.all.toInt().toString()}%"
            if (city.weather.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load("http://openweathermap.org/img/w/${city.weather[0].icon}.png")
                    .into(itemView.imgWeatherIcon)
            }

        }
    }

}