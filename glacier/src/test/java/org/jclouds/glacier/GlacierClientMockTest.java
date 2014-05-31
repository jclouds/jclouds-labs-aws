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

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.jclouds.glacier.util.TestUtils.MiB;
import static org.jclouds.glacier.util.TestUtils.buildPayload;
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
import org.jclouds.glacier.util.ContentRange;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;
import com.google.inject.Module;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.RecordedRequest;

/**
 * Mock test for Glacier.
 */
@Test
public class GlacierClientMockTest {

   private static final String VAULT_NAME = "ConcreteVaultName";

   private static final Set<Module> modules = ImmutableSet.<Module> of(new ExecutorServiceModule(sameThreadExecutor(),
         sameThreadExecutor()));

   static GlacierClient getGlacierClient(URL server) {
      Properties overrides = new Properties();
      // prevent expect-100 bug http://code.google.com/p/mockwebserver/issues/detail?id=6
      overrides.setProperty(PROPERTY_SO_TIMEOUT, "0");
      overrides.setProperty(PROPERTY_MAX_RETRIES, "1");
      return ContextBuilder.newBuilder("glacier").credentials("accessKey", "secretKey").endpoint(server.toString())
            .modules(modules).overrides(overrides).buildApi(GlacierClient.class);
   }

   public void testCreateVault() throws IOException, InterruptedException {
      // Prepare the response
      MockResponse mr = new MockResponse();
      mr.setResponseCode(201);
      mr.addHeader(GlacierHeaders.REQUEST_ID, "AAABZpJrTyioDC_HsOmHae8EZp_uBSJr6cnGOLKp_XJCl-Q");
      mr.addHeader(HttpHeaders.DATE, "Sun, 25 Mar 2012 12:02:00 GMT");
      mr.addHeader(HttpHeaders.LOCATION, "/111122223333/vaults/" + VAULT_NAME);
      MockWebServer server = new MockWebServer();
      server.enqueue(mr);
      server.play();

      // Send the request and check the response
      try {
         GlacierClient client = getGlacierClient(server.getUrl("/"));
         URI responseUri = client.createVault(VAULT_NAME);
         assertEquals(responseUri.toString(), server.getUrl("/") + "111122223333/vaults/" + VAULT_NAME);
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getRequestLine(), "PUT /-/vaults/" + VAULT_NAME + " HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteVault() throws IOException, InterruptedException {
      // Prepare the response
      MockResponse mr = new MockResponse();
      mr.setResponseCode(204);
      mr.addHeader(GlacierHeaders.REQUEST_ID, "AAABZpJrTyioDC_HsOmHae8EZp_uBSJr6cnGOLKp_XJCl-Q");
      mr.addHeader(HttpHeaders.DATE, "Sun, 25 Mar 2012 12:02:00 GMT");
      MockWebServer server = new MockWebServer();
      server.enqueue(mr);
      server.play();

      // Send the request and check the response
      try {
         GlacierClient client = getGlacierClient(server.getUrl("/"));
         assertTrue(client.deleteVault(VAULT_NAME));
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getRequestLine(), "DELETE /-/vaults/" + VAULT_NAME + " HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void testDescribeVault() throws IOException, InterruptedException {
      // Prepare the response
      MockResponse mr = new MockResponse();
      mr.setResponseCode(200);
      mr.addHeader(GlacierHeaders.REQUEST_ID, "AAABZpJrTyioDC_HsOmHae8EZp_uBSJr6cnGOLKp_XJCl-Q");
      mr.addHeader(HttpHeaders.DATE, "Sun, 25 Mar 2012 12:02:00 GMT");
      mr.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
      mr.addHeader(HttpHeaders.CONTENT_LENGTH, "260");
      mr.setBody("{\"CreationDate\" : \"2012-02-20T17:01:45.198Z\",\"LastInventoryDate\" : "
            + "\"2012-03-20T17:03:43.221Z\",\"NumberOfArchives\" : 192,\"SizeInBytes\" : 78088912,\"VaultARN\" : "
            + "\"arn:aws:glacier:us-east-1:012345678901:vaults/examplevault\",\"VaultName\" : \"examplevault\"}");
      MockWebServer server = new MockWebServer();
      server.enqueue(mr);
      server.play();

      // Send the request and check the response
      try {
         GlacierClient client = getGlacierClient(server.getUrl("/"));
         VaultMetadata vm = client.describeVault(VAULT_NAME);
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getRequestLine(), "GET /-/vaults/" + VAULT_NAME + " HTTP/1.1");
         assertEquals(vm.getVaultName(), "examplevault");
         assertEquals(vm.getVaultARN(), "arn:aws:glacier:us-east-1:012345678901:vaults/examplevault");
         assertEquals(vm.getSizeInBytes(), 78088912);
         assertEquals(vm.getNumberOfArchives(), 192);
      } finally {
         server.shutdown();
      }
   }

   public void testListVaults() throws IOException, InterruptedException {
      // Prepare the response
      MockResponse mr = new MockResponse();
      mr.setResponseCode(200);
      mr.addHeader(GlacierHeaders.REQUEST_ID, "AAABZpJrTyioDC_HsOmHae8EZp_uBSJr6cnGOLKp_XJCl-Q");
      mr.addHeader(HttpHeaders.DATE, "Sun, 25 Mar 2012 12:02:00 GMT");
      mr.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
      mr.addHeader(HttpHeaders.CONTENT_LENGTH, "497");
      mr.setBody("{" + "\"Marker\": null,\"VaultList\": [ {\"CreationDate\": \"2012-03-16T22:22:47.214Z\","
            + "\"LastInventoryDate\": \"2012-03-21T22:06:51.218Z\",\"NumberOfArchives\": 2,"
            + "\"SizeInBytes\": 12334,\"VaultARN\": \"arn:aws:glacier:us-east-1:012345678901:vaults/examplevault1\","
            + "\"VaultName\": \"examplevault1\"}, {\"CreationDate\": \"2012-03-19T22:06:51.218Z\","
            + "\"LastInventoryDate\": \"2012-03-21T22:06:51.218Z\", \"NumberOfArchives\": 0,\"SizeInBytes\": 0,"
            + "\"VaultARN\": \"arn:aws:glacier:us-east-1:012345678901:vaults/examplevault2\","
            + "\"VaultName\": \"examplevault2\"},{\"CreationDate\": \"2012-03-19T22:06:51.218Z\","
            + "\"LastInventoryDate\": \"2012-03-25T12:14:31.121Z\",\"NumberOfArchives\": 0,\"SizeInBytes\": 0,"
            + "\"VaultARN\": \"arn:aws:glacier:us-east-1:012345678901:vaults/examplevault3\","
            + "\"VaultName\": \"examplevault3\"}]}");
      MockWebServer server = new MockWebServer();
      server.enqueue(mr);
      server.play();

      // Send the request and check the response
      try {
         GlacierClient client = getGlacierClient(server.getUrl("/"));
         PaginatedVaultCollection vc = client.listVaults();
         Iterator<VaultMetadata> i = vc.iterator();
         assertEquals(i.next().getVaultName(), "examplevault1");
         assertEquals(i.next().getVaultName(), "examplevault2");
         assertEquals(i.next().getVaultName(), "examplevault3");
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getRequestLine(), "GET /-/vaults HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void testListVaultsWithQueryParams() throws IOException, InterruptedException {
      // Prepare the response
      MockResponse mr = new MockResponse();
      mr.setResponseCode(200);
      mr.addHeader(GlacierHeaders.REQUEST_ID, "AAABZpJrTyioDC_HsOmHae8EZp_uBSJr6cnGOLKp_XJCl-Q");
      mr.addHeader(HttpHeaders.DATE, "Sun, 25 Mar 2012 12:02:00 GMT");
      mr.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
      mr.addHeader(HttpHeaders.CONTENT_LENGTH, "497");
      mr.setBody("{\"Marker\": \"arn:aws:glacier:us-east-1:012345678901:vaults/examplevault3\","
            + "\"VaultList\": [{\"CreationDate\": \"2012-03-16T22:22:47.214Z\",\"LastInventoryDate\":"
            + "\"2012-03-21T22:06:51.218Z\",\"NumberOfArchives\": 2,\"SizeInBytes\": 12334,"
            + "\"VaultARN\": \"arn:aws:glacier:us-east-1:012345678901:vaults/examplevault1\","
            + "\"VaultName\": \"examplevault1\"},{\"CreationDate\": \"2012-03-19T22:06:51.218Z\","
            + "\"LastInventoryDate\": \"2012-03-21T22:06:51.218Z\",\"NumberOfArchives\": 0,"
            + "\"SizeInBytes\": 0,\"VaultARN\": \"arn:aws:glacier:us-east-1:012345678901:vaults/examplevault2\","
            + "\"VaultName\": \"examplevault2\"}]}");
      MockWebServer server = new MockWebServer();
      server.enqueue(mr);
      server.play();

      // Send the request and check the response
      try {
         GlacierClient client = getGlacierClient(server.getUrl("/"));
         PaginatedVaultCollection vc = client.listVaults(PaginationOptions.Builder.limit(2).marker(
               "arn:aws:glacier:us-east-1:012345678901:vaults/examplevault1"));
         Iterator<VaultMetadata> i = vc.iterator();
         assertEquals(i.next().getVaultName(), "examplevault1");
         assertEquals(i.next().getVaultName(), "examplevault2");
         assertFalse(i.hasNext());
         assertEquals(vc.nextMarker().get(), "arn:aws:glacier:us-east-1:012345678901:vaults/examplevault3");
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getRequestLine(), "GET /-/vaults?limit=2&"
               + "marker=arn%3Aaws%3Aglacier%3Aus-east-1%3A012345678901%3Avaults/examplevault1 HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void testUploadArchive() throws IOException, InterruptedException {
      // Prepare the response
      MockResponse mr = new MockResponse();
      mr.setResponseCode(201);
      String responseId = "NkbByEejwEggmBz2fTHgJrg0XBoDfjP4q6iu87-TjhqG6eGoOY9Z8i1_AUyUsuhPAdTqLHy8pTl5nfCFJmDl2yEZONi5L26Omw12vcs01MNGntHEQL8MBfGlqrEXAMPLEArchiveId";
      mr.addHeader(GlacierHeaders.REQUEST_ID, "AAABZpJrTyioDC_HsOmHae8EZp_uBSJr6cnGOLKp_XJCl-Q");
      mr.addHeader(HttpHeaders.DATE, "Sun, 25 Mar 2012 12:00:00 GMT");
      mr.addHeader(GlacierHeaders.TREE_HASH, "beb0fe31a1c7ca8c6c04d574ea906e3f97b31fdca7571defb5b44dca89b5af60");
      mr.addHeader(
            HttpHeaders.LOCATION,
            "/111122223333/vaults/examplevault/archives/NkbByEejwEggmBz2fTHgJrg0XBoDfjP4q6iu87-TjhqG6eGoOY9Z8i1_AUyUsuhPAdTqLHy8pTl5nfCFJmDl2yEZONi5L26Omw12vcs01MNGntHEQL8MBfGlqrEXAMPLEArchiveId");
      mr.addHeader(GlacierHeaders.ARCHIVE_ID, responseId);
      MockWebServer server = new MockWebServer();
      server.enqueue(mr);
      server.play();

      // Send the request and check the response
      try {
         GlacierClient client = getGlacierClient(server.getUrl("/"));
         String id = client.uploadArchive("examplevault", buildPayload(10), "test description");
         RecordedRequest request = server.takeRequest();
         assertEquals(id, responseId);
         assertEquals(request.getRequestLine(), "POST /-/vaults/examplevault/archives HTTP/1.1");
         assertEquals(request.getHeader(GlacierHeaders.ARCHIVE_DESCRIPTION), "test description");
         assertNotNull(request.getHeaders(GlacierHeaders.TREE_HASH));
         assertNotNull(request.getHeaders(GlacierHeaders.LINEAR_HASH));
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteArchive() throws IOException, InterruptedException {
      // Prepare the response
      MockResponse mr = new MockResponse();
      mr.setResponseCode(204);
      mr.addHeader(GlacierHeaders.REQUEST_ID, "AAABZpJrTyioDC_HsOmHae8EZp_uBSJr6cnGOLKp_XJCl-Q");
      mr.addHeader(HttpHeaders.DATE, "Sun, 25 Mar 2012 12:00:00 GMT");
      MockWebServer server = new MockWebServer();
      server.enqueue(mr);
      server.play();

      // Send the request and check the response
      try {
         String id = "NkbByEejwEggmBz2fTHgJrg0XBoDfjP4q6iu87-TjhqG6eGoOY9Z8i1_AUyUsuhPAdTqLHy8pTl5nfCFJmDl2yEZONi5L26Omw12vcs01MNGntHEQL8MBfGlqrEXAMPLEArchiveId";
         GlacierClient client = getGlacierClient(server.getUrl("/"));
         boolean result = client.deleteArchive("examplevault", id);
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getRequestLine(), "DELETE /-/vaults/examplevault/archives/" + id + " HTTP/1.1");
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testInitiateMultipartUpload() throws IOException, InterruptedException {
      // Prepare the response
      MockResponse mr = new MockResponse();
      mr.setResponseCode(201);
      mr.addHeader(GlacierHeaders.REQUEST_ID, "AAABZpJrTyioDC_HsOmHae8EZp_uBSJr6cnGOLKp_XJCl-Q");
      mr.addHeader(HttpHeaders.DATE, "Sun, 25 Mar 2012 12:00:00 GMT");
      mr.addHeader(
            HttpHeaders.LOCATION,
            "/111122223333/vaults/examplevault/multipart-uploads/OW2fM5iVylEpFEMM9_HpKowRapC3vn5sSL39_396UW9zLFUWVrnRHaPjUJddQ5OxSHVXjYtrN47NBZ-khxOjyEXAMPLE");
      mr.addHeader(GlacierHeaders.MULTIPART_UPLOAD_ID,
            "OW2fM5iVylEpFEMM9_HpKowRapC3vn5sSL39_396UW9zLFUWVrnRHaPjUJddQ5OxSHVXjYtrN47NBZ-khxOjyEXAMPLE");
      MockWebServer server = new MockWebServer();
      server.enqueue(mr);
      server.play();

      // Send the request and check the response
      try {
         int partSizeInMB = 4;
         GlacierClient client = getGlacierClient(server.getUrl("/"));
         String multipartId = client.initiateMultipartUpload("examplevault", partSizeInMB, "MyArchive-101");
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getRequestLine(), "POST /-/vaults/examplevault/multipart-uploads HTTP/1.1");
         assertEquals(multipartId,
               "OW2fM5iVylEpFEMM9_HpKowRapC3vn5sSL39_396UW9zLFUWVrnRHaPjUJddQ5OxSHVXjYtrN47NBZ-khxOjyEXAMPLE");
         assertEquals(request.getHeader(GlacierHeaders.PART_SIZE), "4194304");
         assertEquals(request.getHeader(GlacierHeaders.ARCHIVE_DESCRIPTION), "MyArchive-101");
      } finally {
         server.shutdown();
      }
   }

   public void testUploadPart() throws IOException, InterruptedException {
      // Prepare the response
      MockResponse mr = new MockResponse();
      mr.setResponseCode(204);
      mr.addHeader(GlacierHeaders.REQUEST_ID, "AAABZpJrTyioDC_HsOmHae8EZp_uBSJr6cnGOLKp_XJCl-Q");
      mr.addHeader(HttpHeaders.DATE, "Sun, 25 Mar 2012 12:00:00 GMT");
      mr.addHeader(GlacierHeaders.TREE_HASH, "c06f7cd4baacb087002a99a5f48bf953");
      MockWebServer server = new MockWebServer();
      server.enqueue(mr);
      server.play();

      // Send the request and check the response
      try {
         GlacierClient client = getGlacierClient(server.getUrl("/"));
         String hash = client.uploadPart("examplevault",
               "OW2fM5iVylEpFEMM9_HpKowRapC3vn5sSL39_396UW9zLFUWVrnRHaPjUJddQ5OxSHVXjYtrN47NBZ-khxOjyEXAMPLE",
               ContentRange.Builder.fromPartNumber(0, 4), buildPayload(4 * MiB));
         RecordedRequest request = server.takeRequest();
         assertEquals(
               request.getRequestLine(),
               "PUT /-/vaults/examplevault/multipart-uploads/OW2fM5iVylEpFEMM9_HpKowRapC3vn5sSL39_396UW9zLFUWVrnRHaPjUJddQ5OxSHVXjYtrN47NBZ-khxOjyEXAMPLE HTTP/1.1");
         assertEquals(hash, "c06f7cd4baacb087002a99a5f48bf953");
         assertEquals(request.getHeader(HttpHeaders.CONTENT_RANGE), "bytes 0-4194303/*");
         assertEquals(request.getHeader(HttpHeaders.CONTENT_LENGTH), "4194304");
      } finally {
         server.shutdown();
      }
   }
}
