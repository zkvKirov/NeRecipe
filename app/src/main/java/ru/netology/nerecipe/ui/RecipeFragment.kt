package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
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

class RecipeFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var draft: RecipeCreateResult? = null

    lateinit var recipeCardslist: ArrayList<RecipeCard>
    lateinit var adapter: RecipeAdapter

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
        recipeCardslist = ArrayList()
        adapter = RecipeAdapter(viewModel)
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
            recipeCardslist.addAll(recipes)
            adapter.notifyDataSetChanged()
        }

        binding.fab.setOnClickListener {
            viewModel.onAddButtonClicked(draft)
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteButtonClicked()
        }

        // код для drag&drop
        val callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.list)

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

//    @Deprecated("Deprecated in Java")
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//
//        inflater.inflate(R.menu.top_app_bar, menu)
//        val searchItem: MenuItem = menu.findItem(R.id.search)
//        val searchView: SearchView = searchItem.actionView as SearchView
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
//            android.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//            override fun onQueryTextChange(newText: String?): Boolean {
//                val userInput = newText?.lowercase()
//                if (userInput != null) {
//                    filter(userInput)
//                }
//                return true
//            }
//        })
//    }
//
//    private fun filter(text: String) {
//        val filteredlist: ArrayList<RecipeCard> = ArrayList()
//        for (item in recipeCardslist) {
//            if (item.title?.lowercase()?.contains(text.lowercase()) == true) {
//                filteredlist.add(item)
//            }
//        }
//        if (filteredlist.isEmpty()) {
//            Toast.makeText(context, "No Data Found...", Toast.LENGTH_SHORT).show()
//        } else {
//            adapter.filterList(filteredlist)
//        }
//    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                // TODO написать код
                true
            }
            R.id.filter -> {
                onFilterButtonClicked() // - навигации не происходит
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private val navigateToCheckboxFragment = SingleLiveEvent<Unit>()

    private fun onFilterButtonClicked() {
        navigateToCheckboxFragment.call()
    }
}