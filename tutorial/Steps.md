## Assumptions

The demo will instantiate tables of Entity type “Table”. 

If you are using atlas for the first time execute the script “${Atlas_Home}/bin/quick_start.py”. This will create the sample Table Type and Column Types in the Atlas Repository.
 If the sandbox does not contain the quick_start.py you may have to build atlas from source.

Follow the steps in the following link to complete the build

                  https://github.com/shivajid/atlas/blob/master/AtlasBuild.md



##Executing the Scripts

Querying the mysql database

Login to the mysql machine

    mysql -u root
    show databases; 

(You should see a test database)
	use test;
	show tables; 

(You should see the DRIVERS and TIMESHEET tables listed)



Login to the sandbox :-

	ssh  root@sandbox.hortonworks.com

Change user to hive

	su hive

change directory to hive home
         
         cd /home/hive or just “cd”

This job has been run multiple times in this environment. There is no way of deleting and cleaning the models. Check the hive table or
./sqoop_job.sh <iteration number>
e.g ./sqoop_job.sh 8

This will create new Entities of Table and hive_table type called 
* MYSQL_DRIVERS8, 
* MYSQL_TIMESHEET8; 
* default.hortondrivers8@atlasdemo and 
* default.hortontimesheet8@atlasdemo
and 
* create a lineage between these tables
* 

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

CTAS
Next login to hive

     [hive@atlas-partner-demo01 ~]$ hive
     
Change to use def
     
     hive > use default;

Show the table
    
     show tables;

Now create a table from the hive table where the data was created in hive. 
     
     create table hortondrivers$num._ctas as select drivers_id from hortondrivers${num}.

Replace the $num with the iteration number that you have run the scripts with

![](https://github.com/shivajid/atlas/blob/master/tutorial/images/Screen%20Shot%202015-07-09%20at%209.23.55%20AM.png)


Now query the “hortondrivers9_ctas and look at the lineage

![](https://github.com/shivajid/atlas/blob/master/tutorial/images/hive_table.png)


The lineage for hortondrivers9_ctas. You can see that the new table is automatically ties up the lineage to the MYSQL_DRIVERS9 table.







