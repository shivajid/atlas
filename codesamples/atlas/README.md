### Demo Script

API Help

    ./atlasclient  --help

1) Create a New DataSet Type

    

    ./atlasclient  --c=createDataSetType --type=Tims_Fict_Table
    
1a) Create a New Lineage Type

   ./atlasclient --c=createProcessType --type=Jamies_Lineage

2) Create new Traits
	Create a subtrait
	
    ./atlasclient  -c=createtrait --traittype=SuperPM
	
    ./atlasclient  -c=createtrait --traittype=PM --parenttrait=SuperPM
	

3) Create the entity with the Subtrait

    ./atlasclient --c=createDataSetEntity --type=Tims_Fict_Table --name=Andrew_Demo --traitnames=PM
    ./atlasclient --c=createDataSetEntity --type=Tims_Fict_Table --name=MYSQL_DRIVERS55 --traitnames=PM

4) Create a Data Set Search

    ./atlasclient --c=search --type=Tims_Fict_Table --name=MYSQL_DRIVERS55

5) Cretae a lineage

    ./atlasclient --c=createProcessEntity --inptype=Tims_Fict_Table --outtype=Tims_Fict_Table --inpvalue=Andrew_Demo --outvalue=MYSQL_DRIVERS55 --traitnames=SuperPM --type=Jamies_Lineage --name=Lineage12


##### Full Text Search API


    http://atlas-partner-demo01.cloud.hortonworks.com:21000/api/atlas/discovery/search/fulltext?query=Hive
