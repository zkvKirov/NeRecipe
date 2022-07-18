package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipeAdapter
import ru.netology.nerecipe.data.RecipeCard
import ru.netology.nerecipe.data.RecipeCreateResult
import ru.netology.nerecipe.databinding.RecipeFragmentBinding
import ru.netology.nerecipe.helper.SimpleItemTouchHelperCallback
import ru.netology.nerecipe.viewModel.RecipeViewModel
import ru.netology.nmedia.util.SingleLiveEvent
import kotlin.collections.ArrayList

class RecipeFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var draft: RecipeCreateResult? = null
    private var checkboxes: MutableList<String> = ArrayList()
    private var recipeCardsList: ArrayList<RecipeCard> = ArrayList()
    private val filteredList: ArrayList<RecipeCard> = ArrayList()
    private lateinit var adapter: RecipeAdapter

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
        navigateToCheckboxFragment.observe(this) {
            val direction = RecipeFragmentDirections.toCheckboxFragment()
            findNavController().navigate(direction)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RecipeFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        adapter = RecipeAdapter(viewModel, recipeCardsList)
        binding.listOfRecipes.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            if (filteredList.isNotEmpty()) {
                binding.emptySpace1.visibility = View.GONE
                binding.listOfRecipes.visibility = View.VISIBLE
                adapter.submitList(filteredList)
                recipeCardsList.addAll(filteredList)
            } else {
                if (recipes.isEmpty()) {
                    binding.listOfRecipes.visibility = View.GONE
                    binding.emptySpace1.visibility = View.VISIBLE
                } else {
                    binding.emptySpace1.visibility = View.GONE
                    binding.listOfRecipes.visibility = View.VISIBLE
                    adapter.submitList(recipes) {
                        val newRecipe = recipes.size > adapter.itemCount
                        if (newRecipe) {
                            binding.listOfRecipes.smoothScrollToPosition(0)
                        }
                    }
                }
                recipeCardsList.addAll(recipes)
            }
        }

        filteredList.clear()

        binding.fab.setOnClickListener {
            viewModel.onAddButtonClicked(draft)
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteButtonClicked()
        }

        // код для drag&drop
        val callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.listOfRecipes)

    }.root

    override fun onResume() {
        super.onResume()

        setFragmentResultListener(
            requestKey = CheckboxFragment.CHECKBOX_KEY
        ) { requestKey, bundle ->
            if (requestKey != CheckboxFragment.CHECKBOX_KEY) return@setFragmentResultListener
            val line = bundle[CheckboxFragment.CHECKBOX_KEY].toString()
            var word: String
            if (line.contains(",")) checkboxes = line.split(", ") as MutableList<String> else checkboxes.add(line)
            if (checkboxes.size == 1) {
                word = checkboxes[0].substring(1)
                word = word.substring(0, word.length-1)
                checkboxes[0] = word
            } else {
                checkboxes.forEachIndexed { index, it ->
                    if(it.contains('[')) {
                        word = it.substring(1)
                        checkboxes[index] = word
                    }
                    if(it.contains(']')) {
                        word = it.substring(0, it.length-1)
                        checkboxes[index] = word
                    }
                }
            }
            filterFilter(checkboxes)
            checkboxes.clear()
        }

        setFragmentResultListener(
            requestKey = RecipeContentFragment.REQUEST_KEY
        ) { requestKey, bundle ->
            if (requestKey != RecipeContentFragment.REQUEST_KEY) return@setFragmentResultListener
            val newTitle = bundle[RecipeContentFragment.NEW_TITLE].toString()
            val newAuthor = bundle[RecipeContentFragment.NEW_AUTHOR].toString()
            val newCategory = bundle[RecipeContentFragment.NEW_CATEGORY].toString()
            val newSteps = bundle[RecipeContentFragment.STEP].toString()
            viewModel.onSaveButtonClicked(
                RecipeCreateResult(
                    newTitle,
                    newAuthor,
                    newCategory
                )
            )
            draft = null
        }

        setFragmentResultListener(
            requestKey = RecipeContentFragment.DRAFT_KEY
        ) { requestKey, bundle ->
            if (requestKey != RecipeContentFragment.DRAFT_KEY) return@setFragmentResultListener
            val newRecipeTitle = bundle[RecipeContentFragment.NEW_TITLE].toString()
            val newRecipeAuthor = bundle[RecipeContentFragment.NEW_AUTHOR].toString()
            val newRecipeCategory = bundle[RecipeContentFragment.NEW_CATEGORY].toString()
            val newRecipeSteps = bundle[RecipeContentFragment.STEP].toString()
            draft = RecipeCreateResult(
                newRecipeTitle,
                newRecipeAuthor,
                newRecipeCategory
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_app_bar, menu)
                val searchItem: MenuItem = menu.findItem(R.id.search)
                val searchView: SearchView = searchItem.actionView as SearchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    android.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        val userInput = newText?.lowercase()
                        if (userInput != null) {
                            searchFilter(userInput)
                        }
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.search -> {
                        true
                    }
                    R.id.filter -> {
                        onFilterButtonClicked()
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun searchFilter(text: String) {
        val searchList: ArrayList<RecipeCard> = ArrayList()
        for (item in recipeCardsList) {
            if (item.title.lowercase().contains(text.lowercase())) {
                searchList.add(item)
            }
        }
        if (searchList.isEmpty()) {
            Toast.makeText(context, "No Data Found...", Toast.LENGTH_SHORT).show()
            searchList.clear()
            adapter.submitList(searchList)
        } else {
            adapter.submitList(searchList)
        }
    }

    private val navigateToCheckboxFragment = SingleLiveEvent<Unit>()

    private fun onFilterButtonClicked() {
        navigateToCheckboxFragment.call()
    }

    private fun filterFilter(arrayList: List<String>) : ArrayList<RecipeCard> {
        //val filteredList: ArrayList<RecipeCard> = ArrayList()
        for (item in recipeCardsList) {
            for (text in arrayList) {
                if (item.category.lowercase() == text.lowercase())
                    filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(context, "No Found...", Toast.LENGTH_SHORT).show()
            filteredList.clear()
            adapter.submitList(filteredList)
        } else {
            adapter.submitList(filteredList)
        }
        return filteredList
    }
}