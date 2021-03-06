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

package com.nhaarman.acorn.android.uistate

import com.nhaarman.acorn.android.util.RootViewGroup
import com.nhaarman.acorn.android.util.TestTransitionFactory
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.jupiter.api.Test

internal class NotVisibleTest {

    val root = spy(RootViewGroup())

    val state = NotVisible(
        root,
        TestTransitionFactory()
    )

    @Test
    fun `'uiNotVisible' makes no transition`() {
        expect(state.uiNotVisible()).toBe(state)
    }

    @Test
    fun `'withoutScene' makes no transition`() {
        expect(state.withoutScene()).toBe(state)
    }

    @Test
    fun `'uiVisible' results in Visible state`() {
        expect(state.uiVisible()).toBeInstanceOf<Visible>()
    }

    @Test
    fun `'withScene' results in NotVisibleWithDestination state`() {
        expect(state.withScene(mock(), mock(), null)).toBeInstanceOf<NotVisibleWithDestination>()
    }

    @Test
    fun `'withScene' does not touch root viewgroup`() {
        /* When */
        state.withScene(mock(), mock(), null)

        /* Then */
        verifyZeroInteractions(root)
    }
}