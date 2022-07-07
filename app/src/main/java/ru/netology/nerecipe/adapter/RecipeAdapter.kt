package ru.netology.nerecipe.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.RecipeCard
import ru.netology.nerecipe.databinding.RecipeCardBinding
import ru.netology.nerecipe.helper.ItemTouchHelperAdapter
import java.util.*
import kotlin.collections.ArrayList

class RecipeAdapter(
    private val interactionListener: RecipeInteractionListener
) : ListAdapter<RecipeCard, RecipeCardViewHolder>(PostDiffCallback), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeCardBinding.inflate(inflater, parent, false)
        return RecipeCardViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: RecipeCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object PostDiffCallback : DiffUtil.ItemCallback<RecipeCard>() {
        override fun areItemsTheSame(oldItem: RecipeCard, newItem: RecipeCard) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RecipeCard, newItem: RecipeCard) =
            oldItem == newItem
    }

    // код для drag & drop

    // необходимо как-то инициализировать, иначе при попытке переатщить рецепт приложение вылетает
    private lateinit var recipeCardslist: ArrayList<RecipeCard>

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(recipeCardslist, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(recipeCardslist, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }
// если расскомментировать, то без инициализации recipeCardslist приложение не запускается, необходимо удалить и установить заново
//    override fun getItemCount(): Int {
//        return recipeCardslist.size
//    }
}


class RecipeCardViewHolder(
    private val binding: RecipeCardBinding,
    listener: RecipeInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var card: RecipeCard

    private val popupMenu by lazy {
        PopupMenu(itemView.context, binding.menu).apply {
            inflate(R.menu.option_card)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.remove -> {
                        val builder = AlertDialog.Builder(itemView.context)
                        builder.setTitle("Удаление рецепта")
                            .setIcon(R.drawable.ic_clear_24)
                            .setMessage("Вы точно хотите удалить рецепт?")
                            .setPositiveButton("OK") { _, _ ->
                                listener.onRemoveClicked(card)
                                Toast.makeText(itemView.context, "Рецепт был удалён", Toast.LENGTH_SHORT).show()
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
                        listener.onEditClicked(card)
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
        binding.favorite.setOnClickListener {
            listener.onFavoriteClicked(card)
        }
        binding.groupRecipe.setOnClickListener {
            listener.onRecipeClicked(card)
        }
    }

    fun bind(card: RecipeCard) {
        this.card = card
        with(binding) {
            title.text = card.title
            authorName.text = card.author
            category.text = card.category
            favorite.isChecked = card.isFavorite
            step1.text = card.step1
            step2.text = card.step2
        }
    }

}
