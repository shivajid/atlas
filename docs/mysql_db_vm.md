## Create Source System (Mysql Tables)

#### Install Mysql

* Create a CentOS 6.5 Virtula Machine
* Install mysql server
* wget http://dev.mysql.com/get/mysql-community-release-el6-5.noarch.rpm/from/http://repo.mysql.com/
* sudo yum localinstall mysql-community-release-el6-*.noarch.rpm
* sudo yum install mysql-community-server
* sudo service mysqld start
* sudo chkconfig mysqld on
* chkconfig --list mysqld

[Refer to the MySQL Install docs](https://dev.mysql.com/doc/refman/5.6/en/linux-installation-yum-repo.html)

Next login as 

    mysql -u root -h <hostname>

#### Create Table and Load the Data
Execute the script in the github repo 
    MySQLSourceSystem.ddl

This will create all the needed tables.

Load the Drivers Data. Copy the drivers.csv file over from the repo

    LOAD DATA LOCAL INFILE '<dir>/drivers.csv' into table DRIVERS FIELDS TERMINATED BY "," LINES TERMINATEd BY '\n' (DRIVER_ID, DRIVER_NAME,CERTIFIED, WAGE_PLAN);
    

###### Now you should have the Source System Ready!
