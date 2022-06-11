package com.zyl315.animehunter.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.R
import com.zyl315.animehunter.databinding.FragmentPlaySpeedBinding
import com.zyl315.animehunter.repository.interfaces.RequestState

class PlaySpeedFragment(var currentSpeed: Float) : PopupFragment<FragmentPlaySpeedBinding>() {

    private val speedList: MutableList<Float> = mutableListOf(0.5f, 0.75f, 1.0f, 2.0f, 3.0f)
    var onClick: ((Float) -> Unit)? = null
    override var popupGravity: Int = Gravity.END

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.setBackgroundResource(R.color.black_70)
        val speedAdapter = PlaySpeedAdapter(speedList)
        mBinding.run {
            recycleVie.layoutManager = LinearLayoutManager(requireContext())
            recycleVie.adapter = speedAdapter
        }
        return view
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaySpeedBinding {
        return FragmentPlaySpeedBinding.inflate(inflater, container, false)
    }

    override fun onBackPressed(): Boolean {
        dismiss()
        return true
    }

    inner class PlaySpeedAdapter(private val speedList: MutableList<Float>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return PlaySpeedViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_play_speed, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val speed = speedList[position]
            if (holder is PlaySpeedViewHolder) {
                holder.playSpeed.text = speed.toString() + "X"
                holder.itemView.setOnClickListener {
                    currentSpeed = speedList[holder.bindingAdapterPosition]
                    onClick?.let { block ->
                        block(speed)
                    }
                    dismiss()
                }
            }
        }

        override fun getItemCount(): Int {
            return speedList.size
        }
    }

}


class PlaySpeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val playSpeed: TextView = itemView.findViewById(R.id.tv_playSpeed)
}