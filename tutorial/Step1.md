# This demo will instantiate tables of Entity type "Table".

### Assumptions

1. A Hadoop Cluster or HDP Sandbox with
  - Apache Hive, Apache Sqoop
  - Apache Atlas
    - installed by  Ambari, package
    - or [from source](../AtlasBuild.md)

## Demo setup

### Confirm the MySQL tables are present

Login to the mysql machine
  ```
mysql -u root
  ```

  ```
use test;
show tables; 
  ```
  
*(You should see the DRIVERS and TIMESHEET tables listed)*

See the data in the Drivers table

  ```
select * from DRIVERS;
select * from TIMESHEET LIMIT 20;
  ```

### Prepare your HDP Cluster

- Login to your HDP cluster or sandbox: `ssh root@sandbox.hortonworks.com`
- If using Atlas for the first time execute the following script which will create the sample Table Type and Column Types in the Atlas Repository.
  ```
/usr/hdp/current/atlas-server/bin/quick_start.py
  ```

### Install the Atlas CLI

Run the followin steps to install the atlasClient code

  ```
cd
curl -L -o atlasInstallScripts.tar.gz https://www.dropbox.com/s/s50nuf6i73uw4gt/atlasInstallScripts.tar.gz?dl=0
tar -xvf atlasInstallScripts.tar.gz
sudo ./install
cd atlasClient
  ```

**Note**: `reset_atlas.sh` in the scripts folder can be used for resetting & Atlas

Now you are ready to use the *atlas* CLI.

Find the complete manual in [codesamples](../codesamples/atlas).

## The Demo

### Step 2

Next we are going to configure Sqoop to import the MySQL Tables & Metadata to Hive.

1. Update `sqoop_job.sh` to point to your MySQL server.

2. Execute the script: `./sqoop_job.sh`

This will create new Entities of [type](https://github.com/shivajid/atlas/blob/master/docs/TypeSystem.md) Table and hive_table type called 

* DRIVERS, 
* TIMESHEET 
* default.drivers@Sandbox and 
* default.hortontimesheet@Sandbox
* create a lineage between the mysql tables and the hive tables

Next Validate

1. Login Ambari Hive View
  - e.g. on HDP Sandbox: http://sandbox.hortonworks.com:8080/#/main/views/HIVE/1.0.0/Hive
2. Run the following queries. You should see the tables and the data imported into hive.
  ```
show tables;
select * from drivers;
select * from timesheet;
  ```

Once you have validated the tables in Hive. You can check the for the table metadata in Atlas.

## Validation in Atlas

Login to Atlas: http://sandbox.hortonworks.com:21000/#!/search
![Atlas Home](https://github.com/shivajid/atlas/blob/master/tutorial/images/AtlasHome.png)

Search 
“Table where name=”DRIVERS” 

Search Results
![Search](https://github.com/shivajid/atlas/blob/master/tutorial/images/Screen%20Shot%202015-07-09%20at%208.59.16%20AM.png)


Click through and see the details. The below link shows the list of attributes and the value. 

Note - The details page has a BUG where it does not show numeric values on the screen.


![Details/Attributs](https://github.com/shivajid/atlas/blob/master/tutorial/images/Screen%20Shot%202015-07-09%20at%209.15.12%20AM.png)

Lineage -> Output


![](https://github.com/shivajid/atlas/blob/master/tutorial/images/lineage.png)



The Schema tab show the columns for the MYSQL table




![](https://github.com/shivajid/atlas/blob/master/tutorial/images/schema.png)


Now you can search for the hive tables

hive_table where name=”default.hortondrivers@Sandbox”

![] (https://github.com/shivajid/atlas/blob/master/tutorial/images/hive_table.png)

Listing all “Types”. Using the atlasClient we will see how to run the steps.


<pre>
bin/atlasclient  --listtype
</pre>

### ETL - Creating a bad drivers table

* Next login to the hive view and execute
<pre>
     create table bad_drivers AS select d.driver_name  , count(d.driver_name)  from DRIVERS d, TIMESHEET t where d.driver_id = t.driver_id and  t.hours_logged > 60 group by d.driver_name;

      select * from bad_drivers;
</pre>

### Viewing the New Table in Atlas

Login back to atlas UI. Search for 

<pre>
hive_table where name = "default.bad_drivers@Sandbox"
</pre>

You should see the bad_drivers schema, details and the lineage. You should see that the bad drivers was created from the 2 tables and the complete lineage all the way to the Mysql table.

### Loading the business classification

A business classification is created to create a hierarchy of 
<pre>
Sensitive
	|-- EMP_PII
	|	|-- [DRIVERS, default.drivers@Sandbox]
	|--- Financials
	|	|-- [timesheet, default.timesheet@Sandbox, default.bad_drivers@Sandbox]
	|--- Violation
		|-- [default.bad_drivers@Sandbox]
</pre>	

We are going to load the business classification and apply them to the Entities.
<pre>
bin/atlasclient --c=loadtraithierarchy --jsonfilepath=resources/SensitivityHierarchy.json
</pre>

#### Validation

Login to the Atlas UI. You should now see additional tags in the UI on the left. 

Click on the EMP_PII, Financials and Violations you should see the corresponding table show up
