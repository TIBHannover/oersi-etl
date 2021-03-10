git clone https://github.com/metafacture/metafacture-core.git -b oersi
cd metafacture-core
./gradlew install
cd ..
git clone https://github.com/metafacture/metafacture-fix.git -b oersi
cd metafacture-fix
./gradlew install
cd ..
