# MQ-SampleClient

### This sample application is meant to be used a client to interact with an IBM MQ instance.  The included code in `client.java` is meant to be used as an example and not for a production environment.

### Pre Requisites
1.  JDK 1.8+ or above.
1.  Maven version 3.6.3 or above.
1.  Integrated Development Environment such as VSCode, Eclipse, IntelliJ.
1.  If mutual TLS is enabled, have the appropriated trust \ key store files as well as the password(s).


### General Instructions
1.  The sample code allows the client application to interact with the MQ Queue Manager using the PCF api.
1.  Currently the following operations are implemented:
    1.  List all Queues
    1.  Create a new Queue
    1.  Delete an existing Queue.

1.  Update the `config.properties` files with all the required fields and their appropriate values.  The properties `CIPHER_SUITE and SSL*` are only required if mutual TLS is enabled.

    ```
    HOSTNAME=host.example.com
    PORT=1414
    CHANNEL=DEV.APP.SVRCONN
    USERNAME=user1
    PASSWORD=passw0rd
    CIPHER_SUITE=TLS_RSA_WITH_AES_128_CBC_SHA256
    SSL_KEY_STORE_FILE=key.jks
    SSL_KEY_STORE_PASSWORD=passw0rd
    SSL_TRUST_STORE_FILE=trust.jks
    SSL_TRUST_STORE_PASSWORD=passw0rd
    QUEUE_MANAGER_NAME=MQ_MANAGER1
    SAMPLE_QUEUE_NAME=ABC.TESTQ.1
    USE_MUTUAL_SSL=true
    ```
1.  Set `USE_MUTUAL_SSL` to `false` if mutual TLS is not being used.
1.  For mutual TLS connectivity:

        a.  Make sure that the relevant account specific trust \ key store files are present under `src\resources` directory.
        a.  The property `CIPHER_SUITE` must match as the one set on the server.  See https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_8.0.0/com.ibm.mq.dev.doc/q113220_.htm
1.  For configuring mutual TLS see: https://cloud.ibm.com/docs/mqcloud?topic=mqcloud-mqoc_jms_tls

### Run using Maven 
1.  Run `mvn compile`
1.  Run `mvn exec:java -Dexec.mainClass="com.samples.mq.connect.Client"`


### Notes
1.  This application has been tested against IBM MQ server v 9.2.0 running on the IBM Cloud.  For more inforamation see: https://cloud.ibm.com/docs/mqcloud?topic=mqcloud-mqoc_getting_started
 
