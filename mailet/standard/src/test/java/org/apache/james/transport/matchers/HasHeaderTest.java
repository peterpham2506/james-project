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


package org.apache.james.transport.matchers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;
import org.apache.mailet.Matcher;
import org.apache.mailet.base.test.FakeMail;
import org.apache.mailet.base.test.FakeMatcherConfig;
import org.apache.mailet.base.test.MailUtil;
import org.junit.Before;
import org.junit.Test;

public class HasHeaderTest {

    private static final String HEADER_NAME_1 = "JUNIT";
    private static final String HEADER_NAME_2 = "defaultHeaderName";
    private static final String HEADER_VALUE_1 = "defaultHeaderValue";
    private static final String HEADER_VALUE_2 = "defaultHeaderValue2";

    private FakeMail mockedMail;
    private Matcher matcher;

    @Before
    public void setUp() throws Exception {
        MimeMessage mimeMessage = MailUtil.createMimeMessage(HEADER_NAME_1, HEADER_VALUE_1);
        mockedMail = MailUtil.createMockMail2Recipients(mimeMessage);
        matcher = new HasHeader();
    }

    @Test
    public void matchShouldReturnAddressesWhenRightHeaderNameWithoutValue() throws MessagingException {

        FakeMatcherConfig mci = FakeMatcherConfig.builder()
                .matcherName("HasHeader")
                .condition(HEADER_NAME_1)
                .build();

        matcher.init(mci);



        assertThat(matcher.match(mockedMail)).containsAll(mockedMail.getRecipients());
    }

    @Test
    public void matchShouldReturnNullWhenWrongHeaderNameWithoutValue() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder()
                .matcherName("HasHeader")
                .condition(HEADER_NAME_2)
                .build());

        assertThat(matcher.match(mockedMail)).isNull();
    }

    @Test
    public void matchShouldReturnAddressesWhenGoodHeaderNameAndValue() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder()
                .matcherName("HasHeader")
                .condition(HEADER_NAME_1 + "=" + HEADER_VALUE_1)
                .build());

        assertThat(matcher.match(mockedMail)).containsAll(mockedMail.getRecipients());
    }

    @Test
    public void matchShouldReturnNullWhenWrongValue() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder()
                .matcherName("HasHeader")
                .condition(HEADER_NAME_1 + "=" + HEADER_VALUE_2)
                .build());

        assertThat(matcher.match(mockedMail)).isNull();
    }

    @Test
    public void matchShouldReturnNullWhenWrongHeaderNameWithValueSpecified() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder()
                .matcherName("HasHeader")
                .condition(HEADER_NAME_2 + "=" + HEADER_VALUE_2)
                .build());

        assertThat(matcher.match(mockedMail)).isNull();
    }

    @Test
    public void matchShouldIgnoreExtraEquals() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder()
                .matcherName("HasHeader")
                .condition(HEADER_NAME_1 + "=" + HEADER_VALUE_1 + "=any")
                .build());

        assertThat(matcher.match(mockedMail)).containsAll(mockedMail.getRecipients());
    }

    @Test
    public void matchShouldNotMatchMailsWithNoHeaderWhenValueSpecified() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder()
                .matcherName("HasHeader")
                .condition(HEADER_NAME_1 + "=" + HEADER_VALUE_1)
                .build());

        Mail mail = MailUtil.createMockMail2Recipients(MailUtil.createMimeMessage());

        assertThat(matcher.match(mail)).isNull();
    }

    @Test
    public void matchShouldNotMatchMailsWithNoHeaderWhenValueNotSpecified() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder()
                .matcherName("HasHeader")
                .condition(HEADER_NAME_1)
                .build());

        Mail mail = MailUtil.createMockMail2Recipients(MailUtil.createMimeMessage());

        assertThat(matcher.match(mail)).isNull();
    }

    @Test
    public void matchShouldReturnNullWhenOneConditionIsNotTrue() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder()
                .matcherName("HasHeader")
                .condition(HEADER_NAME_1 + "+" + HEADER_NAME_2)
                .build());

        assertThat(matcher.match(mockedMail)).isNull();
    }

    @Test
    public void matchShouldReturnAddressesWhenAllConditionsMatch() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder()
                .matcherName("HasHeader")
                .condition(HEADER_NAME_1 + "+" + HEADER_NAME_2)
                .build());

        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        mimeMessage.addHeader(HEADER_NAME_1, HEADER_VALUE_1);
        mimeMessage.addHeader(HEADER_NAME_2, HEADER_VALUE_2);
        mimeMessage.saveChanges();
        Mail mail = MailUtil.createMockMail2Recipients(mimeMessage);

        assertThat(matcher.match(mail)).containsAll(mockedMail.getRecipients());
    }

    @Test
    public void matchShouldFindTheRightHeaderLineWhenUsedWithValue() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder()
                .matcherName("HasHeader")
                .condition(HEADER_NAME_1 + "=" + HEADER_VALUE_2)
                .build());

        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        mimeMessage.addHeader(HEADER_NAME_1, HEADER_VALUE_1);
        mimeMessage.addHeader(HEADER_NAME_1, HEADER_VALUE_2);
        mimeMessage.saveChanges();
        Mail mail = MailUtil.createMockMail2Recipients(mimeMessage);

        assertThat(matcher.match(mail)).containsAll(mockedMail.getRecipients());
    }
}
