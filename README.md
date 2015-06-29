# Data Governance Initiative

This repository is to help with the Partner Demonstration of the Apache Atlas project.

Enterprises that include Hadoop in their modern data architecture must address certain realities when bringing legacy and new data from disparate platforms under management in their cluster.

The Data Governance Initiative is working to develop an extensible foundation that addresses enterprise requirements for comprehensive data governance and assures that Hadoop:

* Snaps into existing frameworks to openly exchange metadata
* Addresses enterprise data governance requirements within its own stack of technologies

The DGI solution will feature deep integration with Apache Falcon for data lifecycle management and Apache Ranger for centralized security policies. It will also interoperate with and extend existing third-party data governance and management tools by shedding light on the data access patterns within the Hadoop cluster.

In addition to Hortonworks, the members of DGI are enterprise Hadoop users Aetna, Merck, Schlumberger, and Target and also Hortonworks’ technology partner SAS.

Enterprises across all major industries adopt Apache Hadoop for its ability to store and process an abundance of new types of data in a modern data architecture. This “Any Data” capability has always been a hallmark feature of Hadoop, opening insight from new data sources such as clickstream, web and social, geo-location, IoT, server logs, or traditional data sets from ERP, CRM, SCM or other existing data systems.

But this means that enterprises adopting modern data architecture with Hadoop must reconcile data management realities when they bring existing and new data from disparate platforms under management. As customers deploy Hadoop into corporate data and processing environments, metadata and data governance must be vital parts of any enterprise-ready data lake.

For these reasons, we established the Data Governance Initiative (DGI) with Aetna, Merck, Target, and SAS to introduce a common approach to Hadoop data governance into the open source community. Since then, this co-development effort has grown to include Schlumberger. Together we work on this shared framework to shed light on how users access data within Hadoop while interoperating with and extending existing third-party data governance and management tools.

## Apache Atlas

Apache Atlas proposes to provide governance capabilities in Hadoop that use both a prescriptive and forensic models enriched by business taxonomical metadata. Atlas, at its core, is designed to exchange metadata with other tools and processes within and outside of the Hadoop stack, thereby enabling platform-agnostic governance controls that effectively address compliance
requirements.

![Apache Atlas](http://hortonworks.com/wp-content/uploads/2015/04/atlas_2.png)

## The core capabilities defined by the project include the following:

* Data Classification – to create an understanding of the data within Hadoop and provide a classification of this data to external and internal sources

* Centralized Auditing – to provide a framework for capturing and reporting on access to and modifications of data within Hadoop

* Search and Lineage – to allow pre-defined and ad-hoc exploration of data and metadata while maintaining a history of how a data source or explicit data was constructed

* Security and Policy Engine – to protect data and rationalize data access according to compliance policy.
The Atlas community plans to deliver those requirements with the following components:

 - Flexible Knowledge Store,

 - Advanced Policy Rules Engine,

 - Agile Auditing,

Support for specific data lifecycle management workflows built on the Apache Falcon framework, and
Integration and extension of Apache Ranger to add real-time, attribute-based access control to Ranger’s already strong role-based access control capabilities.

# Why Atlas?

Atlas targets a scalable and extensible set of core foundational governance services – enabling enterprises to effectively and efficiently meet their compliance requirements within Hadoop while ensuring integration with the whole data ecosystem. Apache Atlas is organized around two guiding principals:

Metadata Truth in Hadoop: Atlas should provide true visibility in Hadoop. By using both a prescriptive and forensic model, Atlas provides technical and operational audit as well as lineage enriched by business taxonomical metadata. Atlas facilitates easy exchange of metadata by enabling any metadata consumer to share a common metadata store that facilitates interoperability across many metadata producers.

Developed in the Open: Engineers from Aetna, Merck, SAS, Schlumberger, and Target are working together to help ensure Atlas is built to solve real data governance problems across a wide range of industries that use Hadoop. This approach is an example of open source community innovation that helps accelerate product maturity and time-to-value for the data-first enterprise.


# Technical Details

Apache Hadoop consists of below projects for Data Inestion 

* Apache Sqoop - 

Apache Sqoop is utility for ingesting data from Relation Databases into Hadoop. It has been in the Hadoop Ecosystem for a while and well proven in the industry.

* Apache Flume - 

