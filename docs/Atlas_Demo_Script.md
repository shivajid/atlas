1) Create a New DataSet Type

./atlasclient  --help

./atlasclient  --c=createDataSetType --type=Tims_Fict_Table

2) Create new Traits
	Create a subtrait
	
./atlasclient  -c=createtrait --traitnames=SuperPM
	
./atlasclient  -c=createtrait --traittype=PM --parenttrait=SuperPM
	

3) Create the entity with the Subtrait

./atlasclient --c=createDataSetEntity --type=Tims_Fict_Table --name=Andrew_Demo --traitnames=PM

4) Create a Data Set Search

./atlasclient --c=search --type=Table --name=MYSQL_DRIVERS55

5) Cretae a lineage

./atlasclient --c=createProcessEntity --inptype=Tims_Fict_Table --outtype=Table --inpvalue=Andrew_Demo --outvalue=MYSQL_DRIVERS99 --traitnames=SuperPM --type=Jamies_Lineage --name=Lineage12


http://atlas-partner-demo01.cloud.hortonworks.com:21000/api/atlas/discovery/search/fulltext?query=Hive



	