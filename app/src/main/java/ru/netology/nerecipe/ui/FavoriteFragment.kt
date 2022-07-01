package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.adapter.RecipeAdapter
import ru.netology.nerecipe.data.RecipeCreateResult
import ru.netology.nerecipe.databinding.FavoriteFragmentBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FavoriteFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigateToRecipeContentScreenEvent.observe(this) {
            val direction = FavoriteFragmentDirections.toRecipeContentFragment(it)
            findNavController().navigate(direction)
        }

        viewModel.navigateToRecipeFragment.observe(this) {
            val direction = FavoriteFragmentDirections.toRecipeFragment()
            findNavController().navigate(direction)
        }
        viewModel.navigateToFullRecipeFragment.observe(this) {
            val direction = FavoriteFragmentDirections.toFullRecipeFragment(it)
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
        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            val favorite = recipes.filter { it.isFavorite }
            if (favorite.isEmpty()) {
                binding.listFavorite.visibility = View.GONE
                binding.emptySpace2.visibility = View.VISIBLE
            } else {
                binding.emptySpace2.visibility = View.GONE
                binding.listFavorite.visibility = View.VISIBLE
                adapter.submitList(favorite)
            }

        }
        binding.recipeButton.setOnClickListener {
            viewModel.onRecipeButtonClicked()
        }
    }.root

    override fun onResume() {
        super.onResume()

        setFragmentResultListener(
            requestKey = RecipeContentFragment.REQUEST_KEY
        ) { requestKey, bundle ->
            if (requestKey != RecipeContentFragment.REQUEST_KEY) return@setFragmentResultListener
            val newTitle = bundle[RecipeContentFragment.NEW_TITLE].toString()
            val newAuthor = bundle[RecipeContentFragment.NEW_AUTHOR].toString()
            val newCategory = bundle[RecipeContentFragment.NEW_CATEGORY].toString()
            val step1 = bundle[RecipeContentFragment.STEP1].toString()
            val step2 = bundle[RecipeContentFragment.STEP2].toString()
            viewModel.onSaveButtonClicked(RecipeCreateResult(newTitle, newAuthor, newCategory, step1, step2))
        }
    }
}