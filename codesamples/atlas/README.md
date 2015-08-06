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


### Help

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

### Create a Simple Type

As you may know types are the basic building blocks of Apache Atlas. Here we are going to help you create simple type. This is an in built type that takes a name and description.

    ./atlasclient --c=createSimpleType --type=simpletype --description=simple_type

In the above command we have create a  type with name "simpletype" and description simple_type.
* Note - There is shortcoming in the description option where you can only pass words without spaces. You "_" to concatnate. This will be enhanced later.

So for

<table>
<tr>
<td><strong> Action</strong><td>
<td><strong>Options</strong><td>
<td><strong>mandatory</strong></td>
<td><strong>description</strong></td>
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
 <tr>
<td><td>
<td>description<td>
<td>YES</td>
<td>Description of the type you want to create</td>
 </tr>
</table>

### List all Types

You may want to query all the types available in the system. You can use the browser REST API or just use the belo command. It will list all the types. You would need to know the types to see all the options available.

    ./atlasclient --listtype


### Create DataSet Type

DataSet Type is used to create Tables. This is a special type. The Atlas detailed ui is designed to show this information, especially with the 

Atlas comes with built in Type called <strong>Table</strong> that is loaded using the quick_start.py file. This is packaged with atlas. You can find it in <atlas_home>/bin/quick_start.py

If you want to create your own Dataset type you can use.

    ./atlasclient  --c=createDataSetType --type=Tims_Fict_Table
    
It creates the datasettype with name "Tims_Fict_Table". 

Command Options:-

<table>
<tr>
<td><strong> Action</strong><td>
<td><strong>Options</strong><td>
<td><strong>mandatory</strong></td>
<td><strong>description</strong></td>
 </tr>
<tr>
<td>createDataSetType<td>
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







### Create Process Type

Lineages in Atlas are created by Process Types. Process types are speacial types in Atlas. This is used to create lineage between any 2 Entities of DataSet Type. Atlas's quick start comes with "LOAD_PROCESS" built in type. You can create your own type by the following command

     ./atlasclient --c=createProcessType --type=Jamies_Lineage
     

<table>
<tr>
<td><strong> Action</strong><td>
<td><strong>Options</strong><td>
<td><strong>mandatory</strong></td>
<td><strong>description</strong></td>
 </tr>
<tr>
<td>createProcessType<td>
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

### Create Traits

Traits - a distinguishing characteristic or quality or an Entity.

Traits and tags are used inter operably in Atlas. Traits are used to tag an entity. E.g. You can create a Trait of type "PII", "Security","Geolocation" etc.

Traits have an hierarchy. You can have traits inherit from other traits. This will allow you to create a hierarchy of traits and then search by trait.

In the example below I will create trait called Super PM and create a child of that called PM, which will inherit from PM.

Note that Traits can have attributes too. But the CLI does not have that option currently.

     ./atlasclient  -c=createtrait --traittype=SuperPM
	
    ./atlasclient  -c=createtrait --traittype=PM --parenttrait=SuperPM

This will create a trait hierarchy of

<pre>
/SuperPM
       |----/PM
</pre>




### Create Entity

Now that you have created Types, you can create your own Entities. Entities are instances of Types. They represent the actual values that are modeled. You can think of Types as "cast or mold or a die". The actual entities are the models of that die/cast/mold.

Lets create a Simple Entity of a simple type we created above

      /atlasclient --c=createSimpleEntity --name=demo --type=simpletype  --description=demo_instance_pretty_simple
      


<table>
<tr>
<td><strong> Action</strong><td>
<td><strong>Options</strong><td>
<td><strong>mandatory</strong></td>
<td><strong>description</strong></td>
 </tr>
<tr>
<td>createSimpleEntity<td>
<td><td>
<td></td>
<td></td>
 </tr>
<tr>
<td><td>
<td>type<td>
<td>YES</td>
<td>This is the Simple type that you will use to create the entity</td>
 </tr>
 <tr>
<td><td>
<td>name<td>
<td>YES</td>
<td>This is the name of the entity</td>
 </tr>
  <tr>
<td><td>
<td>description<td>
<td>YES</td>
<td>This is the description. By default it will adds a description if not added</td>
 </tr>
</table>


Lets create a DataSet(Table) Entity of a DataSet type we created above. <strong>Note</strong> that the "--traitnames" tag that we are attaching to the option. This allows traits to be attached to Entities created. It will allow you to search the entity using traits in the Atlas UI.

     ./atlasclient --c=createDataSetEntity --type=Tims_Fict_Table --name=Andrew_Demo --traitnames=PM
     ./atlasclient --c=createDataSetEntity --type=Tims_Fict_Table --name=MYSQL_DRIVERS55 --traitnames=PM
     
We Create 2 Entities. We will use it for Creating a Lineage between these 2.

Once you have create an Entity, you can search for them using a DSL from the Atlas Application UI or using the search option in the atlasclient.

<table>
<tr>
<td><strong> Action</strong><td>
<td><strong>Options</strong><td>
<td><strong>mandatory</strong></td>
<td><strong>description</strong></td>
 </tr>
<tr>
<td>createDataSetEntity<td>
<td><td>
<td></td>
<td></td>
 </tr>
<tr>
<td><td>
<td>type<td>
<td>YES</td>
<td>This is the Simple type that you will use to create the entity</td>
 </tr>
 <tr>
<td><td>
<td>name<td>
<td>YES</td>
<td>This is the name of the entity</td>
 </tr>
  <tr>
<td><td>
<td>description<td>
<td>NO</td>
<td>This is the description. By default it will adds a description if not added</td>
 </tr>
   <tr>
<td><td>
<td>traitnames<td>
<td>NO</td>
<td>This will add Traits/tags to Atlas</td>
 </tr>
</table>

### Importing Business Taxonmony

In Atlas to Model a Business Taxonomy you would use a trait. To simplify a hierarchy of traits and import of the traits, you can give Atlas a generic Json file that it will iterate over and create a taxonomy of traits.

E.g.

<pre>
/Organization
	|
	----Products
	|	|
	|	----Vehicles
	|		\Entites [Light Truck, Heavy Truck]
	----Parts
	|	|
	|	----TruckParts
	|	|
	|	----AutoParts	

</pre>

This will be modeled in the following json format. 


<pre>
{"Organization": {
  
  "Products": {
    "Vehicles": {
    	"Entities1":[
      {"type": "Table", "name": "DRIVERS"},
     {"type": "Table", "name": "TIMESHEET"},
     {"type": "hive_table", "name": "test.drivers@atlasdemo"}
    ]
  },
  "Parts": {
  "TruckParts": "NONE",
  "AutoParts": "NONE"
     }
 }
 }
 }
</pre>

##### Rules for Parsing

A trait hierarchy will be created with the <strong>names</strong> of the elements in the json file.
It will use the values in the JSON Array to assign the Entities identified by the type and name to the branch of the Trait hierarchy. The element/name holding the Array is ignored

In the above hierarchy table Drivers, Timesheet and test.drivers@atlasdemo; if they exist will be assigned to the Entities.

<strong> Note </strong> : There is no way to delete an Entity today. 

##### How to import the Json File

<pre>

./atlasclient --c=loadtraithierarchy --jsonfilepath=/Users/sdutta/TestHierarchy.json
</pre>

<strong>Validation</strong>

In the Atlas UI on the left hand side you should see the new traits being created. Now you should be able to click the trait and see the narrowed down entities attached to those traits.


