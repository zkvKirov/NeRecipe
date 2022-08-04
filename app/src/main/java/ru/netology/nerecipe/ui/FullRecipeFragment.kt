package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipeCardViewHolder
import ru.netology.nerecipe.adapter.StepAdapter
import ru.netology.nerecipe.data.RecipeCreateResult
import ru.netology.nerecipe.data.StepCreateResult
import ru.netology.nerecipe.data.StepsCard
import ru.netology.nerecipe.databinding.FullRecipeFragmentBinding
import ru.netology.nerecipe.helper.SimpleItemTouchHelperCallback
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
            parentFragmentManager.commit {
                replace(R.id.nav_host_fragment, RecipeContentFragment.create(it))
                addToBackStack(null)
            }
        }

        viewModel.navigateToStepContentScreenEvent.observe(this) {
            parentFragmentManager.commit {
                replace(R.id.nav_host_fragment, StepContentFragment(it))
                addToBackStack(null)
            }
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
            adapter.notifyDataSetChanged()
            viewHolder.bind(recipe)

        }

        val callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.fullRecipe.stepsList)

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
            val newSteps: ArrayList<StepsCard> =
                if (bundle[RecipeContentFragment.NEW_STEP] != null) {
                    bundle[RecipeContentFragment.NEW_STEP] as ArrayList<StepsCard>
                } else {
                    ArrayList()
                }
            viewModel.onSaveButtonClicked(
                RecipeCreateResult(
                    newTitle, newAuthor, newCategory, newSteps
                )
            )
        }

        setFragmentResultListener(
            requestKey = StepContentFragment.REQUEST_KEY
        ) { requestKey, bundle ->
            if (requestKey != StepContentFragment.REQUEST_KEY) return@setFragmentResultListener
            val newContent = bundle[StepContentFragment.NEW_CONTENT].toString()
            val newPicture = bundle[StepContentFragment.NEW_PICTURE].toString()
            viewModel.onSaveStepButtonClicked(
                StepCreateResult(
                    newContent,
                    newPicture
                )
            )
        }
    }

}