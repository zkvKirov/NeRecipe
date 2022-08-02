package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.adapter.RecipeCardViewHolder
import ru.netology.nerecipe.adapter.StepAdapter
import ru.netology.nerecipe.data.RecipeCreateResult
import ru.netology.nerecipe.data.StepsCard
import ru.netology.nerecipe.databinding.FullRecipeFragmentBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FullRecipeFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val args by navArgs<FullRecipeFragmentArgs>()
    private lateinit var adapter: StepAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigateToRecipeContentScreenEvent.observe(this) {
            val direction = FullRecipeFragmentDirections.toRecipeContentFragment()
            findNavController().navigate(direction)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FullRecipeFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        val viewHolder = RecipeCardViewHolder(binding.fullRecipe, viewModel, viewModel)
        adapter = StepAdapter(viewModel)
        binding.fullRecipe.stepsList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            binding.fullRecipe.stepsList.visibility = View.VISIBLE
            val recipe = recipes.find {
                it.id == args.recipeId
            } ?: run {

                findNavController().navigateUp()
                return@observe
            }
            adapter.submitList(recipe.stepsCard)
            viewHolder.bind(recipe)
        }
    }.root

    override fun onResume() {
        super.onResume()

        setFragmentResultListener(
            requestKey = RecipeContentFragment.RECIPE_KEY
        ) { requestKey, bundle ->
            if (requestKey != RecipeContentFragment.RECIPE_KEY) return@setFragmentResultListener
            val newTitle = bundle[RecipeContentFragment.NEW_TITLE].toString()
            val newAuthor = bundle[RecipeContentFragment.NEW_AUTHOR].toString()
            val newCategory = bundle[RecipeContentFragment.NEW_CATEGORY].toString()
            val newSteps: ArrayList<StepsCard> = bundle[RecipeContentFragment.NEW_STEP] as ArrayList<StepsCard>

            viewModel.onSaveButtonClicked(RecipeCreateResult(newTitle, newAuthor, newCategory, newSteps))
        }
    }

}