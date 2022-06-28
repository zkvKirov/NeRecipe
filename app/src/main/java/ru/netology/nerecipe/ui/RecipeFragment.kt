package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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

    private var draft: RecipeCreateResult? = null

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
            val direction = RecipeFragmentDirections.toFullRecipeFragment(it)
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
            draft = null
        }

        setFragmentResultListener(
            requestKey = RecipeContentFragment.DRAFT_KEY
        ) { requestKey, bundle ->
            if (requestKey != RecipeContentFragment.DRAFT_KEY) return@setFragmentResultListener
            val newRecipeTitle = bundle[RecipeContentFragment.NEW_TITLE].toString()
            val newRecipeAuthor = bundle[RecipeContentFragment.NEW_AUTHOR].toString()
            val newRecipeCategory = bundle[RecipeContentFragment.NEW_CATEGORY].toString()
            val newRecipeStep1 = bundle[RecipeContentFragment.STEP1].toString()
            val newRecipeStep2 = bundle[RecipeContentFragment.STEP2].toString()
            draft = RecipeCreateResult(newRecipeTitle, newRecipeAuthor, newRecipeCategory, newRecipeStep1, newRecipeStep2)
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
            if (recipes.isEmpty()) {
                binding.list.visibility = View.GONE
                binding.emptySpace1.visibility = View.VISIBLE
            } else {
                binding.emptySpace1.visibility = View.GONE
                binding.list.visibility = View.VISIBLE
                adapter.submitList(recipes) {
                    val newRecipe = recipes.size > adapter.itemCount
                    if (newRecipe) {
                        binding.list.smoothScrollToPosition(0)
                    }
                }
            }
        }
        binding.fab.setOnClickListener {
            viewModel.onAddButtonClicked(draft)
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteButtonClicked()
        }

        //binding.searchBar.setOnQueryTextListener()

        val callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.list)
    }.root
}