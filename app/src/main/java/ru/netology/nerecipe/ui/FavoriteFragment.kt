package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipeAdapter
import ru.netology.nerecipe.data.RecipeCard
import ru.netology.nerecipe.data.RecipeCreateResult
import ru.netology.nerecipe.databinding.FavoriteFragmentBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

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
        //adapter = RecipeAdapter(viewModel, recipeCardslist)
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
            recipeCardslist.addAll(recipes)
            adapter.notifyDataSetChanged()
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
//
//    @Deprecated("Deprecated in Java")
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//
//        inflater.inflate(R.menu.top_app_bar, menu)
//        val searchItem: MenuItem = menu.findItem(R.id.search)
//        val searchView: SearchView = searchItem.actionView as SearchView
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
//            android.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//                return false
//            }
//            override fun onQueryTextChange(msg: String): Boolean {
//                filter(msg)
//                return false
//            }
//        })
//        return
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
}