# cmput663-architecture

This repository is created for the CMPUT663 course at the University of Alberta in the Fall 2017. It analyzes the impact of architectural change on the build result of ten big Java projects. The introduced framework can easily be reused for different tasks where GitHub versions must be downloaded and their architecture reconstructed and compared.

For a full report see tex/report. All foudn results can be found and recalculated from results/.

# Setup

## Get Database

Download TravisTorrent: https://travistorrent.testroots.org/dumps/travistorrent_8_2_2017.sql.gz

Create new SQL Database / User

```
create user 'archi'@'localhost' identified by 'archi';
create database archi;
grant all privileges on archi.* to archi@'localhost';
flush privileges;

zcat travistorrent_8_2_2017.sql.gz | mysql -u archi -p archi
```

Password: archi

Wait a little bit

## Add Local Maven Dependencies

Go to folder source

```
mvn install:install-file -Dfile=local-mvn-repo/arcade.jar -DgroupId=edu.usc.softarch -DartifactId=arcade -Dversion=0.1.0 -Dpackaging=jar
mvn install:install-file -Dfile=local-mvn-repo/HUSACCT.jar -DgroupId=nl.hu.husacct -DartifactId=husacct -Dversion=5.4.0 -Dpackaging=jar
```

## Build Jar

```
mvn clean install
```

Use *-shaded.jar. 

Important: The folders arcadepy and cfg must be in the same folder as the *.jar file

# Usage

Invoke with 
```
java -jar architecture-0.0.1-SNAPSHOT-shaded.jar PROJECT_NAME VALUE
```

If VALUE is 0 all project versions will be analyzed, if set to a positive integer this many versions will be analyzed. If set to -1 a JSON database file for this project is created out of the MySQL database (if script needs to run on a machine without MySQL)

Results will be in the extracted folder.

# Analyzed Projects 

select gh_lang, gh_project_name, count(gh_project_name) as commits, tr_log_analyzer from travistorrent_8_2_2017 where gh_lang = "java" and tr_log_analyzer="java-maven" group by gh_project_name order by commits desc limit 10;

To analyze these projects, create all database files and then runn the runProjects.sh script.


