package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.data.StepsCard

interface StepInteractionListener {
    fun onStepRemoveClicked(stepsCard: StepsCard) // написать оба эти метода во viewmodel, имплементировать этот интерфейс
    fun onStepEditClicked(stepsCard: StepsCard)
}