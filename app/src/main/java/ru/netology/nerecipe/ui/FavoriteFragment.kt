package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipeAdapter
import ru.netology.nerecipe.data.RecipeCard
import ru.netology.nerecipe.data.RecipeCreateResult
import ru.netology.nerecipe.data.StepsCard
import ru.netology.nerecipe.databinding.FavoriteFragmentBinding
import ru.netology.nerecipe.helper.SimpleItemTouchHelperCallback
import ru.netology.nerecipe.viewModel.RecipeViewModel
import ru.netology.nmedia.util.SingleLiveEvent

class FavoriteFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var recipeCardsList: ArrayList<RecipeCard> = ArrayList()
    private var checkboxes: MutableList<String> = ArrayList()
    private val filteredList: ArrayList<RecipeCard> = ArrayList()
    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigateToRecipeContentScreenEvent.observe(this) {
            parentFragmentManager.commit {
                replace(R.id.nav_host_fragment, RecipeContentFragment.create(it))
                addToBackStack(null)
            }
        }

        viewModel.navigateToRecipeFragment.observe(this) {
            val direction = FavoriteFragmentDirections.toRecipeFragment()
            findNavController().navigate(direction)
        }
        viewModel.navigateToFullRecipeFragment.observe(this) {
            val direction = FavoriteFragmentDirections.toFullRecipeFragment(it)
            findNavController().navigate(direction)
        }
        navigateToCheckboxFragment.observe(this) {
            val direction = FavoriteFragmentDirections.toCheckboxFragment()
            findNavController().navigate(direction)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FavoriteFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        adapter = RecipeAdapter(viewModel, viewModel)
        binding.listFavorite.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            val favorite = recipes.filter { it.isFavorite }
            if (filteredList.isNotEmpty()) {
                binding.emptySpace2.visibility = View.GONE
                binding.listFavorite.visibility = View.VISIBLE
                adapter.submitList(filteredList)
            } else {
                if (favorite.isEmpty()) {
                    binding.listFavorite.visibility = View.GONE
                    binding.emptySpace2.visibility = View.VISIBLE
                } else {
                    binding.emptySpace2.visibility = View.GONE
                    binding.listFavorite.visibility = View.VISIBLE
                    adapter.submitList(favorite)
                }
                recipeCardsList.clear()
                recipeCardsList.addAll(favorite)
            }
        }

        filteredList.clear()

        binding.recipeButton.setOnClickListener {
            viewModel.onRecipeButtonClicked()
        }

        val callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.listFavorite)

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
            requestKey = RecipeContentFragment.RECIPE_KEY
        ) { requestKey, bundle ->
            if (requestKey != RecipeContentFragment.RECIPE_KEY) return@setFragmentResultListener
            val newTitle = bundle[RecipeContentFragment.NEW_TITLE].toString()
            val newAuthor = bundle[RecipeContentFragment.NEW_AUTHOR].toString()
            val newCategory = bundle[RecipeContentFragment.NEW_CATEGORY].toString()
            val newSteps: ArrayList<StepsCard> =
                if (bundle[RecipeContentFragment.NEW_STEP] != null) {
                    bundle[RecipeContentFragment.NEW_STEP] as ArrayList<StepsCard>
                } else {
                    ArrayList()
                }
            viewModel.onSaveButtonClicked(
                RecipeCreateResult(
                    newTitle,
                    newAuthor,
                    newCategory,
                    newSteps
                )
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
            Toast.makeText(context, "Ничего не найдено", Toast.LENGTH_SHORT).show()
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
        for (item in recipeCardsList) {
            for (text in arrayList) {
                if (item.category.lowercase() == text.lowercase())
                    filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(context, "Таких рецептов нет", Toast.LENGTH_SHORT).show()
            filteredList.clear()
            adapter.submitList(filteredList)
        } else {
            adapter.submitList(filteredList)
        }
        return filteredList
    }

}