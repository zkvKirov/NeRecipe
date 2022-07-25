package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.RecipeCreateResult
import ru.netology.nerecipe.data.StepCreateResult
import ru.netology.nerecipe.data.StepsCard
import ru.netology.nerecipe.databinding.RecipeContentFragmentBinding
import ru.netology.nerecipe.util.AndroidUtils
import ru.netology.nerecipe.viewModel.RecipeViewModel

class RecipeContentFragment(
    private val initialContent: RecipeCreateResult?
) : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private var draftStep: StepCreateResult? = null
    private var stepsData: ArrayList<StepsCard> = ArrayList()

    //private val args by navArgs<RecipeContentFragmentArgs>()

      // код для добавления поля edit text по нажатию на кнопке
//    private lateinit var mLayout: ConstraintLayout
//    private lateinit var mEditText: TextInputLayout
//    private lateinit var mButton: FloatingActionButton

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

        binding.editTitle.editText?.setText(initialContent?.newTitle)
        binding.editAuthor.editText?.setText(initialContent?.newAuthor)
        binding.category.editText?.setText(initialContent?.newCategory)

        binding.editTitle.requestFocus(0)

        binding.ok.setOnClickListener {
            onOkButtonClicked(binding)
        }

        binding.fabStep.setOnClickListener {
            viewModel.onAddStepClicked(draftStep)
        }

        // код для добавления поля edit text по нажатию на кнопке
//        mLayout = binding.recipeEditWindow
//        mEditText = binding.editStep
//        mButton = binding.fabStep
//        //mButton.setOnClickListener(onFabStepButtonClicked()) // (2 вариант)
//        mButton.setOnClickListener {
//            val lParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
//            lParams.addRule(RelativeLayout.BELOW, R.id.edit_step)
//            mLayout.addView(mEditText, lParams)
//        }

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

    // код для добавления поля edit text по нажатию на кнопке (2 вариант)
//    private fun onFabStepButtonClicked(): View.OnClickListener? {//
//        mLayout.addView(createNewTextView())
//        return
//    }

//    private fun createNewTextView(): View {
//        val lParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
//        val textView: TextView = TextView(context)
//        textView.layoutParams = lParams
//        return textView
//    }

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
            setFragmentResult(REQUEST_KEY, resultBundle)
            parentFragmentManager.popBackStack()
            //findNavController().popBackStack()
        }
        AndroidUtils.hideKeyboard(binding.root)
    }

    companion object {
        const val REQUEST_KEY = "createRecipe"
        const val DRAFT_KEY = "draftRecipe"
        const val NEW_TITLE = "newTitle"
        const val NEW_AUTHOR = "newAuthor"
        const val NEW_CATEGORY = "newCategory"
        const val NEW_STEP = "step"
    }

}