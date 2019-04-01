<!---
   Copyright 2019 Martin van den Bemt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
--->

# Printergateway #


The printergateway serves as a gateway for my printer to send scans to the location I really need. My printer
(and old HP Color LaserJet CM2320nf MFP) only has the option to send my scans in a mail 
(or scan it with a client that I don't user / have). So I wrote a gateway that is able to write the scans to a 
different location by using a mail server that dispatches it to the right place based on configuration.

## Build ##

There are 2 builds for this project : docker image and a tar.gz and zip for ane you can start on the commandline and
as (service) daemon through the Java Service Wrapper (an old version).

## image ##

If you have build the image, you can use it locally or deploy to a repository.
An exmple of a private registry, follow these steps :

Assume your private registry is at 192.168.1.122:5000/printergateway

* docker image ls
* find the latest image id for printergateaway
* docker tag <image id> 192.168.1.122:5000/printergateway 
* docker push 192.168.1.122:5000/printergateway

Some links for the maven jib plugin :

* https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#quickstart
* https://github.com/GoogleContainerTools/jib/blob/master/docs/faq.md#how-do-i-set-parameters-for-my-image-at-runtime



## Starting ##

Defaults :
* The logging configuration will be read from /conf/logging.properties
* The configuration will be read from /conf/printergateway.conf

#### start as application ####

Minimal start (when you seperate secrets and config (assuming pwd is the "root" directory of those directories)) :
```
docker run -it -v `pwd`/conf:/conf -v `pwd`/secrets:/secrets -p 25000:25000 --rm printergateway -config /conf/printergateway.conf
```

Overriding all defaults on the commandline (mimicing the defaults) :

```
docker run -it -e "JAVA_TOOL_OPTIONS=-Djava.util.logging.config.file=/conf/logging.properties" -v `pwd`/tmp/conf:/conf -v `pwd`/tmp/secrets:/secrets -p 25000:25000 --rm printergateway -config /conf/printergateway.conf
```

#### start as daemon ####

Please refer to the details in the previous section

```
docker run -d -v `pwd`/conf:/conf -v `pwd`/secrets:/secrets -p 25000:25000 --restart=always --name printergateway printergateway -config /conf/printergateway.conf
```



### Google Drive Integration ###


#### Create a test private key for the unit tests ####

To be able to test the google drive integration, we need a test private key, to prevent using a real one. 
The test private key can be found in the src/test/resources directory. 

The googldrive api reads the p12 with the following characteristics :
 * keystore password must be _**notascret**_
 * private key password must be _**notascret**_
 * the private key must be present under alias _**privatekey**_

```bash
openssl req -x509 -days 36500 -new -key private.pem -out public.pem -subj "/CN=printergatewaytest"
openssl pkcs12 -export -in public.pem -inkey private.pem -out printergatewaytestcert.p12 -name "privatekey" -password pass:notasecret
```

