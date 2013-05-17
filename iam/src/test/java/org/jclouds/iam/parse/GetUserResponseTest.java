/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.iam.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.iam.domain.User;
import org.jclouds.iam.xml.UserHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetUserResponseTest")
public class GetUserResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/get_user.xml");

      User expected = expected();

      UserHandler handler = injector.getInstance(UserHandler.class);
      User result = factory.create(handler).parse(is);

      assertEquals(result, expected);
      assertEquals(result.getPath(), expected.getPath());
      assertEquals(result.getName(), expected.getName());
   }

   public User expected() {
      return User.builder()
                 .path("/division_abc/subdivision_xyz/")
                 .name("Bob")
                 .id("AIDACKCEVSQ6C2EXAMPLE")
                 .arn("arn:aws:iam::123456789012:user/division_abc/subdivision_xyz/Bob")
                 .createDate(new SimpleDateFormatDateService().iso8601SecondsDateParse("2009-03-06T21:47:48Z")).build();
   }

}
