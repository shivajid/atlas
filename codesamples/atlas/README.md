# Objective

Apache Atlas is open source data governance tool, built by the community and spearheaded by Hortonworks. It is part of the Data Governance Initiative.

Apache Atlas has made the version 0.5 GA. It has bunch of rest API's to interact with.

The code samples folder is a CLI tool written with Apache Commons to make interaction with Apache Atlas easier and simpler.

The broad features includes

* Creating a Simple Type
* Creating a Data Set Type
* Creating Process Type
* Creating a Simple Entity
* Create a Sample Table
* Creating a Trait
* Creating a Super Trait
* Importing a Business Taxonomy
* Importing Tables from a mysql server
* Apply a Taxonomy to an entity
* Searching for Entity by Type and Name


## Table of Content
  ** Installation
  **Usage
  **Sample Scripts 

### Installation

* Pre-requisites
Java version - This code is built on JDK 8. Please use JDK 8
maven version - This code is built with maven version 3.2

This code was built with an early release of apache incubator version 6. The jars needs to be installed locally. There are 4 jars that are needed.
** atlas-webapp-0.6-incubating-SNAPSHOT-classes.jar
** atlas-repository-0.6-incubating-SNAPSHOT.jar
** atlas-typesystem-0.6-incubating-SNAPSHOT.jar
** atlas-client-0.6-incubating-SNAPSHOT.jar 
** hive-bridge-0.6-incubating-SNAPSHOT.jar

    mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=/Users/sdutta/atlas-server/apache-atlas-0.6-incubating-SNAPSHOT/bridge/hive/atlas-webapp-0.6-incubating-SNAPSHOT-classes.jar  -DgroupId=com.hortonworks.atlas -DartifactId=atlas-webapp -Dversion=0.6-incubating-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

    mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=/Users/sdutta/atlas-server/apache-atlas-0.6-incubating-SNAPSHOT/bridge/hive/atlas-typesystem-0.6-incubating-SNAPSHOT.jar  -DgroupId=com.hortonworks.atlas -DartifactId=atlas-typesystem -Dversion=0.6-incubating-SNAPSHOT -Dpackaging=jar -DgeneratePom=true


    mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=/Users/sdutta/atlas-server/apache-atlas-0.6-incubating-SNAPSHOT/bridge/hive/atlas-repository-0.6-incubating-SNAPSHOT.jar  -DgroupId=com.hortonworks.atlas -DartifactId=atlas-repository -Dversion=0.6-incubating-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

    mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=/Users/sdutta/atlas-server/apache-atlas-0.6-incubating-SNAPSHOT/bridge/hive/atlas-client-0.6-incubating-SNAPSHOT.jar  -DgroupId=com.hortonworks.atlas -DartifactId=atlas-client -Dversion=0.6-incubating-SNAPSHOT -Dpackaging=jar -DgeneratePom=true


    mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=/Users/sdutta/atlas-server/apache-atlas-0.6-incubating-SNAPSHOT/hook/hive/hive-bridge-0.6-incubating-SNAPSHOT.jar
  -DgroupId=com.hortonworks.atlas -DartifactId=atlas-hive -Dversion=0.6-incubating-SNAPSHOT -Dpackaging=jar -DgeneratePom=true



### Demo Script

API Help

    ./atlasclient  --help

1) Create a New DataSet Type

    

    ./atlasclient  --c=createDataSetType --type=Tims_Fict_Table
    
1a) Create a New Lineage Type

    ./atlasclient --c=createProcessType --type=Jamies_Lineage

2) Create new Traits
	Create a subtrait
	
    ./atlasclient  -c=createtrait --traittype=SuperPM
	
    ./atlasclient  -c=createtrait --traittype=PM --parenttrait=SuperPM
	

3) Create the entity with the Subtrait

    ./atlasclient --c=createDataSetEntity --type=Tims_Fict_Table --name=Andrew_Demo --traitnames=PM
    ./atlasclient --c=createDataSetEntity --type=Tims_Fict_Table --name=MYSQL_DRIVERS55 --traitnames=PM

4) Create a Data Set Search

    ./atlasclient --c=search --type=Tims_Fict_Table --name=MYSQL_DRIVERS55

5) Cretae a lineage

    ./atlasclient --c=createProcessEntity --inptype=Tims_Fict_Table --outtype=Tims_Fict_Table --inpvalue=Andrew_Demo --outvalue=MYSQL_DRIVERS55 --traitnames=SuperPM --type=Jamies_Lineage --name=Lineage12


##### Full Text Search API


    http://atlas-partner-demo01.cloud.hortonworks.com:21000/api/atlas/discovery/search/fulltext?query=Hive
