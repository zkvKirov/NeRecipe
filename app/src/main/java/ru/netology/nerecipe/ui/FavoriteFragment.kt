package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.adapter.RecipeAdapter
import ru.netology.nerecipe.databinding.FavoriteFragmentBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FavoriteFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigateToRecipeFragment.observe(this) {
            val direction = FavoriteFragmentDirections.toRecipeFragment()
            findNavController().navigate(direction)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FavoriteFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        val adapter = RecipeAdapter(viewModel)
        binding.listFavorite.adapter = adapter
//        viewModel.data.observe(viewLifecycleOwner) { recipes ->
//            val newRecipe = recipes.size > adapter.itemCount
//            adapter.submitList(recipes) {
//                if (newRecipe) {
//                    binding.listFavorite.smoothScrollToPosition(0)
//                }
//            }
//        }
        binding.recipeButton.setOnClickListener {
            viewModel.onFavoriteButtonClicked()
        }
    }.root
}