#!/bin/bash

############################################################################
# This shell file is to imulate a sqoop handler process. 
# Version- 1.0
# Date - July 9th, 2015
# Note - The paths in the scripts are specific to the hortonworks sandbox - 2.3
###############################################################################


#
# The below line executes the sqoop process to import the drivers table
#

sqoop import --connect jdbc:mysql://172.24.0.207:3306/test --username trucker1 --password trucker --table DRIVERS -m 1 --target-dir demo$1 --hive-import --hive-table hortondrivers$1

#
# The below line emulates the import of the timesheet table 
#

sqoop import --connect jdbc:mysql://172.24.0.207:3306/test --username trucker1 --password trucker --table TIMESHEET -m 1 --target-dir demo$1 --hive-import --hive-table hortontimesheet$1


#
# The below code create the entities in atlas for Drivers and Timesheet table.
# The Drivers and Timesheet tables are pre fixed with type MYSQL to denote the source in TimeSheet
#

/usr/jdk64/jdk1.8.0_40/bin/java -cp AtlasDemo1.jar:/usr/hdp/2.3.0.0-2434/atlas/bridge/hive/*:/usr/hdp/2.3.0.0-2434/atlas/hook/hive/* com.atlas.test.mysqlTypeCreator http://atlas-partner-demo01.cloud.hortonworks.com:21000 test MYSQL_DRIVERS$1 MYSQL_TIMESHEET$1 nosearch


#
# The below line creates the entity for hive_table type
# hive table entities in atlas are created in the following format
# <databasename>.<tablename>@<clustername>
#

/usr/jdk64/jdk1.8.0_40/bin/java -cp AtlasDemo1.jar:/usr/hdp/2.3.0.0-2434/atlas/bridge/hive/*:/usr/hdp/2.3.0.0-2434/atlas/hook/hive/* com.atlas.test.HiveMetaDataGenerator http://atlas-partner-demo01.cloud.hortonworks.com:21000 atlasdemo default hortondrivers$1

#
# The below line creates the entity for hive_table type
# hive table entities in atlas are created in the following format
# <databasename>.<tablename>@<clustername>
#

/usr/jdk64/jdk1.8.0_40/bin/java -cp AtlasDemo1.jar:/usr/hdp/2.3.0.0-2434/atlas/bridge/hive/*:/usr/hdp/2.3.0.0-2434/atlas/hook/hive/* com.atlas.test.HiveMetaDataGenerator http://atlas-partner-demo01.cloud.hortonworks.com:21000 atlasdemo default hortontimesheet$1

#
# The below line creates the lineage between the mysql and the drivers table
#
/usr/jdk64/jdk1.8.0_40/bin/java -cp AtlasDemo1.jar:/usr/hdp/2.3.0.0-2434/atlas/bridge/hive/*:/usr/hdp/2.3.0.0-2434/atlas/hook/hive/* com.atlas.test.AtlasEntityConnector http://atlas-partner-demo01.cloud.hortonworks.com:21000 Table MYSQL_DRIVERS$1 hive_table default.hortondrivers$1@atlasdemo

#
# The below line creates the lineage between the mysql and the drivers table
#

/usr/jdk64/jdk1.8.0_40/bin/java -cp AtlasDemo1.jar:/usr/hdp/2.3.0.0-2434/atlas/bridge/hive/*:/usr/hdp/2.3.0.0-2434/atlas/hook/hive/* com.atlas.test.AtlasEntityConnector http://atlas-partner-demo01.cloud.hortonworks.com:21000 Table MYSQL_TIMESHEET$1 hive_table default.hortontimesheet$1@atlasdemo 

