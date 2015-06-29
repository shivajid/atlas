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

Features
Data Classification

Import or define taxonomy business-oriented annotations for data
Define, annotate, and automate capture of relationships between data sets and underlying
elements including source, target, and derivation processes
Export metadata to third-party systems
Centralized Auditing

Capture security access information for every application, process, and interaction with data
Capture the operational information for execution, steps, and activities
Search & Lineage (Browse)

Pre-defined navigation paths to explore the data classification and audit information
Text-based search features locates relevant data and audit event across Data Lake quickly
and accurately

Browse visualization of data set lineage allowing users to drill-down into operational,
security, and provenance related information
Security & Policy Engine

Rationalize compliance policy at runtime based on data classification schemes, attributes
and roles.
Advanced definition of policies for preventing data derivation based on classification
(i.e. re-identification) – Prohibitions
Column and Row level masking based on cell values and attibutes.
# Technical Details

Apache Hadoop consists of below projects for Data Inestion 

* Apache Sqoop - 

Apache Sqoop is hadoop project for ingesting data from Relation Databases into Hadoop. It has been in the Hadoop Ecosystem for a while and well proven in the industry.

* Apache Flume - 
Apache Flumen is a  good utility for ingesting data from various sources into hadoop.

Feed and Data Lineage

* Apache Falcon -
Falcon is a feed processing and feed management system aimed at making it easier for end consumers to onboard their feed processing and feed management on hadoop clusters. 

Falcon needs a workflow engine. It uses oozie by default.

* Apache Atlas*

Apache Atlas is a scalable and extensible set of core foundational governance services that enables enterprises to effectively and efficiently meet their compliance requirements within Hadoop and allows integration with the complete enterprise data ecosystem.

## The Demo project

To demonstrate a simple scenario we are going to setup system with the following.

* Source System.

This is a mysql database. 

* Destination System
 
We have HDP Cluster. For our tutorial we are going to use the HDP 2.3 Sandbox

Design

In the Source System we are going to have an online system that is storing Truck Drivers Information System. The Truck Driver Information System, tracks the Truck Companies, employees, status, license, route and hours logged.

Following are the tables in the Source System:-

#### Organization

<table>
 <tr>
  <td>Cloumn Name</td>
  <td>Cloumn Type</td>
  <td>Not Null </td>
  <td> Primary Key</td>
 </tr>
 <tr>
  <td>OraganizationId</td>
  <td>Varchar(100)</td>
  <td>Y</td>
  <td>Y</td>
 </tr>
  <tr>
  <td>OraganizationName</td>
  <td>Varchar(1000)</td>
  <td>Y</td>
  <td>N</td>
 </tr>
  <tr>
  <td>Industry</td>
  <td>Varchar(100)</td>
  <td>N</td>
  <td>N</td>
 </tr>
 <tr>
  <td>Publicy_Traded</td>
  <td>Boolean</td>
  <td>Y</td>
  <td>Y</td>
 </tr>
</table>

#### Employee


<table>
 <tr>
  <td>Cloumn Name</td>
  <td>Cloumn Type</td>
  <td>Not Null </td>
  <td> Primary Key</td>
 </tr>
 <tr>
  <td>Employee_ID</td>
  <td>Varchar(100)</td>
  <td>Y</td>
  <td>Y</td>
 </tr>
  <tr>
  <td>Emplpyee_Name</td>
  <td>Varchar(1000)</td>
  <td>Y</td>
  <td>N</td>
 </tr>
  <tr>
  <td>Email</td>
  <td>Varchar(100)</td>
  <td>Y</td>
  <td>N</td>
 </tr>
 <tr>
  <td>Designation</td>
  <td>Varchar(200)</td>
  <td>Y</td>
  <td>N</td>
 </tr>
  <tr>
  <td>OragnizationID</td>
  <td>Varchar(100)</td>
  <td>Y</td>
  <td>N</td>
 </tr>
  <tr>
  <td>JobType</td>
  <td>Varchar(100)</td>
  <td>Y</td>
  <td>N</td>
 </tr>
</table>


#### DRIVERS

<table>
 <tr>
  <td>Cloumn Name</td>
  <td>Cloumn Type</td>
  <td>Not Null </td>
  <td> Primary Key</td>
 </tr>
 <tr>
  <td>DRIVER_ID</td>
  <td>Varchar(200)</td>
  <td>Y</td>
  <td>Y</td>
 </tr>
  <tr>
  <td>DRIVER_NAME</td>
  <td>Varchar(1000)</td>
  <td>Y</td>
  <td>N</td>
 </tr>
  <tr>
  <td>CERTIFIED</td>
  <td>Varchar(100)</td>
  <td>Y</td>
  <td>N</td>
 </tr>
 <tr>
  <td>WAGE_PLAN</td>
  <td>Varchar(200)</td>
  <td>Y</td>
  <td>N</td>
 </tr>
</table>

### Create Table Scripts

#### Install Mysql

* Create a CentOS 6.5 Virtula Machine
* Install mysql server
* wget http://dev.mysql.com/get/mysql-community-release-el6-5.noarch.rpm/from/http://repo.mysql.com/
* sudo yum localinstall mysql-community-release-el6-*.noarch.rpm
* sudo yum install mysql-community-server
* sudo service mysqld start
* sudo chkconfig mysqld on
* chkconfig --list mysqld
* 














