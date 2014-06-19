## **What is Leshy?** 

_Leshy_ is a framework for replacing default Java serialization with your custom implementation on the fly without any code change in your application by using _Java Instrumentation API_. Leshy doesn't require any startup parameter for using _Java Instrumentation API_. It loads its java agent on the fly when it is needed. With Leshy, you can customize and intercept Java serialization/deserialization logic on the fly. Suppose that you are working on a project and serialization/deserialization codes are splitted into all code by using **"java.io.ObjectOutputStream"** for serialization and **"java.io.ObjectInputStream"** for deserialization. With _Leshy_ you can replace all serialization/deserialization logic with zero code change in your application. 

## **What does Leshy mean?**

The Leshy is a male woodland spirit in Slavic mythology who protects wild animals and forests.

## **What features does Leshy have?**

* You can inject your custom serializer/deserializers to default Java serialization/deserialization flow by specifying their conditions for using.

* You can use default Java serialization/deserialization logic in your custom  serialization/deserialization flow.

* You can switch JVM to default serialization/deserialization flow on the fly.

* You can enable "serialization for all types" mode and so all non serializable classes (not implements **"java.io.Serializable"** interface) can be serialized/deserialized by default Java serialization/deserialization flow with no code change.

## **Install**

In your **pom.xml**, you must add repository and dependency for _Leshy_. 
You can change **"leshy.version"** to any existing _Leshy_ library version.

~~~~~ xml

	...
	<properties>
		...
		<leshy.version>1.0.0-RELEASE</leshy.version>
		...
	</properties>
	...
	<dependencies>
		...
		<dependency>
			<groupId>tr.com.serkanozal</groupId>
			<artifactId>leshy</artifactId>
			<version>${leshy.version}</version>
		</dependency>
		...
	</dependencies>
	...
	<repositories>
		...
		<repository>
			<id>serkanozal-maven-repository</id>
			<url>https://github.com/serkan-ozal/maven-repository/raw/master/</url>
		</repository>
		...
	</repositories>
	...

~~~~~

## **Leshy with simple examples**
  
### **Custom Serialization/Deserialization Examples**

Here is **"ClassToSerialize"** class to using in serialization/deserialization samples:  

~~~~~ java

    public class ClassToSerialize implements Serializable {

        ...
	
        private byte byteValue = 1;
        private boolean booleanValue = true;
        private char charValue = 'X';
        private short shortValue = 10;
        private int intValue = 100;
        private float floatValue = 200.0F;
        private long longValue = 1000;
        private double doubleValue = 2000.0;
        private String stringValue = "str";

        ...

    } 

~~~~~

In this demo, our custom serialization/deserialization logic is injected only for **"ClassToSerialize"** class.

~~~~~ java

    public class LeshySerializationDeserializationDemoWithCustomSerializationLogic {

        public static void main(String[] args) throws IOException, ClassNotFoundException {
            serializeAndDeserializeByUsingCustomSerializationLogicWithCustomSerDe();
        }
	
        public static void serializeAndDeserializeByUsingCustomSerializationLogicWithCustomSerDe() throws IOException, ClassNotFoundException {
            SerDeService serdeService = SerDeServiceFactory.getSerdeService();
            serdeService.
                registerSerDe(
                    new SerDeDispatcher(
                        new ClassFilter(ClassToSerialize.class), 
                        new CustomSerDe())).
                setup();
		
            ClassToSerialize serializedObject = new ClassToSerialize().randomize();
		
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(serializedObject);
            bos.flush();

            byte[] objectContent = bos.toByteArray();
		
            ByteArrayInputStream bis = new ByteArrayInputStream(objectContent);
            ObjectInputStream ois = new ObjectInputStream(bis);
            ClassToSerialize deserializedObject = (ClassToSerialize) ois.readObject();
        }
	
        private static class CustomSerDe implements SerDe {

            @Override
            public void serialize(final Object obj, final OutputStream os) throws IOException {
                // Write your custom serialization logic
            }

            @Override
            public Object deserialize(final InputStream is) throws IOException, ClassNotFoundException {
	        // Write your custom deserialization logic
            }
		
        }
	
    } 

~~~~~ 

This demo is about using default Java serialization/deserialization logic in our custom serialization/deserialization logic.

~~~~~ java

    public class CustomSerDe implements SerDe {

        private SerDeService serdeService;
		
        public CustomSerDe(SerDeService serdeService) {
            this.serdeService = serdeService;
        }
		
        @Override
        public void serialize(final Object obj, final OutputStream os) throws IOException {
            serdeService.runInSandbox(
                new SerializationSandbox() {
                    @Override
                    public void runInSandbox() {
                        try {
                            new ObjectOutputStream(os).writeObject(obj);
                        }
                        catch (Throwable t) {
                            t.printStackTrace();
                            throw new RuntimeException(t);
                        }
                    }
                }
            );
        }

        @Override
        public Object deserialize(final InputStream is) throws IOException, ClassNotFoundException {
            return 
                serdeService.runInSandbox(
                    new DeserializationSandbox() {
                        @Override
                        public Object runInSandbox() {
                            try {
                                return new ObjectInputStream(is).readObject();
                            } 
                            catch (Throwable t) {
                                t.printStackTrace();
                                throw new RuntimeException(t);
                            }
                        }
                    }
                );
        }	
    } 

~~~~~ 

### **Hacking JVM for Serialize and Deserialize Non-Serializable Classes**

Here is **"UnserializableClass"** class (note that not implements **"java.io.Serializable"** interface) for using in "serialization for all type" mode sample:  
 
~~~~~ java

    public class UnserializableClass {

        ...
	
        private byte byteValue = 1;
        private boolean booleanValue = true;
        private char charValue = 'X';
        private short shortValue = 10;
        private int intValue = 100;
        private float floatValue = 200.0F;
        private long longValue = 1000;
        private double doubleValue = 2000.0;
        private String stringValue = "str";

        ...

    }  

~~~~~ 

Default Java serialization/deserialization flow doesn't allow using non-serializable classes (not implements **"java.io.Serializable"** interface). But with Leshy's "serialization for all types" mode, JVM is hacked and all classes (serializable or non-serializable) can be serialized or deserialized by default Java serialization/deserialization flow. In addition, you can invert JVM to default mode with _Leshy_ on the fly.

~~~~~ java

    SerDeService serdeService = SerDeServiceFactory.getSerdeService();
    serdeService.serializationOpenForAllTypes();
			
    UnserializableClass serializedObject = new UnserializableClass().randomize();
		
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(serializedObject);
    bos.flush();

    byte[] objectContent = bos.toByteArray();
		
    ByteArrayInputStream bis = new ByteArrayInputStream(objectContent);
    ObjectInputStream ois = new ObjectInputStream(bis);
    UnserializableClass deserializedObject = (UnserializableClass) ois.readObject();

    serdeService.serializationOpenOnlyForSerializableTypes();

~~~~~

You can find all demo codes (including these samples above) at [https://github.com/serkan-ozal/leshy/tree/master/src/test/java/tr/com/serkanozal/leshy/demo](https://github.com/serkan-ozal/leshy/tree/master/src/test/java/tr/com/serkanozal/leshy/demo)
