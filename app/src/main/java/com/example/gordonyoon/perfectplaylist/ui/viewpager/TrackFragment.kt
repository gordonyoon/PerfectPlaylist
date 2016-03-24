package com.example.gordonyoon.perfectplaylist.ui.viewpager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.gordonyoon.perfectplaylist.R
import org.jetbrains.anko.find

class TrackFragment(): Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_track, container, false)
    }

    fun updateTrack(trackTitle: String, artistName: String) {
        view?.find<TextView>(R.id.trackTitle)?.text = trackTitle
        view?.find<TextView>(R.id.artistName)?.text = artistName
    }

    fun clear() {
        view?.find<TextView>(R.id.trackTitle)?.text = ""
        view?.find<TextView>(R.id.artistName)?.text = ""
    }
}