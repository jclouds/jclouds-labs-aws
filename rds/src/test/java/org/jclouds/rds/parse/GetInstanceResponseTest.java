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
package org.jclouds.rds.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.rds.domain.Instance;
import org.jclouds.rds.xml.InstanceHandler;
import org.testng.annotations.Test;

import com.google.common.net.HostAndPort;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetInstanceResponseTest")
public class GetInstanceResponseTest extends BaseHandlerTest {
   protected final DateService dateService = new SimpleDateFormatDateService();

   public void test() {
      InputStream is = getClass().getResourceAsStream("/get_instance.xml");

      Instance expected = expected();

      InstanceHandler handler = injector.getInstance(InstanceHandler.class);
      Instance result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
  }

   public Instance expected() {
      return Instance.builder()
                     .engine("mysql")
                     .multiAZ(false)
                     .licenseModel("general-public-license")
                     .rawStatus("available")
                     .status(Instance.Status.AVAILABLE)
                     .engineVersion("5.1.50")
                     .endpoint(HostAndPort.fromParts("simcoprod01.cu7u2t4uz396.us-east-1.rds.amazonaws.com", 3306))
                     .id("simcoprod01")
                     .securityGroupNameToStatus("default", "active")
                     .availabilityZone("us-east-1a")
                     .createdTime(dateService.iso8601DateParse("2011-05-23T06:06:43.110Z"))
                     .allocatedStorageGB(10)
                     .instanceClass("db.m1.large")
                     .masterUsername("master").build();
   }
}
