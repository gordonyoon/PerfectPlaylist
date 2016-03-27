package com.example.gordonyoon.perfectplaylist.ui.viewpager.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.gordonyoon.perfectplaylist.R
import org.jetbrains.anko.find

class TrackFragment(): Fragment() {

    private var trackTitle: String = ""
    private var artistName: String = ""

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_track, container, false)
        if (!trackTitle.isEmpty() || !artistName.isEmpty()) {
            updateTrack(view, trackTitle, artistName)
            trackTitle = ""
            artistName = ""
        }
        return view
    }

    fun updateTrack(trackTitle: String, artistName: String) {
        if (view == null) {
            this.trackTitle = trackTitle
            this.artistName = artistName
        }
        updateTrack(view, trackTitle, artistName)
    }

    private fun updateTrack(parent: View?, trackTitle: String, artistName: String) {
        parent?.find<TextView>(R.id.trackTitle)?.text = trackTitle
        parent?.find<TextView>(R.id.artistName)?.text = artistName
    }

    fun clear() {
        view?.find<TextView>(R.id.trackTitle)?.text = ""
        view?.find<TextView>(R.id.artistName)?.text = ""
    }

    fun setTextExpired() {
        view?.find<TextView>(R.id.trackTitle)?.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        view?.find<TextView>(R.id.artistName)?.setTextColor(resources.getColor(android.R.color.holo_red_dark))
    }

    fun setTextUnexpired() {
        view?.find<TextView>(R.id.trackTitle)?.setTextColor(resources.getColor(android.R.color.black))
        view?.find<TextView>(R.id.artistName)?.setTextColor(resources.getColor(android.R.color.black))
    }
}