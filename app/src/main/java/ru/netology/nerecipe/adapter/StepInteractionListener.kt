package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.data.StepsCard

interface StepInteractionListener {
    fun onStepRemoveClicked(stepsCard: StepsCard)
    fun onStepEditClicked(stepsCard: StepsCard)
}