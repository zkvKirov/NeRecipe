package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.adapter.RecipeCardViewHolder
import ru.netology.nerecipe.databinding.FullRecipeFragmentBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FullRecipeFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val args by navArgs<FullRecipeFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigateToRecipeContentScreenEvent.observe(this) {
            val direction = FullRecipeFragmentDirections.toRecipeContentFragment(it)
            findNavController().navigate(direction)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FullRecipeFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        val viewHolder = RecipeCardViewHolder(binding.fullRecipe, viewModel)
        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            val recipe = recipes.find {
                it.id == args.recipeId
            } ?: run {
                findNavController().navigateUp()
                return@observe
            }
            binding.fullRecipe.step1.visibility = View.VISIBLE
            binding.fullRecipe.step2.visibility = View.VISIBLE
            viewHolder.bind(recipe)
        }
    }.root

}