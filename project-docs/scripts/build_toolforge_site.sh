# This script requires 2 variables
#
# maven.username=<sourceforge username>
# maven.sourceforge.password=<sourceforge password>
# 
# update the sources of toolforge. All module are checked out in this dir,
#

# the following needs to be tweaked.
#
export BASE=/home/asmedes/dev/toolforge

cd $BASE
#cvs update -dPA
#unpack the CVS repo used for testing
cd /tmp
rm -rf test-CVSROOT
tar xvzf $BASE/karma-core/resources/test-CVSROOT.tgz
#now, build the modules and deploy the sites.
cd $BASE/toolforge-core
maven clean jar:install site:deploy
cd $BASE/karma-core
maven clean jar:install site:deploy
cd $BASE/karma-cli
maven clean jar:install site:deploy
cd $BASE/karma-launcher
maven clean jar:install site:deploy
cd $BASE/project-docs
maven clean site:deploy
