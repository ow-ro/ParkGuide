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
import ca.owro.npapp.Models.Leg
import ca.owro.npapp.Models.Trip
import ca.owro.npapp.R
import com.bumptech.glide.Glide


class LegAdapter(val ctx: Context, val legs: ArrayList<Leg>, val itemClick: (Leg) -> Unit) : RecyclerView.Adapter<LegAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.leg_list_view_item, parent, false)

        return  ViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return legs.count()
    }

    override fun onBindViewHolder(holder: LegAdapter.ViewHolder, position: Int) {
        holder.bindLegs(legs[position])
    }


    inner class ViewHolder(itemView: View, val itemClick: (Leg) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val legOrder = itemView.findViewById<TextView>(R.id.legOrderText)
        val legTitle = itemView.findViewById<TextView>(R.id.tripLegDesc)
        val legTime = itemView.findViewById<TextView>(R.id.tripLegTime)
        val legBG = itemView.findViewById<ImageView>(R.id.tripLegImg)

        fun bindLegs(leg: Leg) {

            legOrder?.text = leg.order.toString()
            legTitle.text = leg.description
            legTime.text = leg.time

            Glide.with(ctx).load(leg.image).centerCrop().into(legBG)

            itemView.setOnClickListener { itemClick(leg) }

        }

    }

}