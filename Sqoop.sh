#!/bin/bash
# A Simple Shell Script To get Sqoop running
# Author - Shivaji Dutta
# Change the ip address to the ipadderss of the mysql server

sqoop import --connect jdbc:mysql://172.16.106.207:3306/test --username trucker --password trucker --table DRIVERS -m 1 --target-dir demo1 --hive-import --hive-table drivers
