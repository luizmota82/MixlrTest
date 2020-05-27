package io.luizmota.mixlrtest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.show_list_item.view.*

internal class ShowsAdapter(val requestManager: RequestManager) : Adapter<ShowItemViewHolder>() {
    private var shows: List<Show> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowItemViewHolder =
        ShowItemViewHolder(
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.show_list_item, parent, false),
            requestManager = requestManager
        )

    override fun getItemCount(): Int = shows.size

    override fun onBindViewHolder(holder: ShowItemViewHolder, position: Int) {
        holder.populate(show = shows[position])
    }

    fun display(schedule: List<Show>) {
        shows = schedule
        notifyDataSetChanged()
    }
}

internal class ShowItemViewHolder(itemView: View, val requestManager: RequestManager) :
    RecyclerView.ViewHolder(itemView) {
    fun populate(show: Show) {
        if (show.imageUrl.isNotEmpty()) {
            requestManager.load(show.imageUrl)
                .dontTransform()
                .into(itemView.show_image)
        }
        itemView.show_title.text = show.title
        itemView.show_host.text = show.host
        itemView.show_start_time.text = itemView.resources.getString(R.string.show_begins_at, show.startTime)
        itemView.show_end_time.text = itemView.resources.getString(R.string.show_ends_at, show.endTime)

        when {
            show.isOnAir -> {
                itemView.show_media_toggle.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_play_arrow_black_24dp))
                itemView.show_media_toggle.visibility = View.VISIBLE
            }
            show.isLive -> {
                itemView.show_media_toggle.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_stop_black_24dp))
                itemView.show_media_toggle.visibility = View.VISIBLE
            }
            else -> itemView.show_media_toggle.visibility = View.INVISIBLE
        }
    }
}
