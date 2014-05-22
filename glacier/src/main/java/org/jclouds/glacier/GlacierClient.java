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

import java.io.Closeable;
import java.net.URI;

import org.jclouds.glacier.domain.PaginatedVaultCollection;
import org.jclouds.glacier.domain.VaultMetadata;
import org.jclouds.glacier.options.PaginationOptions;

/**
 * Provides access to Amazon Glacier resources via their REST API.
 * <p/>
 *
 * @see GlacierAsyncClient
 * @see <a href="http://aws.amazon.com/documentation/glacier/" />
 * @author Roman Coedo
 */
public interface GlacierClient extends Closeable {

   /**
    * A PUT request operation with a vault name to create a new vault to store archives.
    *
    * @param vaultName
    *           A name for the Vault being created.
    * @return A reference to an URI pointing to the resource created.
    * @see <a href="http://docs.aws.amazon.com/amazonglacier/latest/dev/api-vault-put.html" />
    */
   URI createVault(String vaultName);

   /**
    * A DELETE request operation with a vault name to delete an existing vault. The vault must be empty.
    *
    * @param vaultName
    *           Name of the Vault being deleted.
    * @return False if the vault was not empty and therefore not deleted, true otherwise.
    * @see <a href="http://docs.aws.amazon.com/amazonglacier/latest/dev/api-vault-delete.html" />
    */
   boolean deleteVaultIfEmpty(String vaultName);

   /**
    * A GET request operation with a vault name to fetch the vault metadata.
    *
    * @param vaultName
    *           Name of the Vault being described.
    * @return A VaultMetadata object containing all the information relevant to the vault.
    * @see <a href="http://docs.aws.amazon.com/amazonglacier/latest/dev/api-vault-get.html" />
    */
   VaultMetadata describeVault(String vaultName);

   /**
    * A GET request operation to retrieve a vault listing.
    *
    * @param options
    *          Options used for pagination.
    * @return A PaginatedVaultCollection object containing the list of vaults.
    * @see <a href="http://docs.aws.amazon.com/amazonglacier/latest/dev/api-vaults-get.html" />
    */
   PaginatedVaultCollection listVaults(PaginationOptions options);

   /**
    * A GET request operation to retrieve a vault listing.
    *
    * @see GlacierClient#listVaults(PaginationOptions)
    */
   PaginatedVaultCollection listVaults();
}
