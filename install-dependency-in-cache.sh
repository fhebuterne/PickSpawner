#!/bin/bash

# Why this script ?
# Many dependencies like complete spigot (with nms included) and external lib like authlib and datafixerupper
# are not on maven repository, so we need to download it manually and compile with it

if [[ -d ./tmp ]]; then
  echo "remove existing tmp directory"
  rm -r ./tmp
fi

mkdir ./tmp && cd ./tmp || exit

BASE_URL="https://ci.fabien-hebuterne.fr/common/"

versionsMd5=(
  '1.18.2:spigot-1.18.2-R0.1-SNAPSHOT.jar:1d7e9458deed7f8c28b4808ab79e26e2'
  '1.19.2:spigot-1.19.2-R0.1-SNAPSHOT.jar:f2962a3a02e3ecf9c729c508d47ad16a'
  '1.19.3:spigot-1.19.3-R0.1-SNAPSHOT.jar:990b3c6b7cb26ba7146ddffeec19f889'
  '1.19.4:spigot-1.19.4-R0.1-SNAPSHOT.jar:3a6ad963e122fa9d390baea00c49e3c8'
  '1.20.1:spigot-1.20.1-R0.1-SNAPSHOT.jar:9424a0c2ab2937e2087f04ea3104d638'
  '1.20.2:spigot-1.20.2-R0.1-SNAPSHOT.jar:e7c864e4a13e2486e4509bf8cc89af3e'
  '1.20.4:spigot-1.20.4-R0.1-SNAPSHOT.jar:44282b85c0c886d5abb812b13c8667de'
  '1.20.6:spigot-1.20.6-R0.1-SNAPSHOT.jar:80814391c6c38ebcb1ea1b98632a9590'
  '1.21:spigot-1.21-R0.1-SNAPSHOT.jar:9961507a636e6704086c0cf5705d8443'
  '1.21.3:spigot-1.21.3-R0.1-SNAPSHOT.jar:72ff13989fe9de4b21c0a19a06c08f9b'
  '1.21.4:spigot-1.21.4-R0.1-SNAPSHOT.jar:5b19eb7363596253b49ecde141005689'
)

for versionWithMd5 in "${versionsMd5[@]}"; do
  if [[ $versionWithMd5 == *":"* ]]; then
    splitted=(${versionWithMd5//:/ })
    targetFolder=${splitted[0]}
    file=${splitted[1]}
    md5=${splitted[2]}

    if [ "$targetFolder" = "." ]; then
        curl -O "$BASE_URL/$file" --output-dir "$targetFolder"
    else
        mkdir ./"$targetFolder"
        curl -O "$BASE_URL/$targetFolder/$file" --output-dir "./$targetFolder"
    fi

    MD5CHECK=$(md5sum "$targetFolder/$file" | cut -d' ' -f1)

    if [ "$MD5CHECK" = "$md5" ]; then
      echo "Download file $file - OK"
    else
      echo "Download file $file - BAD MD5 - BUILD CANCELED"
      echo "$MD5CHECK"
      exit 1
    fi
  fi
done