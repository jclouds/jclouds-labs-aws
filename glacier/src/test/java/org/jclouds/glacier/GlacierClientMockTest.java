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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.jclouds.glacier.util.TestUtils.buildPayload;
import static org.jclouds.util.Strings2.urlEncode;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.glacier.domain.PaginatedVaultCollection;
import org.jclouds.glacier.domain.VaultMetadata;
import org.jclouds.glacier.options.PaginationOptions;
import org.jclouds.glacier.reference.GlacierHeaders;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.google.inject.Module;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.RecordedRequest;

/**
 * Mock test for Glacier.
 */
@Test(singleThreaded = true)
public class GlacierClientMockTest {

   private static final String REQUEST_ID = "AAABZpJrTyioDC_HsOmHae8EZp_uBSJr6cnGOLKp_XJCl-Q";
   private static final String DATE = "Sun, 25 Mar 2012 12:00:00 GMT";
   private static final String HTTP = "HTTP/1.1";
   private static final String VAULT_NAME = "examplevault";
   private static final String VAULT_NAME1 = "examplevault1";
   private static final String VAULT_NAME2 = "examplevault2";
   private static final String VAULT_NAME3 = "examplevault3";
   private static final String LOCATION = "/111122223333/";
   private static final String VAULT_LOCATION = LOCATION + "vaults/" + VAULT_NAME;
   private static final String VAULT_ARN_PREFIX = "arn:aws:glacier:us-east-1:012345678901:vaults/";
   private static final String VAULT_ARN = VAULT_ARN_PREFIX + VAULT_NAME;
   private static final String VAULT_ARN1 = VAULT_ARN_PREFIX + VAULT_NAME1;
   private static final String VAULT_ARN3 = VAULT_ARN_PREFIX + VAULT_NAME3;
   private static final String ARCHIVE_ID = "NkbByEejwEggmBz2fTHgJrg0XBoDfjP4q6iu87-TjhqG6eGoOY9Z8i1_AUyUsuhPAdTqLHy8pTl5nfCFJmDl2yEZONi5L26Omw12vcs01MNGntHEQL8MBfGlqrEXAMPLEArchiveId";
   private static final String ARCHIVE_LOCATION = VAULT_LOCATION + "/archives/" + ARCHIVE_ID;
   private static final String TREEHASH = "beb0fe31a1c7ca8c6c04d574ea906e3f97b31fdca7571defb5b44dca89b5af60";
   private static final String DESCRIPTION = "test description";
   private static final Set<Module> modules = ImmutableSet.<Module> of(new ExecutorServiceModule(sameThreadExecutor(),
         sameThreadExecutor()));

   private MockWebServer server;
   private GlacierClient client;

   private static GlacierClient getGlacierClient(URL server) {
      Properties overrides = new Properties();
      // prevent expect-100 bug http://code.google.com/p/mockwebserver/issues/detail?id=6
      overrides.setProperty(PROPERTY_SO_TIMEOUT, "0");
      overrides.setProperty(PROPERTY_MAX_RETRIES, "1");
      return ContextBuilder.newBuilder("glacier").credentials("accessKey", "secretKey").endpoint(server.toString())
            .modules(modules).overrides(overrides).buildApi(GlacierClient.class);
   }

   private static MockResponse buildBaseResponse(int responseCode) {
      MockResponse mr = new MockResponse();
      mr.setResponseCode(responseCode);
      mr.addHeader(GlacierHeaders.REQUEST_ID, REQUEST_ID);
      mr.addHeader(HttpHeaders.DATE, DATE);
      return mr;
   }

   private static String getResponseBody(String path) throws IOException {
      return Resources.toString(Resources.getResource(GlacierClientMockTest.class, path), UTF_8);
   }

   @BeforeTest
   private void initServer() throws IOException {
      server = new MockWebServer();
      server.play();
      client = getGlacierClient(server.getUrl("/"));
   }

   @AfterTest
   private void shutdownServer() throws IOException {
      server.shutdown();
   }

   @Test
   public void testCreateVault() throws InterruptedException {
      MockResponse mr = buildBaseResponse(201);
      mr.addHeader(HttpHeaders.LOCATION, VAULT_LOCATION);
      server.enqueue(mr);

      URI responseUri = client.createVault(VAULT_NAME);
      assertEquals(responseUri.toString(), server.getUrl("/") + VAULT_LOCATION.substring(1));
      assertEquals(server.takeRequest().getRequestLine(), "PUT /-/vaults/" + VAULT_NAME + " " + HTTP);
   }

