# Hera
[![travis-ci](https://www.travis-ci.org/thegenius/hera.svg?branch=master)](https://travis-ci.org/thegenius/hera)
[![codecov](https://codecov.io/gh/thegenius/hera/branch/master/graph/badge.svg)](https://codecov.io/gh/thegenius/hera)
[![maven-central](https://img.shields.io/badge/maven-0.0.2-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Chera)
[![apache-license](https://img.shields.io/badge/license-Apache--2.0-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)  

  
This is a brave new java rpc framework.

## Support:  
	[1] asm generated proxy, faster than jdk dynamic proxy.  
	[2] function overload without annotations and configures.    
	[3] future based async call.  
	[3] server and client in the same node with same thread pool.    
    [4] clean exception message, when you have trouble then you will have useful tip.	

## Hello World
```java
package com.lvonce;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.HeraNode;

public class App {

	public static interface Service {
		public String hello(String name);
	}

	public static class Provider implements Service {
		public String hello(String name) {
			return "Hello " + name;
		}
	}

	public static void main(String[] args) {
		if (args[0].equals("a")) {
			HeraNode.exports(new Provider(), Service.class);
			HeraNode.start(3721);
		}

		if (args[0].equals("b")) {
			Service service = HeraNode.imports(Service.class, "127.0.0.1", 3721);
			String result = service.hello("World!");
			RpcLogger.info(App.class, result);
		}
	}
}
```
You can run the example within the example directory by following command:
```
    cd example/helloworld
    mvn clean package
    java -jar target/example-1.0-SNAPSHOT-jar-with-dependencies.jar a &
    java -jar target/example-1.0-SNAPSHOT-jar-with-dependencies.jar b
```

## QUICK START
Now you can use maven to integrate hera with your own project:

```
<dependency>
    <groupId>com.lvonce</groupId>
    <artifactId>hera</artifactId>
    <version>0.0.2</version>
</dependency>
```

## DESIGN

![design](https://raw.githubusercontent.com/thegenius/hera/master/doc/hera_design.png)


## ROADMAP
This framework will follow the following versiont strategy:
major.minor.fixOrUpdate
minor with odd number: new futures and new interface
minor with even number: performance and stablity

Month Realse Note:
2017.08: Core Structure
2017.09: Http Support


