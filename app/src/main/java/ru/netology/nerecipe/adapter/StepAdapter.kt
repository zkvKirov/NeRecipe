package ru.netology.nerecipe.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.StepsCard
import ru.netology.nerecipe.databinding.RecipeStepsBinding

class StepAdapter(
    private val interactionListener: StepInteractionListener,
    private val steps: List<StepsCard>
) : RecyclerView.Adapter<StepsCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepsCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeStepsBinding.inflate(inflater, parent, false)
        return StepsCardViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: StepsCardViewHolder, position: Int) {
        holder.bind(steps[position])
    }

    override fun getItemCount(): Int = steps.size
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