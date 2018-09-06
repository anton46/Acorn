/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.android.presentation

import android.support.test.InstrumentationRegistry
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.nhaarman.bravo.android.test.R
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.expect.expect
import org.junit.Test

internal class WrappedLayoutResourceViewCreatorTest {

    @Test
    fun properViewIsReturned() {
        /* Given */
        val viewGroup = FrameLayout(InstrumentationRegistry.getContext())
        val creator = WrappedLayoutResourceViewCreator<View>(R.layout.linearlayout) {
            MyContainer(it)
        }

        /* When */
        val result = creator.create(viewGroup)

        /* Then */
        expect(result.view).toBeInstanceOf<LinearLayout>()
    }

    @Test
    fun properContainerIsReturned() {
        /* Given */
        val viewGroup = FrameLayout(InstrumentationRegistry.getContext())
        val creator = WrappedLayoutResourceViewCreator<View>(R.layout.linearlayout) {
            MyContainer(it)
        }

        /* When */
        val result = creator.create(viewGroup)

        /* Then */
        expect(result.container).toBeInstanceOf<MyContainer> {
            expect(it.v).toBe(result.view)
        }
    }

    private class MyContainer(val v: View) : Container
}