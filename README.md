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

This codebase is old and had some renewal to make it work with my new needs. The only thing working for sure is the Google Drive 
integration and probably the file one. The mail one is probably not working.
After cleaning up some GSuites accounts my gateway stopped working and the problems started with creating a new key. 
I logged here what I did to get it working again. I haven't tested the dependency changes in docker, so your mileage may vary.

Reasonable pull requests are welcome (first discuss things like refactoring, etc).

The printergateway serves as a gateway for my printer to send scans to the location I really need. My printer
(and old HP Color LaserJet CM2320nf MFP) only has the option to send my scans in a mail 
(or scan it with a client that I don't user / have). So I wrote a gateway that is able to write the scans to a 
different location by using a mail server that dispatches it to the right place based on configuration.

## Build docker image ##

Even though the maven image build is in a profile, it is activated by default. The initial idea was to
provide more installer types, but since I have no use for it atm I let that idea go.

So just run :

```
mvn clean package
```

which installs the image locally.

#### Copying image to a docker registry ####

If you have build the image, you can use it locally or deploy to a repository.
An example of a private registry, follow these steps :

Assume your private registry is at 192.168.1.122:5000/printergateway

* docker image ls
* find the latest image id for printergateaway
* docker tag <image id> 192.168.1.122:5000/printergateway 
* docker push 192.168.1.122:5000/printergateway


## configuration ##
In the example directory are a sample loogging.properties and printergateway.conf configuration.


## Starting ##

When starting the container there are some defaults, if you do not provide one on the commandline  :

* The logging configuration will be read from /conf/logging.properties
* The configuration will be read from /conf/printergateway.conf

#### start as application ####

Minimal start (when you separate secrets and config (assuming pwd is the "root" directory of those directories)) :
```
docker run -it -v `pwd`/conf:/conf -v `pwd`/secrets:/secrets -p 25000:25000 --rm printergateway -config /conf/printergateway.conf
```

Overriding all defaults on the commandline (mimicking the defaults) :

```
docker run -it -e "JAVA_TOOL_OPTIONS=-Djava.util.logging.config.file=/conf/logging.properties" -v `pwd`/tmp/conf:/conf -v `pwd`/tmp/secrets:/secrets -p 25000:25000 --rm printergateway -config /conf/printergateway.conf
```

#### start as daemon ####

Please refer to the details in the previous section

Prefix the printergateway with your private docker registry. 
So eg if your local registry is at 192.168.1.122:5000 use 192.168.1.122:5000/printergateway to use the image

```
docker run -d -v `pwd`/conf:/conf -v `pwd`/secrets:/secrets -p 25000:25000 --restart=always --name printergateway printergateway -config /conf/printergateway.conf
```

#### watch logging ####
Assuming the name was printergateway
```
docker logs -f printergateway
````

### Maven jib plugin ###
Some links for the maven jib plugin :

* https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#quickstart
* https://github.com/GoogleContainerTools/jib/blob/master/docs/faq.md#how-do-i-set-parameters-for-my-image-at-runtime

### Google Drive Integration ###

#### Give access to the application ####

I use GSuite, so maybe things are different if you just use gmail.
Authorisation is a PITA if you use documentation. 

 * Go to https://console.developers.google.com/apis/credentials
 * Select the right project (you probably need the gsuite one)
 * Choose from Create credentials the Service Account Key
 * Select New service account from Service Account dropdown
 * Create and select Create Without Role.
 * Download for p12 file pops up save this key to a place, so it can be found by the Printergateway.
 * Go to IAM & Admin > Service accounts
 * Use the Email of that serviceaccount in the account-user and account-id configuraation item (you need this address later)
 * Go to the menu image on the left (the 3 lines) and select API & Services  -> Library.
 * Click on Google Drive API under G Suite and enable it.
 * Go to the Admin Console of your G Suite App (it should be a Google Admin titles screen)
 * Go to security and click on API reference
 * Go to security and go to Advanced Settings and open it
 * Click on Manage API client access
 * Paste the mail address from account-user or account-id (they are the same) in the Client Name field (you can also paste the service account mail address)
 * In One or More API Scopes add https://www.googleapis.com/auth/drive and click Authorize
 * Make sure the whole path to the folder the upload is in, is accessible by the account, so you should give the account mail address access.
 * If you think that is too much rights, just add a new structure from the root of your drive and add rights there
 * You can symlink that folder in another folder, by selecting that folder, hit shift-z and "move" it to another place.
 * You should see it in the new folder and Details of the folder should mention 2 locations.
 

#### Create a test private key for the unit tests ####

To be able to test the google drive integration, we need a test private key, to prevent using a real one. 
The test private key can be found in the src/test/resources directory. 

The googledrive api reads the p12 with the following characteristics :
 * keystore password must be _**notascret**_
 * private key password must be _**notascret**_
 * the private key must be present under alias _**privatekey**_

```bash
openssl req -x509 -days 36500 -new -key private.pem -out public.pem -subj "/CN=printergatewaytest"
openssl pkcs12 -export -in public.pem -inkey private.pem -out printergatewaytestcert.p12 -name "privatekey" -password pass:notasecret
```

