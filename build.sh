#!/bin/bash

app_name=$(grep "app.name" app.config | cut -d'=' -f2)
app_version=$(grep "app.version" app.config | cut -d'=' -f2)
app_icon=$(grep "app.icon" app.config | cut -d'=' -f2)

mkdir -p dist

javac -d bin src/*.java || { echo "Error compiling Java"; exit 1; }
jar --create --file="dist/$app_name.jar" --main-class="$app_name" -C bin . || { echo "Error creating JAR"; exit 1; }

# FOR MACOS BUILD
#################################

cd dist || { echo "Error: Wasn't able to access dist"; exit 1; }

if [ ! -f "../$app_icon" ]; then
    echo "Error: Icon not found in ../$app_icon"
    exit 1
fi

jpackage --name "$app_name" \
         --input . \
         --main-jar "$app_name.jar" \
         --main-class "GameOfLife" \
         --type dmg \
         --app-version "$app_version" \
         --icon "../$app_icon" \


# FOR WINDOWS BUILD
#################################

# jpackage --name "$app_name" \
#          --input . \
#          --main-jar "$app_name.jar" \
#          --main-class "GameOfLife" \
#          --type exe \
#          --app-version "$app_version" \
#          --icon "../$app_icon"
