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
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipeAdapter
import ru.netology.nerecipe.data.RecipeCard
import ru.netology.nerecipe.data.RecipeCreateResult
import ru.netology.nerecipe.databinding.FavoriteFragmentBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel
import ru.netology.nmedia.util.SingleLiveEvent

class FavoriteFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private lateinit var recipeCardslist: ArrayList<RecipeCard>
    private lateinit var adapter: RecipeAdapter

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
        recipeCardslist = ArrayList()
        adapter = RecipeAdapter(viewModel)
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
            recipeCardslist.addAll(favorite)
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
            val newSteps = bundle[RecipeContentFragment.STEP].toString()
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
                            filter(userInput)
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
                    else -> return false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun filter(text: String) {
        val filteredlist: ArrayList<RecipeCard> = ArrayList()
        for (item in recipeCardslist) {
            if (item.title.lowercase().contains(text.lowercase())) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(context, "No Data Found...", Toast.LENGTH_SHORT).show()
            filteredlist.clear()
            adapter.submitList(filteredlist)
        } else {
            adapter.submitList(filteredlist)
        }
    }

    private val navigateToCheckboxFragment = SingleLiveEvent<Unit>()

    private fun onFilterButtonClicked() {
        navigateToCheckboxFragment.call()
    }

}