# DGI Setup on Hadoop Cluster

### HDP Sandbox
For the demo purposes we will use the HDP 2.3 Sandbox.

Download the Sandbox from [HDP 2.3](http://hortonworks.com/hdp/downloads/)
Sandbox has everything needed except for latest of Apache Atlas.

### Apache Atlas

Apache Atlas needs to be built from source.

You would have to install "git" to get a clone of the Atlas repository.

          git clone https://git-wip-us.apache.org/repos/asf/incubator-atlas.git atlas
          
  
#### Install Maven

          wget http://www.carfab.com/apachesoftware/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.tar.gz
          tar xvf apache-maven-3.2.5-bin.tar.gz
          mv apache-maven-3.2.5 /usr/local/
          export PATH=/usr/local/apache-maven-3.2.5/bin:$PATH

Now you should be able to build teh atlas project

          cd /root/atlas
          export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=256m" && mvn clean install
          mvn clean verify assembly:assembly -DskipTests


Tar can be found in atlas/target/apache-atlas-${project.version}-bin.tar.gz

Tar is structured as follows

|- bin
   |- atlas_start.py
   |- atlas_stop.py
   |- atlas_config.py
   |- quick_start.py
   |- cputil.py
|- conf
   |- application.properties
   |- client.properties
   |- atlas-env.sh
   |- log4j.xml
|- docs
|- server
   |- webapp
      |- atlas.war
|- README
|- NOTICE.txt
|- LICENSE.txt
|- DISCLAIMER.txt
|- CHANGES.txt


3. Installing & running Atlas
--------------------------------

a. Installing Atlas
~~~~~~~~~~~~~~~~~~~~~~

          tar -xzvf apache-atlas-${project.version}-bin.tar.gz
          cd atlas-${project.version}

b. Starting Atlas Server
~~~~~~~~~~~~~~~~~~~~~~~~~

          bin/atlas-start.sh

#### Dashboard

Apache Atlas comes with a dashboard

* The Dashboard runs on port 21000. 
* The login is admin/admin








          

          
          
          
          


  
  
  
