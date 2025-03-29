* Build gRPC
  ```
  cd src/main/resources
  python -m grpc_tools.protoc -I. --python_out=..\..\..\pygrpc --grpc_python_out=..\..\..\pygrpc temp.proto
  ```

* Use Artifact in Maven
  1. Update settings file `C:\Users\<username>\.m2\settings.xml`
  ```
    <settings>
        <servers>
            <server>
                <id>github</id>
                <username><your-username></username>
                <password><gh-personal-access-token-with read/write packages permission></password>
            </server>
        </servers>
    </settings>
    ```
  2. use in another project
  ```
  <repository>
			<id>github</id>
			<name>Github Repository</name>
			<url>https://maven.pkg.github.com/mayurprajapati/appium-wrapper</url>
		</repository>
  ```
  ```
  <dependency>
			<groupId>botrix</groupId>
			<artifactId>automation</artifactId>
			<version>0.0.7</version>
		</dependency>
  ```
