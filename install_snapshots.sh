git clone https://github.com/metafacture/metafacture-core.git
cd metafacture-core
./gradlew install
cd ..
git clone https://github.com/metafacture/metafacture-fix.git
cd metafacture-fix/org.metafacture.fix.parent
./gradlew install
cd ../..
