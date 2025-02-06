Step config app auth OIDC
step 1 : Start key cloak using : bin\kc.bat start-dev
step 2 : admin keycloak : http://localhost:8080
step 3 : Start https using ngrok : ngrok http http://localhost:8080
step 4 : Change domain URL in build.gradle
