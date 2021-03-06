/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package org.apache.james.mailbox;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MessageUidTest {

    @Rule public ExpectedException exception = ExpectedException.none();
    
    private static final MessageUid _1 = MessageUid.of(1);
    private static final MessageUid _2 = MessageUid.of(2);
    private static final MessageUid _3 = MessageUid.of(3);
    private static final MessageUid _4 = MessageUid.of(4);

    @Test
    public void distanceShouldReturnZeroWhenSameValue() {
        assertThat(_1.distance(_1)).isEqualTo(0);
    }

    @Test
    public void distanceShouldThrowWhenNullArgument() {
        exception.expect(NullPointerException.class);
        _1.distance(null);
    }


    @Test
    public void distanceShouldReturnPositiveDistanceWhenGreaterArgument() {
        assertThat(_1.distance(_4)).isEqualTo(3);
    }
    
    @Test
    public void distanceShouldReturnNegativeDistanceWhenSmallerArgument() {
        assertThat(_3.distance(_2)).isEqualTo(-1);
    }
}
