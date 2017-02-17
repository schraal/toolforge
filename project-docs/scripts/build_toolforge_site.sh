# This script requires 2 variables
#
# maven.username=<sourceforge username>
# maven.sourceforge.password=<sourceforge password>
# 
# update the sources of toolforge. All module are checked out in this dir,
#

# the following needs to be tweaked.
#
#export BASE=/home/toolforge
export BASE=/Users/schraal/projects/toolforge

cd $BASE
#cvs update -dPA
#unpack the CVS repo used for testing
cd /tmp
rm -rf test-CVSROOT
tar xvzf $BASE/karma-core/resources/test-CVSROOT.tgz
#now, build the modules and deploy the sites.
cd $BASE/toolforge-core
mvn clean install site:deploy
cd $BASE/karma-core
mvn clean install site:deploy
cd $BASE/karma-cli
mvn clean install site:deploy
cd $BASE/karma-launcher
mvn clean install site:deploy
cd $BASE/project-docs
mvn clean site:site site:deploy
