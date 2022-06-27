package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import ru.netology.nerecipe.adapter.RecipeAdapter
import ru.netology.nerecipe.data.RecipeCreateResult
import ru.netology.nerecipe.databinding.RecipeFragmentBinding
import ru.netology.nerecipe.helper.SimpleItemTouchHelperCallback
import ru.netology.nerecipe.viewModel.RecipeViewModel

class RecipeFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigateToRecipeContentScreenEvent.observe(this) {
            val direction = RecipeFragmentDirections.toRecipeContentFragment(it)
            findNavController().navigate(direction)
        }

        viewModel.navigateToFavoriteFragment.observe(this) {
            val direction = RecipeFragmentDirections.toFavoriteFragment()
            findNavController().navigate(direction)
        }
        viewModel.navigateToFullRecipeFragment.observe(this) {
            val direction = RecipeFragmentDirections.toFullRecipeFragment()
            findNavController().navigate(direction)
        }

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RecipeFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        val adapter = RecipeAdapter(viewModel)
        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            val newRecipe = recipes.size > adapter.itemCount
            adapter.submitList(recipes) {
                if (newRecipe) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        }
        binding.fab.setOnClickListener {
            viewModel.onAddButtonClicked()
        }
        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteButtonClicked()
        }
        val callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.list)
    }.root
}