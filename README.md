# ms-user-repo
This would accept Webservice calls to store and retrieve user repo fields

# Add in build.gradle

 `compile group: 'com.github.ulisesbocchio', name: 'jasypt-spring-boot-starter', version: '2.1.1'`
       
# Add Annotation in SpringBoot Application Java File 

`@EnableEncryptableProperties`


# To Create encrypted value for client secret

Download jasypt from [here](http://www.jasypt.org/download.html)

```
#jasypt-1.9.2-dist\jasypt-1.9.2\bin> ./encrypt input=password password=MY_SECRET

----ENVIRONMENT-----------------

Runtime: Oracle Corporation OpenJDK 64-Bit Server VM 11.0.2+9



----ARGUMENTS-------------------

input: password
password: MY_SECRET



----OUTPUT----------------------

p6kiu9cOFoH4klCxLJ2zmpGg5vmG2fCQ
```
In above section `password` is the value that you want to encrypt and `MY_SECRET` is the key for encryption whereas `p6kiu9cOFoH4klCxLJ2zmpGg5vmG2fCQ` is the encrypted value that can be used.

# For Development and Test environment

### Windows OS
1. Set in environment variable: `set JASYPT_ENCRYPTOR_PASSWORD=MY_SECRET`
2. Verify its set: `echo %JASYPT_ENCRYPTOR_PASSWORD%`
3. Execute the Spring Boot jar with `java -Djdbc.pass=ENC(p6kiu9cOFoH4klCxLJ2zmpGg5vmG2fCQ)  -Djdbc.url=jdbc:postgresql://localhost:5434/ -jar ms-oauth2-server`

### Unix OS
1. Set in environment variable: `export JASYPT_ENCRYPTOR_PASSWORD=MY_SECRET`
2. Verify its set: `echo $JASYPT_ENCRYPTOR_PASSWORD`
3. Execute the Spring Boot jar with `java -Djdbc.pass=ENC(p6kiu9cOFoH4klCxLJ2zmpGg5vmG2fCQ)  -Djdbc.url=jdbc:postgresql://localhost:5434/ -jar ms-oauth2-server`

### Eclipse IDE
1. In Run Configuration->Environment
2. Add Name=`jasypt.encryptor.password` value=`MY_SECRET`
3. Save Run Configuration and Run.




# For Docker

```
>docker run -e JASYPT_ENCRYPTOR_PASSWORD=MY_SECRET -p 8888:8888 org.sj/ms-oauth2-server -Djdbc.pass=ENC(sTU0WwEA45K/jHxcswowpRxE0xU2h4Vv) -Djdbc.url=jdbc:postgresql://10.0.75.1:5434/ org.sj.msoauth2server.MsOauth2ServerApplication
```


# For Production environment

Avoid exposing the password in the command line of jar execution, since you can query the processes with ps, previous commands with history, etc etc. You could:

1. Create a script like this: `touch setEnv.sh`
2. Edit setEnv.sh to export the `JASYPT_ENCRYPTOR_PASSWORD` variable

```
#!/bin/bash
export JASYPT_ENCRYPTOR_PASSWORD=MY_SECRET
```

3. Execute the file with `. setEnv.sh`
4. Run the app in background `` &
5. Delete the file `setEnv.sh`
6. Unset the previous environment variable with: `unset JASYPT_ENCRYPTOR_PASSWORD`


