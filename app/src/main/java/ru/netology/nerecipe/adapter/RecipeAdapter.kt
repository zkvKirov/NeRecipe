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
) : ListAdapter<RecipeCard, RecipeCardViewHolder>(PostDiffCallback), ItemTouchHelperAdapter, Filterable {

    private var recipeCardslist: List<RecipeCard> = ArrayList() // сюда необходимо как-то передать актуальный список чтобы рецепты в нём можно было перетаскивать

    private var recipelist: ArrayList<String> = ArrayList()  // сюда необходимо как-то передать актуальный список чтобы искать в нём

    var recipeFilterList = ArrayList<String>()

    init {
        recipeFilterList = recipelist
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeCardBinding.inflate(inflater, parent, false)
        return RecipeCardViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: RecipeCardViewHolder, position: Int) {
        holder.bind(getItem(position))
        // код для поска - закомментировано чтобы не вылетало
//        val selectCountryTextView =
//            holder.itemView.findViewById<TextView>(com.google.android.material.R.id.select_dialog_listview)
//        selectCountryTextView.text = recipeFilterList[position]
    }

    private object PostDiffCallback : DiffUtil.ItemCallback<RecipeCard>() {
        override fun areItemsTheSame(oldItem: RecipeCard, newItem: RecipeCard) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RecipeCard, newItem: RecipeCard) =
            oldItem == newItem
    }

    // код для drag & drop

//    override fun getItemCount(): Int {
//        return recipeCardslist.size
//    } // если расскомментировать метод, то при запуске приложения список рецептов отображается всегда будет пустой, даже если рецепты в списке по факту есть

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

    // код для поиска
//    override fun getItemCount(): Int {
//        return recipeFilterList.size
//    } // если расскомментировать метод, то при запуске приложения список рецептов всегда будет пустой,
        // при этом операция добавления выполняется и рецепт в нужный список (который не видно на экране) добавляется
        // а если закомментировать то приложение сразу вылетает, из-за того что размер списка не известен

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    recipeFilterList = recipelist
                } else {
                    val resultList = ArrayList<String>()
                    for (row in recipelist) {
                        if (row.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    recipeFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = recipeFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                recipeFilterList = results?.values as ArrayList<String>
                notifyDataSetChanged()
            }
        }
    }
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
