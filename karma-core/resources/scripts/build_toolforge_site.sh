# Dit script heeft 2 variabelen nodig in de ~/build.properties. Deze
# zijn niet hard-coded, omdat het o.a. om het password gaat.
# maven.username=<sourceforge username>
# maven.sourceforge.password=<sourceforge password>
# 
#update the sources of toolforge. All module are checked out in this dir,
cd ~/toolforge
cvs update -dPA
#unpack the CVS repo used for testing
cd /tmp
rm -rf test-CVSROOT
tar xvzf ~/toolforge/karma-core/resources/test-CVSROOT.tgz
#now, build the modules and deploy the sites.
cd ~/toolforge
cd toolforge-core
maven clean jar:install site:deploy
cd ..
cd karma-core
maven clean jar:install site:deploy
cd ..
cd karma-cli
maven clean jar:install site:deploy
cd ..
cd karma-launcher
maven clean jar:install site:deploy
