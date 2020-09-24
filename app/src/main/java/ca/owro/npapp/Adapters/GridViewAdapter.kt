package ca.owro.npapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import ca.owro.npapp.R
import kotlin.collections.ArrayList
import ca.owro.npapp.Utilities.UtilitiesManager.ResourceManager.getActivityOrTerrainResourceId

class GridViewAdapter(val context: Context, val activitiesOrTerrain: ArrayList<String>): BaseAdapter() {

    var view: View? = null
    var layoutInflater: LayoutInflater? = null

    override fun getCount(): Int {
        return activitiesOrTerrain.count()
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        layoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null) {
            view = View(context)
            view = layoutInflater!!.inflate(R.layout.single_grid_item, null)
            val gridImgView = view!!.findViewById<ImageView>(R.id.gridImgView)
            val gridTxtView = view!!.findViewById<TextView>(R.id.gridTxtView)
            gridImgView.setImageResource(getActivityOrTerrainResourceId(activitiesOrTerrain?.get(position)))
            gridTxtView.text = activitiesOrTerrain?.get(position)
        }
        return view
    }

}