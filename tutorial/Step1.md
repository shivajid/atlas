## Assumptions

The demo will instantiate tables of Entity type “Table”. 

If you are using atlas for the first time execute the script <strong>“${Atlas_Home}/bin/quick_start.py”</strong>. This will create the sample Table Type and Column Types in the Atlas Repository.
 If the sandbox does not contain the quick_start.py you may have to build atlas from source.

Follow the steps in the following link to complete the build
<pre>
https://github.com/shivajid/atlas/blob/master/AtlasBuild.md
</pre>


##Executing the Scripts

#### Step 1

Login to the mysql machine
<pre>
    mysql -u root
    show databases; 
</pre>
(You should see a test database)
<pre>
	use test;
	show tables; 
</pre>
(You should see the DRIVERS and TIMESHEET tables listed)

See the data in the Drivers table

<pre>
Select * from DRIVERS;
Select * from TIMESHEET LIMIT 20;
</pre>


Login to the sandbox :-

	ssh  root@sandbox.hortonworks.com

### Getting the scripts


Run the followin steps to install the atlasClient code

<pre>
	wget https://www.dropbox.com/s/s50nuf6i73uw4gt/atlasInstallScripts.tar.gz?dl=0 -O atlasInstallScripts.tar.gz
	gzip -d atlasInstallScripts.tar.gz
	tar -xvf atlasInstallScripts.tar
	cd atlas_install_scripts
	./install
	cd atlasClient
</pre>	
<strong>Note</strong>: There is <code>reset_atlas.sh</code> in the scripts folder. This helps in reseting and restarting atlas.

Now you are ready to run the <em>atlasClient</em> cli.  The complete manual is under the [codesamples](https://github.com/shivajid/atlas/tree/master/codesamples/atlas) folder.

For this demo we are going to run the following steps.

### Step 2

Next we are going to import the mysql tables and create the corresponding tables in hive and metadata in hive. This is done by sqoop_job.sh. Before you run the script please edit the scrip to point to the correct mysql host.

Once done, execute the script.

<pre>
	./sqoop_job.sh 
</pre>


This will create new Entities of [type](https://github.com/shivajid/atlas/blob/master/docs/TypeSystem.md) Table and hive_table type called 

* DRIVERS, 
* TIMESHEET 
* default.drivers@Sandbox and 
* default.hortontimesheet@Sandbox
* create a lineage between the mysql tables and the hive tables

Next Validate

a) Login Ambari Hive View
	e.g. on HDP Sandbox
	- http://sandbox.hortonworks.com:8080/#/main/views/HIVE/1.0.0/Hive
	- Run the following queries. You should see the tables and the data imported into hive.
	
<pre>
show tables;
select * from drivers;
select * from timesheet;
</pre> 

Once you have validated the tables in hive. You can check the for the table metadata in Atlas.



## Validation in Atlas

Login to Atlas
	
     http://sandbox.hortonworks.com:21000/#!/search
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
