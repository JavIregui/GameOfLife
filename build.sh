#!/bin/bash

# Leer las propiedades desde app.config
app_name=$(grep "app.name" app.config | cut -d'=' -f2)
app_version=$(grep "app.version" app.config | cut -d'=' -f2)
app_icon=$(grep "app.icon" app.config | cut -d'=' -f2)

# Ejecutar jpackage con la información extraída
javac -d bin src/*.java
jar --create --file="dist/$app_name.jar" --main-class="$app_name" -C bin .

cd dist
jpackage --name "$app_name" \
         --input . \
         --main-jar "$app_name.jar" \
         --main-class "GameOfLife" \
         --type dmg \
         --app-version "$app_version" \
         --icon "../$app_icon" \

# jpackage --name "$app_name" \
#          --input . \
#          --main-jar "$app_name.jar" \
#          --main-class "GameOfLife" \
#          --type exe \
#          --app-version "$app_version" \
#          --icon "../$app_icon"
