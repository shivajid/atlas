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

* Installation
* Usage
* Sample Scripts 

### Installation

The script is designed to work from your local laptop.

There are 2 options
* You can build the maven repo. 
* You can download the binary zip

Below are the steps for building from scratch.

#### Pre-requisites
Java version - This code is built on JDK 8. Please use JDK 8
maven version - This code is built with maven version 3.2

This code was built with an early release of apache incubator version 6. The jars needs to be installed locally. There are 4 jars that are needed.

* atlas-webapp-0.6-incubating-SNAPSHOT-classes.jar
* atlas-repository-0.6-incubating-SNAPSHOT.jar
* atlas-typesystem-0.6-incubating-SNAPSHOT.jar
* atlas-client-0.6-incubating-SNAPSHOT.jar 
* hive-bridge-0.6-incubating-SNAPSHOT.jar

Blow is a sample script to install the jars to your local maven repository. This is a one time activity. I have placed the jars in my drop box account.

[Download Atlas jars](https://www.dropbox.com/sh/62vj5ewcnxv7plh/AADgTUkuIQoGKQqmj-obMhOla?dl=0)

    mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=/Users/sdutta/atlas-server/apache-atlas-0.6-incubating-SNAPSHOT/bridge/hive/atlas-webapp-0.6-incubating-SNAPSHOT-classes.jar  -DgroupId=com.hortonworks.atlas -DartifactId=atlas-webapp -Dversion=0.6-incubating-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

    mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=/Users/sdutta/atlas-server/apache-atlas-0.6-incubating-SNAPSHOT/bridge/hive/atlas-typesystem-0.6-incubating-SNAPSHOT.jar  -DgroupId=com.hortonworks.atlas -DartifactId=atlas-typesystem -Dversion=0.6-incubating-SNAPSHOT -Dpackaging=jar -DgeneratePom=true


    mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=/Users/sdutta/atlas-server/apache-atlas-0.6-incubating-SNAPSHOT/bridge/hive/atlas-repository-0.6-incubating-SNAPSHOT.jar  -DgroupId=com.hortonworks.atlas -DartifactId=atlas-repository -Dversion=0.6-incubating-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

    mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=/Users/sdutta/atlas-server/apache-atlas-0.6-incubating-SNAPSHOT/bridge/hive/atlas-client-0.6-incubating-SNAPSHOT.jar  -DgroupId=com.hortonworks.atlas -DartifactId=atlas-client -Dversion=0.6-incubating-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

     mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=/Users/sdutta/atlas-server/apache-atlas-0.6-incubating-SNAPSHOT/hook/hive/hive-bridge-0.6-incubating-SNAPSHOT.jar -DgroupId=com.hortonworks.atlas -DartifactId=atlas-hive -Dversion=0.6-incubating-SNAPSHOT -Dpackaging=jar -DgeneratePom=true


#### Now that your repo is ready you can build this up

Build this out

    mvn clean package

This should generate a jar. If you have issues with the build. Email to - sdutta@hortonworks.com (Shivaji)

Now that you have built the jars out you are ready to test this out.

The build comes with a shell file call atlasclient. It is in the root of the folder.

#### Edit the atlasclient script

    #!/bin/bash


    java -jar target/atlas-1.0-SNAPSHOT-jar-with-dependencies.jar --url=http://sandbox.hortonworks.com:21000 $*


* Change the java location to point to Java 8 in your system.
* * On Mac you can set it by executing

    export JAVA_HOME="$(/usr/libexec/java_home -v 1.8)"

* Update the url host to point to the atlas host

### Usage

1) The script comes with help. You can always run "./atlasclient --help" to get details on the script usage.


###### Help

    hw11825:atlas sdutta$ ./atlasclient --help

    usage: atlasclient
    --ambariClusterName <ambariClusterName>   Ambari Cluster Name
    --c <action>                              action you want to perform
                                              [search|createSimpleType|cre
                                              ateDataSetType|createProcess
                                              Type|createSimpleEntity|crea
                                              teDataSetEntity|createProces
                                              sEntity|createtrait|loadtrai
                                              thierarchy|importmysql]
     -createHiveTables                            used with importmysql
                                              option. Indicating if
                                              hive_table types should also
                                              be created
      --db <db>                                 mysql db
      --description <name>                      description of type or
                                              entity
     -genLineage                                  used with importmysql
                                              option. Indicating if
                                              hive_table and the the mysql
                                              tables should show as
                                              lineage
     -help                                        requesting help
    --inptype <inp_type>                      name of type for input to a
                                              lineage
    --inpvalue <inp_value>                    value for input to a lineage
    --jsonfilepath <jsonfilepath>             json filename. The complete
                                              filepath
     -listtype                                    display all types
    --mysqlhost <mysqlhost>                   mysql host. It assumes mysql
                                              is running on port 3306
    --name <name>                             name of type or entity
    --outtype <out_type>                      name of output to a lineage
    --outvalue <out_value>                    value for output to a
                                              lineage
    --parenttrait <parenttrait>               value of parent trait
    --parenttype <parenttype>                 Super type
    --password <password>                     mysql password
    --traitnames <traitnames>                 name of the trait
    --traittype <traittype>                   value for trait type
    --type <type>                             String describing the type
                                              of the object. You can find
                                              by querying the list -
                                              http://host:21000/api/atlas/
                                              types?type=CLASS
    --url <URL>                               Url for the atlas host
                                              http://host:21000
    --username <username>                     mysql username


The general design of the script is defined by 

    ./atlasclient <action> <options>

The action command is passed by "-c" (I will rename it better in the next release). Each action has a dependent set of options.

e.g. to search for a type

    ./atlasclient --c=search --type=Table --name=sales_fact_monthly_mv

You should see a json response for the entity back. This shows a json with guid for the sales_fact_monthly_mv table.

Next we will step through some usage scenarios. If you want to understand more check the documentation about the [TypeSystem](https://github.com/shivajid/atlas/blob/master/TypeSystem.md) or the [Atlas Overview Deck](https://github.com/shivajid/atlas/blob/master/Atlas.v6.1.BrightTalk.7.2.2015%5B3%5D.pptx?raw=true).

###### Create a Simple Type

As you may know types are the basic building blocks of Apache Atlas. Here we are going to help you create simple type. This is an in built type that takes a name and description.

    ./atlasclient --c=createSimpleType --type=simpletype --description=simple_type

In the above command we have create a  type with name "simpletype" and description simple_type.
* Note - There is shortcoming in the description option where you can only pass words without spaces. You "_" to concatnate. This will be enhanced later.

So for 
<table>
<tr>
<td>Action<td>
<td>Options<td>
<td>mandatory</td>
<td>description</td>
 </tr>
<tr>
<td>createSimpleType<td>
<td><td>
<td></td>
<td></td>
 </tr>
<tr>
<td><td>
<td>type<td>
<td>YES</td>
<td>Name of the type you want to create</td>
 </tr>
</table>

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
