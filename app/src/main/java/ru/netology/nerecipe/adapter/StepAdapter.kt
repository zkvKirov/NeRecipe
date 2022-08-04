package ru.netology.nerecipe.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.StepsCard
import ru.netology.nerecipe.databinding.RecipeStepsBinding
import ru.netology.nerecipe.helper.ItemTouchHelperAdapter
import java.util.*

class StepAdapter(
    private val interactionListener: StepInteractionListener
) : ListAdapter<StepsCard, StepsCardViewHolder>(PostDiffCallback), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepsCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeStepsBinding.inflate(inflater, parent, false)
        return StepsCardViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: StepsCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object PostDiffCallback : DiffUtil.ItemCallback<StepsCard>() {
        override fun areItemsTheSame(oldItem: StepsCard, newItem: StepsCard) =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: StepsCard, newItem: StepsCard) =
            oldItem == newItem
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val updated = currentList.toMutableList()
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(updated, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(updated, i, i - 1)
            }
        }
        submitList(updated)
        return true
    }

}

class StepsCardViewHolder(
    private val binding: RecipeStepsBinding,
    listener: StepInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var step: StepsCard

    private val popupMenu by lazy {
        PopupMenu(itemView.context, binding.menu).apply {
            inflate(R.menu.option_card)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.remove -> {
                        val builder = AlertDialog.Builder(itemView.context)
                        builder.setTitle("Удаление этапа рецепта")
                            .setIcon(R.drawable.ic_clear_24)
                            .setMessage("Вы точно хотите удалить этот этап?")
                            .setPositiveButton("OK") { _, _ ->
                                listener.onStepRemoveClicked(step)
                                Toast.makeText(itemView.context, "Этап был удалён", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("Cancel") {dialog, _ ->
                                dialog.cancel()
                            }
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                        true
                    }
                    R.id.edit -> {
                        listener.onStepEditClicked(step)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    init {
        binding.menu.setOnClickListener {
            popupMenu.show()
        }
    }

    fun bind(step: StepsCard) {
        this.step = step
        with(binding) {
            content.text = step.content
        }
    }

}