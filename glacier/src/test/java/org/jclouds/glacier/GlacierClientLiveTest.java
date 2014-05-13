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
package org.jclouds.glacier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;

import org.jclouds.ContextBuilder;
import org.jclouds.glacier.domain.PaginatedVaultCollection;
import org.jclouds.glacier.domain.VaultMetadata;
import org.testng.annotations.Test;

/**
*
* @author Roman Coedo
*/
@Test(groups = { "integration", "live" })
public class GlacierClientLiveTest {

   static GlacierClient getGlacierClient() {
      return ContextBuilder.newBuilder("glacier").credentials("ADDME", "ADDME").buildApi(GlacierClient.class);
   }

   @Test(groups = { "integration", "live" })
   public void testDeleteVaultIfEmptyOrNotFound() throws Exception {
      assertTrue(getGlacierClient().deleteVaultIfEmpty("testDeleteVault"));
   }

   @Test(groups = { "integration", "live" })
   public void testCreateVault() throws Exception {
      GlacierClient client = getGlacierClient();
      String path = client.createVault("testCreateVault").toString();
      assertTrue(path.contains("https://glacier.us-east-1.amazonaws.com/"));
      assertTrue(path.contains("/vaults/testCreateVault"));
      client.deleteVaultIfEmpty("testCreateVault");
   }

   @Test(groups = { "integration", "live" })
   public void testDescribeVault() throws Exception {
      GlacierClient client = getGlacierClient();
      client.createVault("testDescribeVault");
      VaultMetadata vault = getGlacierClient().describeVault("testDescribeVault");
      assertEquals(vault.getVaultName(), "testDescribeVault");
      assertEquals(vault.getNumberOfArchives(), 0);
      assertEquals(vault.getSizeInBytes(), 0);
      assertEquals(vault.getLastInventoryDate(), null);
      client.deleteVaultIfEmpty("testDescribeVault");
   }

   @Test(groups = { "integration", "live" })
   public void testListVaults() throws Exception {
      GlacierClient client = getGlacierClient();
      client.createVault("testListVaults1");
      client.createVault("testListVaults2");
      client.createVault("testListVaults3");
      PaginatedVaultCollection vaults = getGlacierClient().listVaults();
      Iterator<VaultMetadata> i = vaults.iterator();
      assertEquals(i.next().getVaultName(), "testListVaults1");
      assertEquals(i.next().getVaultName(), "testListVaults2");
      assertEquals(i.next().getVaultName(), "testListVaults3");
      client.deleteVaultIfEmpty("testListVaults1");
      client.deleteVaultIfEmpty("testListVaults2");
      client.deleteVaultIfEmpty("testListVaults3");
   }
}
