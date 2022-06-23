package ru.netology.nerecipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.RecipeCard
import ru.netology.nerecipe.databinding.RecipeCardBinding

class RecipeAdapter(
    private val interactionListener: RecipeInteractionListener
) : ListAdapter<RecipeCard, RecipeCardViewHolder>(PostDiffCallback) {

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
                        listener.onRemoveClicked(card)
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
    }

    fun bind(card: RecipeCard) {
        this.card = card
        with(binding) {
            title.text = card.title
            authorName.text = card.author
            category.text = card.category

//            like.apply {
//                text = displayLikes(post.likes)
//                isChecked = post.likedByMe
//            }
        }
    }

}