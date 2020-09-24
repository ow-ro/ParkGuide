package ca.owro.npapp.Adapters

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.owro.npapp.Models.Trip
import ca.owro.npapp.R


class TripsAdapter(val ctx: Context, val trips: ArrayList<Trip>, val itemClick: (Trip) -> Unit) : RecyclerView.Adapter<TripsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trips_list_view_item, parent, false)
        return  ViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return trips.count()
    }

    override fun onBindViewHolder(holder: TripsAdapter.ViewHolder, position: Int) {
        holder.bindTrip(trips[position])
    }


    inner class ViewHolder(itemView: View, val itemClick: (Trip) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val tripName = itemView.findViewById<TextView>(R.id.tripTitle)
        val tripLocation = itemView.findViewById<TextView>(R.id.tripLocationTitle)
        val tripLength = itemView.findViewById<TextView>(R.id.tripTimeTitle)
        val tripMonths = itemView.findViewById<TextView>(R.id.tripCalTitle)
        val tripDriveTime = itemView.findViewById<TextView>(R.id.tripCarTitle)
        val tripBG = itemView.findViewById<ImageView>(R.id.tripBackgroundImg)
        val tripParks = itemView.findViewById<TextView>(R.id.tripParks)

        fun bindTrip(trip: Trip) {

            tripName?.text = trip.name
            tripLocation?.text = trip.start
            tripLength?.text = trip.length
            tripMonths?.text = trip.months
            tripDriveTime?.text = trip.driveTime
            tripParks?.text = trip.parks

            when (trip.gradient) {
                1 -> tripBG.setBackgroundResource(R.drawable.trips_1)
                2 -> tripBG.setBackgroundResource(R.drawable.trips_2)
                3 -> tripBG.setBackgroundResource(R.drawable.trips_3)
                4 -> tripBG.setBackgroundResource(R.drawable.trips_4)
                5 -> tripBG.setBackgroundResource(R.drawable.trips_5)
            }

            itemView.setOnClickListener { itemClick(trip) }

        }

    }

}