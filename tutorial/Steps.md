## Assumptions

The demo will instantiate tables of Entity type “Table”. 

If you are using atlas for the first time execute the script “${Atlas_Home}/bin/quick_start.py”. This will create the sample Table Type and Column Types in the Atlas Repository.
 If the sandbox does not contain the quick_start.py you may have to build atlas from source.

Follow the steps in the following link to complete the build

                  https://github.com/shivajid/atlas/blob/master/AtlasBuild.md



##Executing the Scripts

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
MYSQL_DRIVERS8, 
MYSQL_TIMESHEET8; 
default.hortondrivers8@atlasdemo and 
default.hortontimesheet8@atlasdemo
and create a lineage between these tables
