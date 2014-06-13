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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.jclouds.io.Payload;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;

/**
 * Calculates the linear hash and the tree hash of the payload.
 *
 */
public class TreeHasher {

   private static int CHUNK_SIZE = 1024 * 1024;

   private final Payload payload;
   private String hash;
   private String treeHash;

   public TreeHasher(Payload payload) {
      this.payload = payload;
      hash = null;
      treeHash = null;
   }

   private static Queue<HashCode> hashQueue(Queue<HashCode> q) {
      //Hash pairs of values and add them to the result queue.
      Queue<HashCode> result = Lists.newLinkedList();
      while (q.size() > 1) {
         result.offer(Hashing.sha256().hashBytes(Bytes.concat(q.poll().asBytes(), q.poll().asBytes())));
      }

      //If there is one hash left, add it too.
      if (q.size() == 1)
         result.offer(q.poll());
      return result;
   }

   /**
    * Build a TreeHash based on a map of hashed chunks.
    *
    * @return A String containing the calculated TreeHash.
    */
   public static String buildTreeHashFromMap(Map<Integer, String> map) {
      //Sort the Map with the hash parts.
      TreeMap<Integer, String> sortedMap = Maps.newTreeMap();
      sortedMap.putAll(map);

      //Convert the strings and add them to our queue.
      Queue<HashCode> q = Lists.newLinkedList();
      Iterator<Map.Entry<Integer, String>> i = sortedMap.entrySet().iterator();
      while (i.hasNext())
         q.offer(HashCode.fromString(i.next().getValue()));

      //Rehash the queue until its size is 1.
      while (q.size() > 1)
         q = hashQueue(q);
      return q.poll().toString();
   }

   /**
    * Build the Hash and the TreeHash values of the payload.
    */
   public void buildHashes() throws IOException {
      InputStream is = payload.openStream();
      Hasher hasher = Hashing.sha256().newHasher();
      Queue<HashCode> q = Lists.newLinkedList();

      //Divide the payload in chunks and queue them.
      byte[] buffer = new byte[CHUNK_SIZE];
      int bytesRead;
      while ((bytesRead = is.read(buffer)) >= 0) {
         q.offer(Hashing.sha256().hashBytes(buffer, 0, bytesRead));
         hasher.putBytes(buffer, 0, bytesRead);
      }

      //Rehash the queue until its size is 1.
      while (q.size() > 1)
         q = hashQueue(q);

      this.treeHash = q.poll().toString();
      this.hash = hasher.hash().toString();
      is.close();
   }

   /**
    * Gets the hash of the payload. Builds the hash if it was not initialized yet.
    *
    * @return A String containing the calculated hash.
    */
   public String getHash() throws IOException {
      if (hash == null)
         this.buildHashes();
      return hash;
   }

   /**
    * Gets the TreeHash of the payload. Builds the TreeHash if it was not initialized yet.
    *
    * @return A String containing the calculated TreeHash.
    */
   public String getTreeHash() throws IOException {
      if (treeHash == null)
         this.buildHashes();
      return treeHash;
   }
}
