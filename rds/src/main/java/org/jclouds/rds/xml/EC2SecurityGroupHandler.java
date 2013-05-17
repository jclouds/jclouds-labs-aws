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
package org.jclouds.rds.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.rds.domain.Authorization.Status;
import org.jclouds.rds.domain.EC2SecurityGroup;
import org.xml.sax.SAXException;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference/API_EC2SecurityGroup.html"
 *      >xml</a>
 * 
 * @author Adrian Cole
 */
public class EC2SecurityGroupHandler extends ParseSax.HandlerForGeneratedRequestWithResult<EC2SecurityGroup> {

   private StringBuilder currentText = new StringBuilder();
   private EC2SecurityGroup.Builder builder = EC2SecurityGroup.builder();

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2SecurityGroup getResult() {
      try {
         return builder.build();
      } finally {
         builder = EC2SecurityGroup.builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "EC2SecurityGroupId")) {
         builder.id(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "EC2SecurityGroupName")) {
         builder.name(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "EC2SecurityGroupOwnerId")) {
         builder.ownerId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Status")) {
         String rawStatus = currentOrNull(currentText);
         builder.rawStatus(rawStatus);
         builder.status(Status.fromValue(rawStatus));
      }
      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
