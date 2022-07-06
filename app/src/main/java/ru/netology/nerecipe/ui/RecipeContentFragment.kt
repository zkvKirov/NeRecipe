package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import ru.netology.nerecipe.databinding.RecipeContentFragmentBinding

class RecipeContentFragment : Fragment() {

    private val args by navArgs<RecipeContentFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RecipeContentFragmentBinding.inflate(layoutInflater, container, false).also { binding ->

        binding.category.setOnClickListener {
            if (binding.category.isFocused) {
                binding.chipsGroup.visibility = View.VISIBLE
            }
        }
        choiceChips(binding)

        binding.editTitle.setText(args.initialContent?.newTitle)
        binding.editAuthor.setText(args.initialContent?.newAuthor)
        binding.category.setText(args.initialContent?.newCategory)
        binding.editStep1.setText(args.initialContent?.newStep1)
        binding.editStep2.setText(args.initialContent?.newStep2)
        binding.editTitle.requestFocus(0)
        binding.ok.setOnClickListener {
            onOkButtonClicked(binding)
        }
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            val draft = Bundle(5)
            draft.putString(NEW_TITLE, binding.editTitle.text.toString())
            draft.putString(NEW_AUTHOR, binding.editAuthor.text.toString())
            draft.putString(NEW_CATEGORY, binding.category.text.toString())
            draft.putString(STEP1, binding.editStep1.text.toString())
            draft.putString(STEP2, binding.editStep2.text.toString())
            setFragmentResult(DRAFT_KEY, draft)
            Toast.makeText(context, "черновик рецепта сохранён", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }.root

    private fun choiceChips(binding: RecipeContentFragmentBinding) {
        binding.chipsGroup.setOnCheckedStateChangeListener { group, checkedId ->
            val chip: Chip? = group.findViewById(checkedId.first())
            chip?.let {
                binding.category.setText(it.text)
                binding.chipsGroup.visibility = View.GONE
            }
        }
    }

    private fun onOkButtonClicked(binding: RecipeContentFragmentBinding) {
        if (!binding.editTitle.text.isNullOrBlank()) {
            val resultBundle = Bundle(5)
            resultBundle.putString(NEW_TITLE, binding.editTitle.text.toString())
            resultBundle.putString(NEW_AUTHOR, binding.editAuthor.text.toString())
            resultBundle.putString(NEW_CATEGORY, binding.category.text.toString())
            resultBundle.putString(STEP1, binding.editStep1.text.toString())
            resultBundle.putString(STEP2, binding.editStep2.text.toString())
            setFragmentResult(REQUEST_KEY, resultBundle)
        }
        findNavController().popBackStack()
    }

    companion object {
        const val REQUEST_KEY = "createRecipe"
        const val DRAFT_KEY = "draftRecipe"
        const val NEW_TITLE = "newTitle"
        const val NEW_AUTHOR = "newAuthor"
        const val NEW_CATEGORY = "newCategory"
        const val STEP1 = "step1"
        const val STEP2 = "step2"
    }

}