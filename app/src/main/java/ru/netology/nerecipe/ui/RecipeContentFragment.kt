package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.RecipeContentFragmentBinding
import ru.netology.nerecipe.util.AndroidUtils

class RecipeContentFragment : Fragment() {

    private val args by navArgs<RecipeContentFragmentArgs>()

      // код для добавления поля edit text по нажатию на кнопке
//    private lateinit var mLayout: ConstraintLayout
//    private lateinit var mEditText: TextInputLayout
//    private lateinit var mButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RecipeContentFragmentBinding.inflate(layoutInflater, container, false).also { binding ->

        val items = listOf("Европейская", "Азиатская", "Паназиатская", "Восточная", "Американская", "Русская", "Средиземноморская")
        //val items = listOf("Europe", "Asia", "Pan-Asian", "Eastern", "American", "Russian", "Mediterranean")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.category.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.editTitle.editText?.setText(args.recipeInitialContent?.newTitle).toString()
        binding.editAuthor.editText?.setText(args.recipeInitialContent?.newAuthor).toString()
        binding.category.editText?.setText(args.recipeInitialContent?.newCategory).toString()

        binding.editTitle.requestFocus(0)

        binding.ok.setOnClickListener {
            onOkButtonClicked(binding)
        }

        binding.fabStep.setOnClickListener {
            onFabStepButtonClicked(binding)
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
            findNavController().popBackStack()
        }

    }.root

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
            //resultBundle.putParcelableArrayList()
            setFragmentResult(REQUEST_KEY, resultBundle)
            findNavController().popBackStack()
        }
        AndroidUtils.hideKeyboard(binding.root)
    }

    private fun onFabStepButtonClicked(binding: RecipeContentFragmentBinding) {
        if (binding.editStep.editText?.text.isNullOrBlank()) {
            Toast.makeText(context, "сначала заполните поле шаг, пред тем как добавлять новый", Toast.LENGTH_SHORT).show()
        } else {

        }
    }

    companion object {
        const val REQUEST_KEY = "createRecipe"
        const val DRAFT_KEY = "draftRecipe"
        const val NEW_TITLE = "newTitle"
        const val NEW_AUTHOR = "newAuthor"
        const val NEW_CATEGORY = "newCategory"
        const val STEP = "step"
    }

}