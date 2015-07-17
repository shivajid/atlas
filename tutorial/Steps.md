##Executing the Scripts

Login to the atlas-partner-demo01

         ssh -i secloud.pem root@atlas-partner-demo01.cloud.hortonworks.com

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