   @Test
   public void testDeleteVault() throws InterruptedException {
      server.enqueue(buildBaseResponse(204));

      assertTrue(client.deleteVault(VAULT_NAME));
      assertEquals(server.takeRequest().getRequestLine(), "DELETE /-/vaults/" + VAULT_NAME + " " + HTTP);
   }

   @Test
   public void testDescribeVault() throws InterruptedException, IOException {
      MockResponse mr = buildBaseResponse(200);
      mr.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8);
      mr.setBody(getResponseBody("/json/describeVaultResponseBody.json"));
      mr.addHeader(HttpHeaders.CONTENT_LENGTH, mr.getBody().length);
      server.enqueue(mr);

      VaultMetadata vm = client.describeVault(VAULT_NAME);
      assertEquals(server.takeRequest().getRequestLine(), "GET /-/vaults/" + VAULT_NAME + " " + HTTP);
      assertEquals(vm.getVaultName(), VAULT_NAME);
      assertEquals(vm.getVaultARN(), VAULT_ARN);
      assertEquals(vm.getSizeInBytes(), 78088912);
      assertEquals(vm.getNumberOfArchives(), 192);
   }

   @Test
   public void testListVaults() throws InterruptedException, IOException {
      MockResponse mr = buildBaseResponse(200);
      mr.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8);
      mr.setBody(getResponseBody("/json/listVaultsResponseBody.json"));
      mr.addHeader(HttpHeaders.CONTENT_LENGTH, mr.getBody().length);
      server.enqueue(mr);

      PaginatedVaultCollection vc = client.listVaults();
      Iterator<VaultMetadata> i = vc.iterator();
      assertEquals(i.next().getVaultName(), VAULT_NAME1);
      assertEquals(i.next().getVaultName(), VAULT_NAME2);
      assertEquals(i.next().getVaultName(), VAULT_NAME3);
      assertEquals(server.takeRequest().getRequestLine(), "GET /-/vaults " + HTTP);
   }

   @Test
   public void testListVaultsWithQueryParams() throws InterruptedException, IOException {
      MockResponse mr = buildBaseResponse(200);
      mr.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8);
      mr.setBody(getResponseBody("/json/listVaultsWithQueryParamsResponseBody.json"));
      mr.addHeader(HttpHeaders.CONTENT_LENGTH, mr.getBody().length);
      server.enqueue(mr);

      PaginatedVaultCollection vc = client.listVaults(PaginationOptions.Builder.limit(2).marker(VAULT_ARN1));
      Iterator<VaultMetadata> i = vc.iterator();
      assertEquals(i.next().getVaultName(), VAULT_NAME1);
      assertEquals(i.next().getVaultName(), VAULT_NAME2);
      assertFalse(i.hasNext());
      assertEquals(vc.nextMarker().get(), VAULT_ARN3);
      assertEquals(server.takeRequest().getRequestLine(),
            "GET /-/vaults?limit=2&marker=" + urlEncode(VAULT_ARN1, '/') + " " + HTTP);
   }

   @Test
   public void testUploadArchive() throws InterruptedException {
      MockResponse mr = buildBaseResponse(201);
      mr.addHeader(GlacierHeaders.TREE_HASH, TREEHASH);
      mr.addHeader(HttpHeaders.LOCATION, ARCHIVE_LOCATION);
      mr.addHeader(GlacierHeaders.ARCHIVE_ID, ARCHIVE_ID);
      server.enqueue(mr);

      assertEquals(client.uploadArchive(VAULT_NAME, buildPayload(10), DESCRIPTION), ARCHIVE_ID);
      RecordedRequest request = server.takeRequest();
      assertEquals(request.getRequestLine(), "POST /-/vaults/" + VAULT_NAME + "/archives " + HTTP);
      assertEquals(request.getHeader(GlacierHeaders.ARCHIVE_DESCRIPTION), DESCRIPTION);
      assertNotNull(request.getHeaders(GlacierHeaders.TREE_HASH));
      assertNotNull(request.getHeaders(GlacierHeaders.LINEAR_HASH));
   }

   @Test
   public void testDeleteArchive() throws InterruptedException {
      MockResponse mr = buildBaseResponse(204);
      server.enqueue(mr);

      assertTrue(client.deleteArchive(VAULT_NAME, ARCHIVE_ID));
      assertEquals(server.takeRequest().getRequestLine(), "DELETE /-/vaults/" + VAULT_NAME + "/archives/" + ARCHIVE_ID + " " + HTTP);
   }
}
