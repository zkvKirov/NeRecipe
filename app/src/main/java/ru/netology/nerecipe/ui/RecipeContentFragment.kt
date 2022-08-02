package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.*
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.RecipeCreateResult
import ru.netology.nerecipe.data.StepCreateResult
import ru.netology.nerecipe.data.StepsCard
import ru.netology.nerecipe.databinding.RecipeContentFragmentBinding
import ru.netology.nerecipe.util.AndroidUtils
import ru.netology.nerecipe.viewModel.RecipeViewModel

class RecipeContentFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private var draftStep: StepCreateResult? = null
    private var stepsData: ArrayList<StepsCard> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    ) = RecipeContentFragmentBinding.inflate(layoutInflater, container, false).also { binding ->

        val items = listOf("Европейская", "Азиатская", "Паназиатская", "Восточная", "Американская", "Русская", "Средиземноморская")
        //val items = listOf("Europe", "Asia", "Pan-Asian", "Eastern", "American", "Russian", "Mediterranean")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.category.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.editTitle.editText?.setText(requireArguments().getString(INITIAL_CONTENT_ARGUMENTS_TITLE))
        binding.editAuthor.editText?.setText(requireArguments().getString(INITIAL_CONTENT_ARGUMENTS_AUTHOR))
        binding.category.editText?.setText(requireArguments().getString(INITIAL_CONTENT_ARGUMENTS_CATEGORY))

        binding.editTitle.requestFocus(0)

        binding.ok.setOnClickListener {
            onOkButtonClicked(binding)
        }

        binding.fabStep.setOnClickListener {
            viewModel.onAddStepClicked(draftStep)
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            val draft = Bundle(4)
            draft.putString(NEW_TITLE, binding.editTitle.editText?.text.toString())
            draft.putString(NEW_AUTHOR, binding.editAuthor.editText?.text.toString())
            draft.putString(NEW_CATEGORY, binding.category.editText?.text.toString())
            setFragmentResult(DRAFT_KEY, draft)
            Toast.makeText(context, "черновик рецепта сохранён", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            //findNavController().popBackStack()
        }

    }.root

    override fun onResume() {
        super.onResume()

        setFragmentResultListener(
            requestKey = StepContentFragment.REQUEST_KEY
        ) { requestKey, bundle ->
            if (requestKey != StepContentFragment.REQUEST_KEY) return@setFragmentResultListener
            val newContent = bundle[StepContentFragment.NEW_CONTENT].toString()
            val newPicture = bundle[StepContentFragment.NEW_PICTURE].toString()
            stepsData = viewModel.onSaveStepButtonClicked(
                StepCreateResult(
                    newContent,
                    newPicture
                )
            )
        }
    }

    private fun onOkButtonClicked(binding: RecipeContentFragmentBinding) {
        binding.editTitle.error = null
        binding.authorTextField.error = null
        binding.category.error = ""
        binding.category.isErrorEnabled = false
        if (binding.editTitle.editText?.text.isNullOrBlank()) {
            binding.titleTextField.error = getString(R.string.error)
        } else if (binding.editAuthor.editText?.text.isNullOrBlank()) {
            binding.authorTextField.error = getString(R.string.error)
        } else if (binding.category.editText?.text.isNullOrBlank()) {
            binding.category.error = getString(R.string.error)
        } else {
            val resultBundle = Bundle(4)
            resultBundle.putString(NEW_TITLE, binding.editTitle.editText?.text.toString())
            resultBundle.putString(NEW_AUTHOR, binding.editAuthor.editText?.text.toString())
            resultBundle.putString(NEW_CATEGORY, binding.category.editText?.text.toString())
            resultBundle.putSerializable(NEW_STEP, stepsData)
            setFragmentResult(RECIPE_KEY, resultBundle)
            parentFragmentManager.popBackStack()
            //findNavController().popBackStack()
        }
        AndroidUtils.hideKeyboard(binding.root)
    }

    companion object {
        const val RECIPE_KEY = "createRecipe"
        const val DRAFT_KEY = "draftRecipe"
        const val NEW_TITLE = "newTitle"
        const val NEW_AUTHOR = "newAuthor"
        const val NEW_CATEGORY = "newCategory"
        const val NEW_STEP = "newStep"

        private const val INITIAL_CONTENT_ARGUMENTS_TITLE = "InitialContentTitle"
        private const val INITIAL_CONTENT_ARGUMENTS_AUTHOR = "InitialContentAuthor"
        private const val INITIAL_CONTENT_ARGUMENTS_CATEGORY = "InitialContentCategory"
        private const val INITIAL_CONTENT_ARGUMENTS_STEPS = "InitialContentStepsCard"

        fun create(initialContent: RecipeCreateResult?) = RecipeContentFragment().apply {
            arguments = Bundle(4).also {
                it.putString(INITIAL_CONTENT_ARGUMENTS_TITLE, initialContent?.newTitle)
                it.putString(INITIAL_CONTENT_ARGUMENTS_AUTHOR, initialContent?.newAuthor)
                it.putString(INITIAL_CONTENT_ARGUMENTS_CATEGORY, initialContent?.newCategory)
                it.putSerializable(INITIAL_CONTENT_ARGUMENTS_STEPS, initialContent?.newStepsCard)
            }
        }
    }

}