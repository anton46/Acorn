/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nhaarman.acorn.android.transition.internal

import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.DefaultTransitionFactory
import com.nhaarman.acorn.android.transition.Transition
import com.nhaarman.acorn.android.transition.TransitionFactory
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Scene

internal class BindingTransitionFactory(
    private val viewControllerFactory: ViewControllerFactory,
    private val bindings: Sequence<TransitionBinding>
) : TransitionFactory {

    private val delegate by lazy { DefaultTransitionFactory(viewControllerFactory) }

    override fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): Transition {
        return bindings
            .mapNotNull { it.transitionFor(previousScene, newScene, data) }
            .firstOrNull()
            ?: delegate.transitionFor(previousScene, newScene, data)
    }
}