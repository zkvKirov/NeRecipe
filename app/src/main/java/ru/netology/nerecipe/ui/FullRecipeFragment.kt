package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipeAdapter
import ru.netology.nerecipe.databinding.FullRecipeFragmentBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FullRecipeFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FullRecipeFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        val adapter = RecipeAdapter(viewModel)
        binding.fullRecipe // сюда надо передать целый заполненный пост!
    }.root

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.full_recipe_fragment, container, false)
//    }
}