package ru.netology.nerecipe.ui

import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.RecipeCard
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(R.layout.main_activity), SearchView.OnQueryTextListener {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        val searchItem: MenuItem = menu.findItem(R.id.search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return true
    }

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

    private fun filter(text: String) {
        val filteredlist: ArrayList<RecipeCard> = ArrayList()
        val adapter = RecipeFragment().adapter
        val recipeCardslist = RecipeFragment().recipeCardslist
        for (item in recipeCardslist) {
            if (item.title?.lowercase()?.contains(text.lowercase()) == true) {
                filteredlist.add(item)
            }
        }
        adapter.filterList(filteredlist)
    }
}