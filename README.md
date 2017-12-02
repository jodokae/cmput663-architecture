# cmput663-architecture

# Setup

Download TravisTorrent: https://travistorrent.testroots.org/dumps/travistorrent_8_2_2017.sql.gz

Create new SQL Database / User

create user 'archi'@'localhost' identified by 'archi';
create database archi;
grant all privileges on archi.* to archi@'localhost';
flush privileges;

zcat travistorrent_8_2_2017.sql.gz | mysql -u archi -p archi

Password: archi

Wait


# Testing object

select gh_lang, gh_project_name, count(gh_project_name) as commits from travistorrent_8_2_2017 where gh_lang = "java" group by gh_project_name order by commits desc limit 10;

select gh_lang, gh_project_name, count(gh_project_name) as commits, tr_log_analyzer from travistorrent_8_2_2017 where gh_lang = "java" and tr_log_analyzer="java-maven" group by gh_project_name order by commits desc limit 10;

=> Analyze SonarQube as Test object. Largest

Relevant info:
select tr_log_analyzer, gh_project_name, git_trigger_commit, tr_build_number, tr_status from travistorrent_8_2_2017 where gh_project_name = "SonarSource/sonarqube" && tr_log_analyzer = "java-maven" order by tr_build_number ;


Only Maven because maven architecture is used to analyse (src/main) for extraction

Get Commit: github.com/{gh_project_name}/archive/{tr_trigger_commit}.zip


# Extraction 

HUSACT: http://husacct.github.io/HUSACCT/download/HUSACCT_5.4.jar
https://github.com/HUSACCT/HUSACCT

Can get architecture and metrics out of source code


Own HUSACCT Project for Java direct use: https://github.com/jodokae/HUSACCT
Build HUSACCT via ant and then copy it to local-mvn-repo (already done)

Java Project can then extract Java Classes / Packages / Dependencies


# TODO

Next Steps (30.10.17):

1. Transform Result
2. Decide how to define architecture


# Attention

Import jars from local-mvn-repo to local maven repository using mvn install:install-file .... (full code please)

Folder arcadepy from resources and cfg are needed beside the jar

mvn install:install-file -Dfile=local-mvn-repo/arcade.jar -DgroupId=edu.usc.softarch -DartifactId=arcade -Dversion=0.1.0 -Dpackaging=jar

