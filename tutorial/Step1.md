## Assumptions

The demo will instantiate tables of Entity type “Table”. 

If you are using atlas for the first time execute the script “${Atlas_Home}/bin/quick_start.py”. This will create the sample Table Type and Column Types in the Atlas Repository.
 If the sandbox does not contain the quick_start.py you may have to build atlas from source.

Follow the steps in the following link to complete the build

                  https://github.com/shivajid/atlas/blob/master/AtlasBuild.md



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


* Dowload the [Atlas Client install Scripts tar file](https://www.dropbox.com/s/s50nuf6i73uw4gt/atlasInstallScripts.tar.gz?dl=0).


Run the followin steps to install the atlasClient code

<pre>
	wget https://www.dropbox.com/s/s50nuf6i73uw4gt/atlasInstallScripts.tar.gz?dl=0 -O atlasInstallScripts.tar.gz
	gzip -d atlasInstallScripts.tar.gz
	tar -xvf atlasInstallScripts.tar.gz
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
	- Run the following queries
	- <pre>show tables;</pre> 

----

## Validation 

Login to Atlas
	
     http://sandbox.hortonworks.com:21000/#!/search
![Atlas Home](https://github.com/shivajid/atlas/blob/master/tutorial/images/AtlasHome.png)

Search 
“Table where name=”MYSQL_DRIVERS$num” ← replace the num to the iteration number.
Search Results
![Search](https://github.com/shivajid/atlas/blob/master/tutorial/images/Screen%20Shot%202015-07-09%20at%208.59.16%20AM.png)


Click through and see the details. The below link shows the list of attributes and the value. The details page has a BUG where it does not show numeric values on the screen.


![Details/Attributs](https://github.com/shivajid/atlas/blob/master/tutorial/images/Screen%20Shot%202015-07-09%20at%209.15.12%20AM.png)

Lineage -> Output


![](https://github.com/shivajid/atlas/blob/master/tutorial/images/lineage.png)



The Schema tab show the columns for the MYSQL table




![](https://github.com/shivajid/atlas/blob/master/tutorial/images/schema.png)


Now you can search for the hive tables

hive_table where name=”default.hortondrivers9@atlasdemo”

![] (https://github.com/shivajid/atlas/blob/master/tutorial/images/hive_table.png)

Listing all “Types” of Type=CLASS

http://atlas-partner-demo01.cloud.hortonworks.com:21000/api/atlas/types?type=CLASS
![](https://github.com/shivajid/atlas/blob/master/tutorial/images/Screen%20Shot%202015-07-13%20at%2011.13.57%20PM.png)

##CTAS

*Next login to hive

     [hive@atlas-partner-demo01 ~]$ hive
     
*Change to use def
     
     hive > use default;

*Show the table
    
     show tables;

*Now create a table from the hive table where the data was created in hive. 
     
     create table hortondrivers$num._ctas as select drivers_id from hortondrivers${num}.

Replace the $num with the iteration number that you have run the scripts with

![](https://github.com/shivajid/atlas/blob/master/tutorial/images/Screen%20Shot%202015-07-09%20at%209.23.55%20AM.png)


* Now query the “hortondrivers9_ctas and look at the lineage

![](https://github.com/shivajid/atlas/blob/master/tutorial/images/hive_table.png)


The lineage for hortondrivers9_ctas. You can see that the new table is automatically ties up the lineage to the MYSQL_DRIVERS9 table.

![](https://github.com/shivajid/atlas/blob/master/tutorial/images/linage_hivetb.png)




