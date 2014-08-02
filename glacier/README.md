AWS Glacier
======================

Amazon Glacier is a cold storage service which can be used as an alternative to traditional storage services for data archiving and backup. It is optimized for data infrequently accessed and offers nice scalability and an extremely low cost. There is no limit to the amount of data you can store, and you only pay for what you use.

The main downside of Glacier is its huge retrieval time. Once you've requested your data, it will take several hours until it's ready to download.

You can find more information about Amazon Glacier [here](https://aws.amazon.com/glacier/).

Terminology
-----------
The following resources are defined by the Glacier API:

##### Vault
A vault defines a container in a region for a collection of archives and is associated to an account. Each vault can store an unlimited amount of archives but cannot contain other vaults.

##### Archive
Archives are the basic storage unit in Glacier. An archive has an unique identifier and an optional description. The identifier is set by Amazon when the archive is uploaded, and the service returns its ID.

##### Multipart upload
In order to upload an archive in parts, we need to create a new multipart upload. Each multipart upload is associated to a vault.

##### Job
A Job represents a request we send to Glacier to read data. When a Job finishes it produces an output
that we can read. The estimated time to finish a job is ~4-5 hours.

There are two different kind of jobs:
* Inventory retrieval: Used to list the archives within a vault.
* Archive retrieval: Used to read an archive.

**Note**: Inventories are updated only once every 24 hours. The archive list in an inventory may be outdated.

There are also two different ways to discover if a Job has finished: by polling or setting a Notification-Configuration to the Vault. This Notification-Configuration will tell to an Amazon SNS when is the data ready to be retrieved. Only one Notification-Configuration can be set per Vault.

At the time of writing only polling is supported.

Glacier client
----------------
The Glacier client offers a set of methods in line with the Glacier API. These methods allow the manipulation of the resources described above.

##### Vault operations
* `createVault` An account can only create up to 1000 vaults per region, but each vault can contain an unlimited number of archives.

* `deleteVault` Vaults can only be deleted if there are no archives in the vault in the last inventory computed by Amazon, and there are no new uploads after it. This is very important, as Amazon only updates inventories once every 24 hours.

* `describeVault` The response include information like the vault ARN, the creation data, the number of archives contained within the vault and total size of these archives. Again, the number of archives and the total size is based on the last inventory computed by amazon and the information may be outdated.

* `listVaults` Lists the vaults in a region. By default this operation returns up to 1,000 vaults, but you can control this using the request options.

##### Archive operations
* `uploadArchive` Archives up to 4GB in size can be uploaded using this operation. Once the archive is uploaded it is immutable. The archive or its description cannot be modified. Except for the optional description Glacier does not support additional metadata.

* `deleteArchive` Be aware that after deleting an archive it may still be listed in the inventories until amazon compute a new inventory.

**Note**: Due to an issue with java 6, only archives up to 1GB are supported using uploadArchive operation. This will be fixed once jclouds drops support for java 6.

##### Multipart upload operations
* `initiateMultipartUpload` Using a multipart upload you can upload archives up to 40,000GB (10,000 parts, 4GB each). The part size must be a megabyte multiplied by a power of 2 and every part must be the same size except the last one, which can have a smaller size. In addition, you don't need to know the archive size to initiate a multipart upload.

* `uploadPart` This operation is used to upload a part into a multipart upload. The part size has to match the one specified in the multipart upload request, and the range needs to be aligned. In addition, to ensure that the data is not corrupted, the tree hash and the linear hash are checked by Glacier. You can learn more about AWS tree hash [here](http://docs.aws.amazon.com/amazonglacier/latest/dev/checksum-calculations.html).

* `completeMultipartUpload` After uploading all the parts this operation should be called to inform Glacier. Again, the checksum and the ranges of the whole archive will be computed to verify the data.

* `abortMultipartUpload` Aborts the ongoing multipart upload. Once aborted, you cannot upload any more parts to it.

* `listParts` You can list the parts of an ongoing multipart upload at any time. By default it returns up to 1,000 uploaded parts, but you can control this using the request options.

* `listMultipartUploads` You can list the ongoing multipart uploads for a specific vault. By default, this operation returns up to  1,000 multipart uploads. You can control this using the request options.

**Note**: Due to an issue with java 6, only parts up to 1GB are supported using uploadPart operation. This will be fixed once jclouds drops support for java 6.

##### Job operations
* `initiateJob` Initiates a job. The job can be an inventory retrieval or an archive retrieval. Once the job is started the estimated time to complete it is ~4-5 hours. See [this](http://docs.aws.amazon.com/amazonglacier/latest/dev/api-initiate-job-post.html) for more detailed information about the available options for both jobs.

* `describeJob` Retrieves information about an ongoing job. Among the information you will find the initiatino date, the user who initiated the job or the status message.

* `listJobs` Lists the ongoing jobs and the recently finished jobs for a vault. By default this operation returns up to 1,000 jobs in the response, but you can control this using the request options. Note that this operation also returns the recently finished jobs and you can still download their output.

* `getJobOutput` Gets the raw job output of any kind of job. You can download the job output within the 24 hour period after Glacier comlpetes a job.

* `getInventoryRetrievalOutput` Gets the job output for the given job ID. The job must be an inventory retrieval, otherwise this operation will fail. This operation will parse the job output and build a collection of objects for you.

BlobStore View
--------------
Due to the Glacier's huge retrieval times, many of the BlobStore view methods are slow. In the following list you can check an estimated time for each one of them and some peculiarities:

##### Container operations
* `deleteContainer` This operation may take days depending on the state of the container. If the container still has blobs in it they will be deleted and wait until the inventory for that vault is updated.

* `list` This is a regular request.

* `containerExists` This is a regular request.

* `createContainerInLocation` This is a regular request. Note that the location is currently not supported.

##### Blob operations
* `list` An inventory will be retrieved to obtain the list. Note that this will take several hours and the result may be inaccurate (Inventories are updated every 24 hours).

* `blobMetadata` An inventory will be retrieved to obtain the blob list and the method will iterate through it. This operation will take several hours and the result may be inaccurate (Inventories are updated every 24 hours).

* `blobExists` This operation calls blobMetadata, check blobMetadata operation for more information.

* `getBlob` This operation will take more than 4 hours.

* `removeBlob` This is a regular request.

* `putBlob` This is a regular request.

Known issues
------------
At the time of writing, only the us-east-1 region is supported by our signer.

Resources
---------
* [Glacier information](https://aws.amazon.com/glacier/)
* [API reference](http://docs.aws.amazon.com/amazonglacier/latest/dev/amazon-glacier-api.html)
* [About Inventories](http://aws.amazon.com/glacier/faqs/#data-inventories)
* [Job options](http://docs.aws.amazon.com/amazonglacier/latest/dev/api-initiate-job-post.html)

License
-------
Licensed under the Apache License, Version 2.0
