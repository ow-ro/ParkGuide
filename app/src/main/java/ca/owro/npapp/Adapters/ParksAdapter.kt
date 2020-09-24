package ca.owro.npapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.owro.npapp.Models.Park
import ca.owro.npapp.R
import com.bumptech.glide.Glide
import java.util.*
import kotlin.collections.ArrayList

class ParksAdapter(val ctx: Context, val parks: ArrayList<Park>, val itemClick: (Park) -> Unit) : RecyclerView.Adapter<ParksAdapter.ViewHolder>(), Filterable {

    var parksFilterList = ArrayList<Park>()

    init {
        parksFilterList = parks
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParksAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.park_list_view_item, parent, false)

        return  ViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {

        return parksFilterList.count()

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.bindPark(parksFilterList[position])

    }

    inner class ViewHolder(itemView: View, val itemClick: (Park) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val parkName = itemView.findViewById<TextView>(R.id.parkNameTxt)
        val parkListImage = itemView.findViewById<ImageView>(R.id.listParkImage)
        val parkLocation = itemView.findViewById<TextView>(R.id.parkLocTxt)

        fun bindPark(park: Park) {

            parkName?.text = park.name
            parkLocation?.text = park.location
            Glide.with(ctx).load(park.listImage).centerCrop().into(parkListImage)

            itemView.setOnClickListener { itemClick(park) }

        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    parksFilterList = parks
                } else {
                    val resultList = ArrayList<Park>()
                    for (park in parks) {
                        if (park.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(park)
                        }
                    }
                    parksFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = parksFilterList
                return filterResults

            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                parksFilterList = results?.values as ArrayList<Park>
                notifyDataSetChanged()
            }

        }
    }

}