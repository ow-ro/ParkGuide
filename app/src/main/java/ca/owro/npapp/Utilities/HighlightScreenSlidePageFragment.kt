package ca.owro.npapp.Utilities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import ca.owro.npapp.Activities.WebViewActivity
import ca.owro.npapp.Models.Highlight
import ca.owro.npapp.R
import com.bumptech.glide.Glide

class HighlightScreenSlidePageFragment(highlight: Highlight) : Fragment() {

    var hl = highlight

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        val view = inflater.inflate(R.layout.fragment_screen_slide_page, container, false)
        val tv = view.findViewById<TextView>(R.id.slideTagline)
        val img = view.findViewById<ImageView>(R.id.slideImage)

        val csl = view.findViewById<ConstraintLayout>(R.id.slideConstraintLayout)

        tv.text = hl.tagline
        Glide.with(this).load(hl.image).centerCrop().into(img)

        csl.setOnClickListener { v ->
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("link",hl.link)
            startActivity(intent)
        }

        return view
    }


}