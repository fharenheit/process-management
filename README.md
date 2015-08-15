## Process Management Pilot

### 소스코드 빌드

소스코드 빌드는 아래와 같이 Git을 이용하여 clone한 후 Apache Maven으로 빌드를 하도록 합니다.

```
# mvn package
```

### AspectJ로 실행하기

AspectJ를 수동으로 적용시키기 위해서는 다음의 커맨드를 입력할 수 있습니다.

```
# java -javaagent:lib/aspectjweaver-1.8.6.jar <MAIN_CLASS>
```

Spark으로 작성한 PI를 AspectJ를 이용하여 적용하기 위해서는 다음의 커맨드를 입력할 수 있습니다.

```
# java -javaagent:lib/aspectjweaver-1.8.6.jar org.opencloudengine.users.fharenheit.scala.SparkPi
```

만약 Maven의 Exec Plugin으로 실행하고자 하는 경우는 Maven이 설치되어 있는 상태에서 다음과 같이 커맨드를 실행합니다.

```
# mvn exec:java
```

### 참고 라이브러리

* AspectJ + Akka Monitoring : https://github.com/eigengo/activator-akka-aspectj
* https://github.com/nativelibs4java
* https://github.com/nativelibs4java/JNAerator
