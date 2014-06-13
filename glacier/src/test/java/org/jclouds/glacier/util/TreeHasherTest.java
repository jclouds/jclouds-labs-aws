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
package org.jclouds.glacier.util;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.io.Payload;
import org.jclouds.io.payloads.ByteSourcePayload;
import org.testng.annotations.Test;

import com.google.common.io.ByteSource;

@Test(groups = "unit", testName = "TreeHasherTest")
public class TreeHasherTest {

   private static int MB = 1024 * 1024;

   @Test
   public void testTreeHasherWith1MBPayload() throws IOException {
      Payload payload = new ByteSourcePayload(getData(1 * MB));
      TreeHasher hasher1 = new TreeHasher(payload);
      assertEquals(hasher1.getHash(), "9bc1b2a288b26af7257a36277ae3816a7d4f16e89c1e7e77d0a5c48bad62b360");
      assertEquals(hasher1.getTreeHash(), "9bc1b2a288b26af7257a36277ae3816a7d4f16e89c1e7e77d0a5c48bad62b360");
   }

   @Test
   public void testTreeHasherWith2MBPayload() throws IOException {
      Payload payload2 = new ByteSourcePayload(getData(2 * MB));
      TreeHasher hasher2 = new TreeHasher(payload2);
      assertEquals(hasher2.getHash(), "5256ec18f11624025905d057d6befb03d77b243511ac5f77ed5e0221ce6d84b5");
      assertEquals(hasher2.getTreeHash(), "560c2c9333c719cb00cfdffee3ba293db17f58743cdd1f7e4055373ae6300afa");
   }

   private ByteSource getData(int size) {
      byte[] data = new byte[size];
      for (int i = 0; i < size; i++) {
         data[i] = 'a';
      }
      return ByteSource.wrap(data);
   }
}
