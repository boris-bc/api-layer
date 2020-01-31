package com.ca.mfaas.cache;/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
@RunWith(JUnit4.class)
public class CompositeKeyGeneratorWithoutLastTest {

    @Test
    public void testGenerate() {
        CompositeKeyGeneratorWithoutLast kg = new CompositeKeyGeneratorWithoutLast();

        assertEquals(
            new CompositeKey("a", "b"),
            kg.generate(null, null, new Object[] {"a", "b", "c"})
        );

        assertEquals(
            new CompositeKey(),
            kg.generate(null, null, new Object[] {"a"})
        );

        try {
            kg.generate(null, null);
        } catch (IllegalArgumentException e) {
            assertEquals("At least one argument with value is required", e.getMessage());
        }
    }

}
