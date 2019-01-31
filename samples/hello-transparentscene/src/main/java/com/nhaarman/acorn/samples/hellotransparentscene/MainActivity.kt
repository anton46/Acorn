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

package com.nhaarman.acorn.samples.hellotransparentscene

import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import com.nhaarman.acorn.android.AcornAppCompatActivity
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.Transition
import com.nhaarman.acorn.android.transition.TransitionFactory
import com.nhaarman.acorn.android.transition.transitionFactory
import com.nhaarman.acorn.android.util.inflateView
import com.nhaarman.acorn.navigation.experimental.CombinedContainer
import com.nhaarman.acorn.navigation.experimental.ExperimentalTransparentNavigator
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey
import com.nhaarman.acorn.state.ContainerState
import kotlinx.android.synthetic.main.first_scene.view.*
import kotlinx.android.synthetic.main.second_scene.view.*

@UseExperimental(ExperimentalTransparentNavigator::class)
class MainActivity : AcornAppCompatActivity() {

    override fun provideNavigatorProvider(): NavigatorProvider {
        return HelloNavigationNavigatorProvider
    }

    override fun provideViewControllerFactory(): ViewControllerFactory {
        return object : ViewControllerFactory {

            override fun supports(scene: Scene<*>): Boolean {
                return scene.key == SceneKey.defaultKey<SecondScene>()
            }

            override fun viewControllerFor(scene: Scene<*>, parent: ViewGroup): ViewController {
                return object : ViewController, com.nhaarman.acorn.navigation.experimental.CombinedContainer {

                    override val view: View by lazy {
                        parent.inflateView(R.layout.first_and_second_scene)
                    }

                    override val firstContainer: Container
                        get() = FirstSceneViewController(view.firstSceneRoot)

                    override val secondContainer: Container
                        get() = SecondSceneViewController(view.secondSceneRoot)

                    override fun saveInstanceState(): ContainerState {
                        return ContainerState()
                    }

                    override fun restoreInstanceState(bundle: ContainerState) {
                    }
                }
            }
        }
    }

    override fun provideTransitionFactory(viewControllerFactory: ViewControllerFactory): TransitionFactory {
        return transitionFactory(viewControllerFactory) {
            (SceneKey.defaultKey<FirstScene>() to SceneKey.defaultKey<SecondScene>()) use FirstSecondTransition
            (SceneKey.defaultKey<SecondScene>() to SceneKey.defaultKey<FirstScene>()) use SecondFirstTransition
        }
    }
}

@UseExperimental(ExperimentalTransparentNavigator::class)
object FirstSecondTransition : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        val secondScene = parent.inflateView(R.layout.second_scene)
        parent.addView(secondScene)

        val viewController = object : ViewController, com.nhaarman.acorn.navigation.experimental.CombinedContainer {

            override val view: View by lazy {
                parent
            }

            override val firstContainer: Container
                get() = FirstSceneViewController(view.firstSceneRoot)

            override val secondContainer: Container
                get() = SecondSceneViewController(view.secondSceneRoot)

            override fun saveInstanceState(): ContainerState {
                return ContainerState()
            }

            override fun restoreInstanceState(bundle: ContainerState) {
            }
        }

        callback.attach(viewController)

        parent.doOnPreDraw {
            secondScene.overlayView
                .apply {
                    alpha = 0f
                    animate().alpha(1f)
                }

            secondScene.cardView.apply {
                translationY = height.toFloat()
                animate().translationY(0f)
                    .withEndAction {
                        callback.onComplete(viewController)
                    }
            }
        }
    }
}

object SecondFirstTransition : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        val firstAndSecondRoot = parent.findViewById<ViewGroup>(R.id.firstAndSecondRoot)
        if (firstAndSecondRoot != null) {
            val firstRoot = firstAndSecondRoot.firstSceneRoot
            val secondRoot = firstAndSecondRoot.secondSceneRoot

            firstAndSecondRoot.removeView(firstRoot)
            parent.addView(firstRoot, 0)

            val viewController = FirstSceneViewController(parent)
            callback.attach(viewController)

            secondRoot.overlayView
                .animate()
                .alpha(0f)

            secondRoot.cardView
                .animate()
                .translationY(secondRoot.cardView.height.toFloat())
                .withEndAction {
                    parent.removeView(firstAndSecondRoot)
                    callback.onComplete(viewController)
                }

            return
        }

        val viewController = FirstSceneViewController(parent)
        callback.attach(viewController)

        val overlayView = parent.secondSceneRoot.overlayView
        val cardView = parent.secondSceneRoot.cardView

        overlayView.animate()
            .alpha(0f)

        cardView.animate()
            .translationY(cardView.height.toFloat())
            .withEndAction {
                parent.removeView(parent.secondSceneRoot)
                callback.onComplete(viewController)
            }
    }
}
