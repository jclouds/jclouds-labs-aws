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
package org.jclouds.glacier.predicates.validators;

import org.jclouds.io.Payload;
import org.jclouds.io.payloads.ByteSourcePayload;
import org.testng.annotations.Test;

import com.google.common.io.ByteSource;

@Test(groups = "unit", testName = "PayloadValidatorTest")
public class PayloadValidatorTest {

   private static final PayloadValidator VALIDATOR = new PayloadValidator();

   public void testValidate() {
      byte[] data = "test string data".getBytes();
      Payload payload = new ByteSourcePayload(ByteSource.wrap(data));
      payload.getContentMetadata().setContentLength((long)data.length);
      VALIDATOR.validate(payload);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNoContentLength() {
      byte[] data = "test string data".getBytes();
      Payload payload = new ByteSourcePayload(ByteSource.wrap(data));
      payload.getContentMetadata().setContentType("text/plain");
      VALIDATOR.validate(payload);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNullPayload() {
      VALIDATOR.validate(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testContentLengthTooBig() {
      byte[] data = "test string data".getBytes();
      Payload payload = new ByteSourcePayload(ByteSource.wrap(data));
      payload.getContentMetadata().setContentLength(42949672960L);
      VALIDATOR.validate(payload);
   }
}
